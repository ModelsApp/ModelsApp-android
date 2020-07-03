package com.square.android.presentation.view.signUp

import com.mukesh.countrypicker.Country
import com.square.android.data.pojo.ProfileInfo
import com.square.android.data.pojo.SignUpData
import com.square.android.presentation.view.BaseView

interface SignUpOneView : BaseView {
    fun showBirthday(displayBirthday: String)
    fun showData(signUpData: SignUpData)

    fun showDialInfo(country: Country)
}
