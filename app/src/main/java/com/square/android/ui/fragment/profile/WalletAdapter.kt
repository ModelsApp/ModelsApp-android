package com.square.android.ui.fragment.profile

import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_profile.*
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.profile_subitem_create.view.*

class WalletAdapter(data: List<ProfileItem>, private val handler: Handler) : BaseAdapter<ProfileItem, WalletAdapter.Holder>(data) {

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

    class Holder(containerView: View, var handler: Handler) : BaseHolder<ProfileItem>(containerView) {

        override fun bind(item: ProfileItem, vararg extras: Any? ) {
            val openedItems = if(extras[0] == null) null else extras[0] as MutableList<Int>

            bindOpened(item, openedItems)

            clickView.visibility = if (item.type == TYPE_DROPDOWN) View.VISIBLE else View.GONE
            arrow.visibility = if (item.type == TYPE_DROPDOWN) View.VISIBLE else View.GONE
            tv.visibility = if (item.type == TYPE_PLAIN) View.VISIBLE else View.GONE

            clickView.setOnClickListener { handler.clickViewClicked(adapterPosition) }

            icon.setImageDrawable(icon.context.getDrawable(item.iconRes))
            title.text = item.title

            item.rightIconRes?.let {
                rightIcon.visibility = View.VISIBLE

                rightIcon.setImageDrawable(itemsLl.context.getDrawable(it))

                (title.layoutParams as ConstraintLayout.LayoutParams).also { layoutParams ->
                    layoutParams.marginEnd = itemsLl.context.resources.getDimensionPixelSize(R.dimen.item_profile_icon_size)
                }

            } ?: run {
                rightIcon.visibility = View.GONE
                (title.layoutParams as ConstraintLayout.LayoutParams).also { layoutParams ->
                    layoutParams.marginEnd = itemsLl.context.resources.getDimensionPixelSize(R.dimen.item_profile_subicon_size)
                }
            }

            itemsLl.removeAllViews()

            if (item.type == TYPE_PLAIN) {
                tv.text = item.textValue
            } else if (item.type == TYPE_DROPDOWN) {
                if (itemsLl.childCount <= 0) {
                    item.subItems?.let {
                        val subItems: MutableList<View> = mutableListOf()

                        for(objc in it){
                            val subItem: View? = when (objc) {
                                is ProfileSubItems.Create -> bindCreate(objc)
                                // TODO
                                else -> null
                            }

                            subItem?.let{ si ->
                                subItems.add(si)
                            }
                        }

                        if (!subItems.isNullOrEmpty()) {
                            for (subItem in subItems) {
                                itemsLl.addView(subItem)
                            }
                        }
                    }
                }
            }
        }

        private fun bindCreate(item: ProfileSubItems.Create): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_create, null)
            view.createClickView.setOnClickListener { handler.createClicked(item.clickedType) }

            return view
        }

        fun bindOpened(item: ProfileItem, openedItems: MutableList<Int>?) {
            openedItems?.let {
                if(item.type == TYPE_DROPDOWN){
                    val opened = adapterPosition in it

                    arrow.animate().rotation( if(!opened) 0f else 90f).setDuration(50).start();

                    itemsLl.visibility = if(opened) View.VISIBLE else View.GONE
                }
            }
        }
    }

    interface Handler {
        fun clickViewClicked(position: Int)
        fun createClicked(clickedType: Int)
    }

    object OpenedPayload
}