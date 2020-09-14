package com.square.android.ui.fragment.agenda.schedule

import android.view.View
import androidx.core.content.ContextCompat
import com.square.android.R
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.ui.base.BaseAdapter
import com.square.android.data.pojo.CampaignBooking
import com.square.android.extensions.*
import com.square.android.presentation.presenter.agenda.ScheduleHeader
import com.square.android.ui.dialogs.SchedulePendingActionDialog
import kotlinx.android.synthetic.main.item_schedule.*
import kotlinx.android.synthetic.main.item_schedule_header.*
import java.lang.Exception
import java.util.*

private const val TYPE_HEADER = R.layout.item_schedule_header
private const val TYPE_REDEMPTION = R.layout.item_schedule
private const val TYPE_CAMPAIGN_REDEMPTION = R.layout.item_schedule

private var isDialogVisible = false

class ScheduleAdapter(data: List<Any>, private val handler: Handler)
    : BaseAdapter<Any, ScheduleAdapter.RedemptionHolder>(data) {

    override fun getLayoutId(viewType: Int) = viewType

    override fun instantiateHolder(view: View): RedemptionHolder = RedemptionHolder(view, handler)

    override fun getViewType(position: Int): Int {
        val item = data[position]

        return when (item) {
            is RedemptionInfo -> {
                TYPE_REDEMPTION
            }
            is CampaignBooking -> {
                TYPE_CAMPAIGN_REDEMPTION
            }

            is ScheduleHeader -> {
                TYPE_HEADER
            }

            else -> TYPE_HEADER
        }
    }

    fun removeItem(position: Int) {
        notifyItemRemoved(position)
    }

    class RedemptionHolder(containerView: View, var handler: Handler) : BaseHolder<Any>(containerView) {

        override fun bind(item: Any, vararg extras: Any?) {
            when (item) {
                is RedemptionInfo -> bindRedemption(item)
                is ScheduleHeader -> bindHeader(item)
                is CampaignBooking -> bindCampaign(item)
            }
        }

        private fun bindRedemption(redemptionInfo: RedemptionInfo) {
            //            redemption_btn_top.text = redemption_btn_bottom.resources.getString(R.string.details)
//            redemption_btn_top.setTextColor(Color.BLACK)
//            redemption_btn_top.setOnClickListener { handler.redemptionDetailsClicked(redemptionInfo.place.id)}

            if (redemptionInfo.closed || redemptionInfo.claimed) {
                scheduleImg.makeBlackWhite()

                //                redemption_place_name.alpha = 0.3f
//                redemption_address.alpha = 0.3f
//                redemption_hours.alpha = 0.3f
//                redemption_btn_top.alpha = 0.3f
//                redemption_btn_bottom.alpha = 0.3f
            } else {
                scheduleImg.removeFilters()
            }

            if (redemptionInfo.hasPendingAction) {
                scheduleBottomDivider.visibility = View.GONE
                btnReview.visibility = View.VISIBLE
                scheduleMoreIcon.visibility = View.GONE

                scheduleContainer.setBackgroundColor(ContextCompat.getColor(scheduleContainer.context, R.color.gray_btn_disabled_light))

                btnReview.setOnClickListener {
                    if(!isDialogVisible){
                        isDialogVisible = true

                        SchedulePendingActionDialog(btnReview.context)
                                .show(redemptionInfo,
                                        onAction = {

                                            //TODO
                                            // Confirm reservation if it != getString(R.string.i_wont_go_anymore), cancel reservation otherwise.

                                        },
                                        onDismiss = {
                                            isDialogVisible = false
                                        })
                    }
                }

            } else{
                scheduleMoreIcon.visibility = View.VISIBLE
                scheduleBottomDivider.visibility = if(redemptionInfo.dividerVisible) View.VISIBLE else View.GONE
                scheduleContainer.setBackgroundColor(ContextCompat.getColor(scheduleContainer.context, android.R.color.white))
            }

            //TODO scheduleStatusCircle color, what value defines it?
            scheduleStatusCircle.tintFromRes(R.color.status_green)

            scheduleMoreIcon.setOnClickListener {
                //TODO what it should do?
            }

            scheduleTitle.text = redemptionInfo.place.name
            scheduleTopIc.drawableFromRes(R.drawable.r_address)
            scheduleTopIc.imageTintList
            scheduleTopText.text = redemptionInfo.place.address
            scheduleBottomText.text = scheduleBottomText.context.getString(R.string.time_range, redemptionInfo.startTime, redemptionInfo.endTime)

            scheduleImg.loadImage(redemptionInfo.place.mainImage, roundedCornersRadiusPx = Math.round(scheduleImg.context.resources.getDimension(R.dimen.v_16dp)))

            scheduleContainer.setOnClickListener {
                if(redemptionInfo.claimed){
                    handler.claimedItemClicked(redemptionInfo.id)
                } else{
                    handler.claimClicked(redemptionInfo.id)
                }
            }
        }

        // TODO new layout
        private fun bindCampaign(campaignBooking: CampaignBooking){

            scheduleBottomDivider.visibility = if(campaignBooking.dividerVisible) View.VISIBLE else View.GONE

            //TODO scheduleStatusCircle color, what value defines it?
            scheduleStatusCircle.tintFromRes(R.color.status_green)

            scheduleImg.removeFilters()

            scheduleMoreIcon.setOnClickListener {
                //TODO what it should do?
            }

            scheduleTitle.text = campaignBooking.title
            scheduleTopIc.drawableFromRes(R.drawable.r_pin)
            scheduleTopIc.imageTintList

            //TODO
            scheduleTopText.text = "WHERE TO GET ADDRESS IN CAMPAIGN?"
            scheduleBottomText.text = "WHERE TO GET TIME INTERVAL IN CAMPAIGN?"

            scheduleImg.loadImage(campaignBooking.mainImage, roundedCornersRadiusPx = Math.round(scheduleImg.context.resources.getDimension(R.dimen.v_16dp)))

            scheduleContainer.setOnClickListener { handler.campaignItemClicked(campaignBooking.campaignId) }
        }

        private fun bindHeader(header: ScheduleHeader) {
            val calendar = Calendar.getInstance()
            try{
                calendar.timeInMillis = header.title.toDateYMD().time

                scheduleHeader.text = "${calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())}, ${calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${calendar.get(Calendar.DAY_OF_MONTH)}"

                scheduleHeader.setTextColorRes(R.color.text_gray)
            } catch (e: Exception){
                scheduleHeader.text = header.title
                scheduleHeader.setTextColorRes(android.R.color.black)
            }

            scheduleDivider.visibility = if(header.isFirst) View.GONE else View.VISIBLE
        }
    }

    interface Handler {
        fun cancelCampaignClicked(id: Long)

        fun cancelRedemptionClicked(id: Long)

        fun campaignItemClicked(id: Long)

        fun claimClicked(id: Long)

        fun claimedItemClicked(id: Long)

        fun redemptionDetailsClicked(placeId: Long)
    }
}