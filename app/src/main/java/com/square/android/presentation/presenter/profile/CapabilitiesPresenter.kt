package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Capability
import com.square.android.data.pojo.UserCapabilitiesData
import com.square.android.data.pojo.UserCapability
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.CapabilitiesView

@InjectViewState
class CapabilitiesPresenter(): BasePresenter<CapabilitiesView>(){

    private var data: List<UserCapability>? = null
    var selectedItems: MutableList<String> = mutableListOf()

    init {
        launch {
            viewState.showProgress()
            loadCapabilities()
            viewState.hideProgress()
        }
    }

    private fun loadCapabilities() = launch{
        data = repository.getUserCapabilities().await()

        selectedItems = data!!.filter { it.innerUserCapability != null }.map { it.innerUserCapability!!.name }.toMutableList()

        viewState.showData(data!!, selectedItems)
    }

    fun itemClicked(position: Int){
        val name = data!![position].name

        if(name in selectedItems){
            selectedItems.remove(name)
        } else{
            selectedItems.add(data!![position].name)
        }
    }

    fun saveClicked() = launch{
        viewState.showLoadingDialog()

        val itemsToSend = selectedItems.map { Capability(it) }

        repository.addUserCapabilities(UserCapabilitiesData(itemsToSend)).await()

        viewState.hideLoadingDialog()
    }

}