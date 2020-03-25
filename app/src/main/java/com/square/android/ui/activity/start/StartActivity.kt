package com.square.android.ui.activity.start

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.common.api.internal.zzc
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.presentation.presenter.start.StartPresenter
import com.square.android.presentation.view.start.StartView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.ui.fragment.auth.AuthFragment
import com.square.android.ui.fragment.auth.LogInFragment
import com.square.android.ui.fragment.auth.ResetPasswordFragment
import com.square.android.ui.fragment.intro.IntroFragment
import com.square.android.ui.fragment.signUp.*
import com.square.android.utils.ActivityUtils
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import java.lang.Exception
import com.facebook.GraphRequest
import com.facebook.AccessToken
import com.square.android.ui.activity.main.MainActivity

class FbData(val name:String, val surname: String, val fbAccessToken: String, val imageUrl: String?)
class RegisterFacebookEvent(val data: FbData)

class FacebookLogInEvent(val data: String?)

class StartActivity : BaseActivity(), StartView {

    @InjectPresenter
    lateinit var presenter: StartPresenter

    override fun provideNavigator(): Navigator = StartNavigator(this)

    private var loadingDialog: LoadingDialog? = null

    private val eventBus: EventBus by inject()

    private var loginFromRegister = true

    var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = this.window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            this.window.decorView.systemUiVisibility = flags
        }

        setContentView(R.layout.activity_start)

        callbackManager = CallbackManager.Factory.create()

        loadingDialog = LoadingDialog(this)

        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        if(loginFromRegister){
                            showLoadingDialog()

                            val request: GraphRequest = GraphRequest.newMeRequest(loginResult.accessToken,
                                    GraphRequest.GraphJSONObjectCallback { objc, response ->
                                        try {
                                            val name = objc.get("first_name") as String
                                            val surname = objc.get("last_name") as String
                                            var imageUrl: String? = null

                                            if (objc.has("picture")) {
                                                val is_silhouette = objc.getJSONObject("picture").getJSONObject("data").getBoolean("is_silhouette")

                                                if (!is_silhouette) {
                                                    imageUrl = objc.getJSONObject("picture").getJSONObject("data").getString("url")
                                                } else{
                                                    // user has no profile picture
                                                }

                                            }
                                            eventBus.post(RegisterFacebookEvent(FbData(name, surname, loginResult.accessToken.token, imageUrl)))
                                        } catch (e: Exception){
                                            Toast.makeText(this@StartActivity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                                            eventBus.post(FacebookLogInEvent(null))
                                        }

                                        hideLoadingDialog()
                                    })

                            val parameters = Bundle()
                            parameters.putString("fields", "name,picture,first_name,last_name")
                            request.parameters = parameters
                            request.executeAsync()
                        } else{
                            eventBus.post(FacebookLogInEvent(loginResult.accessToken.token))
                        }
                    }

                    override fun onCancel() { eventBus.post(FacebookLogInEvent(null)) }

                    override fun onError(exception: FacebookException) {
                        Toast.makeText(this@StartActivity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                        eventBus.post(FacebookLogInEvent(null))
                    }
                })

        presenter.navigate()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.last() is AuthFragment
                || supportFragmentManager.fragments.last() is zzc
                || supportFragmentManager.fragments.last() is IntroFragment) {
            finishAffinity()

            System.exit(0)
        }
        else {
            super.onBackPressed()
        }
    }

    private class StartNavigator(activity: androidx.fragment.app.FragmentActivity) : AppNavigator(activity, R.id.start_container) {
        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.MAIN -> context.intentFor<MainActivity>()
                    SCREENS.SIGN_UP_REQUIREMENTS -> context.intentFor<SignUpRequirementsActivity>()
                    SCREENS.SIGN_UP_VERIFY_PHONE -> context.intentFor<SignUpVerifyPhoneActivity>(EXTRA_PHONE_NUMBER to data as String)

                    else -> null
                }

        override fun createFragment(screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.INTRO -> IntroFragment()

                    SCREENS.AUTH -> AuthFragment()
                    SCREENS.LOGIN -> LogInFragment()
                    SCREENS.RESET_PASSWORD -> ResetPasswordFragment()
                    SCREENS.SIGN_UP -> SignUpMainFragment()

                    else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
                }

        override fun setupFragmentTransactionAnimation(command: Command,
                                                       currentFragment: Fragment?,
                                                       nextFragment: Fragment,
                                                       fragmentTransaction: FragmentTransaction) {

            if (command is Forward) {
                fragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right)
            } else {
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out)
            }

        }
    }

    fun logInRegister(){
        loginFromRegister = true
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
    }

    fun logInFacebook(){
        loginFromRegister = false
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
    }

    fun logOutRegister(){
        GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback {
            AccessToken.setCurrentAccessToken(null)
            LoginManager.getInstance().logOut()

        }).executeAsync()
    }

    override fun logOutFb(){
        GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback {
            AccessToken.setCurrentAccessToken(null)
            LoginManager.getInstance().logOut()
            presenter.navigateToAuth()
        }).executeAsync()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun showProgress() {}

    override fun hideProgress() {}

}
