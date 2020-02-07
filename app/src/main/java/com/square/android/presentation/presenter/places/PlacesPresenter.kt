package com.square.android.presentation.presenter.places

import android.location.Location
import android.util.SparseArray
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.SCREENS
import com.square.android.data.pojo.*
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.placesList.PlacesUpdatedEvent
import com.square.android.presentation.view.places.PlacesView
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.util.*

private const val POSITION_PLACES = 0
private const val POSITION_EVENTS = 1
private const val POSITION_CAMPAIGNS = 2

class PlaceSelectedEvent(val place: Place, val fromMap: Boolean)

//TODO just for now, change to actual model later
abstract class BaseFilter{
    abstract fun isDefault(): Boolean
    abstract fun clear()
    abstract fun updateValues(filter: BaseFilter)
}

class PlacesFilter(
        private var actualMinHour: Int,

        // multiple selection in rv
        var selectedCategories: MutableList<String> = mutableListOf(),
        // 0 - all, 1 - girls only, 2 - guys only
        var availability: Int = 0,
        // 0 - all, 1 - not full
        var showPlacesType: Int = 0,
        // 0 - complimentary, 1 - discounted
        var offersTypology: Int = 0,
        // 0 - all, 1 - welcome 25, etc.
        var selectedOffersLevel: Int = 0,
        // 0 - all, 1 - reservation needed, 2 - walk-in
        var bookingType: Int = 0,
        // 0 - all, 1 - accepted, 2 - unavailable
        var takeawayOption: Int = 0,
        var timeSlot: TimeSlot = TimeSlot(start = actualMinHour)
): BaseFilter() {

    override fun updateValues(filter: BaseFilter) {
        selectedCategories = (filter as  PlacesFilter).selectedCategories
        availability = (filter as  PlacesFilter).availability
        showPlacesType = (filter as  PlacesFilter).showPlacesType
        offersTypology = (filter as  PlacesFilter).offersTypology
        selectedOffersLevel = (filter as  PlacesFilter).selectedOffersLevel
        bookingType = (filter as  PlacesFilter).bookingType
        takeawayOption = (filter as  PlacesFilter).takeawayOption
        setTimeSlotStartHour((filter as  PlacesFilter).timeSlot.start)
        setTimeSlotEndHour((filter as  PlacesFilter).timeSlot.end)
    }

    override fun isDefault(): Boolean = selectedCategories.isNullOrEmpty() && availability == 0 && showPlacesType == 0  &&
            offersTypology == 0 && selectedOffersLevel == 0 && bookingType == 0 && takeawayOption == 0 && timeSlot.isDefault(actualMinHour)

    override fun clear() {
        setTimeSlotStartHour(actualMinHour)
        setTimeSlotEndHour(24)
        selectedCategories.clear()
        availability = 0
        showPlacesType = 0
        offersTypology = 0
        selectedOffersLevel = 0
        bookingType = 0
        takeawayOption = 0
    }

    fun setTimeSlotStartHour(startHour: Int){ timeSlot.start = startHour}
    fun setTimeSlotEndHour(endHour: Int){ timeSlot.end = endHour }
}

class EventsFilter(): BaseFilter() {
    override fun isDefault(): Boolean = true
    override fun clear() { }
    override fun updateValues(filter: BaseFilter) { }
}

class CampaignsFilter(): BaseFilter() {
    override fun isDefault(): Boolean = true
    override fun clear() { }
    override fun updateValues(filter: BaseFilter) { }
}

class MainData(
        var placesData: MutableList<Place> = mutableListOf(),
        var eventsData: MutableList<Event> = mutableListOf(),
        var campaignData: MutableList<CampaignInfo> = mutableListOf()
)

class TimeSlot(var start: Int, var end: Int = 24) {
    fun isDefault(actualMinHour: Int): Boolean = (start == actualMinHour && end == 24)
}

@InjectViewState
class PlacesPresenter : BasePresenter<PlacesView>() {

