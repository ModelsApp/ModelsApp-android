package com.square.android.ui.activity.settings

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.settings.SettingsPresenter
import com.square.android.presentation.view.settings.SettingsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_settings.*
import ru.terrakok.cicerone.Navigator

class SettingsActivity: BaseActivity(), SettingsView {

    @InjectPresenter
    lateinit var presenter: SettingsPresenter

    //TODO: there will be normal navigator with a fragment for each option(for options with arrow)
    @ProvidePresenter
    fun providePresenter() = SettingsPresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_settings)

        arrowBack.setOnClickListener { onBackPressed() }
    }

}