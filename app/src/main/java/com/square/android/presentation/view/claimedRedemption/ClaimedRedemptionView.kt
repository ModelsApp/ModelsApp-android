package com.square.android.presentation.view.claimedRedemption

import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionFull
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.presentation.view.ProgressView

interface ClaimedRedemptionView: ProgressView {
    fun showData(redemptionFull: RedemptionFull, redemptionInfo: RedemptionInfo, user: Profile.User)
}
