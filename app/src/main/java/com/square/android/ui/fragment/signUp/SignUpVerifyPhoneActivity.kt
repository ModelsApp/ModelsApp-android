package com.square.android.ui.fragment.signUp

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.extensions.hideKeyboard
import com.square.android.extensions.onTextChanged
import com.square.android.presentation.presenter.signUp.SignUpVerifyPhonePresenter
import com.square.android.presentation.view.signUp.SignUpVerifyPhoneView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_sign_up_verify_phone.*
import ru.terrakok.cicerone.Navigator
import android.os.CountDownTimer
import com.square.android.R
import com.square.android.extensions.clearText
import com.squareup.moshi.JsonClass

const val EXTRA_PHONE_NUMBER = "EXTRA_PHONE_NUMBER"

@JsonClass(generateAdapter = true)
data class ConfirmPhoneCodeData(
        val confirmCode: String,
        val phone: String
)

class SignUpVerifyPhoneActivity: BaseActivity(), SignUpVerifyPhoneView{

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    @InjectPresenter
    lateinit var presenter: SignUpVerifyPhonePresenter

    private var loadingDialog: LoadingDialog? = null

    var time: Int = 25

    @ProvidePresenter
     fun providePresenter() = SignUpVerifyPhonePresenter(intent.getStringExtra(EXTRA_PHONE_NUMBER))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_sign_up_verify_phone)

        loadingDialog = LoadingDialog(this)

        presenter.sendCodeSms()

        phoneNumberTv.text = presenter.phoneNumber

        txt_pin_entry.setOnPinEnteredListener {
            hideKeyboard()

            presenter.sendCodeToVerify(it.toString())
        }

        txt_pin_entry.onTextChanged {
            codeError.visibility = View.INVISIBLE
        }

        startTimer()

        sendAgain.setOnClickListener {
            txt_pin_entry.clearText()
            presenter.sendCodeSms()
            startTimer()
        }

        arrowBack.setOnClickListener { onBackPressed() }
    }

    private fun startTimer(){
        sendAgain.isEnabled = false
        time = 25

        object : CountDownTimer(25000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                sendAgain.text = (getString(R.string.send_again) + " (" + time +")")
                time--
            }

            override fun onFinish() {
                sendAgain.text = getString(R.string.send_again)
                sendAgain.isEnabled = true
            }

        }.start()
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun goBack() { onBackPressed() }

    override fun showPinError() {
        codeError.visibility = View.VISIBLE
    }

    override fun showProgress() { }

    override fun hideProgress() { }
}