package com.square.android.presentation.presenter.explore

import android.os.Parcelable
import android.util.SparseArray
import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.place.PlaceExtras
import com.square.android.presentation.view.explore.SearchView
import kotlinx.android.parcel.Parcelize

@Parcelize
class SearchItem(var text: String, var secondText: String, var idString: String): Parcelable

@Parcelize
class SearchResult(var type: Int, var searchedText: String, var searchItems: List<SearchItem>): Parcelable

@Parcelize
class LatestSearch(var type: Int, var searches: MutableList<SearchItem> = mutableListOf()): Parcelable

private const val SEARCH_LIST_COUNT = 3

@InjectViewState
class SearchPresenter(private var searchType: Int): BasePresenter<SearchView>(){

    var latestSearches: SparseArray<LatestSearch> = SparseArray(LIST_ITEMS_SIZE)

    var searchData: SearchResult? = null

    init {
        loadData()
    }

    fun loadResults(text: String) = launch {
        when(searchType){
            POSITION_PLACES -> {
                //TODO waiting for api to be able to get filter data by String

                val places = repository.getPlaces().await().toMutableList()

                val data = places.filter {it.name.toLowerCase().contains(text.toLowerCase())}

                val searches: MutableList<SearchItem> = mutableListOf()
                for(result in data){
                    searches.add(SearchItem(result.name, result.address, result.id.toString()))
                }

                searchData = SearchResult(searchType, text, searches)

                viewState.updateSearchData(searchData!!)

            }
            POSITION_EVENTS -> {
                //TODO waiting for api to be able to get filter data by String

                val events = repository.getEvents().await().toMutableList()

                val eventsPlace: MutableList<Place> = mutableListOf()

                for (event in events) {
                    val place = repository.getPlace(event.placeId).await()
                    place.event = event

                    eventsPlace.add(place)
                }

                val data = eventsPlace.filter {it.name.toLowerCase().contains(text.toLowerCase())}

                val searches: MutableList<SearchItem> = mutableListOf()
                for(result in data){
                    searches.add(SearchItem(result.name, result.address, result.event!!.id))
                }

                searchData = SearchResult(searchType, text, searches)

                viewState.updateSearchData(searchData!!)
            }
            POSITION_CAMPAIGNS -> {
                //TODO waiting for api to be able to get filter data by String

                val campaigns = repository.getCampaigns().await().toMutableList()

                val data = campaigns.filter {it.title!!.toLowerCase().contains(text.toLowerCase())}

                val searches: MutableList<SearchItem> = mutableListOf()
                for(result in data){
                    searches.add(SearchItem(result.title!!, "", result.id.toString()))
                }

                searchData = SearchResult(searchType, text, searches)

                viewState.updateSearchData(searchData!!)
            }
        }
    }

    fun loadData(){
        latestSearches = repository.getLatestSearches()

        //TODO for safety, if SEARCH_LIST_COUNT was changed
        if(latestSearches[searchType].searches.size > SEARCH_LIST_COUNT){
            for(x in SEARCH_LIST_COUNT until latestSearches[searchType].searches.size){
                latestSearches[searchType].searches.removeAt(x)
            }
        }

        viewState.showData(latestSearches[searchType], searchType)
    }

    fun onPlaceClicked(fromResult: Boolean, item: SearchItem){
        if(fromResult){saveLatestSearches(item)}

        router.navigateTo(SCREENS.PLACE, PlaceExtras(item.idString.toLong()))
    }

    fun onEventClicked(fromResult: Boolean, item: SearchItem){
        if(fromResult){saveLatestSearches(item)}

        router.navigateTo(SCREENS.EVENT, item.idString)
    }

    fun onCampaignClicked(fromResult: Boolean, item: SearchItem){
        if(fromResult){saveLatestSearches(item)}

        router.navigateTo(SCREENS.CAMPAIGN_DETAILS, item.idString.toLong())
    }

    private fun saveLatestSearches(item: SearchItem){
        if(latestSearches[searchType].searches.firstOrNull{it.text == item.text} == null ){

            if(latestSearches[searchType].searches.size >= SEARCH_LIST_COUNT){

                for(x in latestSearches[searchType].searches.size-1 downTo 1){
                    latestSearches[searchType].searches[x] = latestSearches[searchType].searches[x-1]
                }
                latestSearches[searchType].searches[0] = item

            } else{
                latestSearches[searchType].searches.add(0, item)
            }

            repository.saveLatestSearches(latestSearches)

            viewState.updateLatestSearch(latestSearches[searchType])
        } else {
            if(latestSearches[searchType].searches.indexOf(latestSearches[searchType].searches.first {it.text == item.text}) != 0){

                val mList = latestSearches[searchType].searches
                mList.remove(latestSearches[searchType].searches.first {it.text == item.text})
                mList.add(0, item)

                latestSearches[searchType].searches = mList

                repository.saveLatestSearches(latestSearches)

                viewState.updateLatestSearch(latestSearches[searchType])
            }
        }
    }
}