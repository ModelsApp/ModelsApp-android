package com.square.android.presentation.view.explore

import com.square.android.data.pojo.CampaignInfo
import com.square.android.presentation.view.BaseView

interface CampaignsListView : BaseView {
    fun showData(data: List<CampaignInfo>)
    fun updateEvents(data: List<CampaignInfo>)
}