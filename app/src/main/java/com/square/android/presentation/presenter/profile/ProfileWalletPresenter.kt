package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.ProfileWalletView

@InjectViewState
class ProfileWalletPresenter(user: Profile.User): BasePresenter<ProfileWalletView>(){

    init {
        viewState.showData(user)
    }

}