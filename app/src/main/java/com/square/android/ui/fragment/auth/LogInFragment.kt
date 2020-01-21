package com.square.android.ui.fragment.auth

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.firebase.iid.FirebaseInstanceId
import com.square.android.App
import com.square.android.R
import com.square.android.data.pojo.AuthData
import com.square.android.extensions.content
import com.square.android.extensions.hideKeyboard
import com.square.android.presentation.presenter.auth.LogInPresenter
import com.square.android.presentation.view.auth.LogInView
import com.square.android.ui.activity.start.FacebookLogInEvent
import com.square.android.ui.activity.start.StartActivity
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.TokenUtils
import kotlinx.android.synthetic.main.fragment_auth_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

class LogInFragment: BaseFragment(), LogInView {

    @InjectPresenter
    lateinit var presenter: LogInPresenter

    private val eventBus: EventBus by inject()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFacebookLogInEvent(event: FacebookLogInEvent) {
        presenter.logInFb(event.data)

        //TODO:F REMOVE
        (activity as StartActivity).logOutRegister()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!eventBus.isRegistered(this)){
            eventBus.register(this)
        }

        arrowBack.setOnClickListener { activity?.onBackPressed() }

        signUpTv.setOnClickListener {
            presenter.navigateToSignUp()
        }

        btnContinueFacebook.setOnClickListener {
            showProgress()
            (activity as StartActivity).logInFacebook()
        }

        btnLogin.setOnClickListener {
            activity?.hideKeyboard()
            val authData = AuthData(et_email.content, et_password.content, "")
            presenter.loginClicked(authData)
        }

        forgotTv.setOnClickListener {
            presenter.forgotClicked()
        }
    }

    override fun sendFcmToken() {
        if (presenter.repository.getUserInfo().id != 0L) {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(activity as Activity) { instanceIdResult ->
                val newToken = instanceIdResult.token
                TokenUtils.sendTokenToApi(App.INSTANCE, presenter.repository, newToken)
            }
        }
    }

    override fun showProgress() {
        btnsLl.visibility = View.INVISIBLE
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.INVISIBLE
        btnsLl.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }

}