package com.square.android.presentation.view.profile

import com.square.android.data.pojo.UserSocialChannel
import com.square.android.presentation.view.LoadingView

interface SocialChannelsView : LoadingView {

    fun showData(data: List<UserSocialChannel>)
    fun showSocialDialog(item: UserSocialChannel)

}