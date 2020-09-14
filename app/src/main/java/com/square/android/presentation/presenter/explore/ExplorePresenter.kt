package com.square.android.presentation.presenter.explore

import android.location.Location
import android.util.SparseArray
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.App.Companion.getString
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.newPojo.NewPlace
import com.square.android.data.newPojo.PlacesFiltersAvailability
import com.square.android.data.newPojo.PlacesFiltersData
import com.square.android.data.newPojo.PlacesFiltersTimeSlots
import com.square.android.data.pojo.*
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.explore.ExploreView
import com.square.android.ui.activity.place.PlaceBottomSheetEvent
import com.square.android.ui.fragment.explore.filters.BaseFilter
import com.square.android.ui.fragment.explore.filters.CampaignsFilter
import com.square.android.ui.fragment.explore.filters.EventsFilter
import com.square.android.ui.fragment.explore.filters.PlacesFilter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.util.*

const val POSITION_PLACES = 0
const val POSITION_EVENTS = 1
const val POSITION_CAMPAIGNS = 2

const val LIST_ITEMS_SIZE = 3

class PlaceSelectedEvent(val place: NewPlace, val fromMap: Boolean = false)
class EventSelectedEvent(val place: Place)
class CampaignSelectedEvent(val campaignInfo: CampaignInfo)

class MainData(
        var placesData: MutableList<NewPlace> = mutableListOf(),
        var eventsData: MutableList<Place> = mutableListOf(),
        var campaignsData: MutableList<CampaignInfo> = mutableListOf()
)

@InjectViewState
class ExplorePresenter: BasePresenter<ExploreView>() {

    var actualDataLoaded: SparseArray<Boolean> = SparseArray(LIST_ITEMS_SIZE)

    var selectedDayPosition: Int = 0

    var days: MutableList<Day> = mutableListOf()
    var actualDates: MutableList<String> = mutableListOf()

    var cities: List<City>? = null

    var selectedCity: City? = null

    var allCategoryFilters: MutableList<String> = mutableListOf()

    private var filters: SparseArray<BaseFilter> = SparseArray(LIST_ITEMS_SIZE)

    private var locationPoint: LatLng? = null

    var data: MainData = MainData()

    var actualTabSelected: Int = POSITION_PLACES

    var initialized = false

    var dataLoaded = false

    var locationInitialized = false

    private val eventBus: EventBus by inject()

    init {
        eventBus.register(this)
    }

    fun getFilter(): BaseFilter = filters[actualTabSelected]

