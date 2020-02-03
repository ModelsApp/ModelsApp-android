package com.square.android.ui.fragment.profile

import android.content.res.ColorStateList
import android.text.*
import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_profile.*
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_profile_balance.*
import kotlinx.android.synthetic.main.item_profile_button.*

const val ITEM_PROFILE = 0
const val ITEM_BUTTON = 1
const val ITEM_BALANCE = 2

class ProfileItemAdapter(data: List<ProfileItem>) : BaseAdapter<ProfileItem, ProfileItemAdapter.Holder>(data) {

    var openedItems: MutableList<Int> = mutableListOf()

    override fun getViewType(position: Int) =
            when(data[position].type){
                TYPE_BUTTON -> ITEM_BUTTON
                TYPE_CUSTOM ->{
                    // TODO change when there's more custom items
                    ITEM_BALANCE
                }
                else -> ITEM_PROFILE
            }

    override fun getLayoutId(viewType: Int) = when(viewType){
        ITEM_PROFILE -> R.layout.item_profile

        ITEM_BUTTON -> R.layout.item_profile_button

        ITEM_BALANCE -> R.layout.item_profile_balance

        else -> R.layout.item_profile
    }

    override fun instantiateHolder(view: View): Holder = Holder(view)

    override fun getItemCount() = data.size

    override fun bindHolder(holder: Holder, position: Int) {
        holder.bind(data[position], openedItems)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: Holder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
    }

    class Holder(containerView: View) : BaseHolder<ProfileItem>(containerView) {

        override fun bind(item: ProfileItem, vararg extras: Any?) {

            if(item.type == TYPE_BUTTON){
                bindButton(item)
            }
            else if(item.type == TYPE_CUSTOM){
                when(item.customType){
                    CUSTOM_TYPE_BALANCE -> bindBalance(item)
                }
            }
            else{
                clickView.setOnClickListener {
                    item.onClick.invoke()
                }

                arrow.visibility = if (item.type == TYPE_ARROW) View.VISIBLE else View.GONE
                tv.visibility = if(!TextUtils.isEmpty(item.subText) && item.type != TYPE_ADD) View.VISIBLE else View.GONE
                divider.visibility = if(item.dividerVisible) View.VISIBLE else View.GONE
                addBtn.visibility = if (item.type == TYPE_ADD) View.VISIBLE else View.GONE

                title.text = item.title

                tv.text = if (!TextUtils.isEmpty(item.subText)) item.subText else ""
                tv.setTextColor(ContextCompat.getColor(tv.context, item.subTextColor))

                arrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(arrow.context, item.arrowTint))

                addBtn.setOnClickListener {
                    item.onAdd.invoke()
                }

                item.iconRes?.let {
                    icon.visibility = View.VISIBLE
                    icon.setImageDrawable(icon.context.getDrawable(it))

                } ?: run{ icon.visibility = View.GONE }
            }
        }

        private fun bindBalance(item: ProfileItem){
            balanceText.text = item.title
        }

        private fun bindButton(item: ProfileItem){
            itemProfileButton.text = item.title

            itemProfileButton.setOnClickListener {
                item.onClick.invoke()
            }
        }
    }
}