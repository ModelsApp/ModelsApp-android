package com.square.android.ui.fragment.profile

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_profile.*
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.square.android.utils.CustomTypefaceSpan
import kotlinx.android.synthetic.main.item_profile_balance.*
import kotlinx.android.synthetic.main.item_profile_button.*
import kotlinx.android.synthetic.main.profile_subitem_agency.view.*
import kotlinx.android.synthetic.main.profile_subitem_ambassador.view.*
import kotlinx.android.synthetic.main.profile_subitem_bank_account.view.*
import kotlinx.android.synthetic.main.profile_subitem_buy_credits.view.*
import kotlinx.android.synthetic.main.profile_subitem_comp_card.view.*
import kotlinx.android.synthetic.main.profile_subitem_create.view.*
import kotlinx.android.synthetic.main.profile_subitem_detail.view.*
import kotlinx.android.synthetic.main.profile_subitem_earn_credits.view.*
import kotlinx.android.synthetic.main.profile_subitem_models_com.view.*
import kotlinx.android.synthetic.main.profile_subitem_plan.view.*
import kotlinx.android.synthetic.main.profile_subitem_polaroid.view.*
import kotlinx.android.synthetic.main.profile_subitem_portfolio.view.*
import kotlinx.android.synthetic.main.profile_subitem_preference.view.*
import kotlinx.android.synthetic.main.profile_subitem_social.view.*

const val ITEM_PROFILE = 0
const val ITEM_BUTTON = 1
const val ITEM_BALANCE = 2

