package com.square.android.ui.fragment.signUp

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.signUp.SignUpRequirementsPresenter
import com.square.android.presentation.view.signUp.SignUpRequirementsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_sign_up_requirements.*
import ru.terrakok.cicerone.Navigator

class SignUpRequirementsActivity: BaseActivity(), SignUpRequirementsView{

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    @InjectPresenter
    lateinit var presenter: SignUpRequirementsPresenter

    @ProvidePresenter
    fun providePresenter() = SignUpRequirementsPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_sign_up_requirements)

        arrowBack.setOnClickListener { onBackPressed() }
    }

}