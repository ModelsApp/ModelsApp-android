package com.square.android.presentation.presenter.signUp

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.network.errorMessage
import com.square.android.data.pojo.SignUpData
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.signUp.SignUpMainView

@InjectViewState
class SignUpMainPresenter: BasePresenter<SignUpMainView>(){

    val signUpData: SignUpData = SignUpData()

    //TODO:F change API - just one request to register user instead of three(repository.registerUser, repository.addPhoto, send fb token)?
    fun register() = launch({
        viewState.showLoadingDialog()

        //TODO:F change to SignUpData from ProfileInfo in signUp fragments and HERE

        //TODO:F no name and surname in SignUpData FOR NOW?
        val response = repository.registerUser(signUpData).await()

        repository.setUserToken(response.token!!)

        var profile = repository.getCurrentUser().await()

        repository.setUserId(profile.id)

        signUpData.image?.let {
            repository.addPhoto(profile.id, it).await()
        }

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