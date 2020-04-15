package com.square.android.presentation.view.agenda

import com.square.android.presentation.view.ProgressView
import com.square.android.ui.fragment.agenda.todo.TodoItem

interface ToDoView: ProgressView{

    fun showData(items: List<TodoItem>)
}