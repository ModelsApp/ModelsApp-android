package com.square.android.ui.fragment.explore.eventsList

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.extensions.asDistance
import com.square.android.extensions.loadImage
import com.square.android.extensions.setTextCarryingEmpty
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.card_event.*
import org.jetbrains.anko.dimen

// TODO based on place right now, as it was before
class EventsAdapter(data: List<Place>,
                    private val handler: Handler) : BaseAdapter<Place, EventsAdapter.EventsHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.card_event

    override fun getItemCount() = data.size

    override fun instantiateHolder(view: View) = EventsHolder(view, handler)

    fun updateDistances() {
        notifyItemRangeChanged(0, data.size, DistancePayload)
    }

    override fun bindHolder(holder: EventsHolder, position: Int) {
        super.bindHolder(holder, position)
        holder.containerView.setOnClickListener { handler.itemClicked(data[position]) }
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: EventsHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        val item = data[position]

        payloads.filter { it is DistancePayload }
                .forEach { holder.bindDistance(item) }
    }

    class EventsHolder(containerView: View,
                       handler: Handler) : BaseHolder<Place>(containerView) {

        override fun bind(item: Place, vararg extras: Any?) {

            eventName.text = item.name
            eventAddress.text = item.address

            //TODO no rating value in place for now
            eventRating.text = listOf("4.0", "4.1" , "4.2" , "4.3" , "4.4" , "4.5" , "4.6" , "4.7" , "4.8", "4.9", "5.0").random()

            if (item.mainImage != null) {
                eventImg.loadImage(item.mainImage!!, R.color.placeholder, eventImg.context.dimen(R.dimen.v_24dp))
            } else {
                if (item.photos?.isEmpty() == true) {
                    eventImg.setImageResource(R.color.placeholder)
                } else {
                    item.photos?.run {
                        eventImg.loadImage(first(), R.color.placeholder, eventImg.context.dimen(R.dimen.v_24dp) )
                    }
                }
            }

            fullTv.visibility = if(item.freeSpots > 0) View.GONE else View.VISIBLE

            bindDistance(item)
        }

        fun bindDistance(item: Place) {
            val distance = item.distance.asDistance()
            eventDistance.setTextCarryingEmpty(distance)
        }
    }

    interface Handler {
        fun itemClicked(place: Place)
    }
}

object DistancePayload