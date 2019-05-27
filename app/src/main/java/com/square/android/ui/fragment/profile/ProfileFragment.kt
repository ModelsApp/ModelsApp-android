package com.square.android.ui.fragment.profile


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.extensions.clearText
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.profile.ProfilePresenter
import com.square.android.presentation.view.profile.ProfileView
import com.square.android.ui.fragment.BaseFragment
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : BaseFragment(), ProfileView {

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    private val countryPicker by lazy {
        CountryPicker.Builder().with(requireContext())
                .build()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun displayNationality(country: Country?) {
        if (country != null) {
            profileNationality.text = country.name

            formDialFlag.visibility = View.VISIBLE
            formDialFlag.setImageResource(country.flag)

        } else {
            profileNationality.clearText()
            formDialFlag.visibility = View.GONE
        }
    }

    override fun showUser(user: Profile.User) {
        user.mainImage?.run {
            profileAvatar.loadImage(this,
                    roundedCornersRadiusPx = 100,
                    whichCornersToRound = RoundedCornersTransformation.CornerType.BOTTOM)
        }

        profileName.text = getString(R.string.name_format, user.name, user.surname)
        profileAgency.text = user.currentAgency

        profileLevel.text = user.level.toString()

        profileCoins.text = getString(R.string.credits_format, user.credits)

        displayNationality(countryPicker.getCountryByName(user.nationality))

//        profileMotherAgency.text = user.motherAgency
//        profileEmail.text = user.email
//        profilePhone.text = user.phone

        container_invite.setOnClickListener { share(user.referralCode) }

        profileSettings.setOnClickListener { presenter.openSettings() }

        how_it_works.setOnClickListener { presenter.navigateTutorialVideos() }
    }

    private fun share(referralCode: String) {
        val text = getString(R.string.shareContent, referralCode)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)

        startActivity(shareIntent)
    }

    override fun showProgress() {
        profileProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        profileProgress.visibility = View.GONE
    }
}
