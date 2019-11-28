package com.square.android.presentation.presenter.fillProfileReferral

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.network.errorMessage
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.fillProfileReferral.FillProfileReferralView

@InjectViewState
class FillProfileReferralPresenter(
        val info: ProfileInfo
) : BasePresenter<FillProfileReferralView>() {

    init {
        viewState.showData(info)
    }

    fun confirmClicked(referral: String) {
        info.referral = referral

        finishRegistration()
    }

    fun skipClicked() {
        info.referral = null

        finishRegistration()
    }

    private fun finishRegistration() {
        launch({
            viewState.showProgress()

            val images = info.images?.toMutableList()

            info.images = null
            val response = repository.fillProfile(info.apply { email = null }).await()

            val userId = repository.getUserId()
            images?.forEach {
                repository.addPhoto(userId, it).await()
            }

            repository.setProfileFilled(true)
            repository.saveProfileInfo("",0)

            viewState.hideProgress()

            router.showSystemMessage(response.message)
            viewState.sendFcmToken()
//            viewState.showPendingUser()
            router.replaceScreen(SCREENS.MAIN)
        }, { error ->
            viewState.hideProgress()
            viewState.showMessage(error.errorMessage)
        })
    }

}
