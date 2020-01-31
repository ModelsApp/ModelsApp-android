package com.square.android.presentation.view.profile

import com.square.android.data.pojo.Profile
import com.square.android.presentation.view.LoadingView

interface EditProfileView : LoadingView {
    fun showData(user: Profile.User)

    fun showBirthday(birthday: String)

    fun changeSaveBtn(enabled: Boolean)

}