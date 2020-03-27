package com.square.android.presentation.presenter.signUp

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.signUp.SignUpVerifyPhoneView
import com.square.android.ui.fragment.signUp.PhoneVerifiedEvent
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class SignUpVerifyPhonePresenter(val phoneNumber: String): BasePresenter<SignUpVerifyPhoneView>(){

    private val eventBus: EventBus by inject()

    fun sendCodeSms() = launch {
        //TODO API request to send code via sms
    }

    fun sendCodeToVerify(code: String) = launch {
        viewState.showLoadingDialog()

        val response = repository.verifyPhoneCode(code, phoneNumber).await()

        if(response.accepted!!){
            eventBus.post(PhoneVerifiedEvent())
            viewState.goBack()
        } else{
            viewState.showPinError()
        }

        viewState.hideLoadingDialog()
    }

}