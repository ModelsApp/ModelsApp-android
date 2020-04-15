package com.square.android.ui.fragment.agenda.calendar

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_calendar.*
import java.util.*

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

            if(item.day.toInt() == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) && item.isCurrentMonth){
                itemCalendarValue.typeface = Typeface.createFromAsset(itemCalendarValue.context.assets, itemCalendarValue.context.getString(R.string.font_poppins_semi_bold))
            } else{
                itemCalendarValue.typeface = Typeface.createFromAsset(itemCalendarValue.context.assets, itemCalendarValue.context.getString(R.string.font_poppins_regular))
            }

            itemCalendarValue.isEnabled = item.isCurrentMonth

            itemCalendarValue.setOnClickListener { handler?.calendarItemClicked(adapterPosition) }

            itemCalendarIconsLl.removeAllViews()

            val events = item.events.distinctBy { it.type }

            var isFirst = true

            for(event in events){
                val view = View(itemCalendarIconsLl.context)

                view.layoutParams = ViewGroup.MarginLayoutParams(itemCalendarIconsLl.context.resources.getDimension(R.dimen.v_5dp).toInt(),itemCalendarIconsLl.context.resources.getDimension(R.dimen.v_5dp).toInt())

                if(!isFirst){
                    (view.layoutParams as ViewGroup.MarginLayoutParams).marginStart = itemCalendarIconsLl.context.resources.getDimension(R.dimen.v_3dp).toInt()
                }

                isFirst = false

                val color: Int = when(event.type){
                    EventType.TYPE_OFFER -> R.color.nice_violet
                    EventType.TYPE_EVENT -> R.color.nice_blue
                    EventType.TYPE_JOB -> R.color.status_green
                    EventType.TYPE_CASTING -> R.color.status_yellow
                }

                view.setBackgroundColor(color)

                val drawable = ContextCompat.getDrawable(itemCalendarIconsLl.context, R.drawable.round_background)
                drawable?.setTintList(ColorStateList.valueOf(ContextCompat.getColor(itemCalendarIconsLl.context, color)))

                view.background = drawable

                itemCalendarIconsLl.addView(view)
            }

            bindSelected(item, selectedPosition)
        }

        fun bindSelected(item: CalendarItem, selectedPosition: Int?) {
            itemCalendarValue.isChecked = (selectedPosition == adapterPosition)
            itemCalendarIconsLl.visibility = if(selectedPosition == adapterPosition) View.INVISIBLE else View.VISIBLE
        }
    }

    interface Handler {
        fun calendarItemClicked(position: Int)
    }

    object SelectedPayload
}