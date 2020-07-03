package com.square.android.ui.fragment.agenda.todo

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import com.square.android.ui.fragment.agenda.calendar.EventType
import kotlinx.android.synthetic.main.item_todo.*

class ToDoAdapter(data: List<TodoItem>,
                      private val handler: Handler?) : BaseAdapter<TodoItem, ToDoAdapter.TodoItemHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_todo

    override fun instantiateHolder(view: View): TodoItemHolder = TodoItemHolder(view, handler)

    override fun getItemCount() = data.size

    override fun bindHolder(holder: TodoItemHolder, position: Int) {
        holder.bind(data[position])
    }

    class TodoItemHolder(containerView: View, var handler: Handler?) : BaseHolder<TodoItem>(containerView) {

        init {
            containerView.setOnClickListener { handler?.todoItemClicked(adapterPosition) }
        }

        override fun bind(item: TodoItem, vararg extras: Any?) {
            todoProgress.progress = Math.round((item.completeTasks.toFloat() / item.tasks.toFloat()) * 100).toFloat()
            todoProgress.text = Math.round((item.completeTasks.toFloat() / item.tasks.toFloat()) * 100).toString()+"%"

            val color: Int = when(item.type){
                EventType.TYPE_OFFER -> R.color.nice_violet
                EventType.TYPE_EVENT -> R.color.nice_blue
                EventType.TYPE_JOB -> R.color.status_green
                EventType.TYPE_CASTING -> R.color.status_yellow
            }

            todoStatusCircle.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(todoStatusCircle.context, color))

            todoTitle.text = item.title

            inProgressValue.text = (item.tasks - item.completeTasks).toString()

            deadlineValue.text = item.deadline
            }
        }

    interface Handler {
        fun todoItemClicked(position: Int)
    }

}

object SelectedPayload
