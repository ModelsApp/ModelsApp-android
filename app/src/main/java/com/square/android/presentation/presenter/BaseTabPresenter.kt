package com.square.android.presentation.presenter

import com.square.android.presentation.view.BaseView
import com.square.android.ui.activity.TabData
import com.square.android.ui.activity.TabFragmentData

abstract class BaseTabPresenter<V : BaseView>: BasePresenter<V>(){

    fun routerNavigate(screenKey: String, data: Any?, tabData: TabData){
        router.navigateTo(screenKey, TabFragmentData(data, tabData))
    }

    fun exit(){
        router.finishChain()
    }

}