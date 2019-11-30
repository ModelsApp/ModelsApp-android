package com.square.android.presentation.view.offersList

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionFull
import com.square.android.presentation.view.LoadingView
import com.square.android.presentation.view.ProgressView

interface OffersListView : LoadingView {
    fun showData(data: List<OfferInfo>, redemptionFull: RedemptionFull)
    fun setSelectedItem(position: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showOfferDialog(offer: OfferInfo)

    fun showCouponDialog(redemptionFull: RedemptionFull, userData: Profile.User, offerInfo: OfferInfo)
}