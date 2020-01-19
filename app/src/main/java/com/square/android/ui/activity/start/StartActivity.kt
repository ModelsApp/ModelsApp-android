package com.square.android.ui.activity.start

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.common.api.internal.zzc
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.start.StartPresenter
import com.square.android.presentation.view.start.StartView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.main.MainActivity
import com.square.android.ui.fragment.auth.AuthFragment
import com.square.android.ui.fragment.auth.LogInFragment
import com.square.android.ui.fragment.auth.ResetPasswordFragment
import com.square.android.ui.fragment.intro.IntroFragment
import com.square.android.ui.fragment.signUp.*
import com.square.android.utils.ActivityUtils
import org.jetbrains.anko.intentFor
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

class StartActivity : BaseActivity(), StartView {

    @InjectPresenter
    lateinit var presenter: StartPresenter

    override fun provideNavigator(): Navigator = StartNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = this.window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            this.window.decorView.systemUiVisibility = flags
        }

        setContentView(R.layout.activity_start)
    }

    override fun onBackPressed() {
        //TODO:F check if it is ok
        if (supportFragmentManager.fragments.last() is SignUpOneFragment
                || supportFragmentManager.fragments.last() is AuthFragment
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

                    else -> null
                }

        //TODO WHEN DONE - remove all old content(classes, layouts, icons, styles etc. )
        override fun createFragment(screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.INTRO -> IntroFragment()

                    SCREENS.AUTH -> AuthFragment()
                    SCREENS.LOGIN -> LogInFragment()
                    SCREENS.RESET_PASSWORD -> ResetPasswordFragment()
                    SCREENS.SIGN_UP -> SignUpMainFragment()

//                    SCREENS.FILL_PROFILE_FIRST -> SignUpOneFragment.newInstance(data as ProfileInfo)
//                    SCREENS.FILL_PROFILE_SECOND -> FillProfileSecondFragment.newInstance(data as ProfileInfo)
//                    SCREENS.FILL_PROFILE_THIRD -> FillProfileThirdFragment.newInstance(data as ProfileInfo)
//                    SCREENS.FILL_PROFILE_REFERRAL -> FillProfileReferralFragment.newInstance(data as ProfileInfo)
                    else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
                }

        override fun setupFragmentTransactionAnimation(command: Command,
                                                       currentFragment: Fragment?,
                                                       nextFragment: Fragment,
                                                       fragmentTransaction: FragmentTransaction) {

            if(command is Forward){
                fragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right)
            } else{
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out)
            }

        }
    }
}
