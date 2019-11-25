package com.square.android.ui.fragment.redemptions

import android.animation.ObjectAnimator
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.daimajia.swipe.SwipeLayout
import com.square.android.R
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_redemption_active.*
import kotlinx.android.synthetic.main.redemption_header.*
import kotlinx.android.synthetic.main.item_campaign_redemption.*
import android.animation.Animator
import android.animation.AnimatorSet
import android.view.animation.DecelerateInterpolator
import android.webkit.URLUtil
import com.square.android.App
import com.square.android.data.pojo.CampaignBooking
import com.square.android.extensions.*
import java.lang.Exception
import java.util.*

private const val TYPE_HEADER = R.layout.redemption_header
private const val TYPE_REDEMPTION = R.layout.item_redemption_active
private const val TYPE_CLAIMED_REDEMPTION = R.layout.item_redemption_claimed
private const val TYPE_CLOSED_REDEMPTION = R.layout.item_redemption_closed
private const val TYPE_CAMPAIGN_REDEMPTION = R.layout.item_campaign_redemption

private var isDialogVisible = false

class RedemptionsAdapter(data: List<Any>, private val handler: Handler)
    : BaseAdapter<Any, RedemptionsAdapter.RedemptionHolder>(data) {

    override fun getLayoutId(viewType: Int) = viewType

    override fun instantiateHolder(view: View): RedemptionHolder = RedemptionHolder(view, handler)

    override fun getViewType(position: Int): Int {
        val item = data[position]

        return when (item) {
            is RedemptionInfo -> {
                when {
                    item.closed -> TYPE_CLOSED_REDEMPTION
                    item.claimed -> TYPE_CLAIMED_REDEMPTION
                    else -> TYPE_REDEMPTION
                }
            }
            is CampaignBooking -> {
                TYPE_CAMPAIGN_REDEMPTION
            }
            else -> TYPE_HEADER
        }
    }

    fun removeItem(position: Int) {
        notifyItemRemoved(position)
    }

    class RedemptionHolder(containerView: View, handler: Handler) : BaseHolder<Any>(containerView) {
        init {

            //Campaign redemption
                campaignRedemptionContainer?.setOnClickListener {

                    val dip4 : Float = campaignRedemptionContainer.resources.getDimension(R.dimen.anim_start)
                    val dip2 : Float = campaignRedemptionContainer.resources.getDimension(R.dimen.anim_end)

                    val anim1Start = ObjectAnimator.ofFloat(campaignRedemptionContainer, "elevation", dip4)
                    val anim2Start = ObjectAnimator.ofFloat(campaignRedemptionImageShadow, "elevation", dip4)
                    val anim3Start = ObjectAnimator.ofFloat(campaignRedemptionImage, "elevation", dip4)

                    val anim1End = ObjectAnimator.ofFloat(campaignRedemptionContainer, "elevation", dip2)
                    val anim2End = ObjectAnimator.ofFloat(campaignRedemptionImageShadow, "elevation", dip2)
                    val anim3End = ObjectAnimator.ofFloat(campaignRedemptionImage, "elevation", dip2)

                    val animationSet = AnimatorSet()
                    val animationSet2 = AnimatorSet()

                    animationSet.playTogether(
                            anim1Start,
                            anim2Start,
                            anim3Start)
                    animationSet.interpolator = DecelerateInterpolator()
                    animationSet.duration = 1

                    animationSet.addListener(object: Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {}

                        override fun onAnimationEnd(animation: Animator?) {
                            handler.campaignItemClicked(adapterPosition)

                            animationSet2.playTogether(
                                    anim1End,
                                    anim2End,
                                    anim3End)
                            animationSet2.interpolator = DecelerateInterpolator()
                            animationSet2.duration = 200
                            animationSet2.start()
                        }

                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}
                    } )
                    animationSet.start()
                }

                campaignRedemptionImage?.setOnClickListener {
                    campaignRedemptionContainer?.callOnClick()
                }
                campaignRedemptionImageShadow?.setOnClickListener {
                    campaignRedemptionContainer?.callOnClick()
                }

            //Redemption
                redemptionContainer?.setOnClickListener {

                    val dip4 : Float = redemptionContainer.resources.getDimension(R.dimen.anim_start)
                    val dip2 : Float = redemptionContainer.resources.getDimension(R.dimen.anim_end)

                    val anim1Start = ObjectAnimator.ofFloat(redemptionContainer, "elevation", dip4)
                    val anim2Start = ObjectAnimator.ofFloat(redemptionImageShadow, "elevation", dip4)
                    val anim3Start = ObjectAnimator.ofFloat(redemptionImage, "elevation", dip4)

                    val anim1End = ObjectAnimator.ofFloat(redemptionContainer, "elevation", dip2)
                    val anim2End = ObjectAnimator.ofFloat(redemptionImageShadow, "elevation", dip2)
                    val anim3End = ObjectAnimator.ofFloat(redemptionImage, "elevation", dip2)

                    val animationSet = AnimatorSet()
                    val animationSet2 = AnimatorSet()

                    animationSet.playTogether(
                            anim1Start,
                            anim2Start,
                            anim3Start)
                    animationSet.interpolator = DecelerateInterpolator()
                    animationSet.duration = 1

                    animationSet.addListener(object: Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {}

                        override fun onAnimationEnd(animation: Animator?) {
                            if (itemViewType == TYPE_REDEMPTION) {
                                handler.claimClicked(adapterPosition)
                            } else if (itemViewType == TYPE_CLAIMED_REDEMPTION) {
                                handler.claimedItemClicked(adapterPosition)
                            }

                            animationSet2.playTogether(
                                    anim1End,
                                    anim2End,
                                    anim3End)
                            animationSet2.interpolator = DecelerateInterpolator()
                            animationSet2.duration = 200
                            animationSet2.start()
                        }

                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}
                    } )
                    animationSet.start()
                }

                redemptionImage?.setOnClickListener {
                    redemptionContainer?.callOnClick()
                }
                redemptionImageShadow?.setOnClickListener {
                    redemptionContainer?.callOnClick()
                }

                redemptionSwipeLayout?.showMode = SwipeLayout.ShowMode.LayDown
                redemptionSwipeLayout?.addSwipeListener(object: SwipeLayout.SwipeListener {
                    override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {}
                    override fun onStartOpen(layout: SwipeLayout?) {}
                    override fun onStartClose(layout: SwipeLayout?) {}
                    override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {}
                    override fun onClose(layout: SwipeLayout?) {}

                    override fun onOpen(layout: SwipeLayout?) {
                        if (itemViewType == TYPE_REDEMPTION) {
                            if(!isDialogVisible){
                                isDialogVisible = true

                                val dialog: MaterialDialog = MaterialDialog.Builder(redemptionSwipeLayout.context)
                                        .title(R.string.remove_item_title)
                                        .content(R.string.remove_item_content)
                                        .contentColorRes(android.R.color.black)
                                        .itemsColor( ContextCompat.getColor( redemptionSwipeLayout.context, R.color.nice_pink))
                                        .positiveText(R.string.ok_lowercase)
                                        .negativeText(R.string.cancel)
                                        .cancelable(true)
                                        .onPositive { dialog, action ->
                                            dialog.cancel()

                                            handler.cancelClicked(adapterPosition)
                                        }
                                        .onNegative { dialog, action ->
                                            dialog.cancel()
                                        }
                                        .cancelListener {
                                            redemptionSwipeLayout?.close()
                                            isDialogVisible = false
                                        }
                                        .build()

                                val titleTv = dialog.titleView
                                val contentTv = dialog.contentView

                                titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                                contentTv?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)

                                dialog.show()
                            }
                        }
                    }
                })

        }

        override fun bind(item: Any, vararg extras: Any?) {
            when (item) {
                is RedemptionInfo -> bindRedemption(item)
                is String -> bindHeader(item)
                is CampaignBooking -> bindCampaign(item)
            }
        }

        private fun bindRedemption(redemptionInfo: RedemptionInfo) {
            if (redemptionInfo.closed || redemptionInfo.claimed) {
                redemptionImage.makeBlackWhite()
            } else {
                redemptionImage.removeFilters()
            }

            redemptionHours?.text = redemptionHours.context.getString(com.square.android.R.string.time_range, redemptionInfo.startTime, redemptionInfo.endTime)
            redemptionTitle.text = redemptionInfo.place.name
            redemptionAddress.text = redemptionInfo.place.address
            redemptionDate.text = redemptionInfo.date
            if (URLUtil.isValidUrl(redemptionInfo.place.photo))
                redemptionImage.loadImage(redemptionInfo.place.photo!!, roundedCornersRadiusPx = 360)
        }

        private fun bindCampaign(campaignBooking: CampaignBooking){
            campaignRedemptionHours.text = campaignBooking.time
            campaignRedemptionDate.text = campaignBooking.pickUpDate
            campaignRedemptionTitle.text = campaignBooking.title

            if (URLUtil.isValidUrl(campaignBooking.mainImage))
                campaignRedemptionImage.loadImage(campaignBooking.mainImage!!, roundedCornersRadiusPx = 360)
        }

        private fun bindHeader(header: String) {
            val calendar = Calendar.getInstance()
            try{
                calendar.timeInMillis = header.toDateYMD().time

                val day = calendar.get(Calendar.DAY_OF_MONTH).toOrdinalString()
                val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

                redemptionHeader.text = App.INSTANCE.getString(R.string.date_format, day, month)

            } catch (e: Exception){
                redemptionHeader.text = header
            }
        }
    }

    interface Handler {
        fun claimClicked(position: Int)

        fun cancelClicked(position: Int)

        fun claimedItemClicked(position: Int)

        fun campaignItemClicked(position: Int)
    }
}