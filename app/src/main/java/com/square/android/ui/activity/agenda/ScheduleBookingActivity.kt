package com.square.android.ui.activity.agenda

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
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
import com.square.android.extensions.getColorFromRes
import com.square.android.extensions.loadImage
import com.square.android.extensions.setVisible
import com.square.android.presentation.presenter.agenda.ScheduleBooking
import com.square.android.presentation.presenter.agenda.ScheduleBookingPresenter
import com.square.android.presentation.view.agenda.ScheduleBookingView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.settings.SettingsActivity
import com.square.android.ui.activity.settings.USER_EXTRA
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.utils.ActivityUtils
import com.square.android.utils.PermissionsManager
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_schedule_booking.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.terrakok.cicerone.Navigator

private const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1_000L
private const val DEFAULT_MAX_WAIT_TIME = 30_000L

private const val LOCATION_ZOOM_LEVEL = 11.0
private const val LOCATION_ZOOM_ANIMATION = 3000L

//TODO change later
private const val MIN_DISTANCE = 500

class LocationTurnedOnEvent()

class ScheduleBookingActivity: BaseActivity(), ScheduleBookingView, PermissionsListener, LocationEngineCallback<LocationEngineResult> {

    @InjectPresenter
    lateinit var presenter: ScheduleBookingPresenter

    @ProvidePresenter
    fun providePresenter() = ScheduleBookingPresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    protected lateinit var mapView: MapView

    protected var mapboxMap: MapboxMap? = null

    protected val markerBackground by lazy { getDefaultMarkerBackground() }

    protected val markerIconBlack by lazy { getBlackMarker() }

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

    var readMoreAlreadyClicked = false

    var bottomActionEnabled = false

    var shouldReloadData: Boolean = false

    var behavior : AppBarLayout.Behavior? = null

    private val eventBus: EventBus by inject()

    private val DIRECTIONS_LAYER_ID = "DIRECTIONS_LAYER_ID"
    private val LAYER_BELOW_ID = "road-label-small"
    private val SOURCE_ID = "SOURCE_ID"

