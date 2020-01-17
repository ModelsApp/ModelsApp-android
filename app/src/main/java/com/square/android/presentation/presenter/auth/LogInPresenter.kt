package com.square.android.presentation.presenter.auth

import com.arellomobile.mvp.InjectViewState
import com.google.firebase.iid.FirebaseInstanceId
import com.square.android.SCREENS
import com.square.android.data.network.errorMessage
import com.square.android.data.network.response.AuthResponse
import com.square.android.data.pojo.AuthData
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.auth.LogInView

@InjectViewState
class LogInPresenter : BasePresenter<LogInView>() {

    fun loginClicked(authData: AuthData) {
        viewState.showProgress()
        launch({
            val response = repository.loginUser(authData).await()
            authDone(response)
        }, { error ->
            viewState.hideProgress()

            viewState.showMessage(error.errorMessage)
        })
    }

    fun forgotClicked(){
        router.navigateTo(SCREENS.RESET_PASSWORD)
    }

    fun navigateToSignUp(){
        router.navigateTo(SCREENS.SIGN_UP)
    }

        // TODO:F register will work different now - all this will be moved to sign up
//    fun registerClicked(authData: AuthData) {
//        viewState.showProgress()
//
//        launch({
//            val response = repository.registerUser(authData).await()
//            authDone(response)
//        }, { error ->
//            viewState.hideProgress()
//
//            viewState.showMessage(error.errorMessage)
//        })
//    }

    private fun authDone(response: AuthResponse) = launch({
        repository.setUserToken(response.token!!)

        val profile = repository.getCurrentUser().await()

        repository.setUserId(profile.id)
        repository.setLoggedIn(true)
        repository.setAvatarUrl(profile.photo)
        repository.setUserName(profile.name, profile.surname)
        repository.setSocialLink(profile.instagram.username)
        repository.setUserPaymentRequired(profile.isPaymentRequired)

//        when {
//            TODO:F new sign up ac with fragments
//            profile.newUser -> router.replaceScreen(SCREENS.FILL_PROFILE_FIRST, ProfileInfo())
//            else -> {
//                repository.setProfileFilled(true)
//
//                router.replaceScreen(SCREENS.MAIN)
//            }
//        }

//        FirebaseInstanceId.getInstance().token
//        viewState.sendFcmToken()
//
//        router.showSystemMessage(response.message)
    }, { error ->
        viewState.hideProgress()

        viewState.showMessage(error.errorMessage)
    })

}