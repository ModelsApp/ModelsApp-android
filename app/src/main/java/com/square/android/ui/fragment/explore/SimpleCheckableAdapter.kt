package com.square.android.ui.fragment.explore

import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_rounded_checkable.*

class SimpleCheckableAdapter(override var data: MutableList<String>, var selectedItemPosition: Int? = null, private val handler: Handler?) : BaseAdapter<String, SimpleCheckableAdapter.ViewHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_rounded_checkable

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

    fun setSelectedItem1(item0Name: String) {
        data[0] = item0Name

        selectedItemPosition = 1

        notifyItemChanged(0)

        notifyItemChanged(1, SelectedPayload)
    }

    override fun instantiateHolder(view: View): ViewHolder = ViewHolder(view, handler)

    class ViewHolder(containerView: View,
                     var handler: Handler?) : BaseHolder<String>(containerView) {

        override fun bind(item: String, vararg extras: Any? ) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            roundedCheckableContainer.setOnClickListener { handler?.itemClicked(adapterPosition) }

            roundedCheckableContainer.setOnLongClickListener {
                handler?.itemLongClicked(adapterPosition)
                true
            }

            roundedCheckableName.text = item

            bindSelected(item, selectedPosition)
        }

        fun bindSelected(item: String, selectedPosition: Int?) {
            roundedCheckableContainer.isChecked = (selectedPosition == adapterPosition)
            roundedCheckableName.isChecked = (selectedPosition == adapterPosition)
        }
    }

    interface Handler {
        fun itemClicked(position: Int)

        fun itemLongClicked(position: Int)
    }

    object SelectedPayload
}