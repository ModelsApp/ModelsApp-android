package com.square.android.ui.fragment.signUp

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.signUp.SignUpVerifyPhonePresenter
import com.square.android.presentation.view.signUp.SignUpVerifyPhoneView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.dialogs.LoadingDialog
import ru.terrakok.cicerone.Navigator

const val EXTRA_PHONE_NUMBER = "EXTRA_PHONE_NUMBER"

class SignUpVerifyPhoneActivity: BaseActivity(), SignUpVerifyPhoneView{

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    @InjectPresenter
    lateinit var presenter: SignUpVerifyPhonePresenter

    private var loadingDialog: LoadingDialog? = null

    @ProvidePresenter
    fun providePresenter() = SignUpVerifyPhonePresenter(intent.getStringExtra(EXTRA_PHONE_NUMBER))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_up_requirements)

        loadingDialog = LoadingDialog(this)

        presenter.sendCodeSms()

        arrowBack.setOnClickListener { onBackPressed() }
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showProgress() { }

    override fun hideProgress() { }
}