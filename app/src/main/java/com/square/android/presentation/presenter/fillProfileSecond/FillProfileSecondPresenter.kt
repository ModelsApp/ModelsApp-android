package com.square.android.presentation.presenter.fillProfileSecond

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.fillProfileSecond.FillProfileSecondView

@InjectViewState
class FillProfileSecondPresenter(val info: ProfileInfo) : BasePresenter<FillProfileSecondView>() {

    init {
        viewState.showData(info)
    }

    fun nextClicked(account: String, motherAgency: String) {
//    fun nextClicked(account: String, motherAgency: String, currentAgency: String) {
        info.instagramName = account
        info.motherAgency = motherAgency
//        info.currentAgency = currentAgency

        router.navigateTo(SCREENS.FILL_PROFILE_THIRD, info)
    }
}
