package com.square.android.presentation.presenter.auth

import com.arellomobile.mvp.InjectViewState
import com.google.firebase.iid.FirebaseInstanceId
import com.square.android.SCREENS
import com.square.android.data.network.errorMessage
import com.square.android.data.network.response.AuthResponse
import com.square.android.data.pojo.AuthData
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.auth.LogInView
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class LoginData(
        val email: String,
        val password: String
)

@InjectViewState
class LogInPresenter : BasePresenter<LogInView>() {

    fun loginClicked(loginData: LoginData) {
        viewState.showProgress()
        launch({
            val response = repository.loginUser(loginData).await()
            authDone(response)
        }, { error ->
            viewState.hideProgress()

            viewState.showMessage(error.errorMessage)
        })
    }

    fun logInFb(fbToken: String) = launch({
        //TODO:F REMOVE
        viewState.hideProgress()

//        viewState.showProgress()
        //TODO:F send fb token to api
//        val response = repository.loginUserFb(fbToken).await()

//        authDone(response, true)
    }, { error ->
        viewState.hideProgress()

        viewState.showMessage(error.errorMessage)
    })

    fun forgotClicked(){
        router.navigateTo(SCREENS.RESET_PASSWORD)
    }

    fun navigateToSignUp(){
        router.navigateTo(SCREENS.SIGN_UP)
    }

    private fun authDone(response: AuthResponse, loggedInFacebook: Boolean = false) = launch({
        repository.setUserToken(response.token!!)

        val profile = repository.getCurrentUser().await()

        repository.setUserId(profile.id)
        repository.setLoggedIn(true)
        repository.setProfileFilled(true)
        repository.setLoggedInFacebook(loggedInFacebook)
        repository.setAvatarUrl(profile.photo)
        repository.setUserName(profile.name, profile.surname)
        repository.setSocialLink(profile.instagram.username)
        repository.setUserPaymentRequired(profile.isPaymentRequired)

        FirebaseInstanceId.getInstance().token
        viewState.sendFcmToken()

        router.replaceScreen(SCREENS.MAIN)

        router.showSystemMessage(response.message)
    }, { error ->
        viewState.hideProgress()

        viewState.showMessage(error.errorMessage)
    })

}