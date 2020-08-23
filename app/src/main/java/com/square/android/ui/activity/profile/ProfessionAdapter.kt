package com.square.android.ui.activity.profile

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.Profession
import com.square.android.extensions.drawableFromRes
import com.square.android.extensions.setVisible
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_checkable_primary.*

class ProfessionAdapter(data: List<Profession>, var selectedItems: MutableList<Profession> = mutableListOf(), var primaryPosition: Int, private val handler: Handler?): BaseAdapter<Profession, ProfessionAdapter.Holder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_checkable_primary

    override fun instantiateHolder(view: View): Holder {
        return Holder(view, handler, data, selectedItems)
    }

    fun notifyChanged(position: Int, isSetToPrimary: Boolean) {
        if(isSetToPrimary && position != primaryPosition && primaryPosition > -1){
            val previousPrimary = primaryPosition

            primaryPosition = position

            notifyItemChanged(previousPrimary, SelectedPayloadProfession)
            notifyItemChanged(primaryPosition, SelectedPayloadProfession)
        } else if (isSetToPrimary){
            primaryPosition = position

            notifyItemChanged(primaryPosition, SelectedPayloadProfession)
        } else{
            if(primaryPosition == position){
                primaryPosition = -1
            }
            notifyItemChanged(position, SelectedPayloadProfession)
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
                is SelectedPayloadProfession -> holder.bindSelected(selectedItems, primaryPosition)
            }
        }
    }

    override fun bindHolder(holder: Holder, position: Int) {
        holder.bind(data[position], primaryPosition)
    }

    class Holder(view: View, private val handler: Handler?, var data: List<Profession>, var selectedItems: MutableList<Profession>) : BaseAdapter.BaseHolder<Profession>(view) {

        override fun bind(item: Profession, vararg extras: Any?) {
            val primaryPosition = if(extras[0] == null) -1 else extras[0] as Int

            itemContainer.setOnClickListener { handler?.itemClicked(adapterPosition) }

            itemImg.drawableFromRes(when(item.name){
                //TODO change icons
                "Actor" -> R.drawable.r_address
                "Athlete" -> R.drawable.r_address
                "Art/Creative director" -> R.drawable.r_address
                "Artist" -> R.drawable.r_address
                "Blogger" -> R.drawable.r_address
                "Casting Director" -> R.drawable.r_address
                "Chef" -> R.drawable.r_address
                "Content creator" -> R.drawable.r_address
                "Dancer" -> R.drawable.r_address
                "Deejay" -> R.drawable.r_address
                "Designer" -> R.drawable.r_address
                "E-gamer" -> R.drawable.r_address
                "Fashion stylist" -> R.drawable.r_address
                "Hair stylist" -> R.drawable.r_address
                "Influencer" -> R.drawable.r_address
                "Journalist" -> R.drawable.r_address
                "Make-up artist" -> R.drawable.r_address
                "Model" -> R.drawable.r_address
                "Musician" -> R.drawable.r_address
                "Performer" -> R.drawable.r_address
                "Photographer" -> R.drawable.r_address
                "Producer" -> R.drawable.r_address
                "Retoucher/Post Producer" -> R.drawable.r_address
                "Videographer" -> R.drawable.r_address
                "Other" -> R.drawable.r_address
                else -> R.drawable.r_address
            })

            itemLabel.text = item.name

            bindSelected(selectedItems, primaryPosition)
        }

        fun bindSelected(selectedItems: MutableList<Profession>, primaryPosition: Int) {
            if(primaryPosition == adapterPosition){
                itemIc.drawableFromRes(R.drawable.checkmark)
                itemPrimaryLabel.setVisible(true)
            } else{
                itemPrimaryLabel.setVisible(false)

                val isChecked = selectedItems.map { it.id }.contains(data[adapterPosition].id)

                itemIc.drawableFromRes(if(isChecked) R.drawable.radio_btn_checked else R.drawable.radio_btn_unchecked)
            }
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}

object SelectedPayloadProfession