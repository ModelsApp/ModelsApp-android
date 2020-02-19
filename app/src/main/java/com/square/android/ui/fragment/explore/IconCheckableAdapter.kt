package com.square.android.ui.fragment.explore

import android.view.View
import com.square.android.R
import com.square.android.extensions.tintFromRes
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_rounded_checkable_value.*

class IconItem(var title:String, var value: String?)

class IconCheckableAdapter(data: List<IconItem>, var selectedItemPosition: Int? = null, private val handler: Handler?) : BaseAdapter<IconItem, IconCheckableAdapter.ViewHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_rounded_checkable_value

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
                     var handler: Handler?) : BaseHolder<IconItem>(containerView) {

        override fun bind(item: IconItem, vararg extras: Any? ) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            roundedCheckableContainer.setOnClickListener { handler?.itemClicked(adapterPosition) }

            roundedCheckableName.text = item.title

            roundedCheckableIc.visibility = if(item.value != null) View.VISIBLE else View.GONE
            roundedCheckableSecondaryName.visibility = if(item.value != null) View.VISIBLE else View.GONE

            roundedCheckableSecondaryName.text = item.value

            bindSelected(item, selectedPosition)
        }

        fun bindSelected(item: IconItem,selectedPosition: Int?) {
            roundedCheckableContainer.isChecked = (selectedPosition == adapterPosition)
            roundedCheckableName.isChecked = (selectedPosition == adapterPosition)
            roundedCheckableSecondaryName.isChecked = (selectedPosition == adapterPosition)

            roundedCheckableIc.tintFromRes(if (selectedPosition == adapterPosition) android.R.color.black else R.color.gray_disabled)
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}