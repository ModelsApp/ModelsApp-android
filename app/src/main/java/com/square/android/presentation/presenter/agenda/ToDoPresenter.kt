package com.square.android.presentation.presenter.agenda

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.agenda.ToDoView
import com.square.android.ui.fragment.agenda.calendar.EventType
import com.square.android.ui.fragment.agenda.todo.TodoItem

@InjectViewState
class ToDoPresenter: BasePresenter<ToDoView>(){

    var data: List<TodoItem>? = null

    init {
        loadData()
    }

    private fun loadData() = launch{
        viewState.showProgress()

        //TODO load data from API

        
        //TODO just for tests
        data = mutableListOf(
                TodoItem(EventType.TYPE_CASTING, "Some title", 6, 4, "Dec 20th"),
                TodoItem(EventType.TYPE_EVENT, "Some title 2", 10, 1, "Dec 22th"),
                TodoItem(EventType.TYPE_OFFER, "Some title 3", 3, 0, "Dec 21th")
        ).toList()

        viewState.showData(data!!)

        viewState.hideProgress()
    }

}