package com.square.android.presentation.view.campaigns

import com.square.android.data.pojo.CampaignInfo
import com.square.android.presentation.view.BaseView

interface CampaignsView : BaseView {
    fun showData(data: List<CampaignInfo>)
    fun updateCampaigns(data: List<CampaignInfo>)
}
