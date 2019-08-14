package com.square.android.presentation.presenter.place

import android.location.Location
import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.data.pojo.BookInfo
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.aboutPlace.AboutLoadedEvent
import com.square.android.presentation.presenter.aboutPlace.DistanceUpdatedEvent
import com.square.android.presentation.presenter.booking.PlaceLoadedEvent
import com.square.android.presentation.presenter.booking.SpotsUpdatedEvent
import com.square.android.presentation.presenter.main.BadgeStateChangedEvent
import com.square.android.presentation.presenter.offer.OffersLoadedEvent
import com.square.android.presentation.presenter.redemptions.RedemptionsUpdatedEvent
import com.square.android.presentation.view.place.PlaceView
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class BookSelectedEvent(val intervalId: String?, val date: String)

@InjectViewState
class PlacePresenter(private val placeId: Long) : BasePresenter<PlaceView>() {
    private var locationPoint: LatLng? = null

    private val eventBus: EventBus by inject()

    private var data: Place? = null

    init {
        eventBus.register(this)

        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBookSelectedEvent(event: BookSelectedEvent) {
        tryBooking(event.intervalId, event.date)
    }

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            locationPoint = LatLng(it.latitude, it.longitude)

            if (data != null) {
                updateLocationInfo()
            }
        }
    }

    private fun tryBooking(intervalId: String?, date: String) {
        launch {
            val userId = repository.getUserInfo().id
            val bookInfo = BookInfo(userId, date, intervalId)
            val result = repository.book(placeId, bookInfo).await()

            val redemptionsEvent = RedemptionsUpdatedEvent()
            val badgeEvent = BadgeStateChangedEvent()
            val spotsEvent = SpotsUpdatedEvent()

            eventBus.post(redemptionsEvent)
            eventBus.post(badgeEvent)
            eventBus.post(spotsEvent)

            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.BOOKING_MADE.apply { venueName = data?.name }), repository)

            if(!TextUtils.isEmpty(result.message)){
                viewState.showMessage(result.message)
            }
        }
    }

    private fun loadData() {
        launch {
            data = repository.getPlace(placeId).await()

            viewState.showData(data!!)

            val offerEvent = OffersLoadedEvent(data!!.offers)
            val intervalsEvent = PlaceLoadedEvent(data!!)
            val aboutEvent = AboutLoadedEvent(data!!)

            eventBus.post(offerEvent)
            eventBus.post(intervalsEvent)
            eventBus.post(aboutEvent)

            if (locationPoint != null) {
                updateLocationInfo()
            }
        }
    }

    private fun updateLocationInfo() {
        val placePoint = data!!.location.latLng()

        val distance = placePoint.distanceTo(locationPoint!!).toInt()

        data!!.distance = distance

        showLocationInfo()
    }

    private fun showLocationInfo() {
        val event = DistanceUpdatedEvent(data!!.distance)

        eventBus.post(event)

        viewState.showDistance(data!!.distance)
    }
}