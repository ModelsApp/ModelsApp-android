package com.square.android.presentation.view.agenda

import com.square.android.presentation.view.ProgressView

interface ScheduleView: ProgressView{
    fun showData(ordered: List<Any>)
}