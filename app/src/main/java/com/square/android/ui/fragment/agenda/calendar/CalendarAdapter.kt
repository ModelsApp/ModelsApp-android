package com.square.android.ui.fragment.agenda.calendar

import android.view.View
import com.square.android.R
import com.square.android.extensions.getDayString
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_calendar.*

class CalendarAdapter(data: List<CalendarItem>,
                  private val handler: Handler?) : BaseAdapter<CalendarItem, CalendarAdapter.CalendarItemHolder>(data) {

    var selectedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.item_calendar

    override fun instantiateHolder(view: View): CalendarItemHolder = CalendarItemHolder(view, handler)

    override fun getItemCount() = data.size

    override fun bindHolder(holder: CalendarItemHolder, position: Int) {
        holder.bind(data[position], selectedItemPosition)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: CalendarItemHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is SelectedPayload }
                .forEach { holder.bindSelected(data[position], selectedItemPosition) }
    }

    fun setSelectedItem(position: Int?) {
        if (position == null) return

        val previousPosition = selectedItemPosition
        selectedItemPosition = position

        previousPosition?.let { notifyItemChanged(it, SelectedPayload) }

        notifyItemChanged(position)
    }

    class CalendarItemHolder(containerView: View, var handler: Handler?) : BaseHolder<CalendarItem>(containerView) {

        override fun bind(item: CalendarItem, vararg extras: Any? ) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            itemCalendarValue.text = item.day

            itemCalendarValue.isEnabled = item.isCurrentMonth

            itemCalendarValue.setOnClickListener { handler?.calendarItemClicked(adapterPosition) }

            itemCalendarIconsLl.removeAllViews()

            val events = item.events.distinctBy { it.type }

            for(event in events){
                val view = View(itemCalendarIconsLl.context).apply { layoutParams.apply {
                    height = itemCalendarIconsLl.context.resources.getDimension(R.dimen.v_8dp).toInt()
                    width = itemCalendarIconsLl.context.resources.getDimension(R.dimen.v_8dp).toInt()
                } }

                val color: Int = when(event.type){
                    EventType.TYPE_OFFER -> R.color.nice_violet
                    EventType.TYPE_EVENT -> R.color.nice_blue
                    EventType.TYPE_JOB -> R.color.status_green
                    EventType.TYPE_CASTING -> R.color.status_yellow
                }

                view.setBackgroundColor(color)

                itemCalendarIconsLl.addView(view)
            }

            bindSelected(item, selectedPosition)
        }

        fun bindSelected(item: CalendarItem, selectedPosition: Int?) {
            itemCalendarValue.isChecked = (selectedPosition == adapterPosition)
        }
    }

    interface Handler {
        fun calendarItemClicked(position: Int)
    }

    object SelectedPayload
}