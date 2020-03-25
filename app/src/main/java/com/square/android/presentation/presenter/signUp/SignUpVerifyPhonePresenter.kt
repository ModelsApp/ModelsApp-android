package com.square.android.presentation.presenter.signUp

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.signUp.SignUpVerifyPhoneView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class SignUpVerifyPhonePresenter(val phoneNumber: String): BasePresenter<SignUpVerifyPhoneView>(){

    private val eventBus: EventBus by inject()

    fun sendCodeSms() = launch {
        viewState.showLoadingDialog()

        //TODO API request to send code via sms

        viewState.hideLoadingDialog()
    }

    fun sendCodeToVerify(){

        //TODO eventBus send PhoneVerifiedEvent when done successfully
    }

}