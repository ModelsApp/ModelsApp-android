package com.square.android.ui.fragment.profile

import android.os.Bundle
import android.os.Handler
import android.view.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.profile.EditProfilePresenter
import com.square.android.presentation.view.profile.EditProfileView
import com.square.android.ui.dialogs.DatePickDialog
import com.square.android.ui.fragment.BaseFragment
import android.widget.AdapterView
import com.square.android.extensions.content
import com.square.android.extensions.loadImage
import com.square.android.extensions.onTextChanged
import com.square.android.ui.DROPDOWN_ITEM_TYPE_CHECKMARK
import com.square.android.ui.SimpleSpinnerAdapter
import com.square.android.ui.dialogs.LoadingDialog
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.util.*

class EditProfileFragment : BaseFragment(), EditProfileView {

    @InjectPresenter
    lateinit var presenter: EditProfilePresenter

    private var loadingDialog: LoadingDialog? = null

    private var userHasPhoto = false

    private var dataLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(activity!!)

        saveTv.setOnClickListener {
            if(validate()){
                presenter.save(collectInfo())
            }
        }

        tvDateOfBirth.setOnClickListener { showBirthDialog() }

        arrowBack.setOnClickListener { activity?.onBackPressed() }

//        iv_gallery.setOnClickListener { presenter.openGallery() }

//        editProfileBook.setOnClickListener{btnClicked()}
//        editProfileMoreCredits.setOnClickListener{btnClicked()}

    }

//    fun btnClicked(){
//        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context?.getString(R.string.youtube_tutorial)))
//        startActivity(browserIntent)
//    }

    private fun isValid(item: CharSequence) = item.isNotEmpty()

    override fun showData(user: Profile.User) {
        val nationalities = resources.getStringArray(R.array.nationalities_array).toList()
        val genders = resources.getStringArray(R.array.genders).toList()

        nationalityLl.setOnClickListener { spinnerNationality.callOnClick() }
        spinnerNationality.adapter = SimpleSpinnerAdapter(activity!!, DROPDOWN_ITEM_TYPE_CHECKMARK, nationalities)
        spinnerNationality.setSelection(nationalities.indexOfFirst { it == user.nationality })

        genderLl.setOnClickListener { spinnerGender.callOnClick() }
        spinnerGender.adapter = SimpleSpinnerAdapter(activity!!, DROPDOWN_ITEM_TYPE_CHECKMARK, genders, halfSize = true)
        spinnerGender.setSelection(genders.indexOfFirst { it == user.gender })

        tvDateOfBirth.text = user.birthDate
        et_name.setText(user.name)
        et_surname.setText(user.surname)

        userImg.loadImage(user.mainImage,
                placeholder = R.color.gray_light,
                roundedCornersRadiusPx = 360,
                whichCornersToRound = listOf(RoundedCornersTransformation.CornerType.ALL))

        user.mainImage?.let {
            userHasPhoto = true
            iconAddPhoto.visibility = View.GONE
            iconPhoto.visibility = View.VISIBLE
        } ?: run {
            userHasPhoto = false
            iconPhoto.visibility = View.GONE
            iconAddPhoto.visibility = View.VISIBLE
        }

        iconPhoto.setOnClickListener { userImg.callOnClick() }
        iconAddPhoto.setOnClickListener { userImg.callOnClick() }

        userImg.setOnClickListener {
//            TODO:F delete later, new endpoints?
            presenter.openGallery()

//            TODO:F change later, new endpoints?
//
//            if(userHasPhoto){
//
//            } else{
//
//            }
        }

        coverImv.loadImage(user.coverImg,
                placeholder = R.color.gray_light,
                roundedCornersRadiusPx = resources.getDimensionPixelSize(R.dimen.v_16dp),
                whichCornersToRound = listOf(RoundedCornersTransformation.CornerType.ALL))

        user.coverImg?.let {
            coverTvImv.visibility = View.GONE
            coverTv.text = getString(R.string.edit_cover_image)
        } ?: run {
            coverTvImv.visibility = View.VISIBLE
            coverTv.text = getString(R.string.add_cover_image)
        }

        coverLl.setOnClickListener {
            //TODO:F there is no cover img in user for now

            // add\change cover img
        }

        et_name.onTextChanged { changeSaveBtn(true) }
        et_surname.onTextChanged { changeSaveBtn(true) }
        tvDateOfBirth.onTextChanged { changeSaveBtn(true) }

        spinnerNationality.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                changeSaveBtn(true)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerGender.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                changeSaveBtn(true)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        Handler().postDelayed({ dataLoaded = true }, 200)
    }

    override fun changeSaveBtn(enabled: Boolean){
        if(enabled && dataLoaded || !enabled){
            saveTv.isEnabled = enabled
        }
    }

    private fun collectInfo(): ProfileInfo {
        val nationality = spinnerNationality.selectedItem.toString()
        //TODO:F gender is not saving correctly in API. check in register too
        val gender = spinnerGender.selectedItem.toString()
        val name = et_name.content
        val surname = et_surname.content
        val birthDate = tvDateOfBirth.content

        return ProfileInfo(
                name = name,
                surname = surname,
                nationality = nationality,
                birthDate = birthDate,
                gender = gender
        )
    }

    override fun validate(): Boolean {
        var allOk = true

        if(!isValid(et_name.content)){
            et_name.setText("")
            et_name.showCustomError(getString(R.string.name_error))

            allOk = false
        }

        if(!isValid(et_surname.content)){
            et_surname.setText("")
            et_surname.showCustomError(getString(R.string.last_name_error))

            allOk = false
        }

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

        return allOk
    }

    override fun showProgress() { }

    override fun hideProgress() { }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
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

                    tvDateOfBirth.text = birthday
                }
    }

    override fun showBirthday(birthday: String) {
        tvDateOfBirth.text = birthday
    }

    override fun onResume() {
        super.onResume()
        presenter.loadData()
    }
}
