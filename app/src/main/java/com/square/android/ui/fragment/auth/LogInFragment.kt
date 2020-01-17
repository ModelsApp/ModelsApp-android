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
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.TokenUtils
import kotlinx.android.synthetic.main.fragment_auth_login.*

class LogInFragment: BaseFragment(), LogInView {
    @InjectPresenter
    lateinit var presenter: LogInPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arrowBack.setOnClickListener { activity?.onBackPressed() }

        signUpTv.setOnClickListener {
            presenter.navigateToSignUp()
        }

        btnContinueFacebook.setOnClickListener {
            // TODO:F
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

}