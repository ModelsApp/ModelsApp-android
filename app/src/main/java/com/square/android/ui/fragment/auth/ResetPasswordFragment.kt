package com.square.android.ui.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.AuthData
import com.square.android.extensions.content
import com.square.android.extensions.hideKeyboard
import com.square.android.presentation.presenter.auth.ResetPasswordPresenter
import com.square.android.presentation.view.auth.ResetPasswordView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_auth_reset_password.*

class ResetPasswordFragment: BaseFragment(), ResetPasswordView {

    @InjectPresenter
    lateinit var presenter: ResetPasswordPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arrowBack.setOnClickListener { activity?.onBackPressed() }

        btnReset.setOnClickListener {
            activity?.hideKeyboard()
            presenter.forgotPasswordClicked(AuthData(et_email.content, "", ""))
        }
    }

    override fun showProgress() {
        btnReset.visibility = View.GONE
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
        btnReset.visibility = View.VISIBLE
    }

}