package com.square.android.ui.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.listeners.OnCountryPickerListener
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.extensions.getCountryNameByDialCode
import com.square.android.presentation.presenter.settings.SettingsCredentialsPresenter
import com.square.android.presentation.view.settings.SettingsCredentialsView
import com.square.android.ui.activity.BaseTabActivity
import com.square.android.ui.activity.TabData
import com.square.android.ui.activity.settings.USER_EXTRA
import com.square.android.ui.fragment.BaseTabFragment
import kotlinx.android.synthetic.main.fragment_settings_credentials.*
import org.jetbrains.anko.bundleOf

class SettingsCredentialsFragment: BaseTabFragment(), SettingsCredentialsView, OnCountryPickerListener {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(user: Profile.User): SettingsCredentialsFragment {
            val fragment = SettingsCredentialsFragment()

            val args = bundleOf(USER_EXTRA to user)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: SettingsCredentialsPresenter

    @ProvidePresenter
    fun providePresenter() = SettingsCredentialsPresenter(arguments?.getParcelable(USER_EXTRA) as Profile.User)

    private lateinit var countryPicker: CountryPicker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_credentials, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        countryPicker = CountryPicker.Builder().with(activity!!)
                .listener(this)
                .build()

        val number = presenter.user.phone.split(" ")

        etMobileNumber.setText(number[1])

        showDefaultDialInfo(number[0])

        et_email.setText(presenter.user.email)
        if(!presenter.user.emailConfirmed){
            //TODO: change error dialog layout to match project
            et_email.setCustomIconError(getString(R.string.confirm_your_email))
        }

        //TODO should those views be enabled? If yes, why there is Next button instead of Save
        et_email.isFocusable = false
        et_email.isClickable = false
        et_email.isEnabled = false
        etMobileNumber.isFocusable = false
        etMobileNumber.isClickable = false
        etMobileNumber.isEnabled = false
        dialCodeLl.isFocusable = false
        dialCodeLl.isClickable = false
        dialCodeLl.isEnabled = false

        dialCodeLl.setOnClickListener {showCountryDialDialog()}

        changePasswordLl.setOnClickListener { presenter.navigateChangePassword(TabData(getString(R.string.change_password), BaseTabActivity.BTN_TYPE.SAVE, btnVisible = true, btnEnabled = false)) }
    }

    private fun showDefaultDialInfo(dialCode: String) {
        showDialInfo(countryPicker.getCountryByName(dialCode.getCountryNameByDialCode()))
    }

    override fun showDialInfo(country: Country) {
        dialCode.text = country.dialCode
        dialFlag.setImageResource(country.flag)
    }

    private fun showCountryDialDialog() {
        activity?.let { countryPicker.showDialog(it) }
    }

    override fun onSelectCountry(country: Country) {
        showDialInfo(country)
    }

    override fun tabBtnClicked() {

    }

}