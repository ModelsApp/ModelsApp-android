package com.square.android.presentation.presenter.settings

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BaseTabPresenter
import com.square.android.presentation.view.settings.SettingsChangePasswordView

@InjectViewState
class SettingsChangePasswordPresenter(var user: Profile.User): BaseTabPresenter<SettingsChangePasswordView>(){

}