package com.square.android.presentation.view.profile

import com.square.android.data.pojo.Speciality
import com.square.android.presentation.view.LoadingView

interface SpecialitiesView : LoadingView {
    fun showData(data: List<Speciality>, selectedItems: MutableList<Speciality>, primaryPosition: Int)

    fun changeSelection(position: Int, isSetToPrimary: Boolean)
}