class ProfileItemAdapter(data: List<ProfileItem>, private val handler: Handler, private val socialHandler: SocialHandler? = null,
                         private var businessHandler: BusinessHandler? = null, private var walletHandler: WalletHandler? = null) : BaseAdapter<ProfileItem, ProfileItemAdapter.Holder>(data) {

    var openedItems: MutableList<Int> = mutableListOf()

    override fun getViewType(position: Int) =
            when(data[position].type){
                TYPE_BUTTON -> ITEM_BUTTON
                TYPE_CUSTOM ->{
                    // TODO change when more custom items
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

    override fun instantiateHolder(view: View): Holder = Holder(view, handler, socialHandler, businessHandler, walletHandler)

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

    class Holder(containerView: View, var handler: Handler, var socialHandler: SocialHandler?,
                 var businessHandler: BusinessHandler?, var walletHandler: WalletHandler?) : BaseHolder<ProfileItem>(containerView) {

        override fun bind(item: ProfileItem, vararg extras: Any?) {
            val openedItems = if (extras[0] == null) null else extras[0] as MutableList<Int>

            bindOpened(item, openedItems)

            if(item.type == TYPE_BUTTON){

                bindButton(item)
            }
            else if(item.type == TYPE_CUSTOM){
                when(item.customType){
                    CUSTOM_TYPE_BALANCE -> bindBalance(item)
                }
            }
            else{
                clickView.visibility = if (item.type == TYPE_DROPDOWN) View.VISIBLE else View.INVISIBLE
                arrow.visibility = if (item.type == TYPE_DROPDOWN) View.VISIBLE else View.GONE
                tv.visibility = if (item.type == TYPE_PLAIN) View.VISIBLE else View.GONE

                divider.visibility = if(item.dividerVisible) View.VISIBLE else View.GONE

                addBtn.visibility = if (item.type == TYPE_ADD) View.VISIBLE else View.GONE

                addBtn.setOnClickListener {
                    when (item.addType) {
                        ADD_TYPE_BANK_ACCOUNT -> walletHandler?.addBankAccountClicked()
                        ADD_TYPE_PAYPAL_ACCOUNT -> walletHandler?.addPaypalAccountClicked()
                    }
                }

                item.arrowTint?.let {
                    arrow.imageTintList =  ColorStateList.valueOf(ContextCompat.getColor(arrow.context, it))
                } ?: run{ arrow.imageTintList =  ColorStateList.valueOf(ContextCompat.getColor(arrow.context, android.R.color.black))}

                clickView.setOnClickListener { handler.clickViewClicked(adapterPosition) }

                if(item.subIconRes == null){
                    if(item.subText == null){
                        tv.setTextColor(ContextCompat.getColor(itemsLl.context,  R.color.nice_pink))
                    } else{
                        tv.setTextColor(ContextCompat.getColor(itemsLl.context,  R.color.gray_hint_light))
                    }
                } else{
                    tv.setTextColor(ContextCompat.getColor(itemsLl.context,  R.color.status_yellow))
                }

                item.iconRes?.let {
                    icon.visibility = View.VISIBLE
                    icon.setImageDrawable(icon.context.getDrawable(it))

                } ?: run{ icon.visibility = View.GONE}

                title.text = item.title

                item.subIconRes?.let {
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
                } else if (item.type == TYPE_DROPDOWN || item.type == TYPE_ADD) {

                    tv.text = if (!TextUtils.isEmpty(item.subText)) item.subText else ""
                    tv.visibility = if (!TextUtils.isEmpty(item.subText)) View.VISIBLE else View.GONE

                    if (itemsLl.childCount <= 0) {
                        item.subItems?.let {
                            val subItems: MutableList<View> = mutableListOf()

                            for(objc in it){
                                val subItem: View? = when (objc) {
                                    is ProfileSubItems.Create -> bindCreate(objc)
                                    // Social
                                    is ProfileSubItems.Plan -> bindPlan(objc)
                                    is ProfileSubItems.Social -> bindSocial(objc)
                                    is ProfileSubItems.YourActivity -> bindYourActivity(objc)
                                    is ProfileSubItems.Specialities -> bindSpecialities(objc)
                                    is ProfileSubItems.Capabilities -> bindCapabilities(objc)

                                    //TODO: not used?
                                    is ProfileSubItems.BuyCredits -> bindBuy(objc)
                                    is ProfileSubItems.Ambassador -> bindAmbassador(objc)

                                    // Business
                                    is ProfileSubItems.PersonalInfo -> bindPersonalInfo(objc)
                                    is ProfileSubItems.MyJobs -> bindMyJobs(objc)
                                    is ProfileSubItems.EarnCredits -> bindEarn(objc)
                                    is ProfileSubItems.MyInterests -> bindMyInterests(objc)
                                    is ProfileSubItems.BusinessSocialChannels -> bindBusinessSocialChannels(objc)
                                    is ProfileSubItems.AppearanceCharacteristics -> bindAppearanceCharacteristics(objc)
                                    is ProfileSubItems.Agency -> bindAgency(objc)
                                    is ProfileSubItems.Portfolio -> bindPortfolio(objc)

                                    //TODO: not used?
                                    is ProfileSubItems.CompCard -> bindCompCard(objc)
                                    is ProfileSubItems.Preference -> bindPreference(objc)
                                    is ProfileSubItems.ModelsCom -> bindModelsCom(objc)
                                    is ProfileSubItems.Polaroid -> bindPolaroid(objc)

                                    // Wallet
                                    is ProfileSubItems.BankAccount -> bindBankAccount(objc)
                                    is ProfileSubItems.PaypalAccount -> bindPaypalAccount(objc)

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
        }

        private fun bindBankAccount(item: ProfileSubItems.BankAccount): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_bank_account, null)

            val drawable: Drawable? = when (item.type) {
                BANK_ACCOUNT_TYPE_VISA -> itemsLl.context.getDrawable(R.drawable.r_shop)
                BANK_ACCOUNT_TYPE_MASTER_CARD -> itemsLl.context.getDrawable(R.drawable.r_shop)
                else -> null
            }

            drawable?.let {
                view.bankAccountIcon.setImageDrawable(it)
            }

            view.bankAccountRb.isChecked = item.isPrimary

            view.bankAccountRb.setOnClickListener { walletHandler?.bankAccountCheckClicked(item.isPrimary) }

            view.bankAccountTitle.text = item.number

            return view
        }

        private fun bindPaypalAccount(item: ProfileSubItems.PaypalAccount): View {
            //TODO just a polaceholder
            val view = View(arrow.context)
//                    LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_paypal_account, null)

            return view
        }

        private fun bindBalance(item: ProfileItem){
            balanceText.text = item.title
        }

        private fun bindButton(item: ProfileItem){
            itemProfileButton.text = item.title

            itemProfileButton.setOnClickListener { socialHandler?.editProfileClicked() }
        }

        private fun bindPlan(item: ProfileSubItems.Plan): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_plan, null)

            view.planText.text = item.text
            view.planChangeBtn.text = itemsLl.context.resources.getString(R.string.change)
            view.planChangeBtn.visibility = if (item.canChange) View.VISIBLE else View.GONE
            view.planChangeBtn.setOnClickListener { socialHandler?.changePlanClicked() }

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

            view.mainContainer.setOnClickListener { socialHandler?.socialConnectClicked(item.type, item.connected) }

            return view
        }

        private fun bindPersonalInfo(item: ProfileSubItems.PersonalInfo): View {
            //TODO just a polaceholder
            val view = View(arrow.context)
//                    LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_personal_info, null)

            return view
        }

        private fun bindMyJobs(item: ProfileSubItems.MyJobs): View {
            //TODO just a polaceholder
            val view = View(arrow.context)
//                    LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_my_jobs, null)

            return view
        }

        private fun bindMyInterests(item: ProfileSubItems.MyInterests): View {
            //TODO just a polaceholder
            val view = View(arrow.context)
//                    LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_interests, null)

            return view
        }

        private fun bindBusinessSocialChannels(item: ProfileSubItems.BusinessSocialChannels): View {
            //TODO just a polaceholder
            val view = View(arrow.context)
//                    LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_business_social_channels, null)

            return view
        }

        private fun bindYourActivity(item: ProfileSubItems.YourActivity): View {
            //TODO just a polaceholder
            val view = View(arrow.context)
//                    LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_activity, null)

            return view
        }

        private fun bindSpecialities(item: ProfileSubItems.Specialities): View {
            //TODO just a polaceholder
            val view = View(arrow.context)
//                    LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_specialities, null)

            return view
        }

        private fun bindCapabilities(item: ProfileSubItems.Capabilities): View {
            //TODO just a polaceholder
            val view = View(arrow.context)
//                    LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_capabilities, null)

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

            view.earnBtn.setOnClickListener { businessHandler?.earnCreditsClicked(item.type) }

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
            view.buyBtn.setOnClickListener { socialHandler?.buyExtraClicked(item.type) }

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

            view.ambassadorBtn.setOnClickListener { socialHandler?.ambassadorClicked(item.type) }

            return view
        }

        private fun bindAppearanceCharacteristics(item: ProfileSubItems.AppearanceCharacteristics): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_detail, null)

            if(item.type == DETAIL_TYPE_DOUBLE){
                view.detailFirstTitle.visibility = View.VISIBLE
                view.detailSecondContainer.visibility = View.VISIBLE
                view.detailFirstText.minLines = 1
                view.detailFirstText.maxLines = 1

                view.detailFirstTitle.text = item.firstTitle
                view.detailFirstText.text = item.firstText
                view.detailSecondTitle.text = item.secondTitle
                view.detailSecondText.text = item.secondText

            } else if(item.type == DETAIL_TYPE_FULL){
                view.detailFirstTitle.visibility = View.GONE
                view.detailSecondContainer.visibility = View.GONE
                view.detailFirstText.minLines = 2
                view.detailFirstText.maxLines = Integer.MAX_VALUE

                val typeface = Typeface.createFromAsset(view.detailFirstText.context.assets, view.detailFirstText.context.getString(R.string.font_poppins_medium))
                val ss = SpannableStringBuilder(item.firstTitle+" "+item.firstText)
                ss.setSpan(CustomTypefaceSpan("", typeface), 0, item.firstTitle.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

                view.detailFirstTitle.text = ""
                view.detailFirstText.text = ss
                view.detailSecondTitle.text = ""
                view.detailSecondText.text = ""
            }

            return view
        }

        private fun bindPolaroid(item: ProfileSubItems.Polaroid): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_polaroid, null)

            view.polaroidTitle.text = item.title
            view.polaroidOpenBtn.setOnClickListener { businessHandler?.polaroidOpenClicked(item.albumId) }

            if(item.expired){
                view.expiredText.visibility = View.VISIBLE

                (view.polaroidTitle.layoutParams as ConstraintLayout.LayoutParams).also { layoutParams ->
                    layoutParams.marginEnd = itemsLl.context.resources.getDimensionPixelSize(R.dimen.subitem_bottom_margin) + view.expiredText.measuredWidth
                }
            } else{
                view.expiredText.visibility = View.GONE

                (view.polaroidTitle.layoutParams as ConstraintLayout.LayoutParams).also { layoutParams ->
                    layoutParams.marginEnd = itemsLl.context.resources.getDimensionPixelSize(R.dimen.subitem_left_margin)
                }
            }

            return view
        }

        private fun bindPortfolio(item: ProfileSubItems.Portfolio): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_portfolio, null)

            view.portfolioTitle.text = item.title
            view.portfolioOpenBtn.setOnClickListener { businessHandler?.portfolioOpenClicked(item.portfolioId) }

            return view
        }

        private fun bindAgency(item: ProfileSubItems.Agency): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_agency, null)

            view.agencyTitle.text = item.title
            view.agencyViewBtn.setOnClickListener { businessHandler?.agencyViewClicked(item.agencyId) }

            return view
        }

        private fun bindCompCard(item: ProfileSubItems.CompCard): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_comp_card, null)

            view.compCardTitle.text = item.title
            view.compCardOpenBtn.setOnClickListener { businessHandler?.compCardOpenClicked(item.compCardId) }

            return view
        }

        private fun bindPreference(item: ProfileSubItems.Preference): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_preference, null)

            view.preferenceSwitch.text = when(item.type){
                PREFERENCE_TYPE_SOCIAL -> itemsLl.context.getString(R.string.social_contents)
                PREFERENCE_TYPE_HOSTESS -> itemsLl.context.getString(R.string.hostess)
                PREFERENCE_TYPE_NIGHT_OUT -> itemsLl.context.getString(R.string.night_out_experiences)
                else -> ""
            }

            view.preferenceSwitch.isChecked = item.checked

            view.preferenceClickView.setOnClickListener {
                val checked = view.preferenceSwitch.isChecked.not()

                item.checked = checked
                view.preferenceSwitch.isChecked = checked
                businessHandler?.preferenceClicked(item.type, checked)
            }

            return view
        }

        private fun bindModelsCom(item: ProfileSubItems.ModelsCom): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_models_com, null)

            val modelsLink = itemsLl.context.getString(R.string.models_com_models_)
            val emptyText = itemsLl.context.getString(R.string.models_com_empty_text)

            val ss = SpannableString(modelsLink + if(TextUtils.isEmpty(item.modelsComUserName)) emptyText else item.modelsComUserName)

            if(TextUtils.isEmpty(item.modelsComUserName)){
                ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(itemsLl.context, R.color.grey_dark)), modelsLink.length, ss.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }

            view.modelsComText.text = ss
            view.modelsComText.setOnClickListener { businessHandler?.modelsComClicked(item.modelsComUserName) }

            return view
        }

        private fun bindCreate(item: ProfileSubItems.Create): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_create, null)
            view.createClickView.setOnClickListener { handler.createClicked(item.clickedType) }

            return view
        }

        fun bindOpened(item: ProfileItem, openedItems: MutableList<Int>?) {

            if(item.type == TYPE_ADD){
                itemsLl.visibility = View.VISIBLE
            } else{
                openedItems?.let {
                    if (item.type == TYPE_DROPDOWN) {
                        val opened = adapterPosition in it

                        arrow.animate().rotation(if (!opened) 0f else 90f).setDuration(50).start();

                        itemsLl.visibility = if (opened) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    interface Handler {
        fun clickViewClicked(position: Int)
        fun createClicked(clickedType: Int)
    }

    interface SocialHandler{
        fun editProfileClicked()
        fun changePlanClicked()
        fun socialConnectClicked(type: Int, isConnected: Boolean)
        fun buyExtraClicked(type: Int)
        fun ambassadorClicked(type: Int)
    }

    interface BusinessHandler{
        fun earnCreditsClicked(type: Int)
        fun polaroidOpenClicked(albumId: Long)
        fun portfolioOpenClicked(portfolioId: Long)
        fun agencyViewClicked(agencyId: Long)
        fun compCardOpenClicked(compCardId: Long)
        fun preferenceClicked(type: Int, isChecked: Boolean)
        fun modelsComClicked(modelsComUserName: String)
    }

    interface WalletHandler{
        fun addBankAccountClicked()
        fun bankAccountCheckClicked(isChecked: Boolean)
        fun addPaypalAccountClicked()
    }

    object OpenedPayload
}