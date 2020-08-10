package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.UserSocialChannel
import com.square.android.data.pojo.UserSocialChannelData
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.SocialChannelsView

@InjectViewState
class SocialChannelsPresenter(): BasePresenter<SocialChannelsView>(){

    private var data: List<UserSocialChannel>? = null

    init {
        launch {
            viewState.showProgress()
            loadSocialChannels()
            viewState.hideProgress()
        }
    }

    fun loadSocialChannels() = launch{
        data = repository.getUserSocialChannels().await()

        viewState.showData(data!!)
    }

    fun itemClicked(position: Int){
        val item = data!![position]

        viewState.showSocialDialog(item)
    }

    fun deleteSocialAccount(item: UserSocialChannel) = launch {
        viewState.showLoadingDialog()

        repository.deleteUserSocialChannel(item.id).await()

        loadSocialChannels()

        viewState.hideLoadingDialog()
    }

    fun addSocialAccount(item: UserSocialChannel, newAccountName: String) = launch {
        viewState.showLoadingDialog()

        repository.addUserSocialChannel(UserSocialChannelData(item.copy(accountName = newAccountName))).await()

        loadSocialChannels()

        viewState.hideLoadingDialog()
    }
}