package com.square.android.ui.fragment.places

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
import com.square.android.presentation.view.places.PlacesView
import com.square.android.ui.fragment.LocationFragment
import androidx.viewpager.widget.ViewPager
import com.square.android.data.pojo.City
import com.square.android.R
import com.square.android.data.pojo.Day
import com.square.android.presentation.presenter.places.*
import com.square.android.ui.activity.place.DaysAdapter
import kotlinx.android.synthetic.main.fragment_places.*

class PlacesFragment: LocationFragment(), PlacesView, DaysAdapter.Handler {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "PlacesPresenter")
    lateinit var presenter: PlacesPresenter

    private var daysAdapter: DaysAdapter? = null

    override fun showProgress() {
        placesProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        placesProgress.visibility = View.GONE
    }

    override fun showData(data: MainData, days: MutableList<Day>) {
//        override fun showData(data: MainData, types: MutableList<String>, activatedItems: MutableList<String>, days: MutableList<Day>) {
//
//        filtersAdapter = FiltersAdapter(types, this, activatedItems)
//        placesFiltersTypesRv.adapter = filtersAdapter
//        placesFiltersTypesRv.layoutManager = LinearLayoutManager(placesFiltersTypesRv.context, RecyclerView.HORIZONTAL,false)
//        placesFiltersTypesRv.addItemDecoration(MarginItemDecorator(placesFiltersTypesRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), false))

        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
                                                                                                         //(item_day_adjustable width = 40dp, without padding ~ 24dp, so padding ~ 16dp, ~16 /2 ~ 8dp to cut)
        daysAdapter = DaysAdapter(days, this, Math.round((displayMetrics.widthPixels.toFloat() - activity!!.resources.getDimension(R.dimen.v_8dp)) / 7))
        placesFiltersDaysRv.adapter = daysAdapter
        placesFiltersDaysRv.layoutManager = LinearLayoutManager(placesFiltersDaysRv.context, RecyclerView.HORIZONTAL,false)
        daysAdapter!!.setSelectedItem(0)

        setUpPager(data)
    }

    //TODO FIX: data in placesListFragment is not shown by default

    private fun setUpPager(data: MainData) {
        placesPager.isPagingEnabled = true
        placesPager.adapter = PlacesFragmentAdapter(childFragmentManager, data)
        placesPager.offscreenPageLimit = 3
        placesTabs.setupWithViewPager(placesPager)

        placesPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener {
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
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(presenter.initialized){
            setSelectedDayItem(presenter.selectedDayPosition)

            when(presenter.actualTabSelected){
                POSITION_PLACES -> {
                   showDays()
                   showCities()
                }
                POSITION_EVENTS -> {
                    hideDays()
                    hideCities()
                }
                POSITION_CAMPAIGNS -> {
                    hideDays()
                    hideCities()
                }
            }
        }

        placesCitiesLl.setOnClickListener {
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
        placesFiltersDaysRv.visibility = View.VISIBLE
    }

    override fun hideDays() {
        placesFiltersDaysRv.visibility = View.GONE
    }

    override fun showCities() {
        placesCitiesLl.visibility = View.VISIBLE
    }

    override fun hideCities() {
        placesCitiesLl.visibility = View.GONE
    }

    override fun changeCityName(name: String) {
        placesCity.text = name
    }

    override fun setSelectedDayItem(position: Int) {
        daysAdapter?.setSelectedItem(position)
    }

    override fun dayItemClicked(position: Int) {
        presenter.dayClicked(position)
    }

}
