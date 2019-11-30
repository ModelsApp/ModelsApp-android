package com.square.android.ui.fragment.offersList

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.RedemptionFull
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.select_offer_card.*

class OffersListAdapter(data: List<OfferInfo>,
                        private val handler: Handler?, private var redemptionFull: RedemptionFull?) : BaseAdapter<OfferInfo, OffersListAdapter.OfferHolder>(data) {
    private var selectedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.select_offer_card

    override fun getItemCount() = data.size

    override fun bindHolder(holder: OfferHolder, position: Int) {
        holder.bind(data[position], selectedItemPosition)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: OfferHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is SelectedPayload }
                .forEach { holder.bindSelected(selectedItemPosition) }
    }

    fun setSelectedItem(position: Int?) {
        if (position == null) return

        val previousPosition = selectedItemPosition
        selectedItemPosition = position

        previousPosition?.let { notifyItemChanged(it, SelectedPayload) }

        notifyItemChanged(position)
    }

    override fun instantiateHolder(view: View): OfferHolder = OfferHolder(view, handler, redemptionFull)

    class OfferHolder(containerView: View,
                      private var handler: Handler?,
                      private var redemptionFull: RedemptionFull?
    ): BaseHolder<OfferInfo>(containerView) {

        override fun bind(item: OfferInfo, vararg extras: Any?) {
            val selectedPosition = extras.first() as Int?

            bindSelected(selectedPosition)

            containerView.setOnClickListener {
                if(item.isAvailable){
                    handler?.itemClicked(adapterPosition)
                }
            }

            item.isAvailable.run {

                redemptionFull?.let {
                    offerAvailableText.text = if (!this)
                        offerAvailableText.context.getString(R.string.not_available_from_to, it.redemption.startTime, it.redemption.endTime)
                    else null
                }

                notAvailableRl.visibility = if(!this) View.VISIBLE else View.GONE
            }

            offerTitle.text = item.name
            offerPrice.text = item.price.toString()

            offerImage.loadImage(url = (item.mainImage ?: item.photo)?: "", roundedCornersRadiusPx = 10)
        }

        fun bindSelected(selectedPosition: Int?) {
            checkedOutline.visibility = if(selectedPosition == adapterPosition) View.VISIBLE else View.GONE
            checkedImv.visibility = if(selectedPosition == adapterPosition) View.VISIBLE else View.GONE
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}