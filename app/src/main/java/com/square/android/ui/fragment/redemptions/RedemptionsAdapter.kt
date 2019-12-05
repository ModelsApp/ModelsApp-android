package com.square.android.ui.fragment.redemptions

import android.graphics.Color
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.redemption_header.*
import android.webkit.URLUtil
import com.square.android.App
import com.square.android.data.pojo.CampaignBooking
import com.square.android.extensions.*
import kotlinx.android.synthetic.main.item_campaign_redemption.*
import kotlinx.android.synthetic.main.item_redemption.*
import java.lang.Exception
import java.util.*

private const val TYPE_HEADER = R.layout.redemption_header
private const val TYPE_REDEMPTION = R.layout.item_redemption
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
                TYPE_REDEMPTION
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

    class RedemptionHolder(containerView: View, var handler: Handler) : BaseHolder<Any>(containerView) {

        override fun bind(item: Any, vararg extras: Any?) {
            when (item) {
                is RedemptionInfo -> bindRedemption(item)
                is String -> bindHeader(item)
                is CampaignBooking -> bindCampaign(item)
            }
        }

        private fun bindRedemption(redemptionInfo: RedemptionInfo) {
            redemption_btn_top.text = redemption_btn_bottom.resources.getString(R.string.details)
            redemption_btn_top.setTextColor(Color.BLACK)

            if (redemptionInfo.closed || redemptionInfo.claimed) {
                redemption_image.makeBlackWhite()

                redemption_place_name.alpha = 0.3f
                redemption_address.alpha = 0.3f
                redemption_hours.alpha = 0.3f
                redemption_btn_top.alpha = 0.3f
                redemption_btn_bottom.alpha = 0.3f

                //TODO remove option
            } else {
                redemption_image.removeFilters()

                redemption_place_name.alpha = 1f
                redemption_address.alpha = 1f
                redemption_hours.alpha = 1f
                redemption_btn_top.alpha = 1f
                redemption_btn_bottom.alpha = 1f

                redemption_btn_bottom.visibility = View.VISIBLE
                redemption_btn_bottom.text = redemption_btn_bottom.resources.getString(R.string.cancel)
                redemption_btn_bottom.setTextColor(ContextCompat.getColor(redemption_btn_bottom.context, R.color.status_red))
                redemption_btn_bottom.setOnClickListener {
                    if (!isDialogVisible) {
                        isDialogVisible = true

                        val dialog: MaterialDialog = MaterialDialog.Builder(redemption_btn_bottom.context)
                                .title(R.string.remove_item_title)
                                .content(R.string.remove_item_content)
                                .contentColorRes(android.R.color.black)
                                .itemsColor(ContextCompat.getColor(redemption_btn_bottom.context, R.color.nice_pink))
                                .positiveText(R.string.yes)
                                .negativeText(R.string.no)
                                .negativeColorRes(R.color.nice_pink)
                                .positiveColorRes(R.color.nice_pink)
                                .cancelable(true)
                                .onPositive { dialog, action ->
                                    dialog.cancel()

                                    handler.cancelRedemptionClicked(redemptionInfo.id)
                                }
                                .onNegative { dialog, action ->
                                    dialog.cancel()
                                }
                                .cancelListener {
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
            redemption_place_name.text = redemptionInfo.place.name
            redemption_address.text = redemptionInfo.place.address
            redemption_hours?.text = redemption_hours.context.getString(com.square.android.R.string.time_range, redemptionInfo.startTime, redemptionInfo.endTime)

            if (URLUtil.isValidUrl(redemptionInfo.place.photo))
                redemption_image.loadImage(redemptionInfo.place.photo!!, roundedCornersRadiusPx = 360)

            redemptionContainer.setOnClickListener {
                if(redemptionInfo.claimed){
                    handler.claimedItemClicked(redemptionInfo.id)
                } else{
                    handler.claimClicked(redemptionInfo.id)
                }
            }
        }

        private fun bindCampaign(campaignBooking: CampaignBooking){
            campaign_title.text = campaignBooking.title

            //TODO no hashtag, campaign type, active boolean, completed boolean
//            campaign_type.text =
//            campaign_hashtag.text =

            if (URLUtil.isValidUrl(campaignBooking.mainImage))
                campaign_image.loadImage(campaignBooking.mainImage!!, roundedCornersRadiusPx = 360)

            campaignRedemptionContainer.setOnClickListener { handler.campaignItemClicked(campaignBooking.campaignId) }
        }

        private fun bindHeader(header: String) {
            val calendar = Calendar.getInstance()
            try{
                calendar.timeInMillis = header.toDateYMD().time

                val day = calendar.get(Calendar.DAY_OF_MONTH).toOrdinalString()
                val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

                redemptionHeader.text = App.INSTANCE.getString(R.string.date_format, day, month)

                redemptionHeader.setTextColorRes(R.color.gray_hint_light)

            } catch (e: Exception){
                redemptionHeader.text = header
                redemptionHeader.setTextColorRes(android.R.color.black)
            }
        }
    }

    interface Handler {
        fun cancelCampaignClicked(id: Long)

        fun cancelRedemptionClicked(id: Long)

        fun campaignItemClicked(id: Long)

        fun claimClicked(id: Long)

        fun claimedItemClicked(id: Long)
    }
}