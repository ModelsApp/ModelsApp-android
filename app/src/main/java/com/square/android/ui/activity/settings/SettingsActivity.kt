package com.square.android.ui.activity.settings

import android.content.Context
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.settings.SettingsPresenter
import com.square.android.presentation.view.settings.SettingsView
import com.square.android.ui.activity.BaseTabActivity
import com.square.android.ui.activity.TabData
import com.square.android.ui.activity.start.StartActivity
import com.square.android.ui.fragment.BaseTabFragment
import com.square.android.ui.fragment.settings.SettingsChangePasswordFragment
import com.square.android.ui.fragment.settings.SettingsCredentialsFragment
import com.square.android.ui.fragment.settings.SettingsMainFragment
import com.square.android.ui.fragment.settings.SettingsPushNotificationsFragment
import org.jetbrains.anko.intentFor

const val USER_EXTRA = "USER_EXTRA"

class SettingsActivity: BaseTabActivity(), SettingsView {

    @InjectPresenter
    lateinit var presenter: SettingsPresenter

    @ProvidePresenter
    fun providePresenter() = SettingsPresenter(intent.getParcelableExtra(USER_EXTRA))

    override fun provideTabNavigator(): BaseTabNavigator = SettingsNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter.navigateToMain(TabData(getString(R.string.settings)))
    }

    override fun onLastFragmentBackPressed() {
        presenter.exit()
    }

    private class SettingsNavigator(activity: BaseTabActivity): BaseTabNavigator(activity) {

        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.START ->
                        context.intentFor<StartActivity>()
                    else -> null
                }

        override fun createTabFragment(screenKey: String?, data: Any?): BaseTabFragment = when (screenKey) {

            SCREENS.SETTINGS_MAIN -> { SettingsMainFragment.newInstance(data as Profile.User) }

            SCREENS.SETTINGS_CREDENTIALS -> { SettingsCredentialsFragment.newInstance(data as Profile.User) }

            SCREENS.SETTINGS_CHANGE_PASSWORD -> { SettingsChangePasswordFragment.newInstance(data as Profile.User) }

            SCREENS.SETTINGS_PUSH_NOTIFICATIONS -> { SettingsPushNotificationsFragment.newInstance() }

            else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
        }
    }

}