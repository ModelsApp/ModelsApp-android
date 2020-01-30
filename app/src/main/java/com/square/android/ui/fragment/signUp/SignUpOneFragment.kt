package com.square.android.ui.fragment.signUp

import android.content.res.ColorStateList
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.listeners.OnCountryPickerListener
import com.square.android.R
import com.square.android.data.pojo.ProfileInfo
import com.square.android.extensions.content
import com.square.android.extensions.toOrdinalString
import com.square.android.presentation.presenter.signUp.SignUpOnePresenter
import com.square.android.presentation.view.signUp.SignUpOneView
import com.square.android.ui.dialogs.DatePickDialog
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_sign_up_1.*
import org.jetbrains.anko.bundleOf
import java.util.*
import com.square.android.ui.DROPDOWN_ITEM_TYPE_CHECKMARK
import com.square.android.ui.SimpleSpinnerAdapter

private const val EXTRA_MODEL = "EXTRA_MODEL"
private const val COUNTRY_DEFAULT_ISO = "US"
private const val REFERRAL_CODE_LENGTH = 4

class SignUpOneFragment: BaseFragment(), SignUpOneView, OnCountryPickerListener  {

    @InjectPresenter
    lateinit var presenter: SignUpOnePresenter

    @ProvidePresenter
    fun providePresenter(): SignUpOnePresenter = SignUpOnePresenter(getModel())

    private lateinit var countryPicker: CountryPicker

    private var passwordVisible = false

    private var termsChecked = false

