package com.square.android.ui.fragment.agenda.todo

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.agenda.ToDoPresenter
import com.square.android.presentation.view.agenda.ToDoView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.agenda.calendar.EventType
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_todo.*

@Parcelize
data class TodoItem(
        var type: EventType,
        var title: String,
        var tasks: Int,
        var completeTasks: Int,
        var deadline: String
): Parcelable

class ToDoFragment: BaseFragment(), ToDoView{

    @InjectPresenter
    lateinit var presenter: ToDoPresenter

    @ProvidePresenter
    fun providePresenter() = ToDoPresenter()

    private var adapter: ToDoAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_todo, container, false)
    }

    override fun showData(items: List<TodoItem>) {
        adapter = ToDoAdapter(items, object: ToDoAdapter.Handler{
            override fun todoItemClicked(position: Int) {
                //TODO
            }
        })

        rvTodo.adapter = adapter
        rvTodo.layoutManager = LinearLayoutManager(rvTodo.context, RecyclerView.VERTICAL,false)
    }

    override fun showProgress() {
        rvTodo.visibility = View.INVISIBLE
        todoProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        todoProgress.visibility = View.GONE
        rvTodo.visibility = View.VISIBLE
    }
}