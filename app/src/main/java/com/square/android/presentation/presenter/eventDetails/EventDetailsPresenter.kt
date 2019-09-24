package com.square.android.presentation.presenter.eventDetails

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Event
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.PlaceType
import com.square.android.extensions.getStringDate
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.event.EventBookEvent
import com.square.android.presentation.presenter.event.SelectedPlaceEvent
import com.square.android.presentation.view.eventDetails.EventDetailsView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.util.*

//TODO send place.type
@InjectViewState
class EventDetailsPresenter(var event: Event) : BasePresenter<EventDetailsView>() {

    private var offers: List<OfferInfo> = listOf()

    private var places: MutableList<Place> = mutableListOf()

    private var currentPositionPlaces: Int? = null
    private var currentPositionIntervals: Int? = null

    private var calendar: Calendar = Calendar.getInstance()
    private var calendar2: Calendar = Calendar.getInstance()

    private lateinit var intervalSlots: List<Place.Interval>

    private val eventBus: EventBus by inject()

    //TODO event from EventPlaceFragment when select clicked
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectedPlaceEvent(event: SelectedPlaceEvent) {
        event.data?.let {
            appendPlace(it.placeId)

            viewState.updateRestaurantName(it.placeName)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventBookEvent(event: EventBookEvent) {
        doBooking()
    }

    init {
        eventBus.register(this)
        loadData()
    }

    private fun loadData() {
        launch {

            for (placeOffer in event.placesOffers){
                val place = repository.getPlace(placeOffer.placeId).await()

                place.offers = place.offers.filter { it.id in placeOffer.offerIds }
                place.slots = placeOffer.slots
                places.add(place)
            }

            val placeTypes: List<PlaceType> = repository.getPlaceTypes().await()

//            val placeImage: String? = placeTypes.filter { it.type == event.type}.firstOrNull()?.image

            viewState.showData(event, offers,calendar, null, places)

            loadIntervals()
        }
    }

    fun intervalItemClicked(position: Int){
        currentPositionIntervals = position

        viewState.setSelectedIntervalItem(position)
    }

    private fun doBooking(){
//        val result = repository.bookEvent(eventId, bookInfo).await()
//
//
//        val redemptionsEvent = RedemptionsUpdatedEvent()
//        val badgeEvent = BadgeStateChangedEvent()
//
//        eventBus.post(redemptionsEvent)
//        eventBus.post(badgeEvent)
//
//        AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.EVENT_BOOKING_MADE.apply { venueName = data?.name }), repository)
//
//        if(!TextUtils.isEmpty(result.message)){
//            viewState.showMessage(result.message)
//        }

        viewState.hideBookingProgress()
    }

    private fun loadIntervals() {
        launch {
            viewState.showProgress()

//            intervalSlots = repository.getIntervalSlots(event.id, getStringDate()).await()
//
//            viewState.hideProgress()
//
//            viewState.showIntervals(intervalSlots)
        }
    }

    fun dayItemClicked(position: Int) {
        viewState.setSelectedDayItem(position)

        calendar2.timeInMillis = calendar.timeInMillis
        calendar2.add(Calendar.DAY_OF_YEAR, position)

        viewState.updateMonthName(calendar2)

        loadIntervals()
    }

    fun getStringDate() = calendar2.getStringDate()

    private fun appendPlace(placeId: Long) {
        val place = places.filter { it.id == placeId}.firstOrNull()
        var index: Int?

        place?.let {
            index =  places.indexOf(place)
            currentPositionPlaces = index
            viewState.setSelectedPlaceItem(currentPositionPlaces!!)
        }
    }

    fun placeItemClicked(index: Int) {
        val place = places[index]

        router.navigateTo(SCREENS.EVENT_PLACE,place)
    }

    override fun onDestroy() {
        eventBus.unregister(this)
    }

}