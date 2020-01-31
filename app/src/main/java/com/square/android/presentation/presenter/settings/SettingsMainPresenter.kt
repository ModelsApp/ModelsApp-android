package com.square.android.presentation.presenter.settings

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BaseTabPresenter
import com.square.android.presentation.view.settings.SettingsMainView
import com.square.android.ui.activity.TabData

@InjectViewState
class SettingsMainPresenter(var user: Profile.User): BaseTabPresenter<SettingsMainView>(){

    fun navigateCredentials(tabData: TabData){
        routerNavigate(SCREENS.SETTINGS_CREDENTIALS, user, tabData)
    }

    fun navigatePushNotifications(tabData: TabData){
        routerNavigate(SCREENS.SETTINGS_PUSH_NOTIFICATIONS, user, tabData)
    }

    fun updateHiddenMood() = launch {
        val isActive = user.hiddenMoodOn.not()

        viewState.showLoadingDialog()

        //TODO API call to change hidden mood? or just saved locally - should be true or false by default?

        user.hiddenMoodOn = isActive

        viewState.updateHiddenMoodSwitch(isActive)
        viewState.hideLoadingDialog()
    }

    fun logout() {
        //TODO:F call to API with null? fb access token -> user logged out from fb

        repository.clearUserData()

        router.replaceScreen(SCREENS.START)
    }

}