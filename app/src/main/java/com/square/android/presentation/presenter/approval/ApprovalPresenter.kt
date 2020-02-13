package com.square.android.presentation.presenter.approval

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.approval.ApprovalView

@InjectViewState
class ApprovalPresenter(val campaign: Campaign): BasePresenter<ApprovalView>() {


}