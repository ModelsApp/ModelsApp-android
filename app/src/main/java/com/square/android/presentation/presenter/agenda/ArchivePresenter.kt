package com.square.android.presentation.presenter.agenda

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.agenda.ArchiveView
import com.square.android.ui.fragment.agenda.calendar.CalendarEvent
import com.square.android.ui.fragment.agenda.calendar.EventType

@InjectViewState
class ArchivePresenter: BasePresenter<ArchiveView>(){

    var data: List<CalendarEvent>? = null

    init {
        loadData()
    }

    private fun loadData() = launch{
        viewState.showProgress()

        //TODO load data from API


        //TODO just for tests
        data = mutableListOf(
                CalendarEvent(EventType.TYPE_JOB,"La Perla dOro","https://cdn.eso.org/images/large/eso1436a.jpg","Dec 14", "2464 Valley View, Milano","",0, "", true),
                CalendarEvent(EventType.TYPE_OFFER,"2 Concert Tickets","https://cdn.eso.org/images/large/eso1436a.jpg","Dec 21", "","The Conga Room",0, "", true),
                CalendarEvent(EventType.TYPE_EVENT,"La Perla dOro","https://cdn.eso.org/images/large/eso1436a.jpg","Dec 26", "2464 Valley View, Milano","",0, "", false),
                CalendarEvent(EventType.TYPE_CASTING,"La Perla dOro","https://cdn.eso.org/images/large/eso1436a.jpg","Dec 26", "2464 Valley View, Milano","",0, "", true)
        ).toList()

        viewState.showData(data!!)

        viewState.hideProgress()
    }

}