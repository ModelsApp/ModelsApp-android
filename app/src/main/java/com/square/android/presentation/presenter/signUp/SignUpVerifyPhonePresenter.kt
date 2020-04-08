package com.square.android.presentation.presenter.signUp

import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.signUp.SignUpVerifyPhoneView
import com.square.android.ui.fragment.signUp.PhoneVerifiedEvent
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class SignUpVerifyPhonePresenter(val phoneNumber: String): BasePresenter<SignUpVerifyPhoneView>(){

    private val eventBus: EventBus by inject()

    private var confirmCode: String? = null

    fun sendCodeSms() = launch {
        confirmCode = null

        val response = repository.sendPhoneCode(phoneNumber).await()

        confirmCode = response.confirmCode
    }

    fun verifyCode(code: String) = launch {
        if(!TextUtils.isEmpty(confirmCode) && code == confirmCode){
            eventBus.post(PhoneVerifiedEvent())
            viewState.goBack()
        } else{
            viewState.showPinError()
        }
    }

}