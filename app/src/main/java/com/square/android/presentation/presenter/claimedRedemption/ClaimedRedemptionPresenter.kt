package com.square.android.presentation.presenter.claimedRedemption

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionFull
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.claimedRedemption.ClaimedRedemptionView

@InjectViewState
class ClaimedRedemptionPresenter(private val redemptionInfo: RedemptionInfo): BasePresenter<ClaimedRedemptionView>(){

    private var data: RedemptionFull? = null

    init {
        loadData()
    }

    private fun loadData() = launch {
        viewState.showProgress()

        data = repository.getRedemption(redemptionInfo.id).await()
        val user: Profile.User = repository.getCurrentUser().await()

        viewState.hideProgress()

        viewState.showData(data!!, redemptionInfo, user)
    }

}
