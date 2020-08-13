package com.square.android.presentation.view.profile

import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.profile.ProfileListData
import com.square.android.presentation.view.BaseView

interface ProfileSocialView : BaseView {
    fun showData(user: Profile.User, actualTokenInfo: BillingTokenInfo)
//    fun showData(user: Profile.User, listData: ProfileListData, actualTokenInfo: BillingTokenInfo)
}