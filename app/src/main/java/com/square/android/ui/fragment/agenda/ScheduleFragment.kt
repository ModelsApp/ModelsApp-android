package com.square.android.ui.fragment.agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.agenda.SchedulePresenter
import com.square.android.presentation.view.agenda.ScheduleView
import com.square.android.ui.fragment.BaseFragment

class ScheduleFragment: BaseFragment(), ScheduleView {

    @InjectPresenter
    lateinit var presenter: SchedulePresenter

    @ProvidePresenter
    fun providePresenter() = SchedulePresenter()

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_schedule, container, false)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}