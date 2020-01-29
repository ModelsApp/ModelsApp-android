package com.square.android.presentation.presenter.settings

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.settings.SettingsMainView

@InjectViewState
class SettingsMainPresenter(var user: Profile.User): BasePresenter<SettingsMainView>(){


}