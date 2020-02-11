package com.square.android.ui.fragment.mainLists

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
import com.square.android.presentation.view.mainLists.MainListsView
import com.square.android.ui.fragment.LocationFragment
import androidx.viewpager.widget.ViewPager
import com.square.android.data.pojo.City
import com.square.android.R
import com.square.android.data.pojo.Day
import com.square.android.presentation.presenter.mainLists.*
import com.square.android.ui.activity.place.DaysAdapter
import kotlinx.android.synthetic.main.fragment_main_lists.*

class MainListsFragment: LocationFragment(), MainListsView, DaysAdapter.Handler {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "PlacesPresenter")
    lateinit var presenter: MainListsPresenter

    private var daysAdapter: DaysAdapter? = null

    override fun showProgress() {
        mainListsPager.visibility = View.INVISIBLE
        mainListsProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        mainListsProgress.visibility = View.GONE
        mainListsPager.visibility = View.VISIBLE
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
            }
        })
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_lists, container, false)
    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(presenter.initialized){
            val displayMetrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
                                                                                                                       //-8dp because (item_day_adjustable width = 40dp, without padding ~ 24dp, so padding ~ 16dp, ~16 /2)
            daysAdapter = DaysAdapter(presenter.days, this, Math.round((displayMetrics.widthPixels.toFloat() - activity!!.resources.getDimension(R.dimen.v_8dp)) / 7))
            mainListsFiltersDaysRv.adapter = daysAdapter
            mainListsFiltersDaysRv.layoutManager = LinearLayoutManager(mainListsFiltersDaysRv.context, RecyclerView.HORIZONTAL,false)
            daysAdapter!!.setSelectedItem(presenter.selectedDayPosition)

            setUpPager(presenter.data)

            mainListsPager.setCurrentItem(presenter.actualTabSelected, false)

            when(presenter.actualTabSelected){
                POSITION_PLACES, POSITION_EVENTS -> {
                   showDays()
                   showCities()
                }
                POSITION_CAMPAIGNS -> {
                    hideDays()
                    hideCities()
                }
            }

            //TODO add filters etc later too

        } else{
            presenter.loadData()
        }

        mainListsCitiesLl.setOnClickListener {
            presenter.cities?.let {
                val bottomSheetCities = BottomSheetCities(it, presenter.selectedCity, object : BottomSheetCities.Handler {
                    override fun cityClicked(selectedCity: City) {
                        presenter.citySelected(selectedCity)
                    }
                })

                bottomSheetCities.show(fragmentManager, BottomSheetCities.TAG)
            }
        }
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

}
