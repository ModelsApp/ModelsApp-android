package com.square.android.presentation.presenter.auth

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.network.errorMessage
import com.square.android.data.network.response.AuthResponse
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.auth.AuthView
import com.google.firebase.iid.FirebaseInstanceId
import com.square.android.data.pojo.ProfileInfo

@InjectViewState
class AuthPresenter : BasePresenter<AuthView>() {

    fun checkUserPending() = launch {
        val profile = repository.getCurrentUser().await()
        if (profile.isAcceptationPending || !profile.accepted) {
            viewState.showUserPending()
            if (profile.isPaymentRequired) {
                allowAndCheckSubs()
            }
        } else {
            viewState.hideUserPending()
        }
    }

    fun navigateLogIn(){
        router.navigateTo(SCREENS.LOGIN)
    }

    fun navigateToSignUp(){
        router.navigateTo(SCREENS.SIGN_UP)
    }

}
