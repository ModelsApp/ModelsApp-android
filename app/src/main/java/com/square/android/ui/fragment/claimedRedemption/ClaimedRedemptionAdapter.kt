package com.square.android.ui.fragment.claimedRedemption

import com.square.android.App
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionFull
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.ui.fragment.claimedCoupon.ClaimedCouponFragment
import com.square.android.ui.fragment.review.ReviewFragment

private const val ITEM_COUNT = 2

private var PAGE_TITLES_RES = listOf(R.string.coupon, R.string.actions)

private const val POSITION_COUPON = 0
private const val POSITION_ACTIONS = 1

class ClaimedRedemptionAdapter(fragmentManager: androidx.fragment.app.FragmentManager, private val redemptionFull: RedemptionFull, private val redemptionInfo: RedemptionInfo, private val user: Profile.User ) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {
    private val titles: List<String> = PAGE_TITLES_RES.map { App.getString(it) }

    override fun getItem(position: Int) =
            when (position) {
                POSITION_COUPON -> ClaimedCouponFragment.newInstance(redemptionFull, user)
                POSITION_ACTIONS -> ReviewFragment.newInstance(redemptionFull.redemption.id, redemptionInfo.offers.firstOrNull())
                else -> throw IllegalArgumentException("Illegal position: $position")
            }

    override fun getCount() = ITEM_COUNT

    override fun getPageTitle(position: Int) = titles[position]
}