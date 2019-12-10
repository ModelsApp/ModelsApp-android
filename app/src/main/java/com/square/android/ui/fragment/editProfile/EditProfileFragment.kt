package com.square.android.ui.fragment.editProfile

import android.net.Uri
import android.os.Bundle
import android.view.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.listeners.OnCountryPickerListener
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.ProfileInfo
import com.square.android.extensions.clearText
import com.square.android.extensions.content
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.editProfile.EditProfilePresenter
import com.square.android.presentation.view.editProfile.EditProfileView
import com.square.android.ui.dialogs.DatePickDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.ValidationCallback
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import android.content.Intent
import com.mapbox.android.core.permissions.PermissionsListener
import com.square.android.utils.PermissionsManager

private const val GENDER_MALE = "male"
private const val GENDER_FEMALE = "female"

class EditProfileFragment : BaseFragment(), EditProfileView, ValidationCallback<CharSequence>, OnCountryPickerListener, PermissionsListener {
    private var isDoneShown = false

    @InjectPresenter
    lateinit var presenter: EditProfilePresenter

    lateinit var countryPicker: CountryPicker

    private var permissionsManager: PermissionsManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formEditProfileNationality.setOnClickListener { showCountryDialog() }

        formEditProfileBirth.setOnClickListener { showBirthDialog() }

        editProfileLogout.setOnClickListener { presenter.logout() }

        tvSave.setOnClickListener { presenter.save(collectInfo()) }

        iv_gallery.setOnClickListener { presenter.openGallery() }

        countryPicker = CountryPicker.Builder().with(requireContext())
                .listener(this)
                .build()


        editProfileBook.setOnClickListener{btnClicked()}
        editProfileMoreCredits.setOnClickListener{btnClicked()}

        setUpValidation()
    }

    fun btnClicked(){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context?.getString(R.string.youtube_tutorial)))
        startActivity(browserIntent)
    }

    override fun showProgress() {
        editProfileProgress.visibility = View.VISIBLE
    }

    override fun onSelectCountry(country: Country) {
        presenter.countrySelected(country)
    }

    override fun hideProgress() {
        editProfileProgress.visibility = View.INVISIBLE
    }

    override fun showBirthday(date: String) {
        formEditProfileBirth.text = date
    }

    override fun isValid(item: CharSequence) = item.isNotEmpty()

    override fun validityChanged(isValid: Boolean) {
        isDoneShown = isValid
    }

    override fun displayNationality(country: Country?) {
        if (country != null) {
            formEditProfileNationality.text = country.name

            formEditFlag.visibility = View.VISIBLE
            formEditFlag.setImageResource(country.flag)

        } else {
            formEditProfileNationality.clearText()
            formEditFlag.visibility = View.GONE
        }
    }

    override fun showData(user: Profile.User, arePushNotificationsAllowed: Boolean) {
        if(PermissionsManager.areLocationPermissionsGranted(activity!!)){
            if(!repository.getLocationPermissionsOneTimeChecked()){
                repository.setGeolocationAllowed(true)
                repository.setLocationPermissionsOneTimeChecked(true)
            }
        }

        user.mainImage?.run {
            profileEditAvatar.loadImage(this, placeholder = R.color.colorPrimary,
                    roundedCornersRadiusPx = 100,
                    whichCornersToRound = listOf(RoundedCornersTransformation.CornerType.BOTTOM))
        }

        formEditProfileName.content = user.name
        formEditProfileSurname.content = user.surname
        formEditProfileBirth.text = user.birthDate

        setNationality(user.nationality)

        formEditProfileMotherAgency.content = user.motherAgency
        formEditProfilePhone.content = user.phone

        profileEditCoins.text = getString(R.string.credits_format, user.credits)

        formEditDialCode.visibility = View.GONE
        formEditDialFlag.visibility = View.GONE

        switchPushNotifi.isChecked = arePushNotificationsAllowed

        permissionsManager = PermissionsManager(this)

        switchAllowGeo.isChecked = PermissionsManager.areOwnLocationPermissionsGranted(activity!!)

        allowGeoClickView.setOnClickListener {
            if(PermissionsManager.areLocationPermissionsGranted(activity!!)){
                switchAllowGeo.isChecked = repository.getGeolocationAllowed().not()
                repository.setGeolocationAllowed(repository.getGeolocationAllowed().not())
            } else{
//                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
//                ActivityCompat.requestPermissions(activity!!, permissions, 1387)

                permissionsManager!!.requestLocationPermissions(this)
            }
        }

        switchPushNotifi.setOnCheckedChangeListener { buttonView, isChecked -> repository.setPushNotificationsAllowed(isChecked) }

        val genderButton = determineGenderButtonId(user.gender)
        genderButton.isChecked = true
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) { }

    override fun onPermissionResult(granted: Boolean) {
            switchAllowGeo.isChecked = granted
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.action_done -> {
                    presenter.save(collectInfo())
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    private fun setNationality(nationality: String) {
        val country = getCountryByName(nationality)

        displayNationality(country)
    }

    private fun determineGenderButtonId(gender: String) =
            if (gender == GENDER_MALE) {
                formEditProfileGenderMale
            } else {
                formEditProfileGenderFemale
            }


    private fun setUpValidation() {
        val validateList = listOf(
                formEditProfileName,
                formEditProfileSurname,
                formEditProfileNationality,
                formEditProfilePhone,
                formEditProfileMotherAgency
        )

        addTextValidation(validateList, this)
    }

    private fun getCountryByName(name: String): Country? {
        return countryPicker.getCountryByName(name)
    }

    private fun showCountryDialog() {
        CountryPicker.Builder().with(requireContext())
                .listener(this)
                .build()
                .showDialog(activity!!)
    }

    private fun showBirthDialog() {
        DatePickDialog(requireContext())
                .show { date: String ->
                    presenter.birthSelected(date)
                }
    }

    private fun collectInfo(): ProfileInfo {
        val name = formEditProfileName.content
        val surname = formEditProfileSurname.content

        val gender = getGender()

        val birthday = formEditProfileBirth.content

        val nationality = formEditProfileNationality.content

//        val email = formEditProfileEmail.content
        val phone = formEditDialCode.content + formEditProfilePhone.content
        val motherAgency = formEditProfileMotherAgency.content

        return ProfileInfo(
                name = name,
                surname = surname,
                gender = gender,
//                email = email,
                phone = phone,
                motherAgency = motherAgency,
                nationality = nationality,
                birthDate = birthday
        )
    }

    private fun getGender() = when (formEditProfileGenderGroup.checkedRadioButtonId) {
        R.id.formEditProfileGenderFemale -> GENDER_FEMALE
        else -> GENDER_MALE
    }

    override fun onResume() {
        super.onResume()
        presenter.loadData()
    }
}
