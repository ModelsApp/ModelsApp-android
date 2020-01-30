package com.square.android.presentation.view.settings

import com.mukesh.countrypicker.Country
import com.square.android.presentation.view.BaseView

interface SettingsCredentialsView: BaseView {
    fun showDialInfo(country: Country)
}