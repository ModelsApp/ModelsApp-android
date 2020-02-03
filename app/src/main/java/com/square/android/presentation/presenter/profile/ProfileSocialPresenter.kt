package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.ProfileSocialView
import com.square.android.ui.activity.profile.ActivePlanExtras

@InjectViewState
class ProfileSocialPresenter(user: Profile.User, var actualTokenInfo: BillingTokenInfo): BasePresenter<ProfileSocialView>(){

    init {
        viewState.showData(user, actualTokenInfo)
    }

    fun openEditProfile() {
        router.navigateTo(SCREENS.EDIT_PROFILE)
    }

    fun navigateToActivePlan(){
        router.navigateTo(SCREENS.ACTIVE_PLAN, ActivePlanExtras(true, actualTokenInfo))
    }

    fun navigateToEarnMoreCredits(){
        router.navigateTo(SCREENS.EARN_MORE_CREDITS)
    }
}