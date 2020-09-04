package com.square.android.ui.fragment.agenda.schedule

import android.view.View
import com.square.android.R
import com.square.android.extensions.drawableFromRes
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_schedule_availability.*

class ScheduleAvailabilityAdapter(data: List<String>, var selectedItemPosition: Int? = null, private val handler: Handler?) : BaseAdapter<String, ScheduleAvailabilityAdapter.ViewHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_schedule_availability

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

    class ViewHolder(containerView: View, var handler: Handler?) : BaseHolder<String>(containerView) {

        override fun bind(item: String, vararg extras: Any? ) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            itemLabel.text = item

            itemContainer.setOnClickListener {
                handler?.itemClicked(adapterPosition)
            }

            bindSelected(item, selectedPosition)
        }

        fun bindSelected(item: String, selectedPosition: Int?) {
            val isChecked = selectedPosition == adapterPosition

            itemIc.drawableFromRes(if(isChecked) R.drawable.radio_btn_checked else R.drawable.radio_btn_unchecked)
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}