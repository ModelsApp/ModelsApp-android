package com.square.android.presentation.presenter.places

import android.location.Location
import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.SCREENS
import com.square.android.data.pojo.*
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.map.CityLocateEvent
import com.square.android.presentation.presenter.placesList.PlacesUpdatedEvent
import com.square.android.presentation.view.places.PlacesView
import com.square.android.ui.activity.event.EventExtras
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.lang.Exception
import java.util.*

class PlaceSelectedEvent(val place: Place, val fromMap: Boolean)

@InjectViewState
class PlacesPresenter : BasePresenter<PlacesView>() {

    var filteringMode = 1

    var actualDataLoaded = true

    private var selectedDayPosition: Int? = null

    var types: MutableList<String> = mutableListOf()

    var days: MutableList<Day> = mutableListOf()
    var actualDates: MutableList<String> = mutableListOf()

    var timeframes: MutableList<FilterTimeframe> = mutableListOf()

    var cities: List<City>? = null

    var selectedCity: City? = null

    var allFilters: MutableList<String> = mutableListOf()
    var allSelected: MutableList<String> = mutableListOf()

    private var locationPoint: LatLng? = null

    private var data: MutableList<Place>? = null

    private var filteredData: MutableList<Place>? = null

    var distancesFilled: Boolean = false

    var searchText: CharSequence? = null

    var initialized = false

    var locationInitialized = false

    var events: List<Event> = listOf()

    var eventPlaces: MutableList<Place> = mutableListOf()

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

        if(actualDataLoaded){
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_WITHOUT_FILTERS.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
        } else{
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_USING_FILTERS.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
        }

