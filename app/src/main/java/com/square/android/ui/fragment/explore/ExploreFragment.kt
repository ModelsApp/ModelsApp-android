package com.square.android.ui.fragment.explore

import android.location.Location
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.square.android.presentation.view.explore.ExploreView
import com.square.android.ui.fragment.LocationFragment
import androidx.viewpager.widget.ViewPager
import com.square.android.data.pojo.City
import com.square.android.R
import com.square.android.data.pojo.Day
import com.square.android.presentation.presenter.explore.*
import com.square.android.ui.activity.main.MainActivity
import com.square.android.ui.activity.main.MainFabClickedEvent
import com.square.android.ui.activity.main.NavFabClickedEvent
import com.square.android.ui.activity.place.DaysAdapter
import com.square.android.ui.fragment.explore.campaignsList.BottomSheetCampaignsFilters
import com.square.android.ui.fragment.explore.eventsList.BottomSheetEventsFilters
import com.square.android.ui.fragment.explore.filters.*
import com.square.android.ui.fragment.explore.placesList.BottomSheetOffersFilters
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_explore.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

class ExploreFragment: LocationFragment(), ExploreView, DaysAdapter.Handler, BaseBottomSheetFilters.Handler {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "PlacesPresenter")
    lateinit var presenter: ExplorePresenter

    private var daysAdapter: DaysAdapter? = null

    private val eventBus: EventBus by inject()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackFromSearchEvent(event: BackFromSearchEvent) {
        initWithMap()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMainFabClickedEvent(event: MainFabClickedEvent) {
        (activity as MainActivity).setUpMainFabImage(R.drawable.ic_list)

        (activity as MainActivity).mainFab.hide()
        (activity as MainActivity).mainFab.show()

        (activity as MainActivity).navFab.show()

        //TODO open map fragment
        presenter.navigateToMap()
    }

    //TODO move to map fragment
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackNavFabClickedEvent(event: NavFabClickedEvent) {
        println("GDFDFFDFFDF onBackNavFabClickedEvent")
    }

    override fun showData(data: MainData, days: MutableList<Day>) {
        if(!presenter.initialized) {
            presenter.initialized = true
            val displayMetrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
                                                                                                             //-8dp because (item_day_adjustable width = 40dp, without padding ~ 24dp, so padding ~ 16dp, ~16 /2)
            daysAdapter = DaysAdapter(days, this, Math.round((displayMetrics.widthPixels.toFloat() - activity!!.resources.getDimension(R.dimen.v_8dp)) / 7))
            mainListsFiltersDaysRv.adapter = daysAdapter
            mainListsFiltersDaysRv.layoutManager = LinearLayoutManager(mainListsFiltersDaysRv.context, RecyclerView.HORIZONTAL, false)
            daysAdapter!!.setSelectedItem(presenter.selectedDayPosition)

            setUpPager(data)
            presenter.dayClicked(0)
        }
    }

    private fun setUpPager(data: MainData) {
        mainListsPager.isPagingEnabled = true
        mainListsPager.adapter = PlacesFragmentAdapter(childFragmentManager, data)
        mainListsPager.offscreenPageLimit = 3
        mainListsTabs.setupWithViewPager(mainListsPager)

        mainListsPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                presenter.tabClicked(position)

                initWithMap()
            }
        })
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            if(!eventBus.isRegistered(this)){
                println("GDFDFFDFFDF evetbus register")
                eventBus.register(this)
            }

            if (presenter.initialized) {
                val displayMetrics = DisplayMetrics()
                activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
                //-8dp because (item_day_adjustable width = 40dp, without padding ~ 24dp, so padding ~ 16dp, ~16 /2)
                daysAdapter = DaysAdapter(presenter.days, this, Math.round((displayMetrics.widthPixels.toFloat() - activity!!.resources.getDimension(R.dimen.v_8dp)) / 7))
                mainListsFiltersDaysRv.adapter = daysAdapter
                mainListsFiltersDaysRv.layoutManager = LinearLayoutManager(mainListsFiltersDaysRv.context, RecyclerView.HORIZONTAL, false)
                daysAdapter!!.setSelectedItem(presenter.selectedDayPosition)

                setUpPager(presenter.data)

                mainListsPager.setCurrentItem(presenter.actualTabSelected, false)

                when (presenter.actualTabSelected) {
                    POSITION_PLACES -> {
                        showDays()
                        showCities()
                    }
                    POSITION_EVENTS -> {
                        hideDays()
                        showCities()
                    }
                    POSITION_CAMPAIGNS -> {
                        hideDays()
                        hideCities()
                    }
                }

               initWithMap()

                //TODO add filters etc later too

            } else {
                presenter.loadData()
            }

            mainListsCitiesLl.setOnClickListener {
                presenter.cities?.let {
                    val bottomSheetCities = BottomSheetCities(it, presenter.selectedCity, object : BottomSheetCities.Handler {
                        override fun cityClicked(selectedCity: City) {
                            presenter.citySelected(selectedCity)
                        }
                    })

                    bottomSheetCities.show(fragmentManager!!, BottomSheetCities.TAG)
                }
            }

            icSearch.setOnClickListener { presenter.navigateToSearch()}

            icFilters.setOnClickListener {
                when(presenter.actualTabSelected){
                    POSITION_PLACES -> {
                        val bottomSheetOffersFilters = BottomSheetOffersFilters(presenter.getFilter() as PlacesFilter,this)
                        bottomSheetOffersFilters.show(fragmentManager!!, BottomSheetOffersFilters.TAG)
                    }

                    POSITION_EVENTS -> {
                        val bottomSheetEventsFilters = BottomSheetEventsFilters(presenter.getFilter() as EventsFilter,this)
                        bottomSheetEventsFilters.show(fragmentManager!!, BottomSheetEventsFilters.TAG)
                    }

                    POSITION_CAMPAIGNS ->{
                        val bottomSheetCampaignsFilters = BottomSheetCampaignsFilters(presenter.getFilter() as CampaignsFilter,this)
                        bottomSheetCampaignsFilters.show(fragmentManager!!, BottomSheetCampaignsFilters.TAG)
                    }
                }
            }
    }

    fun initWithMap(){

        (activity as MainActivity).hideMapBottomView()

        if(presenter.actualTabSelected != POSITION_CAMPAIGNS){

            if(!(activity as MainActivity).mainFab.isShown){
                (activity as MainActivity).mainFab.show()
            }

//            (activity as MainActivity).setUpMainFabImage(R.drawable.r_pin)

            (activity as MainActivity).navFab.hide()
        } else{
            (activity as MainActivity).navFab.hide()
            (activity as MainActivity).mainFab.hide()
        }

//        (activity as MainActivity).hideMapBottomView()
//
//        if(presenter.actualTabSelected != POSITION_CAMPAIGNS){
//            if(presenter.isMapShown){
//                (activity as MainActivity).setUpMainFabImage(R.drawable.ic_list)
//                (activity as MainActivity).navFab.show()
//
//                //TODO open map fragment
//                presenter.navigateToMap()
//
//            } else{
//                (activity as MainActivity).setUpMainFabImage(R.drawable.r_pin)
//                (activity as MainActivity).navFab.hide()
//
//                //TODO back to this fragment
//                presenter.backToExplore()
//            }
//
//            (activity as MainActivity).mainFab.show()
//        } else{
//            (activity as MainActivity).navFab.hide()
//            (activity as MainActivity).mainFab.hide()
//        }
    }

    override fun filtersApplyClicked(filter: BaseFilter) {
        presenter.applyFilters(filter)
    }

    override fun filtersClearClicked() {
        presenter.clearFilters()
    }

    override fun showDays() {
        mainListsFiltersDaysRv.visibility = View.VISIBLE
    }

    override fun hideDays() {
        mainListsFiltersDaysRv.visibility = View.GONE
    }

    override fun showCities() {
        mainListsCitiesLl.visibility = View.VISIBLE
    }

    override fun hideCities() {
        mainListsCitiesLl.visibility = View.GONE
    }

    override fun changeCityName(name: String) {
        mainListsCity.text = name
    }

    override fun setSelectedDayItem(position: Int) {
        daysAdapter?.setSelectedItem(position)
    }

    override fun dayItemClicked(position: Int) {
        presenter.dayClicked(position)
    }

    override fun showProgress() {
        mainListsPager.visibility = View.INVISIBLE
        mainListsProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        mainListsProgress.visibility = View.GONE
        mainListsPager.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        println("GDFDFFDFFDF eventbus unregister")
        eventBus.unregister(this)
        super.onDestroy()
    }

}
