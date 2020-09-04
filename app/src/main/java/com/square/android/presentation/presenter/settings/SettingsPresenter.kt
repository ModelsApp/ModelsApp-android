package com.square.android.presentation.presenter.settings

import com.arellomobile.mvp.InjectViewState
import com.square.android.App
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.newPojo.PlacesFiltersAvailability
import com.square.android.data.newPojo.PlacesFiltersData
import com.square.android.data.newPojo.PlacesFiltersTimeSlots
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BaseTabPresenter
import com.square.android.presentation.presenter.explore.POSITION_PLACES
import com.square.android.presentation.view.settings.SettingsView
import com.square.android.ui.activity.TabData
import com.square.android.ui.fragment.explore.filters.PlacesFilter
import java.util.*

@InjectViewState
class SettingsPresenter(var user: Profile.User): BaseTabPresenter<SettingsView>(){

    fun navigateToMain(tabData: TabData){
        routerNavigate(SCREENS.SETTINGS_MAIN, user, tabData)
    }

    init {

        //TODO delete later
        launch{

            val calendar = Calendar.getInstance()
            val month = (calendar.get(Calendar.MONTH) +1)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val yearStr = calendar.get(Calendar.YEAR)
            val monthStr = if (month < 10) {"0${month}"} else month
            val dayStr = if (day < 10) {"0${day}"} else day

            val filter = PlacesFilter(calendar.get(Calendar.HOUR_OF_DAY))

            val categoriesList = listOf(App.getString(R.string.category_1), App.getString(R.string.category_2), App.getString(R.string.category_3), App.getString(R.string.category_4), App.getString(R.string.category_5))
            val offersTypologyList = listOf(App.getString(R.string.complimentary), App.getString(R.string.discounted))

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

            repository.getNearbyPlaces(45.4896221, 9.1890265, 1000, "$yearStr-$monthStr-$dayStr", PlacesFiltersData(
                    city = "Milan",
                    mainCategory = if(filter.selectedCategories.isEmpty()) "" else filter.selectedCategories.map{categoriesList[it]}.first() ,
                    availability = availability,
                    typology = offersTypologyList[filter.offersTypology],
                    walkIn = walkIn,
                    reservationApprove = reservationApprove,
                    timeSlots = timeSlot

            )).await()
        }
    }

}