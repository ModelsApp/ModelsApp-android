package com.square.android.presentation.view.auth

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.presentation.view.BaseView
import com.square.android.presentation.view.ProgressView

interface LogInView : ProgressView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun sendFcmToken()

}