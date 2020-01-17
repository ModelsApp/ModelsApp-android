package com.square.android.presentation.presenter.auth

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.network.errorMessage
import com.square.android.data.pojo.AuthData
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.auth.ResetPasswordView

@InjectViewState
class ResetPasswordPresenter: BasePresenter<ResetPasswordView>() {

    fun forgotPasswordClicked(authData: AuthData) {
        viewState.showProgress()
        launch({
            val response = repository.resetPassword(authData).await()

            viewState.hideProgress()

            router.showSystemMessage(response.message)

        }, { error ->
            viewState.hideProgress()

            viewState.showMessage(error.errorMessage)
        })
    }

}