package com.square.android.presentation.view.explore

import com.square.android.data.newPojo.NewPlace
import com.square.android.presentation.view.BaseView

interface PlacesListView : BaseView{
    fun updateDistances()
    fun showData(data: List<NewPlace>)
    fun updatePlaces(data: List<NewPlace>)
}
