package com.square.android.ui.activity.agenda

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.square.android.R
import com.square.android.data.Repository
import com.square.android.extensions.getBitmap
import com.square.android.presentation.presenter.agenda.ScheduleBookingPresenter
import com.square.android.presentation.view.agenda.ScheduleBookingView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.animateCamera
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.utils.ActivityUtils
import com.square.android.utils.PermissionsManager
import kotlinx.android.synthetic.main.activity_schedule_booking.*
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator
import java.lang.Exception

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

    private lateinit var style: Style

    private val repository: Repository by inject()

    private var locationEngine: LocationEngine? = null

    private var permissionsManager: PermissionsManager? = null

    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_schedule_booking)

        mapView = map

        permissionsManager = PermissionsManager(this)

        loadingDialog = LoadingDialog(this)

        arrowBack.setOnClickListener {presenter.exit()}
    }

    @SuppressLint("MissingPermission")
    fun showData(){
        locationEngine = null
        var geolocationOn = false

        if(PermissionsManager.areLocationPermissionsGranted(this)) {
            if (repository.getGeolocationAllowed()) {
                geolocationOn = true
            }
        }

        if(geolocationOn){
            locationEngine = LocationEngineProvider.getBestLocationEngine(this)
            locationEngine?.getLastLocation(this)

            requestLocationUpdates()

            //TODO screen 1 a a resze robic w locationGotten?

        } else{
            //TODO project screen 2
        }
    }

    //TODO make it eventBus event
    // used when this was "your geolocation is turned off" case and user navigates to settings and turns on "allow geolocation" and goes back(automatically) to this ac
    fun locationTurnedOn(){
        //TODO reload data(in presenter and then showData())
    }

    fun locationGotten(lastLocation: Location?) {
        if(lastLocation != null && presenter.placeLocationPoint != null){
            if(presenter.placeLocationPoint!!.distanceTo(LatLng(lastLocation.latitude, lastLocation.longitude)) <= MIN_DISTANCE){
               if(presenter.doTimeFrameMatchActualTime(presenter.placeTimeframe!!)){
                   // screen 3 - ktos jest w lokacji ale nie zgadza sie timeframe
               } else{
                   if(!presenter.isUserCheckedIn){
                       // screen 4 - ktos jest w lokacji i zgadza sie timeframe && jeszcze nie zrobil check-in
                   } else{
                       // screen 5 - ktos jest w lokacji i zgadza sie timeframe && zrobil juz check-in
                   }
               }

            } else{
                // screen 1 - ktos nie jest w lokacji
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



   //TODO setup map


    fun mapReady() {
        //TODO


//        mapboxMap?.setOnMarkerClickListener {
//            val markerId = it.title.toLong()
//
//            presenter.markerClicked(markerId)
//
//            previousMarker?.let { it.icon = markerIconGray }
//
//            it.icon = markerIconPink
//
//            previousMarker = it
//            true
//        }
//
//        mapboxMap!!.addOnMapClickListener {
//            presenter.mapClicked()
//            true
//        }
//
////        mapMyLocation.setOnClickListener {
////            presenter.locateClicked()
////        }
//
//        loadMapData()
    }


    private fun loadMapData() {
        presenter.loadData()
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
    }

    override fun onStop() {
        super.onStop()

        locationEngine?.removeLocationUpdates(this)
    }

}