        if(place.isEventPlace){
            place.event?.let {
                val extras = EventExtras(it, place)
                router.navigateTo(SCREENS.EVENT, extras)
            }
        } else {
            router.navigateTo(SCREENS.PLACE, place.id)
        }
    }

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            if(!locationInitialized){
                locationInitialized = true

                locationPoint = LatLng(it.latitude, it.longitude)

                if(actualDataLoaded){
                    updateDistances()
                }
            }
        }
    }

    fun filterClicked(position: Int) {
        try{
            val isTimeframe = timeframes.firstOrNull{it.name == allFilters[position]} != null

            if(isTimeframe){
                if(allFilters[position] in allSelected){
                    allSelected.remove(allFilters[position])
                } else{
                    allSelected.add(allFilters[position])
                }

                viewState.updateFilters(allFilters, allSelected, false)

            } else{
                val contains = allSelected.contains(allFilters[position])
                val currentTimeframes = timeframes.filter { it.type == allFilters[position] }.map { it.name }

                if(contains){
                    allSelected.remove(allFilters[position])
                    if(currentTimeframes.isNotEmpty()){

                        val timeframesToRemove: MutableList<String> = mutableListOf()

                        val allTypesWithThoseTimeframes: MutableList<List<String>> = mutableListOf()

                        for(currentTimeframe in currentTimeframes){
                            val list = timeframes.filter { it.name == currentTimeframe}.map{ it.type }

                            allTypesWithThoseTimeframes.add(list)
                        }

                        for(x in 0 until currentTimeframes.size){
                            if(allSelected.firstOrNull{it in allTypesWithThoseTimeframes[x]} == null){
                                timeframesToRemove.add(currentTimeframes[x])
                            }
                        }

                        for(timeframe in timeframesToRemove){
                            allFilters.remove(timeframe)
                            allSelected.remove(timeframe)
                        }
                    }
                } else{
                    allSelected.add(allFilters[position])

                    for(timeframe in currentTimeframes){
                        if(!allFilters.contains(timeframe)){
                            allFilters.add(timeframe)
                        }
                    }
                }

                viewState.updateFilters(allFilters, allSelected, true)
            }

            checkFilters()

        } catch (e: Exception){

        }
    }

    fun citySelected(c: City) = launch {
        viewState.changeCityName(c.name)

        selectedCity = c

        naviateToCity(c.name)

        data = repository.getPlacesByFilters(PlaceData().apply {
            date = actualDates[0]
            city = selectedCity!!.name
        }).await().toMutableList()

        //TODO - need city property in places gotten by event.placeId
//        for(place in eventPlaces.filter { it.city == selectedCity!!.name }){
//            data!!.add(place)
//        }
        for(place in eventPlaces){
            data!!.add(place)
        }

        distancesFilled = false

        checkFilters()
    }

    private fun naviateToCity(cityname: String){
        //TODO hardcoded, change later
        val latLng: LatLng = when(cityname.toUpperCase(Locale.ENGLISH)){
            "MILAN" -> LatLng(45.464664, 9.188540)
            "LONDON" -> LatLng(51.509865, -0.118092)
            "BUDAPEST" -> LatLng(47.497913, 19.040236)
            else -> LatLng(0.0, 0.0)
        }
        eventBus.post(CityLocateEvent(latLng))
    }

    private fun sendData(isActual: Boolean, updateDistances: Boolean = false){
        val data = if(isActual) data else filteredData

        data?.let {
            eventBus.post(PlacesUpdatedEvent(data.filter { it.freeSpots > 0 }.toMutableList(), updateDistances))
        }
    }

    fun dayClicked(position: Int){
        selectedDayPosition = position
        viewState.setSelectedDayItem(selectedDayPosition!!)
        checkFilters()
    }

    fun searchTextChanged(text: CharSequence?) {
        searchText = text
        checkFilters()
    }

    //mode: 1 - search, 2 - date, 3 - types
    fun changeFiltering(mode: Int) {
        filteringMode = mode

        checkFilters()
    }

    fun clearFilters() {
        viewState.hideClear()

        allSelected.clear()
        allFilters.removeAll { it in timeframes.map { it.name } }

        viewState.updateFilters(allFilters, allSelected, true)

        setActualData()
    }

    //filteringMode: 1 - search, 2 - date, 3 - types
    private fun checkFilters() = launch {
        when(filteringMode){
            1 -> {
                if (TextUtils.isEmpty(searchText)) {
                    setActualData()
                } else {
                    if(data != null){
                        actualDataLoaded = false

                        filteredData = data!!.filter { it.name.contains(searchText.toString(), true) }.toMutableList()

                        fillDistances(false)

                        sendData(false)

                    } else {}
                }
            }

            2 -> {
                selectedDayPosition?.let {
                    actualDataLoaded = false

                    val mDate = actualDates[selectedDayPosition!!]

                    filteredData = repository.getPlacesByFilters(PlaceData().apply {
                        date  = mDate
                        selectedCity?.let { city = selectedCity!!.name } }).await().toMutableList()

                    //TODO - need city property in places gotten by event.placeId
//                    for(place in eventPlaces.filter { it.city == selectedCity!!.name }){
//                        filteredData!!.add(place)
//                    }
                    for(place in eventPlaces){
                        filteredData!!.add(place)
                    }

                    fillDistances(false)

                    sendData(false)

                } ?: run {
                    setActualData()
                }
            }

            3 -> {
                if(data != null){
                    if(allSelected.isEmpty()){
                        viewState.hideClear()
                        setActualData()
                    } else {
                        actualDataLoaded = false

                        val selectedTimeframes = timeframes.filter { it.name in allSelected }
                        val selectedTypes = allSelected - selectedTimeframes.map { it.name }

                        filteredData = if(selectedTimeframes.isEmpty()){
                            data!!.filter { it.type[0] in selectedTypes}.toMutableList()
                        } else{
                            //TODO probably should be done with API call(no call allowing list of timeframes and types for now)
                            data!!.filter {it.type[0] in selectedTimeframes.map {t -> t.type }}.toMutableList()
                        }

                        fillDistances(false)

                        sendData(false)

                        viewState.showClear()
                    }

                } else {}
            }

            else -> {}
        }
    }

    fun setActualData(){
        actualDataLoaded = true
        fillDistances(true)

        sendData(true)
    }

    private fun updateDistances() {
        launch {
            if(locationPoint != null){
                fillDistances(true)

                sendData(true, true)
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

            for (x in 0 until 7) {
                days.add(Day(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).substring(0, 1).toUpperCase(), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) +1))
                actualDates.add(calendar.get(Calendar.DAY_OF_MONTH).toString()+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR))

                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            data = repository.getPlacesByFilters(PlaceData().apply {
                selectedCity?.let {
                    date = actualDates[0]
                    city = selectedCity!!.name
                } }).await().toMutableList()

            //TODO events not working correctly right now. Event's city is unknown and all places gotten by event's id will ignore selected city.
            events = repository.getEvents().await()

            for(event in events){
                val place = repository.getPlace(event.placeId).await()
                place.isEventPlace = true
                place.event = event

                eventPlaces.add(place.apply {
                    event.timeframe?.freeSpots?.let {
                        freeSpots = it
                    }
                })
            }

            //TODO - need city property in places gotten by event.placeId
//            for(place in eventPlaces.filter { it.city == selectedCity!!.name }){
//                data!!.add(place)
//            }
            for(place in eventPlaces){
                data!!.add(place)
            }

            updateDistances()

            timeframes = repository.getTimeFrames().await().toMutableList()
            types = repository.getPlaceTypes().await().map{ it.type }.filterNotNull().toMutableList()

            allFilters = types

            viewState.hideProgress()
            viewState.showData(data!!, allFilters, allSelected, days)

            initialized = true

            viewState.setSelectedDayItem(0)
        }
    }

    private fun fillDistances(actualData: Boolean){
        locationPoint?.let {
            if(actualData) {
                data?.let {
                    if(!distancesFilled){
                        distancesFilled = true

                        data!!.forEach { place ->
                            val placePoint = place.location.latLng()

                            val distance = placePoint.distanceTo(locationPoint!!).toInt()

                            place.distance = distance
                        }
                        data = data!!.sortedBy { it.distance }.toMutableList()
                    }
                }
            } else {
                filteredData?.forEach { place ->
                    val placePoint = place.location.latLng()

                    val distance = placePoint.distanceTo(locationPoint!!).toInt()

                    place.distance = distance
                }

                filteredData?.let { filteredData = filteredData!!.sortedBy { it.distance }.toMutableList() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
