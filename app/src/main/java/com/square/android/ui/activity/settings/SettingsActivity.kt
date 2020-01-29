package com.square.android.ui.activity.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.settings.SettingsPresenter
import com.square.android.presentation.view.settings.SettingsView
import com.square.android.ui.activity.BaseTabActivity
import com.square.android.ui.activity.TabData
import com.square.android.ui.fragment.BaseTabFragment
import com.square.android.ui.fragment.settings.SettingsMainFragment
import com.square.android.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_settings.*

const val USER_EXTRA = "USER_EXTRA"

class SettingsActivity: BaseTabActivity(), SettingsView {

    @InjectPresenter
    lateinit var presenter: SettingsPresenter

    @ProvidePresenter
    fun providePresenter() = SettingsPresenter(intent.getParcelableExtra(USER_EXTRA))

    override fun provideTabNavigator(): BaseTabNavigator = SettingsNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_settings)

        provideNavViews(settingsTitle, rightButton, arrowBack)

        presenter.navigateToMain(TabData(getString(R.string.settings), BTN_TYPE.NEXT,btnVisible = true, btnEnabled = false))

        arrowBack.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        if(currentFragmentIndex == 0){
            presenter.exit()
        } else{
            super.onBackPressed()
        }
    }

    private class SettingsNavigator(activity: BaseTabActivity): BaseTabNavigator(activity, R.id.settingsContainer, true) {

        override fun createTabActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? = null

        override fun createTabFragment(screenKey: String?, data: Any?): BaseTabFragment = when (screenKey) {

            SCREENS.SETTINGS_MAIN -> { SettingsMainFragment.newInstance(data as Profile.User) }

            else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
        }

    }

}