    fun loadData() = launch {

        if(!dataLoaded){

            dataLoaded = true

            viewState.showProgress()

            cities = repository.getCities().await()

            val tryCity = cities!!.firstOrNull { it.name == "Milan" }

            selectedCity = tryCity ?: cities!![0]

            viewState.changeCityName(selectedCity!!.name)

            val calendar = Calendar.getInstance()

            allCategoryFilters = repository.getPlaceTypes().await().map { it.type }.filterNotNull().toMutableList()

            filters.put(POSITION_PLACES, PlacesFilter(calendar.get(Calendar.HOUR_OF_DAY)))
            filters.put(POSITION_EVENTS, EventsFilter())
            filters.put(POSITION_CAMPAIGNS, CampaignsFilter())
            actualDataLoaded.put(POSITION_PLACES, true)
            actualDataLoaded.put(POSITION_EVENTS, true)
            actualDataLoaded.put(POSITION_CAMPAIGNS, true)

            for (x in 0 until 7) {
                days.add(Day(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).substring(0, 1).toUpperCase(), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1))
                actualDates.add(calendar.get(Calendar.DAY_OF_MONTH).toString() + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR))

                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            dataFromApi(sendDataAfter = true).await()

            viewState.showData(data, days)

            viewState.setSelectedDayItem(0)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlaceSelectedEvent(event: PlaceSelectedEvent) {
        val place = event.place

        if (actualDataLoaded[actualTabSelected]) {
//            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_WITHOUT_FILTERS.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
        } else {
//            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_USING_FILTERS.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
        }

        eventBus.post(PlaceBottomSheetEvent(event.fromMap, place.id, selectedDayPosition))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventSelectedEvent(e: EventSelectedEvent) {
        val event = e.place.event
        val eventId = e.place.event!!.id

        if (actualDataLoaded[actualTabSelected]) {
//            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.EVENT_OPENED_WITHOUT_FILTERS.apply { venueName = e.place.name }, hashMapOf("eventId" to eventId)), repository)
        } else {
//            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.EVENT_OPENED_USING_FILTERS.apply { venueName = e.place.name }, hashMapOf("eventId" to eventId)), repository)
        }

        router.navigateTo(SCREENS.EVENT, event!!.id)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCampaignSelectedEvent(event: CampaignSelectedEvent) {
        val id = event.campaignInfo.id

        if (actualDataLoaded[actualTabSelected]) {
//            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.CAMPAIGN_OPENED_WITHOUT_FILTERS.apply { venueName = event.campaignInfo.title }, hashMapOf("campaignId" to id.toString())), repository)
        } else {
//            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.CAMPAIGN_OPENED_USING_FILTERS.apply { venueName = event.campaignInfo.title }, hashMapOf("campaignId" to id.toString())), repository)
        }

        router.navigateTo(SCREENS.CAMPAIGN_DETAILS, id)
    }

    fun tabClicked(whichTab: Int) {
        actualTabSelected = whichTab

        if (whichTab == POSITION_PLACES) {
            viewState.showDays()
            viewState.showCities()
        } else if(whichTab == POSITION_EVENTS){
            viewState.hideDays()
            viewState.showCities()
        } else {
            viewState.hideDays()
            viewState.hideCities()
        }
    }

    fun navigateToSearch(){
        router.navigateTo(SCREENS.SEARCH, actualTabSelected)
    }

    fun navigateToMap(){
        router.navigateTo(SCREENS.MAP, if(actualTabSelected == POSITION_PLACES) data.placesData else data.eventsData)
    }

    //TODO fire when "Apply" button clicked in filters sheet
    fun applyFilters(filter: BaseFilter) {
        filters[actualTabSelected].updateValues(filter)
        checkFilters()
    }

    //TODO fire when "clear" tv clicked in filters sheet
    fun clearFilters() {
        filters[actualTabSelected].clear()
        checkFilters()
    }

    //TODO delete later
    fun latLngGotten(latLng: LatLng?) = launch {
        latLng?.let {
            if (!locationInitialized) {
                locationInitialized = true

                locationPoint = LatLng(it.latitude, it.longitude)

                sendData(listOf(POSITION_EVENTS, POSITION_PLACES)).await()
            }
        }
    }


    fun locationGotten(lastLocation: Location?) = launch {
        lastLocation?.let {
            if (!locationInitialized) {
                locationInitialized = true

                locationPoint = LatLng(it.latitude, it.longitude)

                sendData(listOf(POSITION_EVENTS, POSITION_PLACES)).await()
            }
        }
    }

    fun dayClicked(position: Int) {
        selectedDayPosition = position
        viewState.setSelectedDayItem(selectedDayPosition)

        checkFilters(listOf(POSITION_PLACES))
    }

    fun citySelected(c: City) {
        viewState.changeCityName(c.name)

        selectedCity = c

        //TODO city selected not for all tabs?
        checkFilters(listOf(POSITION_PLACES, POSITION_EVENTS, POSITION_CAMPAIGNS))
    }

    private fun checkFilters(tabsForGlobalFilters: List<Int>? = null) = launch {
        tabsForGlobalFilters?.let {
            dataFromApi(tabsForGlobalFilters, sendDataAfter = true).await()

        } ?: run {
            actualDataLoaded.setValueAt(actualTabSelected, filters[actualTabSelected].isDefault())

            dataFromApi(listOf(actualTabSelected), sendDataAfter = true).await()
        }
    }

    fun sendData(whichTabsToSend: List<Int> = listOf(POSITION_PLACES, POSITION_EVENTS, POSITION_CAMPAIGNS)): Deferred<Unit> = GlobalScope.async {
        launch {
            viewState.showProgress()

            fillDistances(whichTabsToSend).await()

            if (whichTabsToSend.contains(POSITION_PLACES)) {
                eventBus.post(PlacesUpdatedEvent(data.placesData.toMutableList()))
            }

            if (whichTabsToSend.contains(POSITION_EVENTS)) {
                eventBus.post(EventsUpdatedEvent(data.eventsData.toMutableList()))
            }

            if (whichTabsToSend.contains(POSITION_CAMPAIGNS)) {
                eventBus.post(CampaignsUpdatedEvent(data.campaignsData.toMutableList()))
            }

            viewState.hideProgress()
        }
    }

    private fun dataFromApi(whichTabsToLoad: List<Int> = listOf(POSITION_PLACES, POSITION_EVENTS, POSITION_CAMPAIGNS), sendDataAfter: Boolean = false): Deferred<Unit> = GlobalScope.async {
        launch {
            viewState.showProgress()


            // {
            //    "city": "Milano",
            //    "mainCategory": "Bar Cafes & Restaurant",
            //    "availability":  {"male": true, "female": true},
            //    "typology": "Set Price",
            //    "reservationApprove": false,
            //    "walkIn": false,
            //    "timeSlots": {"from": "07:00", "to": "09:00"}
            //}

            if (whichTabsToLoad.contains(POSITION_PLACES)) {
                val calendar = Calendar.getInstance()
                val month = (calendar.get(Calendar.MONTH) +1)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val yearStr = calendar.get(Calendar.YEAR)
                val monthStr = if (month < 10) {"0${month}"} else month
                val dayStr = if (day < 10) {"0${day}"} else day

                val filter = filters[POSITION_PLACES] as PlacesFilter

                val categoriesList = listOf(getString(R.string.category_1), getString(R.string.category_2), getString(R.string.category_3), getString(R.string.category_4), getString(R.string.category_5))
                val offersTypologyList = listOf(getString(R.string.complimentary), getString(R.string.discounted))

                val availability = when(filter.availability ){
                    1 -> PlacesFiltersAvailability(false, true)
                    2 -> PlacesFiltersAvailability(true, false)
                    else -> PlacesFiltersAvailability(true, true)
                }

                var walkIn = false
                var reservationApprove = false

                when(filter.bookingType){
                    1 -> {
                        reservationApprove = true
                    }
                    2 -> {
                        walkIn = true
                    }
                    else -> {
                        walkIn = true
                        reservationApprove = true
                    }
                }

                val startStr = if (filter.timeSlot.start < 10) {"0${filter.timeSlot.start}"} else filter.timeSlot.start.toString()
                val endStr = if (filter.timeSlot.end < 10) {"0${filter.timeSlot.end}"} else filter.timeSlot.end.toString()

                val timeSlot = PlacesFiltersTimeSlots(startStr, endStr)

                data.placesData =  repository.getNearbyPlaces(45.4896221, 9.1890265, 5000, "2020-09-02", PlacesFiltersData(
                        city = "Milano",
                        mainCategory = "Bar Cafes & Restaurant",
                        availability = availability,
                        typology = "Set Price",
                        walkIn = false,
                        reservationApprove = false,
                        timeSlots = PlacesFiltersTimeSlots("07:00", "09:00")

                )).await().toMutableList()

//                data.placesData = repository.getNearbyPlaces(45.4896221, 9.1890265, 5000, "$yearStr-$monthStr-$dayStr", PlacesFiltersData(
//                        city = selectedCity!!.name,
//                        mainCategory = if(filter.selectedCategories.isEmpty()) "" else filter.selectedCategories.map{categoriesList[it]}.first() ,
//                        availability = availability,
//                        typology = offersTypologyList[filter.offersTypology],
//                        walkIn = walkIn,
//                        reservationApprove = reservationApprove,
//                        timeSlots = timeSlot
//
//                )).await().toMutableList()

            }

            if (whichTabsToLoad.contains(POSITION_EVENTS)) {
                //TODO filter by new filters too
                val events = repository.getEvents().await().toMutableList()

                val eventsPlace: MutableList<Place> = mutableListOf()

                for (event in events) {
                    val place = repository.getPlace(event.placeId).await()
                    place.event = event

                    eventsPlace.add(place)
                }

                data.eventsData = eventsPlace
            }

            if (whichTabsToLoad.contains(POSITION_CAMPAIGNS)) {
                //TODO filter by new filters too
                data.campaignsData = repository.getCampaigns().await().toMutableList()
            }

            if (sendDataAfter) {
                sendData(whichTabsToLoad).await()
            } else {
                viewState.hideProgress()
            }
        }
    }

    private fun fillDistances(whichTabsToFillDistances: List<Int> = listOf(POSITION_PLACES, POSITION_EVENTS)): Deferred<Unit?> = GlobalScope.async {
        locationPoint?.let {
            if (whichTabsToFillDistances.contains(POSITION_PLACES)) {
                data.placesData.forEach { place ->
                    //TODO:A
                    val placePoint = if(place.location != null) LatLng(place.location!!.coordinates[0], place.location!!.coordinates[1]) else null

                    val distance = placePoint?.distanceTo(locationPoint!!)?.toInt()

                    place.distance = distance
                }
                data.placesData = data.placesData.sortedBy { it.distance }.toMutableList()
            }

            if (whichTabsToFillDistances.contains(POSITION_EVENTS)) {
                data.eventsData.forEach { eventPlace ->
                    //TODO:A
//                    val placePoint = eventPlace.location.latLng()
                    val placePoint = eventPlace.location()

                    val distance = placePoint?.distanceTo(locationPoint!!)?.toInt()

                    eventPlace.distance = distance
                }
                data.eventsData = data.eventsData.sortedBy { it.distance }.toMutableList()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