    private var fromDial: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_1, container, false)
    }

    override fun validate(): Boolean {
        var allOk = true

        if(!isValid(spinnerNationality.selectedItem.toString())){
            nationalityError.visibility = View.VISIBLE

            allOk = false
        } else{
            nationalityError.visibility = View.GONE
        }

        if(!isValid(tvDateOfBirth.text)){
            dateOfBirthError.visibility = View.VISIBLE

            allOk = false
        } else{
            dateOfBirthError.visibility = View.GONE
        }

        if(!isValid(spinnerGender.selectedItem.toString())){
            genderError.visibility = View.VISIBLE

            allOk = false
        } else{
            genderError.visibility = View.GONE
        }

        if(!isValid(etMobileNumber.content)){
            etMobileNumber.setText("")
            etMobileNumber.showCustomError(getString(R.string.phone_error))

            allOk = false
        }

        if(!isValid(et_email.content)){
            et_email.setText("")
            et_email.showCustomError(getString(R.string.email_error))

            allOk = false
        }

        if(!isValid(et_password.content)){
            if(!passwordVisible){
                passwordIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(passwordIcon.context, R.color.text_gray))
                et_password.transformationMethod = null
                passwordVisible = true
            }

            et_password.setText("")
            et_password.showCustomError(getString(R.string.password_error))

            allOk = false
        }

        if(!TextUtils.isEmpty(et_referral.content) && et_referral.content.length != REFERRAL_CODE_LENGTH){
            et_referral.setText("")
            et_referral.showCustomError(getString(R.string.referral_error))

            allOk = false
        }

        if(!termsCb.isChecked){
            termsError.visibility = View.VISIBLE

            allOk = false
        } else{
            termsError.visibility = View.GONE
        }

        if(allOk){
            presenter.info.nationality = spinnerNationality.selectedItem.toString()
            // date of birth is added in presenter
            presenter.info.gender = spinnerGender.selectedItem.toString()
            presenter.info.phone = "${dialCode.content} ${etMobileNumber.content.trim()}"
            presenter.info.email = et_email.content
            presenter.info.password = et_password.content
            presenter.info.referral = et_referral.content

            // old code
//                    val name = form.formProfileName.content
//                    val surname = form.formProfileLastName.content
//                    val phone = "${form.formDialCode.content} ${form.formDialPhoneNumber.content}"
//                    val phoneN = form.formDialPhoneNumber.content
//                    val phoneC = form.formDialCode.content
//                    val account = form.formProfileAccount.content
//                    presenter.nextClicked(name = name, surname = surname, phone = phone, phoneN = phoneN, phoneC = phoneC, account = account)
        }

        return allOk
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showDefaultDialInfo()

        nationalityLl.setOnClickListener { spinnerNationality.callOnClick() }
        spinnerNationality.adapter = SimpleSpinnerAdapter(activity!!, DROPDOWN_ITEM_TYPE_CHECKMARK, resources.getStringArray(R.array.nationalities_array).toList())

        tvDateOfBirth.setOnClickListener { showBirthDialog() }

        genderLl.setOnClickListener { spinnerGender.callOnClick() }
        spinnerGender.adapter = SimpleSpinnerAdapter(activity!!, DROPDOWN_ITEM_TYPE_CHECKMARK, resources.getStringArray(R.array.genders).toList(), halfSize = true)

        dialCodeLl.setOnClickListener {showCountryDialDialog()}

        etMobileNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        passwordIcon.setOnClickListener {
            if(passwordVisible){
                passwordIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(passwordIcon.context, android.R.color.black))

                et_password.transformationMethod = PasswordTransformationMethod();

                passwordVisible = false
            } else{
                passwordIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(passwordIcon.context, R.color.text_gray))

                et_password.transformationMethod = null

                passwordVisible = true
            }
        }

        termsCbLl.setOnClickListener { termsCb.isChecked = !termsChecked }

        termsCb.setOnCheckedChangeListener { buttonView, isChecked ->
            termsChecked = isChecked
        }
    }

    override fun showDialInfo(country: Country) {
        dialCode.text = country.dialCode
        dialFlag.setImageResource(country.flag)
    }

    private fun showDefaultDialInfo() {
        //TODO: change CountryPicker dialog design
        countryPicker = CountryPicker.Builder().with(activity!!)
                .listener(this)
                .build()

        val country = countryPicker.getCountryByISO(COUNTRY_DEFAULT_ISO)

        showDialInfo(country)
    }

    private fun showCountryDialDialog() {
        activity?.let {
            fromDial = true
            countryPicker.showDialog(it)
        }
    }

    override fun onSelectCountry(country: Country) {
        if(!fromDial){
//            presenter.countrySelected(country)
        } else{
            presenter.countryDialSelected(country)
        }
    }

    private fun isValid(item: CharSequence) = item.toString().trim().isNotEmpty()

    override fun showBirthday(displayBirthday: String) {
        tvDateOfBirth.text = displayBirthday
    }

    private fun showBirthDialog() {
        DatePickDialog(activity!!, maxDate = Calendar.getInstance().timeInMillis)
                .show { calendar: Calendar ->
//                    val mothName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
//                    val dayName = calendar.get(Calendar.DAY_OF_MONTH).toOrdinalString()
//                    val modelBirthday = activity!!.getString(R.string.birthday_format, dayName, mothName)

                    val day = if (calendar.get(Calendar.DAY_OF_MONTH) < 10) "0" + calendar.get(Calendar.DAY_OF_MONTH).toString() else calendar.get(Calendar.DAY_OF_MONTH).toString()
                    val month = if ((calendar.get(Calendar.MONTH) + 1) < 10) "0" + (calendar.get(Calendar.MONTH) + 1).toString() else (calendar.get(Calendar.MONTH) + 1).toString()
                    val year = calendar.get(Calendar.YEAR).toString()

                    val birthday = activity!!.getString(R.string.birthday_display_format, day, month, year)

                    presenter.birthSelected(birthday)
                }
    }

    private fun getModel(): ProfileInfo {
        return arguments?.getParcelable(EXTRA_MODEL) as ProfileInfo
    }

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(info: ProfileInfo): SignUpOneFragment {
            val fragment = SignUpOneFragment()

            val args = bundleOf(EXTRA_MODEL to info)
            fragment.arguments = args

            return fragment
        }
    }

        // Old code for restoring/saving data
    override fun showData(profileInfo: ProfileInfo) {
//        form.formDialPhoneNumber.setText(profileInfo.phoneN)
//
//        if(!TextUtils.isEmpty(profileInfo.phoneC)){
//            dialCode.text = profileInfo.phoneC
//        }
//        if(profileInfo.flagCode != -1){
//            dialFlag.setImageResource(profileInfo.flagCode)
//        }
//
//        form.formProfileName.setText(profileInfo.name)
//        form.formProfileLastName.setText(profileInfo.surname)
//        form.formProfileNationality.text = profileInfo.nationality
//        form.formProfileBirth.text = profileInfo.displayBirthday
//        form.formProfileGender.text = profileInfo.gender
//
//        form.formProfileAccount.setText(profileInfo.instagramName)
    }
    override fun onStop() {
//        val profileInfo = presenter.info

//        if(isValid(form.formProfileName.content)){
//          profileInfo.name = form.formProfileName.content
//        }
//
//        if(isValid(form.formProfileLastName.content)){
//          profileInfo.surname =  form.formProfileLastName.content
//        }
//
//        if(isValid(form.formDialPhoneNumber.content)){
//            profileInfo.phone = "${dialCode.content} ${form.formDialPhoneNumber.content}"
//        }
//
//        if(isValid(form.formProfileAccount.content)){
//            profileInfo.instagramName = form.formProfileAccount.content
//        }
//
//        profileInfo.phoneN = form.formDialPhoneNumber.content
//        profileInfo.phoneC = dialCode.content
//        presenter.saveState(profileInfo, 1)
        super.onStop()
    }

}
