package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.ProfileView

@InjectViewState
class ProfilePresenter: BasePresenter<ProfileView>(){

    init {
        viewState.setupFragmentAdapter()
    }

    fun openSettings() {
        router.navigateTo(SCREENS.EDIT_PROFILE)
    }

}