package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.ProfileBusinessView

@InjectViewState
class ProfileBusinessPresenter(user: Profile.User): BasePresenter<ProfileBusinessView>(){

    init {
        viewState.showData(user)
    }

    fun navigateToEarnMoreCredits(){
        router.navigateTo(SCREENS.EARN_MORE_CREDITS)
    }

}