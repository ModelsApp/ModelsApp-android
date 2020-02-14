package com.square.android.ui.fragment.agenda.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.agenda.CalendarPresenter
import com.square.android.presentation.view.agenda.CalendarView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_calendar.*

class CalendarFragment: BaseFragment(), CalendarView {

    @InjectPresenter
    lateinit var presenter: CalendarPresenter

    @ProvidePresenter
    fun providePresenter() = CalendarPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarList.setHasFixedSize(true)
    }

    override fun showProgress() {
        calendarList.visibility = View.INVISIBLE
        calendarProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        calendarProgress.visibility = View.GONE
        calendarList.visibility = View.VISIBLE
    }

}