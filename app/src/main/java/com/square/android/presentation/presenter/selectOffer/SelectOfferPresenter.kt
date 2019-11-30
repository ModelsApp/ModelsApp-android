package com.square.android.presentation.presenter.selectOffer

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.selectOffer.SelectOfferView

@InjectViewState
class SelectOfferPresenter(redemptionInfo: RedemptionInfo) : BasePresenter<SelectOfferView>() {

    init {
        if(redemptionInfo.claimed){
            router.replaceScreen(SCREENS.CLAIMED_REDEMPTION, redemptionInfo)
        } else{
            router.replaceScreen(SCREENS.OFFERS_LIST, redemptionInfo)
        }
    }
}
