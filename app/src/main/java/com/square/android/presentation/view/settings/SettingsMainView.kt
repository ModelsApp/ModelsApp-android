package com.square.android.presentation.view.settings

import com.square.android.presentation.view.LoadingView

interface SettingsMainView: LoadingView {

    fun updateHiddenMoodSwitch(isActive: Boolean)
}