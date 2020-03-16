package com.square.android.utils

import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.spinner_dropdown_radio_button.*

class MultipleSelectionAdapter(data: List<String>, var selectedItems: MutableList<String> = mutableListOf(), private val handler: Handler?): BaseAdapter<String, MultipleSelectionAdapter.Holder>(data) {
    override fun getLayoutId(viewType: Int) = R.layout.spinner_dropdown_radio_button

    override fun instantiateHolder(view: View): Holder {
        return Holder(view, handler, data, selectedItems)
    }

    fun changeSelection(position: Int) {
        if(data[position] in selectedItems){
            selectedItems.remove(data[position])
        } else{
            selectedItems.add(data[position])
        }

        notifyItemChanged(position, SelectedPayload)
    }

    fun clearSelections(){
        val posToClear: MutableList<Int> = mutableListOf()

        for (item in selectedItems){
            posToClear.add(data.indexOf(item))
        }

        selectedItems.clear()

        for (pos in posToClear){
            notifyItemChanged(pos, SelectedPayload)
        }
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

    class Holder(view: View, private val handler: Handler?, var data: List<String>, var selectedItems: MutableList<String>) : BaseAdapter.BaseHolder<String>(view) {
        override fun bind(item: String, vararg extras: Any?) {

            container.setOnClickListener { handler?.itemClicked(adapterPosition) }

            itemTv.text = item

            bindSelected(selectedItems)
        }

        fun bindSelected(selectedItems: MutableList<String>) {
            container.isChecked = selectedItems.contains(data[adapterPosition])
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}

object SelectedPayload