package com.square.android.presentation.presenter.signUp

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.SignUpData
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.signUp.SignUpTwoView

@InjectViewState
class SignUpTwoPresenter(val info: SignUpData): BasePresenter<SignUpTwoView>(){

    init {
        loadItems()
    }


    private fun loadItems() = launch {
        val specialitiesAndProfessions = repository.getRegisterSpecialitiesAndProfessions().await()
        val capabilities = repository.getRegisterCapabilities().await()

        info.specialitiesAndProfessionsLists = specialitiesAndProfessions
        info.capabilitiesList = capabilities

        viewState.showData(info)
    }
}
