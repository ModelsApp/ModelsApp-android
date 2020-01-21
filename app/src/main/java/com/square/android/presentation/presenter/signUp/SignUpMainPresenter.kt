package com.square.android.presentation.presenter.signUp

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.network.errorMessage
import com.square.android.data.pojo.AuthData
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.signUp.SignUpMainView

@InjectViewState
class SignUpMainPresenter: BasePresenter<SignUpMainView>(){

    val profileInfo: ProfileInfo = ProfileInfo()

    //TODO:F change API - just one request to register user instead of four(repository.registerUser, repository.addPhoto, repository.fillProfile, send fb token)
    fun register() = launch({
        viewState.showLoadingDialog()

        val response = repository.registerUser(AuthData(profileInfo.email!!, profileInfo.password!!, profileInfo.password!!)).await()

        repository.setUserToken(response.token!!)

        var profile = repository.getCurrentUser().await()

        repository.setUserId(profile.id)

        profileInfo.image?.let {
            repository.addPhoto(profile.id, it).await()
        }

        repository.fillProfile(profileInfo).await()

//      //TODO:F send profileInfo.fbToken to api

        profile = repository.getCurrentUser().await()

        viewState.sendFcmToken()

        repository.setLoggedIn(true)
        repository.setLoggedInFacebook(true)
        repository.setProfileFilled(true)
        repository.setAvatarUrl(profile.photo)
        repository.setUserName(profile.name, profile.surname)
        repository.setSocialLink(profile.instagram.username)
        repository.setUserPaymentRequired(profile.isPaymentRequired)

        viewState.hideLoadingDialog()
        router.replaceScreen(SCREENS.MAIN)

    }, { error ->
        viewState.hideLoadingDialog()
        viewState.showMessage(error.errorMessage)
    })

}