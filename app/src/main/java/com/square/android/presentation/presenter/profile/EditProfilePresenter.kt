package com.square.android.presentation.presenter.profile

import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.mukesh.countrypicker.Country
import com.square.android.SCREENS
import com.square.android.SCREENS.GALLERY
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.profile.ProfileUpdatedEvent
import com.square.android.presentation.view.profile.EditProfileView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class EditProfilePresenter : BasePresenter<EditProfileView>() {
    private val eventBus: EventBus by inject()

    private var user: Profile.User? = null

    init {
        loadData()
    }

    fun loadData() {
        launch {
//            viewState.showLoadingDialog()

            user = repository.getCurrentUser().await()

            viewState.showData(user!!)

            viewState.hideLoadingDialog()
        }
    }

    fun save(profileInfo: ProfileInfo) {
        launch {
            viewState.showLoadingDialog()

            if(TextUtils.isEmpty(profileInfo.instagramName)){
                if(TextUtils.isEmpty(user!!.instagram.username)){
                    profileInfo.instagramName = "???"
                } else{
                    profileInfo.instagramName = user!!.instagram.username
                }
            }

            if(TextUtils.isEmpty(profileInfo.phone)){
                if(TextUtils.isEmpty(user!!.phone)){
                    profileInfo.phone = "000000000"
                } else{
                    profileInfo.phone = user!!.phone
                }
            }

            val result = repository.fillProfile(profileInfo).await()

            repository.setUserName(profileInfo.name, profileInfo.surname)

            val event = ProfileUpdatedEvent()
            eventBus.post(event)

            router.showSystemMessage(result.message)

            viewState.changeSaveBtn(false)

            viewState.hideLoadingDialog()
        }
    }

// TODO:F log out probably will be in other fragment
    fun logout() {
        //TODO:F call to API with null? fb access token -> user logged out from fb

        repository.clearUserData()

        router.replaceScreen(SCREENS.START)
    }

    fun openGallery() {
        router.navigateTo(GALLERY, user)
    }
}
