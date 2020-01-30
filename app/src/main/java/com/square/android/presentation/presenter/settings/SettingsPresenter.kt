package com.square.android.presentation.presenter.settings

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.BaseTabPresenter
import com.square.android.presentation.view.settings.SettingsView
import com.square.android.ui.activity.BaseTabActivity
import com.square.android.ui.activity.TabData
import com.square.android.ui.activity.TabFragmentData
import com.square.android.ui.fragment.BaseTabFragment

@InjectViewState
class SettingsPresenter(var user: Profile.User): BaseTabPresenter<SettingsView>(){

    fun navigateToMain(tabData: TabData){
        routerNavigate(SCREENS.SETTINGS_MAIN, user, tabData)
    }

    fun exit(){
        router.finishChain()
    }

}