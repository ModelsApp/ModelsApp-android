package com.square.android.ui.fragment.profile

import android.graphics.Typeface
import android.text.*
import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_profile.*
import android.view.LayoutInflater
import android.text.style.ForegroundColorSpan
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.square.android.utils.CustomTypefaceSpan
import kotlinx.android.synthetic.main.profile_subitem_agency.view.*
import kotlinx.android.synthetic.main.profile_subitem_comp_card.view.*
import kotlinx.android.synthetic.main.profile_subitem_detail.view.*
import kotlinx.android.synthetic.main.profile_subitem_create.view.*
import kotlinx.android.synthetic.main.profile_subitem_models_com.view.*
import kotlinx.android.synthetic.main.profile_subitem_polaroid.view.*
import kotlinx.android.synthetic.main.profile_subitem_portfolio.view.*
import kotlinx.android.synthetic.main.profile_subitem_preference.view.*

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
                                is ProfileSubItems.Detail -> bindDetail(objc)
                                is ProfileSubItems.Polaroid -> bindPolaroid(objc)
                                is ProfileSubItems.Portfolio -> bindPortfolio(objc)
                                is ProfileSubItems.Agency -> bindAgency(objc)
                                is ProfileSubItems.CompCard -> bindCompCard(objc)
                                is ProfileSubItems.Preference -> bindPreference(objc)
                                is ProfileSubItems.ModelsCom -> bindModelsCom(objc)
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

        private fun bindDetail(item: ProfileSubItems.Detail): View {
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
            view.polaroidOpenBtn.setOnClickListener { handler.polaroidOpenClicked(item.albumId) }

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
            view.portfolioOpenBtn.setOnClickListener { handler.portfolioOpenClicked(item.portfolioId) }

            return view
        }

        private fun bindAgency(item: ProfileSubItems.Agency): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_agency, null)

            view.agencyTitle.text = item.title
            view.agencyViewBtn.setOnClickListener { handler.agencyViewClicked(item.agencyId) }

            return view
        }

        private fun bindCompCard(item: ProfileSubItems.CompCard): View {
            val view = LayoutInflater.from(itemsLl.context).inflate(R.layout.profile_subitem_comp_card, null)

            view.compCardTitle.text = item.title
            view.compCardOpenBtn.setOnClickListener { handler.compCardOpenClicked(item.compCardId) }

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
                handler.preferenceClicked(item.type, checked)
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
            view.modelsComText.setOnClickListener { handler.modelsComClicked(item.modelsComUserName) }

            return view
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
        fun polaroidOpenClicked(albumId: Long)
        fun portfolioOpenClicked(portfolioId: Long)
        fun agencyViewClicked(agencyId: Long)
        fun compCardOpenClicked(compCardId: Long)
        fun preferenceClicked(type: Int, isChecked: Boolean)
        fun modelsComClicked(modelsComUserName: String)
    }

    object OpenedPayload
}