    var actualDataLoaded: SparseArray<Boolean> = SparseArray(3)

    private var selectedDayPosition: Int? = null

    var days: MutableList<Day> = mutableListOf()
    var actualDates: MutableList<String> = mutableListOf()

    var cities: List<City>? = null

    var selectedCity: City? = null

    var allCategoryFilters: MutableList<String> = mutableListOf()

    var filters: SparseArray<BaseFilter> = SparseArray(3)

    var actualMinHour: Int = 0

    private var locationPoint: LatLng? = null

    private var data: MainData = MainData()

    private var filteredData: MainData = MainData()

    private var actualTabSelected = POSITION_PLACES

    var distancesFilled: Boolean = false

    var searchText: CharSequence? = null

    var initialized = false

    var locationInitialized = false

    private val eventBus: EventBus by inject()

    init {
        eventBus.register(this)

        loadData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlaceSelectedEvent(event: PlaceSelectedEvent) {
        val place = event.place
        val id = place.id

        if(event.fromMap){
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_FROM_MAP.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
        } else{
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_FROM_LIST.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
        }

        if(actualDataLoaded[actualTabSelected]){
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_WITHOUT_FILTERS.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
        } else{
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_USING_FILTERS.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
        }

        router.navigateTo(SCREENS.PLACE, place.id)

//        if(place.isEventPlace){
//            place.event?.let {
//                val extras = EventExtras(it, place)
//                router.navigateTo(SCREENS.EVENT, extras)
//            }
//        } else {
//            router.navigateTo(SCREENS.PLACE, place.id)
//        }
    }

    //TODO fire when tab changed clicked in tabLayout
    fun tabClicked(whichTab: Int){
        actualTabSelected = whichTab

        if(whichTab == POSITION_PLACES){
            viewState.showDays()
            viewState.showCities()
        } else{
            viewState.hideDays()
            viewState.hideCities()
        }
    }

    //TODO fire when "Apply" button clicked in filters sheet
    fun applyFilters(filter: BaseFilter){
        filters[actualTabSelected].updateValues(filter)
        checkFilters()
    }

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            if(!locationInitialized){
                locationInitialized = true

                locationPoint = LatLng(it.latitude, it.longitude)

                if(actualDataLoaded[POSITION_PLACES]){
                    updateDistances()
                }
            }
        }
    }

//    fun filterClicked(position: Int) {
//
//        val contains = newFilter!!.selectedCategories.contains(allCategoryFilters[position])
//
//        if (contains) {
//            newFilter!!.selectedCategories.remove(allCategoryFilters[position])
//        } else {
//            newFilter!!.selectedCategories.add(allCategoryFilters[position])
//        }
//
//        viewState.updateFilters(allCategoryFilters, newFilter!!.selectedCategories, true)
//
//        checkFilters()
//    }


    //TODO !!
    //TODO !!
    //TODO !!
    //TODO: IMPORTANT: when day clicked or citySelected AND there is actual data loaded, actual data must be reloaded with new day and city

    fun dayClicked(position: Int){
        selectedDayPosition = position
        viewState.setSelectedDayItem(selectedDayPosition!!)
        checkFilters()
    }

    //TODO cities for all three tabs?(offers, events, jobs) If yes - fire checkFilters and (sendData OR setActualAndSendData, and load all filteredData) for all three
    fun citySelected(c: City) = launch {
        viewState.changeCityName(c.name)

        selectedCity = c

//        navigateToCity(c.name)
//
        distancesFilled = false

        checkFilters()
    }

