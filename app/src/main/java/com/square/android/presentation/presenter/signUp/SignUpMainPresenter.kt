package com.square.android.presentation.presenter.signUp

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.signUp.SignUpMainView

@InjectViewState
class SignUpMainPresenter: BasePresenter<SignUpMainView>(){

    val profileInfo: ProfileInfo? = ProfileInfo()

}