package com.square.android.presentation.view.mainLists

import com.square.android.data.pojo.Day
import com.square.android.presentation.presenter.mainLists.MainData
import com.square.android.presentation.view.ProgressView

interface MainListsView : ProgressView {
    fun showData(data: MainData, days: MutableList<Day>)
//    fun showData(data: MainData, types: MutableList<String>, activatedItems: MutableList<String>, days: MutableList<Day>)

//    fun updateFilters(types: MutableList<String>, activated: MutableList<String>, updateAll: Boolean)

    fun setSelectedDayItem(position: Int)
    fun changeCityName(name: String)

    fun showDays()
    fun hideDays()
    fun showCities()
    fun hideCities()
}
