package com.square.android.presentation.presenter.settings

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.BaseTabPresenter
import com.square.android.presentation.view.settings.SettingsMainView
import com.square.android.ui.activity.TabData

@InjectViewState
class SettingsMainPresenter(var user: Profile.User): BaseTabPresenter<SettingsMainView>(){

    fun navigateCredentials(tabData: TabData){
        routerNavigate(SCREENS.SETTINGS_CREDENTIALS, user, tabData)
    }

}