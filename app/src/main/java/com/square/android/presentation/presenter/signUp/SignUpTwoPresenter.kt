package com.square.android.presentation.presenter.signUp

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.SignUpData
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.signUp.SignUpTwoView

@InjectViewState
class SignUpTwoPresenter(val info: SignUpData): BasePresenter<SignUpTwoView>(){

    init {
        viewState.showData(info)
    }
}
