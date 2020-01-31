package com.square.android.ui.fragment.settings

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.mapbox.android.core.permissions.PermissionsListener
import com.square.android.R
import com.square.android.data.local.LocalDataManager
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.settings.SettingsMainPresenter
import com.square.android.presentation.view.settings.SettingsMainView
import com.square.android.ui.activity.BaseTabActivity
import com.square.android.ui.activity.TabData
import com.square.android.ui.activity.settings.USER_EXTRA
import com.square.android.ui.dialogs.DialogHiddenMood
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.ui.dialogs.LogOutDialog
import com.square.android.ui.fragment.BaseTabFragment
import com.square.android.utils.PermissionsManager
import kotlinx.android.synthetic.main.fragment_settings_main.*
import org.jetbrains.anko.bundleOf
import org.koin.android.ext.android.inject

class SettingsMainFragment: BaseTabFragment(), SettingsMainView, PermissionsListener {
    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(user: Profile.User): SettingsMainFragment {
            val fragment = SettingsMainFragment()

            val args = bundleOf(USER_EXTRA to user)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: SettingsMainPresenter

    private val localDataManager: LocalDataManager by inject()

    private var loadingDialog: LoadingDialog? = null

    private var permissionsManager: PermissionsManager? = null

    @ProvidePresenter
    fun providePresenter() = SettingsMainPresenter(arguments?.getParcelable(USER_EXTRA) as Profile.User)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(activity!!)

        switchHiddenMood.isChecked = presenter.user.hiddenMoodOn

        if(PermissionsManager.areLocationPermissionsGranted(activity!!)){
            if(!repository.getLocationPermissionsOneTimeChecked()){
                repository.setGeolocationAllowed(true)
                repository.setLocationPermissionsOneTimeChecked(true)
            }
        }

        permissionsManager = PermissionsManager(this)

        switchAllowGeo.isChecked = PermissionsManager.areOwnLocationPermissionsGranted(activity!!)

        allowGeoLl.setOnClickListener {
            if(PermissionsManager.areLocationPermissionsGranted(activity!!)){
                switchAllowGeo.isChecked = repository.getGeolocationAllowed().not()
                repository.setGeolocationAllowed(repository.getGeolocationAllowed().not())
            } else{
                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(activity!!, permissions, 1387)

                permissionsManager!!.requestLocationPermissions(this)
            }
        }

        credentialsLl.setOnClickListener { presenter.navigateCredentials(TabData(getString(R.string.credentials), BaseTabActivity.BTN_TYPE.NEXT, btnVisible = true)) }
        pushNotificationsLl.setOnClickListener { presenter.navigatePushNotifications(TabData(getString(R.string.push_notifications))) }

        hiddenMoodLl.setOnClickListener {
            val dialogHiddenMood = DialogHiddenMood(activity!!)
            dialogHiddenMood.show(presenter.user.hiddenMoodOn, localDataManager) {
                presenter.updateHiddenMood()
            }
        }

        logOutLl.setOnClickListener {
            val logOutDialog = LogOutDialog(activity!!)
            logOutDialog.show {
                GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()

                }).executeAsync()

                presenter.logout()
            }
        }
    }

    override fun updateHiddenMoodSwitch(isActive: Boolean) {
        switchHiddenMood.isChecked = isActive
    }

    override fun tabBtnClicked() {

    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) { }

    override fun onPermissionResult(granted: Boolean) {
        switchAllowGeo.isChecked = granted
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