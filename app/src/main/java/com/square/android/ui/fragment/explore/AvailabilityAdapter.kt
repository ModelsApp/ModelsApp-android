package com.square.android.ui.fragment.explore

import android.view.View
import androidx.annotation.DrawableRes
import com.square.android.R
import com.square.android.extensions.drawableFromRes
import com.square.android.extensions.tintFromRes
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_availability.*

class AvailabilityItem(var title:String, @DrawableRes var iconRes: Int)

class AvailabilityAdapter(data: List<AvailabilityItem>, var selectedItemPosition: Int? = null, private val handler: Handler?) : BaseAdapter<AvailabilityItem, AvailabilityAdapter.ViewHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_availability

    override fun getItemCount() = data.size

    override fun bindHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], selectedItemPosition)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
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

    override fun instantiateHolder(view: View): ViewHolder = ViewHolder(view, handler)

    class ViewHolder(containerView: View,
                     var handler: Handler?) : BaseHolder<AvailabilityItem>(containerView) {

        override fun bind(item: AvailabilityItem, vararg extras: Any? ) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            availabilityContainer.setOnClickListener { handler?.itemClicked(adapterPosition) }

            availabilityName.text = item.title

            availabilityIc.drawableFromRes(item.iconRes)

            bindSelected(item, selectedPosition)
        }

        fun bindSelected(item: AvailabilityItem,selectedPosition: Int?) {
            availabilityContainer.isChecked = (selectedPosition == adapterPosition)
            availabilityName.isChecked = (selectedPosition == adapterPosition)

            availabilityIc.tintFromRes(if (selectedPosition == adapterPosition) android.R.color.black else R.color.gray_disabled)
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}