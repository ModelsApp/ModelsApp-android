package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.CapabilitiesView

@InjectViewState
class CapabilitiesPresenter(): BasePresenter<CapabilitiesView>(){


    //TODO waiting for api


//    private var data: List<UserSocialChannel>? = null

    init {
//        launch {
//            viewState.showProgress()
//            loadSocialChannels()
//            viewState.hideProgress()
//        }
    }

//    private fun loadSocialChannels() = launch{
//        data = repository.getUserSocialChannels().await()
//
//        viewState.showData(data!!)
//    }
//
//    fun itemClicked(position: Int){
//        val item = data!![position]
//
//        viewState.showSocialDialog(item)
//    }
//
//    fun deleteSocialAccount(item: UserSocialChannel) = launch {
//        viewState.showLoadingDialog()
//
//        //TODO delete doesn't work
//        repository.deleteUserSocialChannel(item.userChannel!!.id).await()
//
//        loadSocialChannels()
//
//        viewState.hideLoadingDialog()
//    }
//
//    fun addSocialAccount(item: UserSocialChannel, newAccountName: String) = launch {
//        viewState.showLoadingDialog()
//
//        if(!item.userChannel?.accountName.textIsEmpty()){
//            //TODO delete doesn't work
//            repository.deleteUserSocialChannel(item.userChannel!!.id).await()
//        }
//
//        val userId = repository.getUserId()
//
//        repository.addUserSocialChannel(UserSocialChannelData( ModelSocialChannel(
//                id = item.id,
//                name = item.name,
//                accountName = newAccountName,
//                userId = userId
//        )
//        )).await()
//
//        loadSocialChannels()
//
//        viewState.hideLoadingDialog()
//    }
}