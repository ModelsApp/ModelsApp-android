package com.square.android.presentation.view.signUp

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.presentation.view.LoadingView

interface SignUpMainView : LoadingView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun sendFcmToken()
}