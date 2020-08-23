package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.ProfileSocialView
import com.square.android.ui.activity.profile.ActivePlanExtras

data class ProfileListData(val socialChannels: List<String>, val profession: List<String>, val specialities: List<String>, val capabilities: List<String>)

@InjectViewState
class ProfileSocialPresenter(user: Profile.User, var actualTokenInfo: BillingTokenInfo): BasePresenter<ProfileSocialView>(){


    //TODO waiting for api


    init {
        // load and pass social channels, profession, specialities, capabilities

        //TODO loading progress bar?
        launch {
//            val socialChannels = repository.getUserSocialChannels().await().map { it.name }
//            val specialities = repository.getUserSpecialities().await().userSpecialities.map { it.name }
//
            val capabilities = repository.getUserCapabilities().await()

//            val professions = repository.getUserProfessions().await().map { it.name }

            //TODO
//            viewState.showData(user, ProfileListData(socialChannels, specialities, professions, capabilities), actualTokenInfo)



            viewState.showData(user, actualTokenInfo)
        }
    }

    fun openEditProfile() {
        router.navigateTo(SCREENS.EDIT_PROFILE)
    }

    fun navigateToSocialChannels(){
        router.navigateTo(SCREENS.SOCIAL_CHANNELS)
    }

    fun navigateToSpecialities(){
        router.navigateTo(SCREENS.SPECIALITIES)
    }

    fun navigateToCapabilities(){
        router.navigateTo(SCREENS.CAPABILITIES)
    }

    fun navigateToProfessions(){
        router.navigateTo(SCREENS.PROFESSIONS)
    }

    fun navigateToActivePlan(){
        router.navigateTo(SCREENS.ACTIVE_PLAN, ActivePlanExtras(true, actualTokenInfo))
    }

    fun navigateToEarnMoreCredits(){
        router.navigateTo(SCREENS.EARN_MORE_CREDITS)
    }
}