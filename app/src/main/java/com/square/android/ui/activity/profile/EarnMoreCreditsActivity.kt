package com.square.android.ui.activity.profile

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.extensions.copyToClipboard
import com.square.android.presentation.presenter.profile.EarnMoreCreditsPresenter
import com.square.android.presentation.view.profile.EarnMoreCreditsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_earn_more_credits.*
import ru.terrakok.cicerone.Navigator

class EarnMoreCreditsActivity: BaseActivity(), EarnMoreCreditsView{

    @InjectPresenter
    lateinit var presenter: EarnMoreCreditsPresenter

    @ProvidePresenter
    fun providePresenter() = EarnMoreCreditsPresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_earn_more_credits)

//      TODO: get and set personal referal code
        tvReferal.text = "1234"

        arrowBack.setOnClickListener { onBackPressed() }

        referalCopyLl.setOnClickListener {  copyToClipboard(tvReferal.text.toString(), true)  }

        btnShare.setOnClickListener {
            //TODO
        }

        btnRefer.setOnClickListener {
            //TODO
        }

        btnIntroduce.setOnClickListener {
            //TODO
        }

    }

}