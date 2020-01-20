package com.square.android.ui.fragment.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.firebase.iid.FirebaseInstanceId
import com.square.android.App
import com.square.android.R
import com.square.android.presentation.presenter.auth.AuthPresenter
import com.square.android.presentation.view.auth.AuthView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.TokenUtils
import kotlinx.android.synthetic.main.fragment_auth.*
import kotlinx.android.synthetic.main.pending_congratulations.view.*
import android.content.ActivityNotFoundException
import android.net.Uri

class AuthFragment: BaseFragment(), AuthView {

    @InjectPresenter
    lateinit var presenter: AuthPresenter

    private var pendingShown = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignup.setOnClickListener {
            if(!pendingShown) presenter.navigateToSignUp()
        }

        btnLogin.setOnClickListener {
            if(!pendingShown) presenter.navigateLogIn()
        }

        pending_screen.btnFollow.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/square.app"))
            i.setPackage(getString(R.string.instagram_package))
            try {
                startActivity(i)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/square.app")))
            }
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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            presenter.checkUserPending()
        }
    }

    override fun showUserPending() {
        pendingShown = true
        pending_screen.visibility = View.VISIBLE
    }

    override fun hideUserPending() {
        pendingShown = false
        pending_screen.visibility = View.GONE
    }
}
