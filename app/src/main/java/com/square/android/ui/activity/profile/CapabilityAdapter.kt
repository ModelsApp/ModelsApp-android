package com.square.android.ui.activity.profile

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.UserCapability
import com.square.android.extensions.drawableFromRes
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_capability.*

class CapabilityAdapter(data: List<UserCapability>, var selectedItems: MutableList<String> = mutableListOf(), private val handler: Handler?): BaseAdapter<UserCapability, CapabilityAdapter.Holder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_capability

    override fun instantiateHolder(view: View): Holder {
        return Holder(view, handler, data, selectedItems)
    }

    fun notifyChanged(position: Int) {
        notifyItemChanged(position, SelectedPayload)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: Holder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.forEach {
            when (it) {
                is SelectedPayload -> holder.bindSelected(selectedItems)
            }
        }
    }

    override fun bindHolder(holder: Holder, position: Int) {
        holder.bind(data[position])
    }

    class Holder(view: View, private val handler: Handler?, var data: List<UserCapability>, var selectedItems: MutableList<String>) : BaseAdapter.BaseHolder<UserCapability>(view) {

        override fun bind(item: UserCapability, vararg extras: Any?) {
            itemContainer.setOnClickListener { handler?.itemClicked(adapterPosition) }

            itemImg.drawableFromRes(when(item.name){
                //TODO change icons
                "Art direction" -> R.drawable.r_address
                "Copy writing" -> R.drawable.r_address
                "Editing" -> R.drawable.r_address
                "Graphic design" -> R.drawable.r_address
                "Photo" -> R.drawable.r_address
                "Video" -> R.drawable.r_address
                else -> 0
            })

            itemLabel.text = item.name

            bindSelected(selectedItems)
        }

        fun bindSelected(selectedItems: MutableList<String>) {
            val isChecked = selectedItems.contains(data[adapterPosition].name)

            itemIc.drawableFromRes(if(isChecked) R.drawable.radio_btn_checked else R.drawable.radio_btn_unchecked)
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}

object SelectedPayload