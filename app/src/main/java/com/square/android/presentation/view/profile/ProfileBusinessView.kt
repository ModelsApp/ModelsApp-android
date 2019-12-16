package com.square.android.presentation.view.profile

import com.square.android.data.pojo.Profile
import com.square.android.presentation.view.BaseView

interface ProfileBusinessView : BaseView {
    fun showData(user: Profile.User)
}