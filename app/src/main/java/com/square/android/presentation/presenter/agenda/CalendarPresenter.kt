package com.square.android.presentation.presenter.agenda

import com.arellomobile.mvp.InjectViewState
import com.square.android.extensions.toDate
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.agenda.CalendarView
import com.square.android.ui.fragment.agenda.calendar.CalendarItem
import java.util.*

@InjectViewState
class CalendarPresenter(): BasePresenter<CalendarView>() {

    var data: List<CalendarItem> = listOf()

    var currentItemSelected: Int = 0

    init {
        loadData()
    }

    fun calendarItemClicked(index: Int){
        currentItemSelected = index
    }

    fun loadData() = launch {
        viewState.showProgress()

        val calendar = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
        val previousMonthCalendar = Calendar.getInstance().apply {
            add(Calendar.MONTH , -1)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }
        val nextMonthCalendar = Calendar.getInstance().apply {
            add(Calendar.MONTH , 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val startDaysToLoad: Int = when(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.get(Calendar.DAY_OF_WEEK)){
            1 -> 6
            7 -> 5
            6 -> 4
            5 -> 3
            4 -> 2
            3 -> 1
            else -> 0
        }

        val endDaysToLoad: Int = when(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) }.get(Calendar.DAY_OF_WEEK)){
            2 -> 6
            3 -> 5
            4 -> 4
            5 -> 3
            6 -> 2
            7 -> 1
            else -> 0
        }

        val startDays: MutableList<CalendarItem> = mutableListOf()
        for (x in 0 until startDaysToLoad){
            startDays.add(0, CalendarItem((previousMonthCalendar.get(Calendar.DAY_OF_MONTH) - x).toString(), false))
        }

        val endDays: MutableList<CalendarItem> = mutableListOf()
        for (x in 0 until endDaysToLoad){

            endDays.add(CalendarItem((nextMonthCalendar.get(Calendar.DAY_OF_MONTH) + x).toString(), false))
        }

        val actualDays: MutableList<CalendarItem> = mutableListOf()
        for(x in 0 until calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
            actualDays.add(CalendarItem((calendar.get(Calendar.DAY_OF_MONTH) + x).toString(), true))
        }

        data = startDays + actualDays + endDays

        viewState.showData(data)

        viewState.hideProgress()
    }

}