//    private fun navigateToCity(cityname: String){
//        //TODO hardcoded, change later
//        val latLng: LatLng = when(cityname.toUpperCase(Locale.ENGLISH)){
//            "MILAN" -> LatLng(45.464664, 9.188540)
//            "LONDON" -> LatLng(51.509865, -0.118092)
//            "BUDAPEST" -> LatLng(47.497913, 19.040236)
//            else -> LatLng(0.0, 0.0)
//        }
//        eventBus.post(CityLocateEvent(latLng))
//    }

    private fun sendData(whichTab: Int?, isActualData: Boolean, updateDistances: Boolean = false){
//        private fun sendData(whichTab: Int?, isActual: Boolean, sendAllData: Boolean = false, updateDistances: Boolean = false){
        val data = if(isActualData) data else filteredData

        if(updateDistances && whichTab == null){
            eventBus.post(PlacesUpdatedEvent(data.placesData.toMutableList(), true))

        } else{
//            if(sendAllData){
//                eventBus.post(PlacesUpdatedEvent(data.placesData.toMutableList(), false))
//                eventBus.post(EventsUpdatedEvent(data.eventsData.toMutableList()))
//                eventBus.post(UpdatedEvent(data.eventsData.toMutableList()))
//            } else{
                when (whichTab){
                    POSITION_PLACES ->  eventBus.post(PlacesUpdatedEvent(data.placesData.toMutableList(), updateDistances))
                    POSITION_EVENTS -> eventBus.post(EventsUpdatedEvent(data.eventsData.toMutableList()))
                    POSITION_CAMPAIGNS -> eventBus.post(UpdatedEvent(data.eventsData.toMutableList()))
                }
//            }
        }
    }

    //TODO fire when clear tv clicked in filters sheet
    fun clearFilters(whichTab: Int) {
        filters[whichTab].clear()

        setActualAndSendData(whichTab)
    }

    //TODO there will be new filters, filtered by API, not locally?
    private fun checkFilters() = launch {
        if(filters[actualTabSelected].isDefault()){
            setActualAndSendData(actualTabSelected)
        } else {
            actualDataLoaded.setValueAt(actualTabSelected, false)
            val mDate = actualDates[selectedDayPosition!!]

            when(actualTabSelected){
                POSITION_PLACES -> {
                    //TODO filter by new filters too
                    filteredData.placesData = repository.getPlacesByFilters(PlaceData().apply {
                        date  = mDate
                        selectedCity?.let { city = selectedCity!!.name } }).await().toMutableList()
                }
                POSITION_EVENTS -> {
                    //TODO change to include filters
                    filteredData.eventsData = repository.getEvents().await().toMutableList()
                }
                POSITION_CAMPAIGNS -> {
                    //TODO change to include filters
                    filteredData.campaignData = repository.getCampaigns().await().toMutableList()
                }
            }

            //TODO fill distances only for places only? Or for places and events?
            if(actualTabSelected == POSITION_PLACES){
                fillDistances(false)
            }

            sendData(actualTabSelected,false)
        }
//        when(filteringMode){
//
//            // old search by text
//            1 -> {
//                if (TextUtils.isEmpty(searchText)) {
//                    setActualAndSendData()
//                } else {
//                    if(data != null){
//                        actualDataLoaded = false
//
//                        filteredData = data!!.filter { it.name.contains(searchText.toString(), true) }.toMutableList()
//
//                        fillDistances(false)
//
//                        sendData(false)
//
//                    } else {}
//                }
//            }

            // old by date
//            2 -> {
//                selectedDayPosition?.let {
//                    actualDataLoaded = false
//
//                    val mDate = actualDates[selectedDayPosition!!]
//
//                    filteredData = repository.getPlacesByFilters(PlaceData().apply {
//                        date  = mDate
//                        selectedCity?.let { city = selectedCity!!.name } }).await().toMutableList()
//
//                    for(place in eventPlaces){
//                        filteredData!!.add(place)
//                    }
//
//                    fillDistances(false)
//
//                    sendData(false)
//
//                } ?: run {
//                    setActualAndSendData()
//                }
//            }

            // old by Types
//            3 -> {
//                if(data != null){
//                    if(newFilter!!.selectedCategories.isEmpty()){
//                        viewState.hideClear()
//                        setActualAndSendData()
//                    } else {
//                        actualDataLoaded = false
//
//                        filteredData = data!!.filter { it.type[0] in newFilter!!.selectedCategories}.toMutableList()
//
//                        fillDistances(false)
//
//                        sendData(false)
//
//                        viewState.showClear()
//                    }
//
//                } else {}
//            }
//            else -> {}
//        }
    }

    private fun setActualAndSendData(whichTab: Int){
        actualDataLoaded.setValueAt(whichTab, true)

        //TODO fill distances only for places only? Or for places and events?
        if(whichTab == POSITION_PLACES){
            fillDistances(true)
        }

        sendData(whichTab,true)
    }

    private fun updateDistances() {
        launch {
            if(locationPoint != null){
                fillDistances(true)

                sendData(null,true, updateDistances = true)
            }
        }
    }

    private fun loadData() {
        launch {
            viewState.showProgress()

            cities = repository.getCities().await()

            val tryCity = cities!!.firstOrNull { it.name == "Milan" }

            selectedCity = tryCity ?: cities!![0]

            viewState.changeCityName(selectedCity!!.name)

            val calendar = Calendar.getInstance()

            allCategoryFilters = repository.getPlaceTypes().await().map{ it.type }.filterNotNull().toMutableList()

            actualMinHour = calendar.get(Calendar.HOUR_OF_DAY)

            filters.put(POSITION_PLACES, PlacesFilter(actualMinHour))
            filters.put(POSITION_EVENTS, EventsFilter())
            filters.put(POSITION_CAMPAIGNS, CampaignsFilter())
            actualDataLoaded.put(POSITION_PLACES, false)
            actualDataLoaded.put(POSITION_EVENTS, false)
            actualDataLoaded.put(POSITION_CAMPAIGNS, false)

            for (x in 0 until 7) {
                days.add(Day(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).substring(0, 1).toUpperCase(), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) +1))
                actualDates.add(calendar.get(Calendar.DAY_OF_MONTH).toString()+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR))

                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

