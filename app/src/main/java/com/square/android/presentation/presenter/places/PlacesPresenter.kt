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
import com.square.android.ui.fragment.places.filters.BaseFilter
import com.square.android.ui.fragment.places.filters.CampaignsFilter
import com.square.android.ui.fragment.places.filters.EventsFilter
import com.square.android.ui.fragment.places.filters.PlacesFilter
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.util.*

const val POSITION_PLACES = 0
const val POSITION_EVENTS = 1
const val POSITION_CAMPAIGNS = 2

class PlaceSelectedEvent(val place: Place, val fromMap: Boolean)

class MainData(
        var placesData: MutableList<Place> = mutableListOf(),
        var eventsData: MutableList<Event> = mutableListOf(),
        var campaignsData: MutableList<CampaignInfo> = mutableListOf()
)

@InjectViewState
class PlacesPresenter : BasePresenter<PlacesView>() {

    var actualDataLoaded: SparseArray<Boolean> = SparseArray(3)

    var selectedDayPosition: Int = 0

    var days: MutableList<Day> = mutableListOf()
    var actualDates: MutableList<String> = mutableListOf()

    var cities: List<City>? = null

    var selectedCity: City? = null

    var allCategoryFilters: MutableList<String> = mutableListOf()

    var filters: SparseArray<BaseFilter> = SparseArray(3)

    private var locationPoint: LatLng? = null

    private var data: MainData = MainData()

    var actualTabSelected = POSITION_PLACES

    var initialized = false

    var locationInitialized = false

    private val eventBus: EventBus by inject()

    init {
        eventBus.register(this)

        loadData()
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

            filters.put(POSITION_PLACES, PlacesFilter(calendar.get(Calendar.HOUR_OF_DAY)))
            filters.put(POSITION_EVENTS, EventsFilter())
            filters.put(POSITION_CAMPAIGNS, CampaignsFilter())
            actualDataLoaded.put(POSITION_PLACES, true)
            actualDataLoaded.put(POSITION_EVENTS, true)
            actualDataLoaded.put(POSITION_CAMPAIGNS, true)

            for (x in 0 until 7) {
                days.add(Day(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).substring(0, 1).toUpperCase(), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) +1))
                actualDates.add(calendar.get(Calendar.DAY_OF_MONTH).toString()+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR))

                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            dataFromApi()

            updateDistances()

            viewState.hideProgress()

            //pass initial data to fragments
            viewState.showData(data, days)

            initialized = true

            viewState.setSelectedDayItem(0)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlaceSelectedEvent(event: PlaceSelectedEvent) {
        val place = event.place
        val id = place.id

        if(actualDataLoaded[actualTabSelected]){
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_WITHOUT_FILTERS.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
        } else{
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_USING_FILTERS.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
        }

        router.navigateTo(SCREENS.PLACE, place.id)
    }

    //TODO fire when tab changed in tabLayout
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

    //TODO fire when "clear" tv clicked in filters sheet
    fun clearFilters() {
        filters[actualTabSelected].clear()
        checkFilters()
    }

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            if(!locationInitialized){
                locationInitialized = true

                locationPoint = LatLng(it.latitude, it.longitude)

                updateDistances()
            }
        }
    }

    fun dayClicked(position: Int){
        selectedDayPosition = position
        viewState.setSelectedDayItem(selectedDayPosition)

        //TODO day selected not for all tabs?
        checkFilters(listOf(POSITION_PLACES, POSITION_EVENTS, POSITION_CAMPAIGNS))
    }

    fun citySelected(c: City){
        viewState.changeCityName(c.name)

        selectedCity = c

        //TODO city selected not for all tabs?
        checkFilters(listOf(POSITION_PLACES, POSITION_EVENTS, POSITION_CAMPAIGNS))
    }

    private fun checkFilters(tabsForGlobalFilters: List<Int>? = null) = launch {
        tabsForGlobalFilters?.let {
            dataFromApi(tabsForGlobalFilters)
            sendData(tabsForGlobalFilters)

        } ?: run {
            actualDataLoaded.setValueAt(actualTabSelected, filters[actualTabSelected].isDefault())

            dataFromApi(listOf(actualTabSelected))

            sendData(listOf(actualTabSelected))
        }
    }

    private fun sendData(whichTabsToSend: List<Int> = listOf(POSITION_PLACES, POSITION_EVENTS, POSITION_CAMPAIGNS)){

        //TODO fill distances only for places only? Or for places and events?
        if(whichTabsToSend.contains(POSITION_PLACES)){
            fillDistances()
        }

        if(whichTabsToSend.contains(POSITION_PLACES)){
            eventBus.post(PlacesUpdatedEvent(data.placesData.toMutableList()))
        }

        if(whichTabsToSend.contains(POSITION_EVENTS)){
            //TODO
//            eventBus.post(EventsUpdatedEvent(data.eventsData.toMutableList()))
        }

        if(whichTabsToSend.contains(POSITION_CAMPAIGNS)){
            //TODO
//            eventBus.post(CampaignsUpdatedEvent(data.campaignData.toMutableList()))
        }
    }

    private fun dataFromApi(whichTabsToLoad: List<Int> = listOf(POSITION_PLACES, POSITION_EVENTS, POSITION_CAMPAIGNS)) = launch{
            if(whichTabsToLoad.contains(POSITION_PLACES)){
                //TODO filter by new filters too
                data.placesData = repository.getPlacesByFilters(PlaceData().apply {
                    selectedCity?.let {
                        date = actualDates[selectedDayPosition]
                        city = selectedCity!!.name
                    } }).await().toMutableList()
            }

            if(whichTabsToLoad.contains(POSITION_EVENTS)){
                //TODO filter by new filters too
                data.campaignsData = repository.getCampaigns().await().toMutableList()
            }

            if(whichTabsToLoad.contains(POSITION_CAMPAIGNS)){
                //TODO filter by new filters too
                data.eventsData = repository.getEvents().await().toMutableList()
            }
    }

    private fun updateDistances() {
            if(locationPoint != null){
                sendData(listOf(POSITION_PLACES))
            }
    }

    //TODO fill distances only for places only? Or for places and events?
    private fun fillDistances(){
        locationPoint?.let {
                data.placesData.forEach { place ->
                    val placePoint = place.location.latLng()

                    val distance = placePoint.distanceTo(locationPoint!!).toInt()

                    place.distance = distance
                }
                data.placesData = data.placesData.sortedBy { it.distance }.toMutableList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
