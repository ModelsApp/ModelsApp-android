package com.square.android.ui.fragment.agenda.archive

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import com.square.android.extensions.*
import com.square.android.ui.fragment.agenda.calendar.CalendarEvent
import com.square.android.ui.fragment.agenda.calendar.EventType
import kotlinx.android.synthetic.main.item_archive.*

class ArchiveAdapter(data: List<CalendarEvent>, private val handler: Handler?)
    : BaseAdapter<CalendarEvent, ArchiveAdapter.ArchiveHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_archive

    override fun instantiateHolder(view: View): ArchiveHolder = ArchiveHolder(view, handler)

    class ArchiveHolder(containerView: View, var handler: Handler?) : BaseHolder<CalendarEvent>(containerView) {

        override fun bind(item: CalendarEvent, vararg extras: Any?) {

            archiveImg.loadImage(item.image, roundedCornersRadiusPx = Math.round(archiveImg.context.resources.getDimension(R.dimen.v_24dp)))

            if(item.completed){
                archiveImg.makeBlackWhite()
                archiveIcon.drawableFromRes(R.drawable.checkmark, android.R.color.white)
            } else{
                archiveImg.makeReddish()
                archiveIcon.drawableFromRes(R.drawable.close_x, android.R.color.white)
            }

            archiveIcon.visibility = View.VISIBLE

            val color: Int = when(item.type){
                EventType.TYPE_OFFER -> R.color.nice_violet
                EventType.TYPE_EVENT -> R.color.nice_blue
                EventType.TYPE_JOB -> R.color.status_green
                EventType.TYPE_CASTING -> R.color.status_yellow
            }

            archiveStatusCircle.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(archiveStatusCircle.context, color))

            archiveTitle.text = item.title
            archiveTopIc.drawableFromRes(R.drawable.r_address)
            archiveTopIc.imageTintList
            archiveTopText.text = item.address
            archiveBottomText.text = item.interval

            archiveContainer.setOnClickListener {
                handler?.archiveItemClicked(adapterPosition)
            }
        }

    }

    interface Handler {
        fun archiveItemClicked(position: Int)
    }
}