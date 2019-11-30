package com.square.android.presentation.view.claimedCoupon

import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionFull
import com.square.android.data.pojo.UserInfo
import com.square.android.presentation.view.BaseView

interface ClaimedCouponView : BaseView {
    fun showData(redemptionFull: RedemptionFull, user: Profile.User)
}
