package com.square.android.presentation.presenter.signUp

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.SignUpData
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.signUp.SignUpThreeView

@InjectViewState
class SignUpThreePresenter(val info: SignUpData) : BasePresenter<SignUpThreeView>() {
//    fun nextClicked(photos: List<ByteArray>, photosUri: List<Uri>) {
//        // crashing - out of memory
////        info.imagesUri = photosUri
//
//        info.images = photos
//        router.navigateTo(SCREENS.FILL_PROFILE_REFERRAL, info)
//    }

    fun navigateToRequirements(){
        router.navigateTo(SCREENS.SIGN_UP_REQUIREMENTS)
    }
}