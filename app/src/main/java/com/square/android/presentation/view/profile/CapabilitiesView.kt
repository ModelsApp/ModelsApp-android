package com.square.android.presentation.view.profile

import com.square.android.data.pojo.UserCapability
import com.square.android.presentation.view.LoadingView

interface CapabilitiesView : LoadingView {
    fun showData(data: List<UserCapability>, selectedItems: MutableList<String>)
}