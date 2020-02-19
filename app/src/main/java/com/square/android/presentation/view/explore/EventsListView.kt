package com.square.android.presentation.view.explore

import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView

interface EventsListView : BaseView {
    fun showData(data: List<Place>)
    fun updateEvents(data: List<Place>)
}