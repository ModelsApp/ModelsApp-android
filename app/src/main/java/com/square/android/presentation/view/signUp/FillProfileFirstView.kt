package com.square.android.presentation.view.signUp

import com.mukesh.countrypicker.Country
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.view.BaseView

interface FillProfileFirstView : BaseView {
    fun showBirthday(displayBirthday: String)
    fun displayNationality(country: Country)
    fun displayGender(gender: String)
    fun showData(profileInfo: ProfileInfo)

    fun showDialInfo(country: Country)
}
