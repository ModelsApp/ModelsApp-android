package com.square.android.ui.activity.agenda

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.appbar.AppBarLayout
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.square.android.BuildConfig
import com.square.android.R
import com.square.android.data.Repository
import com.square.android.extensions.getBitmap
import com.square.android.extensions.loadImage
import com.square.android.extensions.setVisible
import com.square.android.presentation.presenter.agenda.ScheduleBooking
import com.square.android.presentation.presenter.agenda.ScheduleBookingPresenter
import com.square.android.presentation.view.agenda.ScheduleBookingView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.utils.ActivityUtils
import com.square.android.utils.PermissionsManager
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_schedule_booking.*
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.terrakok.cicerone.Navigator


private const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1_000L
private const val DEFAULT_MAX_WAIT_TIME = 30_000L

private const val LOCATION_ZOOM_LEVEL = 14.0
private const val LOCATION_ZOOM_ANIMATION = 3000L


//TODO change later when known
private const val MIN_DISTANCE = 500

//TODO this will be changed to new map ac? Map should be loaded only if "allow geolocation is turned on" and if not, project view 2
class ScheduleBookingActivity: BaseActivity(), ScheduleBookingView, PermissionsListener, LocationEngineCallback<LocationEngineResult> {

    @InjectPresenter
    lateinit var presenter: ScheduleBookingPresenter

    @ProvidePresenter
    fun providePresenter() = ScheduleBookingPresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    protected lateinit var mapView: MapView

    protected var mapboxMap: MapboxMap? = null

    protected val markerBackground by lazy { getDefaultMarkerBackground() }

    protected val markerIconGray by lazy { getGrayMarker() }

    protected val markerIconPink by lazy { getPinkMarker() }

    protected var locationComponent: LocationComponent? = null

    private lateinit var style: Style

    private val repository: Repository by inject()

    private var locationEngine: LocationEngine? = null

    private var permissionsManager: PermissionsManager? = null

    private var loadingDialog: LoadingDialog? = null

    private var isCalculated = false

    private var movePoint: Float = 0F

    private var animationWeight: Float = 0F

    private var isStatusBarLight: Boolean = true

    var readMoreAlreadyClicked = false

    private val DIRECTIONS_LAYER_ID = "DIRECTIONS_LAYER_ID"
    private val LAYER_BELOW_ID = "road-label-small"
    private val SOURCE_ID = "SOURCE_ID"

