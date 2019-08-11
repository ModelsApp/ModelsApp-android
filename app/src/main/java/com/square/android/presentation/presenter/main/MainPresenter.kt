package com.square.android.presentation.presenter.main

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.main.MainView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class BadgeStateChangedEvent()

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {
    private val bus: EventBus by inject()

    private var currentScreenKey: String? = null

    private var checkedInitial = false

    fun navigationClicked(screenKey: String) {
        if (currentScreenKey != screenKey) {
            router.navigateTo(screenKey)
        }
        currentScreenKey = screenKey
    }

    init {
        checkPending()

        bus.register(this)
    }

    fun checkPending() = launch {
        if(!repository.isLoggedIn() || !repository.isProfileFilled()){
            navigationClicked(SCREENS.START)
        } else {
            val user = repository.getCurrentUser().await()
            if (user.isAcceptationPending || !user.accepted) {
                viewState.showUserPending()
                allowAndCheckSubs()
            } else {
                viewState.hideUserPending()
                if (!repository.isLoggedIn() || !repository.isProfileFilled()) {
                    router.replaceScreen(SCREENS.START)
                } else if (currentScreenKey != SCREENS.PLACES) {

                    if (!checkedInitial) {
                        navigationClicked(SCREENS.PLACES)
                        viewState.checkInitial()
                        allowAndCheckSubs()
                        checkedInitial = true
                    }

                    loadBadgeCount()

                    checkSubscriptions()
                } else{
                    checkSubscriptions()
                }

            }
        }
    }

    fun navigateTutorialVideos(){
        router.navigateTo(SCREENS.TUTORIAL_VIDEOS)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBadgeStateChanged(event: BadgeStateChangedEvent) {
        loadBadgeCount()
    }

    override fun onDestroy() {
        super.onDestroy()

        bus.unregister(this)
    }

    private fun loadBadgeCount() {
        launch {
            val count = repository.getBadgeCount().await()

            viewState.setActiveRedemptions(count.activeBooks)
        }
    }
}
