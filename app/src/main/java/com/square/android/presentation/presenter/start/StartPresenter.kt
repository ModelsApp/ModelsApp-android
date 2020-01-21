package com.square.android.presentation.presenter.start

import com.arellomobile.mvp.InjectViewState
import com.facebook.AccessToken
import com.square.android.SCREENS
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.start.StartView

@InjectViewState
class StartPresenter : BasePresenter<StartView>() {

    fun navigate() {
        if(repository.shouldDisplayIntro()){
            router.replaceScreen(SCREENS.INTRO)
        }
        else if(repository.isLoggedInFacebook() && repository.isProfileFilled()){
            //TODO:F remove later
            router.replaceScreen(SCREENS.MAIN)

            //TODO:F uncomment later
//            if(AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired){
//                router.replaceScreen(SCREENS.MAIN)
//            } else{
//                repository.setLoggedIn(false)
//                repository.setLoggedInFacebook(false)
//                viewState.logOutFb()
//            }
        }
        else if(repository.isLoggedIn() && repository.isProfileFilled()){
            router.replaceScreen(SCREENS.MAIN)
        }
        else {
            router.replaceScreen(SCREENS.AUTH)
        }

   // Old code with sign up data saving
//
//        if(repository.shouldDisplayIntro()){
//            router.replaceScreen(SCREENS.INTRO)
//        } else if(!repository.isLoggedIn() && repository.getFragmentNumber() <= 0){
//            router.replaceScreen(SCREENS.AUTH)
//        } else if(repository.isProfileFilled()) {
//            router.replaceScreen(SCREENS.MAIN)
//        }
//        else{
//            val fragmentNumber = repository.getFragmentNumber()
//
//            val gson = Gson()
//
//            var profileInfo = ProfileInfo()
//
//            try {
//                profileInfo = gson.fromJson(repository.getProfileInfo(), ProfileInfo::class.java)
//            } catch (e: Exception){}
//
//            when(fragmentNumber){
//                1 -> {
//                    router.replaceScreen(SCREENS.FILL_PROFILE_FIRST, profileInfo)
//                }
//                2 -> {
//                    router.replaceScreen(SCREENS.FILL_PROFILE_FIRST, profileInfo)
//                    router.navigateTo( SCREENS.FILL_PROFILE_SECOND, profileInfo)
//                   }
//                3 -> {
//                    router.replaceScreen(SCREENS.FILL_PROFILE_FIRST, profileInfo)
//                    router.navigateTo( SCREENS.FILL_PROFILE_SECOND, profileInfo)
//                    router.navigateTo( SCREENS.FILL_PROFILE_THIRD, profileInfo)
//                   }
//                4 -> {
//                    router.replaceScreen(SCREENS.FILL_PROFILE_FIRST, profileInfo)
//                    router.navigateTo( SCREENS.FILL_PROFILE_SECOND, profileInfo)
//                    router.navigateTo( SCREENS.FILL_PROFILE_THIRD, profileInfo)
//                    router.navigateTo( SCREENS.FILL_PROFILE_REFERRAL, profileInfo)
//                }
//                else -> router.replaceScreen(SCREENS.FILL_PROFILE_FIRST,profileInfo)
//            }
//        }

    }

    fun navigateToAuth(){
        router.replaceScreen(SCREENS.AUTH)
    }
}