//            types = repository.getPlaceTypes().await().map{ it.type }.filterNotNull().toMutableList()
//            allCategoryFilters = types

            //TODO filter by new filters too
            data.placesData = repository.getPlacesByFilters(PlaceData().apply {
                selectedCity?.let {
                    date = actualDates[0]
                    city = selectedCity!!.name
                } }).await().toMutableList()

            //TODO filter by new filters by default too
            data.campaignData = repository.getCampaigns().await().toMutableList()

            //TODO filter by new filters by default too
            data.eventsData = repository.getEvents().await().toMutableList()
//            for(event in events){
//                val place = repository.getPlace(event.placeId).await()
//                place.isEventPlace = true
//                place.event = event
//
//                eventPlaces.add(place.apply {
//                    event.timeframe?.freeSpots?.let {
//                        freeSpots = it
//                    }
//                })
//            }

            updateDistances()

            viewState.hideProgress()

            //pass initial data to fragments
            viewState.showData(data, days)
//            viewState.showData(data, allCategoryFilters, (filters[POSITION_PLACES] as PlacesFilter).selectedCategories, days)

            initialized = true

            viewState.setSelectedDayItem(0)
        }
    }

    //TODO fill distances only for places only? Or for places and events?
    private fun fillDistances(actualData: Boolean){
        locationPoint?.let {
            if(actualData) {
                if (!distancesFilled) {
                    distancesFilled = true

                    data.placesData.forEach { place ->
                        val placePoint = place.location.latLng()

                        val distance = placePoint.distanceTo(locationPoint!!).toInt()

                        place.distance = distance
                    }
                    data.placesData = data.placesData.sortedBy { it.distance }.toMutableList()
                }
            } else {
                filteredData.placesData.forEach { place ->
                    val placePoint = place.location.latLng()

                    val distance = placePoint.distanceTo(locationPoint!!).toInt()

                    place.distance = distance
                }

                filteredData.placesData = filteredData.placesData.sortedBy { it.distance }.toMutableList()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
