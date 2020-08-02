@file:Suppress("DEPRECATION")

package com.square.android.ui.fragment.map

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.latLng
import com.square.android.presentation.presenter.map.MapPresenter
import com.square.android.presentation.view.map.MapView
import com.square.android.ui.activity.main.MainActivity
import com.square.android.ui.activity.main.MainFabClickedEvent
import com.square.android.ui.fragment.BaseMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_map.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

class MapFragment(var data: MutableList<Place>) : BaseMapFragment(), MapView, PermissionsListener, LocationEngineCallback<LocationEngineResult> {

    @InjectPresenter
    lateinit var presenter: MapPresenter

    @ProvidePresenter
    fun providePresenter() = MapPresenter(data)

    private val eventBus: EventBus by inject()

    var alreadyLocated = false

    private var previousMarker : Marker? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialized = presenter.initialized
        super.onViewCreated(view, savedInstanceState)
        presenter.initialized = true

        if(!eventBus.isRegistered(this)){
            eventBus.register(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun provideMapView() : com.mapbox.mapboxsdk.maps.MapView = map

    override fun locate(location: LatLng) {
        centerOn(location)
    }

    override fun locateCity(location: LatLng) {
        centerOnCity(location)
    }

    //TODO ERROR SHOWING RANDOMLY WHEN SWITCHING to and from map fragment on fab click(mapBox related):
    // 2020-02-23 16:26:09.914 10533-10716/com.squaremm.android E/AndroidRuntime: FATAL EXCEPTION: Thread-6744
    //    Process: com.squaremm.android, PID: 10533
    //    java.lang.UnsupportedOperationException: eglCreateWindowSurface() can only be called with an instance of Surface, SurfaceView, SurfaceHolder or SurfaceTexture at the moment.
    //        at com.google.android.gles_jni.EGLImpl.eglCreateWindowSurface(EGLImpl.java:97)
    //        at com.mapbox.mapboxsdk.maps.renderer.textureview.TextureViewRenderThread$EGLHolder.createSurface(TextureViewRenderThread.java:391)
    //        at com.mapbox.mapboxsdk.maps.renderer.textureview.TextureViewRenderThread.run(TextureViewRenderThread.java:253)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMainFabClickedEvent(event: MainFabClickedEvent) {
        (activity as MainActivity).mainFab.hide()
        (activity as MainActivity).mainFab.show()

        (activity as MainActivity).setUpMainFabImage(R.drawable.r_pin)
        (activity as MainActivity).navFab.hide()

        (activity as MainActivity).hideMapBottomView()

        presenter.backToExplore()
    }

    override fun mapReady() {
        mapboxMap?.setOnMarkerClickListener {
            val markerId = it.title.toLong()

            presenter.markerClicked(markerId)

            previousMarker?.let { it.icon = markerIconGray }

            it.icon = markerIconPink

            previousMarker = it
            true
        }

        mapboxMap!!.addOnMapClickListener {
            presenter.mapClicked()
            true
        }

//        mapMyLocation.setOnClickListener {
//            presenter.locateClicked()
//        }
//
//        mapPlaceInfo.setOnClickListener { presenter.infoClicked() }

        loadMapData()
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showPlaces(data: List<Place>) {
        val markerOptions = data.map { place ->
            val latLng = place.location()

            val key = place.id.toString()

            MarkerOptions()
                    .title(key)
                    .position(latLng)
                    .icon(markerIconGray)
        }

        mapboxMap?.addMarkers(markerOptions)
    }

    override fun updatePlaces(data: List<Place>) {
        previousMarker = null
        mapboxMap?.clear()

        val markerOptions = data.map { place ->
            val latLng = place.location()

            val key = place.id.toString()

            MarkerOptions()
                    .title(key)
                    .position(latLng)
                    .icon(markerIconGray)
        }

        mapboxMap?.addMarkers(markerOptions)
    }

    override fun showInfo(place: Place) {
        updateCurrentInfoDistance(place.distance)

//        mapPlaceInfo.mapPlaceAvailableValue.text = if(place.freeSpots > 0) place.freeSpots.toString() else mapPlaceInfo.mapPlaceAvailableValue.context.getString(R.string.no)
//        mapPlaceInfo.mapPlaceTitle.text = place.name
//        mapPlaceInfo.mapPlaceAddress.text = place.address
//
//        mapPlaceInfo.visibility = View.VISIBLE

        place.icons?.let {
//            mapPlaceInfo.mapPlaceExtrasRv.visibility = View.VISIBLE
//            mapPlaceInfo.mapPlaceExtrasRv.adapter = PlaceExtrasAdapter(it.extras)
//            mapPlaceInfo.mapPlaceExtrasRv.layoutManager = LinearLayoutManager(mapPlaceInfo.mapPlaceExtrasRv.context, RecyclerView.HORIZONTAL,false)
//            mapPlaceInfo.mapPlaceExtrasRv.addItemDecoration(MarginItemDecorator(mapPlaceInfo.mapPlaceExtrasRv.context.resources.getDimension(R.dimen.rv_item_decorator_minus_1).toInt(), false))
        }

//        if (place.mainImage != null) {
//            mapPlaceInfo.mapPlaceInfoImage.loadImage(place.mainImage!!, R.color.placeholder)
//        } else {
//            mapPlaceInfo.mapPlaceInfoImage.loadFirstOrPlaceholder(place.photos)
//        }
    }
    override fun updateCurrentInfoDistance(distance: Int?) {
//        if (distance != null) {
//            mapPlaceInfo.mapPlaceDistance.text = distance.asDistance()
//            mapPlaceInfo.mapPlaceDistance.visibility = View.VISIBLE
//        } else {
//            mapPlaceInfo.mapPlaceDistance.visibility = View.GONE
//        }
    }

    override fun hideInfo() {
//        mapPlaceInfo.visibility = View.GONE
//
//        previousMarker?.let { it.icon = markerIconGray }
    }

    private fun loadMapData() {
        presenter.loadData()
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }
}
