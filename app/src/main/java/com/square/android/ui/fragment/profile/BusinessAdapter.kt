package com.square.android.ui.fragment.profile

import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_profile.*
import android.view.LayoutInflater

class BusinessAdapter(data: List<ProfileItem>, private val handler: Handler) : BaseAdapter<ProfileItem, BusinessAdapter.Holder>(data) {

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

            clickView.visibility = if(item.type == TYPE_DROPDOWN) View.VISIBLE else View.GONE
            arrow.visibility = if(item.type == TYPE_DROPDOWN) View.VISIBLE else View.GONE
            tv.visibility = if(item.type == TYPE_PLAIN) View.VISIBLE else View.GONE

            clickView.setOnClickListener { handler.clickViewClicked(adapterPosition) }

            icon.setImageDrawable(icon.context.getDrawable(item.iconRes))
            title.text = item.title

            if(item.type == TYPE_PLAIN){
                tv.text = item.textValue
            } else if(item.type == TYPE_DROPDOWN){
                if (itemsLl.childCount <= 0){
                    item.subItems?.let {
                        val subItems: MutableList<View>? = when(item.subItems?.firstOrNull()){
                            is Detail -> bindDetail(item.subItems!!.filterIsInstance<Detail>())
//                          TODO more
                            else -> null
                        }
                        if(!subItems.isNullOrEmpty()){
                            for(subItem in subItems){
                                itemsLl.addView(subItem)
                            }
                        }
                    }
                }
            }
        }

        private fun bindDetail(items: List<Detail>): MutableList<View>? {
            val list: MutableList<View> = mutableListOf()

            for(item in items){
//                val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_detail, null)

                //TODO

//                list.add(view)
            }

            return list
        }

//        private fun bindSocial(items: List<Social>): MutableList<View>?{
//            val list: MutableList<View> = mutableListOf()
//
//            for(item in items){
//                val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_social, null)
//
//                val drawable: Drawable? = when (item.type){
//                    SOCIAL_APP_TYPE_INSTAGRAM -> itemsLl.context.getDrawable(R.drawable.instagram_logo)
//                    SOCIAL_APP_TYPE_FACEBOOK -> itemsLl.context.getDrawable(R.drawable.facebook_logo)
//                    SOCIAL_APP_TYPE_GOOGLE -> itemsLl.context.getDrawable(R.drawable.google_logo)
//                    SOCIAL_APP_TYPE_TRIPADVISOR -> itemsLl.context.getDrawable(R.drawable.trip_advisor_logo)
//                    SOCIAL_APP_TYPE_YELP -> itemsLl.context.getDrawable(R.drawable.yelp_logo)
//                    else -> null
//                }
//
//                drawable?.let {
//                    view.socialImg.setImageDrawable(it)
//                }
//
//                val appName: String? = when (item.type){
//                    SOCIAL_APP_TYPE_INSTAGRAM -> itemsLl.context.getString(R.string.instagram_name)
//                    SOCIAL_APP_TYPE_FACEBOOK -> itemsLl.context.getString(R.string.facebook_name)
//                    SOCIAL_APP_TYPE_GOOGLE -> itemsLl.context.getString(R.string.google_name)
//                    SOCIAL_APP_TYPE_TRIPADVISOR -> itemsLl.context.getString(R.string.trip_advisor_name)
//                    SOCIAL_APP_TYPE_YELP -> itemsLl.context.getString(R.string.yelp_name)
//                    else -> ""
//                }
//
//                view.socialTv.text = if(item.connected) itemsLl.context.getString(R.string.connected) else itemsLl.context.getString(R.string.connect_format, appName)
//                view.socialTv.setTextColor(ContextCompat.getColor(itemsLl.context, if(item.connected) R.color.nice_pink else android.R.color.black))
//
//                view.mainContainer.setOnClickListener { handler.socialConnectClicked(item.type, item.connected) }
//
//                list.add(view)
//            }
//
//            return list
//        }

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
    }

    object OpenedPayload
}