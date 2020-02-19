package com.square.android.presentation.presenter.explore

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.CampaignInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.campaigns.CampaignsView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class CampaignsUpdatedEvent(val data: MutableList<CampaignInfo>)

@InjectViewState
class CampaignsPresenter(var data: MutableList<CampaignInfo>): BasePresenter<CampaignsView>() {

    private val eventBus: EventBus by inject()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCampaignsUpdatedEvent(event: CampaignsUpdatedEvent) {
        data = event.data

        viewState.updateCampaigns(data.toList())
    }

    init {
        eventBus.register(this)

        viewState.showData(data.toList())
    }

    fun itemClicked(campaignInfo: CampaignInfo) {
        eventBus.post(CampaignSelectedEvent(campaignInfo))
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }

}