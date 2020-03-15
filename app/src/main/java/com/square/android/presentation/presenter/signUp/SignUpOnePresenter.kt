package com.square.android.presentation.presenter.signUp

import com.arellomobile.mvp.InjectViewState
import com.mukesh.countrypicker.Country
import com.square.android.data.pojo.SignUpData
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.signUp.SignUpOneView

@InjectViewState
class SignUpOnePresenter(val info: SignUpData) : BasePresenter<SignUpOneView>() {

    init {
        viewState.showData(info)
    }

    fun birthSelected(birthday: String) {
        info.birthDate = birthday
        viewState.showBirthday(birthday)
    }

//    fun nextClicked(name: String, surname: String, phone: String, phoneN: String, phoneC: String, account: String) {
//        info.name = name
//        info.surname = surname
//        info.phone = phone
//        info.phoneN = phoneN
//        info.phoneC = phoneC
//        info.instagramName = account
//
//        router.navigateTo(SCREENS.FILL_PROFILE_SECOND, info)
//    }

//    fun countrySelected(country: Country) {
//        info.nationality = country.name
//
//        viewState.displayNationality(country)
//    }

    fun countryDialSelected(country: Country) {
//        info.flagCode = country.flag

        viewState.showDialInfo(country)
    }
}