    private var dashedLineDirectionsFeatureCollection: FeatureCollection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_schedule_booking)

        mapView = map
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->
            mapboxMap = map

            map.setStyle(Style.LIGHT) {
                style = it

                initDottedLineSourceAndLayer(style)

                initMap()
            }
        }

        permissionsManager = PermissionsManager(this)

        loadingDialog = LoadingDialog(this)

        arrowBack.setOnClickListener { onBackPressed() }

        readMoreTv.setOnClickListener {
            readMoreAlreadyClicked = true

            readMoreLl.visibility = View.GONE
            tvNotes.maxLines = Integer.MAX_VALUE
        }

        placeAppBar.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                    if(!isCalculated){
                        movePoint = 1 - 0.9f
                        animationWeight =  1 / (1 - movePoint)
                        isCalculated = true
                    }
                    updateViews(Math.abs(i / appBarLayout.totalScrollRange.toFloat()))
                })

    }

    private fun updateViews(offset: Float){
        when (offset) {
            in 0.555F..1F -> {
                if(!isStatusBarLight){
                    isStatusBarLight = true
                    setLightStatusBar(this)
                }
            }
            in 0F..0.555F -> {
                if(isStatusBarLight){
                    isStatusBarLight = false
                    clearLightStatusBar(this)
                }
            }
        }

        if(offset > movePoint) {
            topLayoutsLl.visibility = View.INVISIBLE
            topIcon.setVisible(false)
        }
        else {
            topLayoutsLl.visibility = View.VISIBLE
            topIcon.setVisible(true)
        }

        roundedView.apply {
            val mHeight = Math.round(resources.getDimension(R.dimen.v_24dp))

            when {
                offset > movePoint -> {
                    val titleAnimationOffset = (offset - movePoint) * animationWeight

                    val roundedMeasuredHeight = Math.round(mHeight - (mHeight * (titleAnimationOffset)))

                    if(offset == 1f){
                        this.layoutParams.also {
                            it.height = 0
                        }
                    } else{
                        if(roundedMeasuredHeight >= 0) {
                            this.layoutParams.also {
                                it.height = roundedMeasuredHeight
                            }
                        }
                    }
                }
                else -> {
                    this.layoutParams.also {
                        it.height = mHeight
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun showData(data: ScheduleBooking){
        placeName.text = data.placeName
        placeAddress.text = data.placeAddress

        tvDate.text = data.date
        tvCode.text = data.code
        tvTimeframe.text = data.timeframe
        tvType.text = data.type

        tvOfferName.text = data.offerName
        tvTipValue.text = data.offerTip
        tvTakeaway.text = data.ofeferTakeaway
        tvDescription.text = data.offerDescription
        val ss = SpannableString(getString(R.string.notes_)+" "+data.notes)
        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.black)), 0 , getString(R.string.notes_).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvNotes.text = ss
        tvNotes.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onNotesLoaded()
                tvNotes.viewTreeObserver.removeOnGlobalLayoutListener(this)
                tvNotes.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        placeImg.loadImage(data.placeImg,
                placeholder = android.R.color.transparent,
                roundedCornersRadiusPx = 360,
                whichCornersToRound = listOf(RoundedCornersTransformation.CornerType.ALL))

        userImg.loadImage(data.userImg,
                placeholder = android.R.color.transparent,
                roundedCornersRadiusPx = 360,
                whichCornersToRound = listOf(RoundedCornersTransformation.CornerType.ALL))

        checkingInTv.text = if(data.userGender == 0 ) getString(R.string.check_in_ms) else getString(R.string.check_in_mr)
        topUserName.text = data.userName

        locationEngine = null
        var geolocationOn = false

        if(PermissionsManager.areLocationPermissionsGranted(this)) {
            if (repository.getGeolocationAllowed()) {
                geolocationOn = true
            }
        }

        if(geolocationOn){
            mapboxMap?.clear()
            val key = data.placeName.trim().split(" ").first()
            val markerOption = MarkerOptions().title(key).position(data.latlng).icon(markerIconGray)
            mapboxMap?.addMarkers(listOf(markerOption))

            locationEngine = LocationEngineProvider.getBestLocationEngine(this)
            locationEngine?.getLastLocation(this)

            requestLocationUpdates()

            setupScreen(1)
        } else{
            setupScreen(2)
        }
    }

    private fun onNotesLoaded(){
        if(!readMoreAlreadyClicked){
            var startOffset: Int
            var endOffset: Int
            var lineToEnd = 3
            val maxLines = 3
            var isLineSelected = false
            var notEmptyLinesToShowMore = 0

            if (!TextUtils.isEmpty(tvNotes.text)) {
                if (tvNotes.layout != null) {
                    var shouldShowReadMore = true

                    if (tvNotes.layout.lineCount <= maxLines) {
                        shouldShowReadMore = false
                    } else {

                        for(i in 2 until tvNotes.layout.lineCount){
                            startOffset = tvNotes.layout.getLineStart(i)
                            endOffset = tvNotes.layout.getLineEnd(i)
                            if (!TextUtils.isEmpty((tvNotes.layout.text.subSequence(startOffset, endOffset)).toString().trim())) {
                                if (!isLineSelected) {
                                    lineToEnd = i + 1
                                    isLineSelected = true
                                } else {
                                    notEmptyLinesToShowMore++
                                }
                            }
                        }

                        if (notEmptyLinesToShowMore < 2) {
                            shouldShowReadMore = false
                        }
                    }

                    if(shouldShowReadMore){
                        tvNotes.maxLines = lineToEnd
                        readMoreLl.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initMap() {
        mapboxMap?.let {
            initMapLocation()

            mapboxMap!!.uiSettings.isLogoEnabled = false
            mapboxMap!!.uiSettings.isAttributionEnabled = false

            mapReady()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initMapLocation() {
        val options = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.nice_pink))
                .accuracyAlpha(0.08f)
                .bearingTintColor(android.R.color.transparent)
                .elevation(0f)
                .foregroundDrawable(R.drawable.my_location)
                .foregroundDrawableStale(R.drawable.my_location)
                .backgroundDrawable(R.drawable.my_location_bg)
                .backgroundDrawableStale(R.drawable.my_location_bg)
                .build()

        locationComponent = mapboxMap!!.locationComponent

        val nonNullComponent = locationComponent!!

        nonNullComponent.activateLocationComponent(this, style, options)

        nonNullComponent.isLocationComponentEnabled = true

        nonNullComponent.cameraMode = CameraMode.TRACKING
        nonNullComponent.renderMode = RenderMode.COMPASS
        nonNullComponent.zoomWhileTracking(LOCATION_ZOOM_LEVEL, LOCATION_ZOOM_ANIMATION)
    }

    private fun mapReady() {
        mapboxMap!!.uiSettings.setAllGesturesEnabled(false)

        presenter.loadData()
    }

    private fun setupScreen(screenType: Int){
        // screen 1 - ktos nie jest w lokacji
        // screen 2 - geolocation turned off
        // screen 3 - ktos jest w lokacji ale nie zgadza sie timeframe
        // screen 4 - ktos jest w lokacji i zgadza sie timeframe && jeszcze nie zrobil check-in
        // screen 5 - ktos jest w lokacji i zgadza sie timeframe && zrobil juz check-in

        //TODO hide all remaining views, if any.
        map.setVisible(false)
        view_2_3_container.setVisible(false)
        view_4_5_container.setVisible(false)
        checklistLl.setVisible(false)
        detailsCl.setVisible(false)



        //TODO show proper views
        // + setup bottom buttons
        when(screenType){
            1 -> {

                map.setVisible(true)
                checklistLl.setVisible(true)


                presenter.lastUserLocation?.let {
                    val userLocationPoint: Point = Point.fromLngLat(
                            it.longitude,
                            it.latitude)
                    getRoute(userLocationPoint)

                    //TODO center and zoom camera
                }





            }

            2 -> {

            }

            3 -> {

            }

            4 -> {

            }

            5 -> {

            }

        }

    }

    private fun initDottedLineSourceAndLayer(@NonNull loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(SOURCE_ID))
        loadedMapStyle.addLayerBelow(
                LineLayer(
                        DIRECTIONS_LAYER_ID, SOURCE_ID).withProperties(
                        lineWidth(4.5f),
                        lineColor(Color.BLACK),
                        lineTranslate(arrayOf(0f, 4f)),
                        lineDasharray(arrayOf(1.2f, 1.2f))
                ), LAYER_BELOW_ID)
    }

    private fun getRoute(userLocation: Point) {

        val destinationPoint: Point = Point.fromLngLat(
                presenter.placeLocationPoint!!.longitude,
                presenter.placeLocationPoint!!.latitude)

        val client: MapboxDirections = MapboxDirections.builder()
                .origin(userLocation)
                .destination(destinationPoint)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(BuildConfig.MAPBOX_TOKEN)
                .build()

        client.enqueueCall(object : Callback<DirectionsResponse?> {

            override fun onResponse(call: Call<DirectionsResponse?>?, response: Response<DirectionsResponse?>) {
                if (response.body() == null) {
//                    Timber.d("No routes found, make sure you set the right user and access token.")
                    return
                } else if (response.body()!!.routes().size < 1) {
//                    Timber.d("No routes found")
                    return
                }
                drawNavigationPolylineRoute(response.body()!!.routes().get(0))
            }

            override fun onFailure(call: Call<DirectionsResponse?>?, throwable: Throwable) {
//                Timber.d("Error: %s", throwable.message)
//                if (throwable.message != "Coordinate is invalid: 0,0") {
//                    Toast.makeText(this@DashedLineDirectionsPickerActivity,
//                            "Error: " + throwable.message, Toast.LENGTH_SHORT).show()
//                }
            }

        })
    }

    private fun drawNavigationPolylineRoute(route: DirectionsRoute) {
        if (mapboxMap != null) {
            mapboxMap!!.getStyle { style ->
                val directionsRouteFeatureList: MutableList<Feature> = ArrayList()
                val lineString: LineString = LineString.fromPolyline(route.geometry()!!, PRECISION_6)
                val coordinates: List<Point> = lineString.coordinates()
                for (i in 0 until coordinates.size) {
                    directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(coordinates)))
                }
                dashedLineDirectionsFeatureCollection = FeatureCollection.fromFeatures(directionsRouteFeatureList)
                val source: GeoJsonSource? = style.getSourceAs(SOURCE_ID)
                if (source != null) {
                    source.setGeoJson(dashedLineDirectionsFeatureCollection)
                }
            }
        }
    }

    //TODO make it eventBus event
    // used when this was "your geolocation is turned off" case and user navigates to settings and turns on "allow geolocation" and goes back(automatically) to this ac
    fun locationTurnedOn(){
        presenter.loadData()
    }

    fun locationGotten(lastLocation: Location?) {
        if(lastLocation != null && presenter.placeLocationPoint != null){
            presenter.lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)

            if(presenter.placeLocationPoint!!.distanceTo(LatLng(lastLocation.latitude, lastLocation.longitude)) <= MIN_DISTANCE){
               if(presenter.doTimeFrameMatchActualTime(presenter.placeTimeframe!!)){
                   setupScreen(3)
               } else{
                   if(!presenter.isUserCheckedIn){
                       setupScreen(4)
                   } else{
                       setupScreen(5)
                   }
               }
            } else{
                setupScreen(1)
            }
        }
    }

    fun centerOn(location: LatLng) {
        val cameraPosition = CameraPosition.Builder()
                .target(location)
                .zoom(LOCATION_ZOOM_LEVEL)
                .tilt(0.0)
                .bearing(0.0)
                .build()

        val update = CameraUpdateFactory.newCameraPosition(cameraPosition)

        mapboxMap?.animateCamera(update, LOCATION_ZOOM_ANIMATION.toInt())
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showProgress() { }

    override fun hideProgress() { }

    private fun getDefaultMarkerBackground(): Icon {
        val bitmap = getBitmap(R.drawable.marker_background)

        val iconFactory = IconFactory.getInstance(this)

        return iconFactory.fromBitmap(bitmap)
    }

    private fun getGrayMarker(): Icon {
        val bitmap = getBitmap(R.drawable.ic_marker_gray)

        val iconFactory = IconFactory.getInstance(this)

        return iconFactory.fromBitmap(bitmap)
    }

    private fun getPinkMarker(): Icon {
        val bitmap = getBitmap(R.drawable.ic_marker_pink)

        val iconFactory = IconFactory.getInstance(this)

        return iconFactory.fromBitmap(bitmap)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {}
    override fun onPermissionResult(granted: Boolean) {}

    override fun onSuccess(result: LocationEngineResult) {
        locationGotten(result.lastLocation)
    }
    override fun onFailure(exception: Exception) {}

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val request = LocationEngineRequest
                .Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()

        locationEngine?.requestLocationUpdates(request, this, null)
    }

    override fun onStart() {
        super.onStart()

        var geolocationOn = false

        if(PermissionsManager.areLocationPermissionsGranted(this)) {
            if (repository.getGeolocationAllowed()) {
                geolocationOn = true
            }
        }

        if(geolocationOn){
            locationEngine?.let { requestLocationUpdates() }
        } else{
            locationEngine = null
        }


        if(isStatusBarLight){
            setLightStatusBar(this)
        } else{
            clearLightStatusBar(this)
        }

    }

    override fun onStop() {
        super.onStop()

        locationEngine?.removeLocationUpdates(this)

        if(!isStatusBarLight){
            setLightStatusBar(this)
        }
    }

    private fun setLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.decorView.systemUiVisibility = flags
        }
    }

    private fun clearLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.decorView.systemUiVisibility = flags
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mapView?.onSaveInstanceState(outState)
    }

}