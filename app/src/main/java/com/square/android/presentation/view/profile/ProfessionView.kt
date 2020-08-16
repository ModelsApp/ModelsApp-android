package com.square.android.presentation.view.profile

import com.square.android.data.pojo.Profession
import com.square.android.presentation.view.LoadingView

interface ProfessionView : LoadingView {
    fun showData(data: List<Profession>, selectedItems: MutableList<Profession>, primaryPosition: Int)

    fun changeSelection(position: Int, isSetToPrimary: Boolean)
}