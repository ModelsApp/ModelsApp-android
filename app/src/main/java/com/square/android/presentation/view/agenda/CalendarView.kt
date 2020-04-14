package com.square.android.presentation.view.agenda

import com.square.android.presentation.view.ProgressView
import com.square.android.ui.fragment.agenda.calendar.CalendarItem

interface CalendarView: ProgressView{

    fun showData(items: List<CalendarItem>)
}