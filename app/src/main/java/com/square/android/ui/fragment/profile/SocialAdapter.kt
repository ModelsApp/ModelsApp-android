package com.square.android.ui.fragment.profile

import android.graphics.drawable.Drawable
import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_profile.*
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.profile_subitem_ambassador.view.*
import kotlinx.android.synthetic.main.profile_subitem_buy_credits.view.*
import kotlinx.android.synthetic.main.profile_subitem_earn_credits.view.*
import kotlinx.android.synthetic.main.profile_subitem_plan.view.*
import kotlinx.android.synthetic.main.profile_subitem_social.view.*

class SocialAdapter(data: List<ProfileItem>, private val handler: Handler) : BaseAdapter<ProfileItem, SocialAdapter.Holder>(data) {

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

        if (position in openedItems) openedItems.remove(position) else openedItems.add(position)

        notifyItemChanged(position, OpenedPayload)
    }

    class Holder(containerView: View, var handler: Handler) : BaseHolder<ProfileItem>(containerView) {

        override fun bind(item: ProfileItem, vararg extras: Any?) {

            val openedItems = if (extras[0] == null) null else extras[0] as MutableList<Int>
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
                                is ProfileSubItems.Plan -> bindPlan(objc)
                                is ProfileSubItems.Social -> bindSocial(objc)
                                is ProfileSubItems.EarnCredits -> bindEarn(objc)
                                is ProfileSubItems.BuyCredits -> bindBuy(objc)
                                is ProfileSubItems.Ambassador -> bindAmbassador(objc)
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

        private fun bindPlan(item: ProfileSubItems.Plan): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_plan, null)

            view.planText.text = item.text
            view.planChangeBtn.text = itemsLl.context.resources.getString(R.string.change)
            view.planChangeBtn.visibility = if (item.canChange) View.VISIBLE else View.GONE
            view.planChangeBtn.setOnClickListener { handler.changePlanClicked() }

            return view
        }

        private fun bindSocial(item: ProfileSubItems.Social): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_social, null)

            val drawable: Drawable? = when (item.type) {
                SOCIAL_APP_TYPE_INSTAGRAM -> itemsLl.context.getDrawable(R.drawable.instagram_logo)
                SOCIAL_APP_TYPE_FACEBOOK -> itemsLl.context.getDrawable(R.drawable.facebook_logo)
                SOCIAL_APP_TYPE_GOOGLE -> itemsLl.context.getDrawable(R.drawable.google_logo)
                SOCIAL_APP_TYPE_TRIPADVISOR -> itemsLl.context.getDrawable(R.drawable.trip_advisor_logo)
                SOCIAL_APP_TYPE_YELP -> itemsLl.context.getDrawable(R.drawable.yelp_logo)
                else -> null
            }

            drawable?.let {
                view.socialImg.setImageDrawable(it)
            }

            val appName: String? = when (item.type) {
                SOCIAL_APP_TYPE_INSTAGRAM -> itemsLl.context.getString(R.string.instagram_name)
                SOCIAL_APP_TYPE_FACEBOOK -> itemsLl.context.getString(R.string.facebook_name)
                SOCIAL_APP_TYPE_GOOGLE -> itemsLl.context.getString(R.string.google_name)
                SOCIAL_APP_TYPE_TRIPADVISOR -> itemsLl.context.getString(R.string.trip_advisor_name)
                SOCIAL_APP_TYPE_YELP -> itemsLl.context.getString(R.string.yelp_name)
                else -> ""
            }

            view.socialTv.text = if (item.connected) itemsLl.context.getString(R.string.connected) else itemsLl.context.getString(R.string.connect_format, appName)
            view.socialTv.setTextColor(ContextCompat.getColor(itemsLl.context, if (item.connected) R.color.nice_pink else android.R.color.black))

            view.mainContainer.setOnClickListener { handler.socialConnectClicked(item.type, item.connected) }

            return view
        }

        private fun bindEarn(item: ProfileSubItems.EarnCredits): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_earn_credits, null)

            val drawable: Drawable? = when (item.type) {
                EARN_TYPE_SHARE_FRIENDS -> itemsLl.context.getDrawable(R.drawable.r_shop)
                EARN_TYPE_REFER_VENUE -> itemsLl.context.getDrawable(R.drawable.r_shop)
                EARN_TYPE_INTRODUCE_BRAND -> itemsLl.context.getDrawable(R.drawable.r_shop)
                else -> null
            }

            drawable?.let {
                view.earnCreditsIcon.setImageDrawable(it)
            }

            val title: String? = when (item.type) {
                EARN_TYPE_SHARE_FRIENDS -> itemsLl.context.getString(R.string.share_with_friend)
                EARN_TYPE_REFER_VENUE -> itemsLl.context.getString(R.string.refer_a_venue)
                EARN_TYPE_INTRODUCE_BRAND -> itemsLl.context.getString(R.string.introduce_a_brand)
                else -> ""
            }
            view.earnTitle.text = title

            view.earnCreditsValue.text = item.credits.toString()

            val btnText: String? = when (item.type) {
                EARN_TYPE_SHARE_FRIENDS -> itemsLl.context.getString(R.string.share)
                EARN_TYPE_REFER_VENUE -> itemsLl.context.getString(R.string.refer)
                EARN_TYPE_INTRODUCE_BRAND -> itemsLl.context.getString(R.string.refer)
                else -> ""
            }
            view.earnBtn.text = btnText

            view.earnBtn.setOnClickListener { handler.earnMoreClicked(item.type) }

            return view
        }

        private fun bindBuy(item: ProfileSubItems.BuyCredits): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_buy_credits, null)

            val drawable: Drawable? = when (item.type) {
                BUY_TYPE_500 -> itemsLl.context.getDrawable(R.drawable.r_shop)
                BUY_TYPE_1000 -> itemsLl.context.getDrawable(R.drawable.r_shop)
                else -> null
            }

            drawable?.let {
                view.buyCreditsIcon.setImageDrawable(it)
            }

            val title: String? = when (item.type) {
                BUY_TYPE_500 -> itemsLl.context.getString(R.string.credits_500)
                BUY_TYPE_1000 -> itemsLl.context.getString(R.string.credits_1000)
                else -> ""
            }
            view.buyTitle.text = title

            view.buyPrice.text = item.priceText
            view.buyBtn.setOnClickListener { handler.buyExtraClicked(item.type) }

            return view
        }

        private fun bindAmbassador(item: ProfileSubItems.Ambassador): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_ambassador, null)

            val drawable: Drawable? = when (item.type) {
                AMBASSADOR_TYPE_JOIN_TEAM -> itemsLl.context.getDrawable(R.drawable.r_shop)
                else -> null
            }

            drawable?.let {
                view.ambassadorIcon.setImageDrawable(it)
            }

            val title: String? = when (item.type) {
                AMBASSADOR_TYPE_JOIN_TEAM -> itemsLl.context.getString(R.string.join_our_team)
                else -> ""
            }
            view.ambassadorTitle.text = title

            view.ambassadorSmallIcon.setImageDrawable(itemsLl.context.getDrawable(item.smallIconRes))

            val btnText: String? = when (item.type) {
                AMBASSADOR_TYPE_JOIN_TEAM -> itemsLl.context.getString(R.string.apply)
                else -> ""
            }
            view.ambassadorBtn.text = btnText

            view.ambassadorBtn.setOnClickListener { handler.ambassadorClicked(item.type) }

            return view
        }

        fun bindOpened(item: ProfileItem, openedItems: MutableList<Int>?) {
            openedItems?.let {
                if (item.type == TYPE_DROPDOWN) {
                    val opened = adapterPosition in it

                    arrow.animate().rotation(if (!opened) 0f else 90f).setDuration(50).start();

                    itemsLl.visibility = if (opened) View.VISIBLE else View.GONE
                }
            }
        }
    }

    interface Handler {
        fun clickViewClicked(position: Int)
        fun changePlanClicked()
        fun socialConnectClicked(type: Int, isConnected: Boolean)
        fun earnMoreClicked(type: Int)
        fun buyExtraClicked(type: Int)
        fun ambassadorClicked(type: Int)
    }

    object OpenedPayload
}