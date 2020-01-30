package com.square.android.ui.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.settings.SettingsMainPresenter
import com.square.android.presentation.view.settings.SettingsMainView
import com.square.android.ui.activity.BaseTabActivity
import com.square.android.ui.activity.TabData
import com.square.android.ui.activity.settings.USER_EXTRA
import com.square.android.ui.fragment.BaseTabFragment
import kotlinx.android.synthetic.main.fragment_settings_main.*
import org.jetbrains.anko.bundleOf

class SettingsMainFragment: BaseTabFragment(), SettingsMainView {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(user: Profile.User): SettingsMainFragment {
            val fragment = SettingsMainFragment()

            val args = bundleOf(USER_EXTRA to user)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: SettingsMainPresenter

    @ProvidePresenter
    fun providePresenter() = SettingsMainPresenter(arguments?.getParcelable(USER_EXTRA) as Profile.User)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        credentialsLl.setOnClickListener { presenter.navigateCredentials(TabData(getString(R.string.credentials), BaseTabActivity.BTN_TYPE.NEXT, btnVisible = true)) }
    }

    override fun tabBtnClicked() {

    }

}