package com.square.android.ui.fragment.explore.placesList

import android.view.View
import com.square.android.R
import com.square.android.data.newPojo.NewPlace
import com.square.android.extensions.asDistance
import com.square.android.extensions.loadImage
import com.square.android.extensions.setTextCarryingEmpty
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.card_place.*
import org.jetbrains.anko.dimen

class PlacesAdapter(data: List<NewPlace>,
                    private val handler: Handler) : BaseAdapter<NewPlace, PlacesAdapter.PlacesHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.card_place

    override fun getItemCount() = data.size

    override fun instantiateHolder(view: View) = PlacesHolder(view, handler)

    fun updateDistances() {
        notifyItemRangeChanged(0, data.size, DistancePayload)
    }

    override fun bindHolder(holder: PlacesHolder, position: Int) {
        super.bindHolder(holder, position)
        holder.containerView.setOnClickListener { handler.itemClicked(data[position]) }
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: PlacesHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        val item = data[position]

        payloads.filter { it is DistancePayload }
                .forEach { holder.bindDistance(item) }
    }

    class PlacesHolder(containerView: View,
                       handler: Handler) : BaseHolder<NewPlace>(containerView) {

        override fun bind(item: NewPlace, vararg extras: Any?) {

            placeName.text = item.name
            placeAddress.text = item.address

            //TODO no rating value in place for now
            placeRating.text = listOf("4.0", "4.1" , "4.2" , "4.3" , "4.4" , "4.5" , "4.6" , "4.7" , "4.8", "4.9", "5.0").random()

            if (item.getMainImage() != null) {
                placeImg.loadImage(item.getMainImage()!!, R.color.placeholder, placeImg.context.dimen(R.dimen.v_24dp))
            } else {
                if (item.getNotMainPhotos().isEmpty()) {
                    placeImg.setImageResource(R.color.placeholder)
                } else {
                    item.getNotMainPhotos().run {
                        placeImg.loadImage(first(), R.color.placeholder, placeImg.context.dimen(R.dimen.v_24dp) )
                    }
                }
            }

            fullTv.visibility = if(item.slots > 0) View.GONE else View.VISIBLE

            bindDistance(item)
        }

        fun bindDistance(item: NewPlace) {
            val distance = item.distance.asDistance()
            placeDistance.setTextCarryingEmpty(distance)
        }
    }

    interface Handler {
        fun itemClicked(place: NewPlace)
    }
}

object DistancePayload