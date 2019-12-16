package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.ProfileSocialView

@InjectViewState
class ProfileSocialPresenter(user: Profile.User, actualTokenInfo: BillingTokenInfo): BasePresenter<ProfileSocialView>(){

    init {
        viewState.showData(user, actualTokenInfo)
    }
}