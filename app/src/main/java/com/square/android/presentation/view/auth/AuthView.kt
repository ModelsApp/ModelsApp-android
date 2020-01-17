package com.square.android.presentation.view.auth

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.presentation.view.BaseView

interface AuthView : BaseView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun sendFcmToken()

    fun hideUserPending()

//    @StateStrategyType(OneExecutionStateStrategy::class)
//    fun showPendingUser()

    @StateStrategyType(OneExecutionStateStrategy::class) // was without this annotation
    fun showUserPending()
}
