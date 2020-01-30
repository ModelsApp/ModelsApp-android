package com.square.android.ui.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.extensions.isValid
import com.square.android.extensions.onTextChanged
import com.square.android.presentation.presenter.settings.SettingsChangePasswordPresenter
import com.square.android.presentation.view.settings.SettingsChangePasswordView
import com.square.android.ui.activity.settings.USER_EXTRA
import com.square.android.ui.fragment.BaseTabFragment
import kotlinx.android.synthetic.main.fragment_settings_change_password.*
import org.jetbrains.anko.bundleOf

class SettingsChangePasswordFragment: BaseTabFragment(), SettingsChangePasswordView {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(user: Profile.User): SettingsChangePasswordFragment {
            val fragment = SettingsChangePasswordFragment()

            val args = bundleOf(USER_EXTRA to user)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: SettingsChangePasswordPresenter

    @ProvidePresenter
    fun providePresenter() = SettingsChangePasswordPresenter(arguments?.getParcelable(USER_EXTRA) as Profile.User)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actual_password.onTextChanged { validateFields() }
        new_password.onTextChanged { validateFields() }
        confirm_password.onTextChanged { validateFields() }
    }

    private fun validateFields(){
        if(actual_password.isValid() && new_password.isValid() && confirm_password.isValid()){
            setTabBtnEnabled(true)
        } else {
            setTabBtnEnabled(false)
        }
    }

    override fun tabBtnClicked() {
        //TODO: no change password in API
    }

}