package com.square.android.presentation.presenter.agenda

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.agenda.ToDoView
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

//        viewState.showData(data!!)

        viewState.hideProgress()
    }

}