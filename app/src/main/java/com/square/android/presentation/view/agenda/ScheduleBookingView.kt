package com.square.android.presentation.view.agenda

import com.square.android.presentation.presenter.agenda.ScheduleBooking
import com.square.android.presentation.view.LoadingView

interface ScheduleBookingView: LoadingView {

    fun showData(data: ScheduleBooking)

}