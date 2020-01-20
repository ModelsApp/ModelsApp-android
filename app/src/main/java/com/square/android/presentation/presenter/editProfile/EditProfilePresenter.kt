package com.square.android.presentation.presenter.editProfile

import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.mukesh.countrypicker.Country
import com.square.android.SCREENS
import com.square.android.SCREENS.GALLERY
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.profile.ProfileUpdatedEvent
import com.square.android.presentation.view.editProfile.EditProfileView
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

            viewState.showProgress()

            user = repository.getCurrentUser().await()

            viewState.showData(user!!, repository.getPushNotificationsAllowed())

            viewState.hideProgress()
        }
    }

    fun save(profileInfo: ProfileInfo) {
        launch {
            viewState.showProgress()

            if(TextUtils.isEmpty(profileInfo.instagramName)){
                profileInfo.instagramName = "???"
            }

            if(TextUtils.isEmpty(profileInfo.referral)){
                if(TextUtils.isEmpty(user!!.referralCode)){
                    profileInfo.referral = "0000"
                } else{
                    profileInfo.referral = user!!.referralCode
                }
            }

            val result = repository.fillProfile(profileInfo).await()

            repository.setUserName(profileInfo.name, profileInfo.surname)

            val event = ProfileUpdatedEvent()
            eventBus.post(event)

            router.showSystemMessage(result.message)
            
            router.exit()
        }
    }

    fun birthSelected(date: String) {
        viewState.showBirthday(date)
    }

    fun logout() {
        //TODO:F call to API with null? fb access token -> user logged out from fb

        repository.clearUserData()

        router.replaceScreen(SCREENS.START)
    }

    fun countrySelected(country: Country) {
        viewState.displayNationality(country)
    }

    fun openGallery() {
        router.navigateTo(GALLERY, user)
    }
}