    private var dashedLineDirectionsFeatureCollection: FeatureCollection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_schedule_booking)

        if(!eventBus.isRegistered(this)){
            eventBus.register(this)
        }

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

        if(offset > movePoint) {
            topLayoutsLl.visibility = View.INVISIBLE

            topIcon.setVisible(false)
        }
        else {
            topLayoutsLl.visibility = View.VISIBLE

            if(placeNested.isScrollable){
                topIcon.setVisible(true)
            } else{
                topIcon.setVisible(false)
            }
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
        tvTakeaway.text = data.offerTakeaway
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

        //TODO load image to img(that's imageView id in xml)

        placeImg.loadImage(data.placeImg,
                placeholder = android.R.color.transparent,
                roundedCornersRadiusPx = 360,
                whichCornersToRound = listOf(RoundedCornersTransformation.CornerType.ALL))

        userImg.loadImage(data.userImg,
                placeholder = android.R.color.transparent,
                roundedCornersRadiusPx = 360,
                whichCornersToRound = listOf(RoundedCornersTransformation.CornerType.ALL))

        checkingInTv.text = if(data.userGender == 0 ) getString(R.string.checking_in_ms) else getString(R.string.checking_in_mr)
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
            val key = data.placeName
            val markerOption = MarkerOptions().title(key).position(data.latlng).icon(markerIconBlack)
            mapboxMap?.addMarkers(listOf(markerOption))

            locationEngine = LocationEngineProvider.getBestLocationEngine(this)
            locationEngine?.getLastLocation(this)

            requestLocationUpdates()
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

        nonNullComponent.cameraMode = CameraMode.NONE
        nonNullComponent.renderMode = RenderMode.COMPASS
        nonNullComponent.zoomWhileTracking(LOCATION_ZOOM_LEVEL, LOCATION_ZOOM_ANIMATION)
    }

    private fun mapReady() {
        mapboxMap!!.uiSettings.setAllGesturesEnabled(false)
        mapboxMap!!.uiSettings.isZoomGesturesEnabled = true
        mapboxMap!!.uiSettings.isRotateGesturesEnabled = true
        mapboxMap!!.uiSettings.isScrollGesturesEnabled = true

        presenter.loadData()
    }

    private fun setupScreen(screenType: Int){

        try {
            behavior = ((placeAppBar.layoutParams as CoordinatorLayout.LayoutParams).behavior as AppBarLayout.Behavior)
        } catch (e: Exception){ }
        behavior?.setDragCallback(object : DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return true
            }
        })
        placeNested.isScrollable = true

        map.setVisible(false)
        view_2_3_container.setVisible(false)
        view_4_5_container.setVisible(false)
        checklistLl.setVisible(false)
        detailsCl.setVisible(false)
        iconsCl.setVisible(false)

        swipeFr.setVisible(false)
        btnRightImg.setVisible(false)
        bottomText.setTextColor(getColorFromRes(R.color.black_trans_30))
        bottomBtnCl.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray_btn_disabled_light))

        bottomActionEnabled = false

        if(screenType == 1 || screenType == 2){
            //TODO change iconTop

            iconTop.setOnClickListener {
                //TODO
            }
        } else{
            //TODO change iconTop

            iconTop.setOnClickListener {
                //TODO
            }
        }

        when(screenType){
            1 -> {
                // screen 1 - not in location

                placeNested.isScrollable = false

                behavior?.setDragCallback(object : DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                        return false
                    }
                })
                placeAppBar.setExpanded(true, false)

                topIcon.setVisible(false)

                map.setVisible(true)
                checklistLl.setVisible(true)

                presenter.lastUserLocation?.let {
                    val userLocationPoint: Point = Point.fromLngLat(
                            it.longitude,
                            it.latitude)
                    getRoute(userLocationPoint)

                    bottomText.setText(R.string.im_done_here)
                }
            }

            2 -> {
                // screen 2 - geolocation turned off

                topText.setText(R.string.geolocation_turned_off)
                middleText.setText(R.string.switch_on_geolocation)
                btnDone.setText(R.string.go_to_settings)

                btnDone.setOnClickListener {
                    presenter.user?.let {
                        val intent = Intent(this, SettingsActivity::class.java)

                        intent.putExtra(USER_EXTRA, it)

                        startActivity(intent)
                    }
                }

                //TODO change topImg

                swipeFr.setVisible(true)
                bottomText.setText(R.string.im_done_here)

                checklistLl.setVisible(true)

                view_2_3_container.setVisible(true)
            }

            3 -> {
                // screen 3 - in location && timeframes don't match

                topText.setText(R.string.cant_redeem_this_offer_now)

                val ss = SpannableString(getString(R.string.schedule_booking_coupons_text))
                ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.black)), 24 , 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                middleText.text = ss
                btnDone.setText(R.string.manual_check_in)

                btnDone.setOnClickListener {
                    //TODO
                }

                //TODO change topImg

                bottomText.setText(R.string.not_available_now)

                swipeFr.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.placeholder))
                swipeIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black_trans_30_no_alpha))
                swipeFr.setVisible(true)

                iconsCl.setVisible(true)
                detailsCl.setVisible(true)

                view_2_3_container.setVisible(true)
            }

            4 -> {
                // screen 4 - in location && timeframes match && not checked in

                //TODO swipe action
                bottomActionEnabled = true

                bottomBtnCl.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
                bottomText.setTextColor(getColorFromRes(android.R.color.white))
                bottomText.setText(R.string.swipe_to_redeem)

                swipeFr.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white))
                swipeIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
                swipeFr.setVisible(true)

                iconsCl.setVisible(true)
                detailsCl.setVisible(true)

                view_4_5_container.setVisible(true)
            }

            5 -> {
                // screen 5 - in location && timeframes match && checked in

                bottomText.text = getString(R.string.checked_in_confirmed_at, presenter.data!!.checkedInAt)
                btnRightImg.setVisible(true)

                iconsCl.setVisible(true)
                detailsCl.setVisible(true)

                view_4_5_container.setVisible(true)
            }

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLocationTurnedOnEvent(event: LocationTurnedOnEvent){
        shouldReloadData = true
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

                if(coordinates.size > 1){
                    val point: Point = coordinates[(coordinates.size / 2).toInt()]

                    centerOn(LatLng(point.latitude(), point.longitude()))
                } else if(coordinates.isNotEmpty()){
                    val point: Point = coordinates[0]

                    centerOn(LatLng(point.latitude(), point.longitude()))
                } else{
                    presenter.lastUserLocation?.let {
                        centerOn(LatLng(presenter.lastUserLocation!!.latitude, presenter.lastUserLocation!!.longitude))
                    }
                }
            }
        }
    }

    fun locationGotten(lastLocation: Location?) {
        if(lastLocation != null && presenter.placeLocationPoint != null){
            presenter.lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)

            if(presenter.placeLocationPoint!!.distanceTo(LatLng(lastLocation.latitude, lastLocation.longitude)) <= MIN_DISTANCE){
               if(!presenter.doTimeFrameMatchActualTime(presenter.placeTimeframe!!)){
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

    private fun getBlackMarker(): Icon {
        val bitmap = getBitmap(R.drawable.ic_marker_black)

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

        if(shouldReloadData){
            shouldReloadData = false
            presenter.loadData()
        }

    }

    override fun onStop() {
        super.onStop()

        locationEngine?.removeLocationUpdates(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }


}