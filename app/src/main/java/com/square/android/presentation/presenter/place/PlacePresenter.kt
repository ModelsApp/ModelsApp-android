package com.square.android.presentation.presenter.place

import android.location.Location
import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.data.newPojo.OfferInfo
import com.square.android.data.newPojo.PlaceOffer
import com.square.android.data.pojo.*
import com.square.android.extensions.getStringDate
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.agenda.RedemptionsUpdatedEvent
import com.square.android.presentation.presenter.main.BadgeStateChangedEvent
import com.square.android.presentation.view.place.PlaceView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject
import java.lang.Exception
import java.util.*

class PlaceExtras(val placeId: Long, val daySelectedPosition: Int = -1)

@InjectViewState
class PlacePresenter(private val placeId: Long, val daySelectedPosition: Int) : BasePresenter<PlaceView>() {
    var locationPoint: LatLng? = null

    var latitude: Double? = null

    var longitude: Double? = null

    private val eventBus: EventBus by inject()

    var data: Place? = null

    private var offers: List<OfferInfo> = listOf()

    private var currentPositionOffers = 0

    private var currentPositionIntervals: Int? = null

    private var selectedLoaded = false

    private var calendar: Calendar = Calendar.getInstance()
    private var calendar2: Calendar = Calendar.getInstance()

    private lateinit var intervalSlots: List<Place.Interval>

    init {
        loadData()
    }

    fun bookClicked() {
        currentPositionIntervals?.let {
            launch {
                val date = getStringDate()
                val userId = repository.getUserInfo().id
                val bookInfo = BookInfo(userId, date, intervalSlots[it].id)
                val result = repository.book(placeId, bookInfo).await()

                val redemptionsEvent = RedemptionsUpdatedEvent()
                val badgeEvent = BadgeStateChangedEvent()

                loadIntervals()

                eventBus.post(redemptionsEvent)
                eventBus.post(badgeEvent)

//                AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.BOOKING_MADE.apply { venueName = data?.name }), repository)

                if(!TextUtils.isEmpty(result.message)){
                    viewState.showMessage(result.message)
                }
            }
        }
    }

    fun dayItemClicked(position: Int) {
        viewState.setSelectedDayItem(position)

        calendar2.timeInMillis = calendar.timeInMillis
        calendar2.add(Calendar.DAY_OF_YEAR, position)


        // TODO update offers - enabled/disabled
        // TODO j


        //TODO if any offer is expanded
//        loadIntervals()
    }

    fun intervalItemClicked(position: Int){
        currentPositionIntervals = position

        viewState.setSelectedIntervalItem(position)
    }


    //TODO load for offer when clicked
    private fun loadIntervals() {
        launch {
            viewState.showProgress()

            //TODO remove
            calendar2.set(Calendar.MONTH, 8)
            calendar2.set(Calendar.DAY_OF_MONTH, 2)




            //TODO add intervals - new endpoint?

            //TODO change to data.id
            intervalSlots = repository.getIntervalSlots(238, getStringDate()).await()

            println("http fFDFDFD intervals: ${intervalSlots.toString()}")

            viewState.hideProgress()

            viewState.showIntervals(intervalSlots)
        }
    }

    private fun getStringDate() = calendar2.getStringDate()

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            latitude = it.latitude
            longitude = it.longitude

            locationPoint = LatLng(it.latitude, it.longitude)

            if (data != null) {
                updateLocationInfo()
            }
        }
    }

    private fun loadData() {
        launch {

            //TODO change to data.id
            offers = repository.getPlaceOffersNew(238).await()

            val offersWithDetails: MutableList<PlaceOffer> = mutableListOf()

            offers.forEach {
                val item = repository.getOfferDetails(it.id).await()

                if(item.isNotEmpty()){
                    offersWithDetails.add(item.first())
                }
            }

            println("http EOEOEOEOE ${offersWithDetails.toString()}")

            data = repository.getPlace(placeId).await()

            val allExtras: List<PlaceExtra> = repository.getPlaceExtras().await().toMutableList()
            var placeExtras: List<PlaceExtra> = mutableListOf()

            println("DSFDSFDFFDFFF allExtras: ${allExtras.toString()}")
            println("DSFDSFDFFDFFF placeExtras: ${placeExtras.toString()}")

            data!!.icons?.let { icons ->
                if(!icons.extras.isNullOrEmpty()){
                    placeExtras = allExtras.filter { it.image in icons.extras }
                }
            }

            val typeImage: String? = data!!.icons?.typology?.firstOrNull()

            viewState.showData(data!!, offersWithDetails, calendar, typeImage, placeExtras)

            if (locationPoint != null) {
                updateLocationInfo()
            }

            if(!selectedLoaded) {
                selectedLoaded = true
                if (daySelectedPosition > -1) {
                    dayItemClicked(daySelectedPosition)
                }
            }
        }
    }



    fun offersItemClicked(position: Int, place: Place?) {
        //TODO Collapse previous offer if any and expand this

        //TODO load offer intervals





//        try{
//            currentPositionOffers = position
//
//            val offer = offers!![currentPositionOffers]
//
//            viewState.showOfferDialog(offer, place)
//
//        } catch (e: Exception){
//
//        }
    }

    private fun updateLocationInfo() {
        //TODO:A
//        val placePoint = data!!.location.latLng()
        val placePoint = data!!.location()

        val distance = placePoint?.distanceTo(locationPoint!!)?.toInt()

        data!!.distance = distance

        showLocationInfo()
    }

    private fun showLocationInfo() {
        viewState.showDistance(data!!.distance)
    }
}