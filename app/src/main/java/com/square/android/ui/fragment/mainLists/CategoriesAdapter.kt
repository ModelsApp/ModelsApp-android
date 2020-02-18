package com.square.android.ui.fragment.mainLists

import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_rounded_checkable.*

class CategoriesAdapter(data: List<String>, var selectedItems: MutableList<Int> = mutableListOf(), private val handler: Handler?): BaseAdapter<String, CategoriesAdapter.Holder>(data) {
    override fun getLayoutId(viewType: Int) = R.layout.item_rounded_checkable

    override fun instantiateHolder(view: View): Holder {
        return Holder(view, handler, selectedItems)
    }

    fun changeSelection(position: Int) {
        if(position in selectedItems){
            selectedItems.remove(position)
        } else{
            selectedItems.add(position)
        }

        notifyItemChanged(position, SelectedPayload)
    }

    fun clearSelections(){
        val posToClear: MutableList<Int> = mutableListOf()

        for (pos in selectedItems){
            posToClear.add(pos)
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

    class Holder(view: View, private val handler: Handler?, var selectedItems: MutableList<Int>) : BaseAdapter.BaseHolder<String>(view) {
        override fun bind(item: String, vararg extras: Any?) {

            roundedCheckableContainer.setOnClickListener { handler?.itemClicked(adapterPosition) }

            roundedCheckableName.text = item

            bindSelected(selectedItems)
        }

        fun bindSelected(selectedItems: MutableList<Int>) {
            roundedCheckableContainer.isChecked = selectedItems.contains(adapterPosition)
            roundedCheckableName.isChecked = selectedItems.contains(adapterPosition)
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}

object SelectedPayload