package com.square.android.presentation.presenter.claimedCoupon

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionFull
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.claimedCoupon.ClaimedCouponView

@InjectViewState
class ClaimedCouponPresenter(private val redemptionFull: RedemptionFull, private val user: Profile.User) : BasePresenter<ClaimedCouponView>() {

    init {
        viewState.showData(redemptionFull, user)
    }

}
