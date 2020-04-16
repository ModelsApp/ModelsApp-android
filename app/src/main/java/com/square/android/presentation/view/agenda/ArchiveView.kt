package com.square.android.presentation.view.agenda

import com.square.android.presentation.view.ProgressView
import com.square.android.ui.fragment.agenda.calendar.CalendarEvent

interface ArchiveView: ProgressView{

    fun showData(items: List<CalendarEvent>)
}