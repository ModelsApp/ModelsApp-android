package com.square.android.presentation.presenter.agenda

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.agenda.AgendaView

const val POSITION_CALENDAR = 0
const val POSITION_SCHEDULE = 1
const val POSITION_TODO = 2
const val POSITION_ARCHIVE = 3

@InjectViewState
class AgendaPresenter(): BasePresenter<AgendaView>() {

    var actualTabSelected: Int = POSITION_CALENDAR

    fun tabClicked(whichTab: Int) {
        actualTabSelected = whichTab
    }

}