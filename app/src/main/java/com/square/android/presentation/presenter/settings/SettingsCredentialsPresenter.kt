package com.square.android.presentation.presenter.settings

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BaseTabPresenter
import com.square.android.presentation.view.settings.SettingsCredentialsView
import com.square.android.ui.activity.TabData

@InjectViewState
class SettingsCredentialsPresenter(var user: Profile.User): BaseTabPresenter<SettingsCredentialsView>(){

    fun navigateChangePassword(tabData: TabData){
        routerNavigate(SCREENS.SETTINGS_CHANGE_PASSWORD, user, tabData)
    }

}