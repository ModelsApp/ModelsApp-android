package com.square.android.ui.fragment.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.SignUpData
import com.square.android.presentation.presenter.signUp.SignUpTwoPresenter
import com.square.android.presentation.view.signUp.SignUpTwoView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_sign_up_2.*
import org.jetbrains.anko.bundleOf

private const val EXTRA_MODEL = "EXTRA_MODEL"

class SignUpTwoFragment: BaseFragment(), SignUpTwoView {

    @InjectPresenter
    lateinit var presenter: SignUpTwoPresenter

    @ProvidePresenter
    fun providePresenter(): SignUpTwoPresenter = SignUpTwoPresenter(getModel())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        switchVat.setOnCheckedChangeListener { buttonView, isChecked ->
            vatNumberRl.visibility = if(isChecked) View.VISIBLE else View.GONE
        }



//        showDefaultDialInfo()
//
//        nationalityLl.setOnClickListener { spinnerNationality.callOnClick() }
//        spinnerNationality.adapter = SimpleSpinnerAdapter(activity!!, DROPDOWN_ITEM_TYPE_CHECKMARK, resources.getStringArray(R.array.nationalities_array).toList())
//
//        tvDateOfBirth.setOnClickListener { showBirthDialog() }
//
//        genderLl.setOnClickListener { spinnerGender.callOnClick() }
//        spinnerGender.adapter = SimpleSpinnerAdapter(activity!!, DROPDOWN_ITEM_TYPE_CHECKMARK, resources.getStringArray(R.array.genders).toList(), halfSize = true)
//
//        dialCodeLl.setOnClickListener {showCountryDialDialog()}
//
//        etMobileNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())
//
//        passwordIcon.setOnClickListener {
//            if(passwordVisible){
//                passwordIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(passwordIcon.context, android.R.color.black))
//
//                et_password.transformationMethod = PasswordTransformationMethod();
//
//                passwordVisible = false
//            } else{
//                passwordIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(passwordIcon.context, R.color.text_gray))
//
//                et_password.transformationMethod = null
//
//                passwordVisible = true
//            }
//        }
//
//        termsCbLl.setOnClickListener { termsCb.isChecked = !termsChecked }
//
//        termsCb.setOnCheckedChangeListener { buttonView, isChecked ->
//            termsChecked = isChecked
//        }
    }

    override fun showData(signUpData: SignUpData) {

    }

    override fun validate(): Boolean {
        var allOk = true

//        if(!isValid(spinnerGender.selectedItem.toString())){
//            genderError.visibility = View.VISIBLE
//
//            allOk = false
//        } else{
//            genderError.visibility = View.GONE
//        }

//        if(!isValid(et_password.content)){
//            if(!passwordVisible){
//                passwordIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(passwordIcon.context, R.color.text_gray))
//                et_password.transformationMethod = null
//                passwordVisible = true
//            }
//
//            et_password.setText("")
//            et_password.showCustomError(getString(R.string.password_error))
//
//            allOk = false
//        }

//        if(!TextUtils.isEmpty(et_referral.content) && et_referral.content.length != REFERRAL_CODE_LENGTH){
//            et_referral.setText("")
//            et_referral.showCustomError(getString(R.string.referral_error))
//
//            allOk = false
//        }
//
//        if(!termsCb.isChecked){
//            termsError.visibility = View.VISIBLE
//
//            allOk = false
//        } else{
//            termsError.visibility = View.GONE
//        }

        if(allOk){
            presenter.info.vatNumber = if(switchVat.isChecked) et_vat_number.text.toString() else ""

//            presenter.info.nationality = spinnerNationality.selectedItem.toString()
//            // date of birth is added in presenter
//            presenter.info.gender = spinnerGender.selectedItem.toString()
//            presenter.info.phone = "${dialCode.content} ${etMobileNumber.content.trim()}"
//            presenter.info.email = et_email.content
//            presenter.info.password = et_password.content
//            presenter.info.referral = et_referral.content
        }

        return allOk
    }






    private fun isValid(item: CharSequence) = item.toString().trim().isNotEmpty()

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(info: SignUpData): SignUpTwoFragment {
            val fragment = SignUpTwoFragment()

            val args = bundleOf(EXTRA_MODEL to info)
            fragment.arguments = args

            return fragment
        }
    }

    private fun getModel(): SignUpData {
        return arguments?.getParcelable(EXTRA_MODEL) as SignUpData
    }

}