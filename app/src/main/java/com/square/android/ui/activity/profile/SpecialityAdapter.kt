package com.square.android.ui.activity.profile

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.Speciality
import com.square.android.extensions.drawableFromRes
import com.square.android.extensions.setVisible
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_checkable_primary.*

class SpecialityAdapter(data: List<Speciality>, var selectedItems: MutableList<Speciality> = mutableListOf(), var primaryPosition: Int, private val handler: Handler?): BaseAdapter<Speciality, SpecialityAdapter.Holder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_checkable_primary

    override fun instantiateHolder(view: View): Holder {
        return Holder(view, handler, data, selectedItems)
    }

    fun notifyChanged(position: Int, isSetToPrimary: Boolean) {
        if(isSetToPrimary && position != primaryPosition && primaryPosition > -1){
            val previousPrimary = primaryPosition

            primaryPosition = position

            notifyItemChanged(previousPrimary, SelectedPayloadSpeciality)
            notifyItemChanged(primaryPosition, SelectedPayloadSpeciality)
        } else if (isSetToPrimary){
            primaryPosition = position

            notifyItemChanged(primaryPosition, SelectedPayloadSpeciality)
        } else{
            if(primaryPosition == position){
                primaryPosition = -1
            }
            notifyItemChanged(position, SelectedPayloadSpeciality)
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
                is SelectedPayloadSpeciality -> holder.bindSelected(selectedItems, primaryPosition)
            }
        }
    }

    override fun bindHolder(holder: Holder, position: Int) {
        holder.bind(data[position], primaryPosition)
    }

    class Holder(view: View, private val handler: Handler?, var data: List<Speciality>, var selectedItems: MutableList<Speciality>) : BaseAdapter.BaseHolder<Speciality>(view) {

        override fun bind(item: Speciality, vararg extras: Any?) {
            val primaryPosition = if(extras[0] == null) -1 else extras[0] as Int

            itemContainer.setOnClickListener { handler?.itemClicked(adapterPosition) }

            itemImg.drawableFromRes(when(item.name){
                //TODO change icons
                "Art & Design" -> R.drawable.r_address
                "Beauty & Cosmetics" -> R.drawable.r_address
                "Entertainment", "Entertaiment" -> R.drawable.r_address
                "Fashion" -> R.drawable.r_address
                "Food & Beverage" -> R.drawable.r_address
                "Gaming" -> R.drawable.r_address
                "Health & Wellness" -> R.drawable.r_address
                "Luxury" -> R.drawable.r_address
                "Photography" -> R.drawable.r_address
                "Sport & Fitness" -> R.drawable.r_address
                "Technology" -> R.drawable.r_address
                "Travel & Lifestyle" -> R.drawable.r_address
                else -> R.drawable.r_address
            })

            itemLabel.text = item.name

            bindSelected(selectedItems, primaryPosition)
        }

        fun bindSelected(selectedItems: MutableList<Speciality>, primaryPosition: Int) {
            if(primaryPosition == adapterPosition){
                itemIc.drawableFromRes(R.drawable.checkmark)
                itemPrimaryLabel.setVisible(true)
            } else{
                itemPrimaryLabel.setVisible(false)

                val isChecked = selectedItems.contains(data[adapterPosition])

                itemIc.drawableFromRes(if(isChecked) R.drawable.radio_btn_checked else R.drawable.radio_btn_unchecked)
            }
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}

object SelectedPayloadSpeciality