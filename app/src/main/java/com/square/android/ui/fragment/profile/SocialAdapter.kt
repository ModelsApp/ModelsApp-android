package com.square.android.ui.fragment.profile

import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_profile.*

class SocialAdapter(data: List<SocialItem>, private val handler: Handler) : BaseAdapter<SocialItem, SocialAdapter.Holder>(data) {

    var openedItems: MutableList<Int> = mutableListOf()

    override fun getLayoutId(viewType: Int) = R.layout.item_profile

    override fun instantiateHolder(view: View): Holder = Holder(view, handler)

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

        payloads.filter { it is OpenedPayload }
                .forEach {
                    holder.bindOpened(data[position], openedItems)
                }
    }

    fun setOpenedItem(position: Int?) {
        if (position == null) return

        if(position in openedItems) openedItems.remove(position) else openedItems.add(position)

        notifyItemChanged(position, OpenedPayload)
    }

    class Holder(containerView: View, var handler: Handler) : BaseHolder<SocialItem>(containerView) {

        override fun bind(item: SocialItem, vararg extras: Any? ) {
            val openedItems = if(extras[0] == null) null else extras[0] as MutableList<Int>

            bindOpened(item, openedItems)

            clickView.visibility = if(item.type == SOCIAL_ITEM_TYPE_DROPDOWN) View.VISIBLE else View.GONE

            clickView.setOnClickListener { handler.clickViewClicked(adapterPosition) }

            if(item.type == SOCIAL_ITEM_TYPE_PLAIN){
                //TODO fill fields

            } else if(item.type == SOCIAL_ITEM_TYPE_DROPDOWN){
                //TODO populate itemsLl based on subItems type, add appropriate handler click events
                // active plan, social, earn credits, buy credits, ambassador
            }
        }

        fun bindOpened(item: SocialItem, openedItems: MutableList<Int>?) {
            if(item.type == SOCIAL_ITEM_TYPE_DROPDOWN){
                openedItems?.let {
                    //TODO change arrow direction(bottom if opened, right if not opened) - ANIMATE

                    itemsLl.visibility = if(adapterPosition in it) View.VISIBLE else View.GONE
                }
            }
        }
    }

    interface Handler {
        fun clickViewClicked(position: Int)
        fun changePlanClicked()
        fun socialConnectClicked(type: Int)
        fun earnMoreClicked(type: Int)
        fun buyExtraClicked(type: Int)
        fun ambassadorClicked(type: Int)
    }

    object OpenedPayload
}