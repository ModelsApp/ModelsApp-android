package com.square.android.ui.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.profile.*
import com.square.android.presentation.view.profile.ProfileBusinessView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.page_profile_business.*
import org.jetbrains.anko.bundleOf

class ProfileBusinessFragment: BaseFragment(), ProfileBusinessView, ProfileItemAdapter.Handler, ProfileItemAdapter.BusinessHandler {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(user: Profile.User): ProfileBusinessFragment {
            val fragment = ProfileBusinessFragment()

            val args = bundleOf(EXTRA_USER to user)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ProfileBusinessPresenter

    @ProvidePresenter
    fun providePresenter() = ProfileBusinessPresenter(arguments?.getParcelable(EXTRA_USER) as Profile.User)

    private var businessAdapter: ProfileItemAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_profile_business, container, false)
    }

    override fun showData(user: Profile.User) {
        //TODO
        val personalInfo = ProfileItem(TYPE_DROPDOWN, getString(R.string.personal_info), null, R.drawable.r_shop, listOf(
                ProfileSubItems.PersonalInfo()
        ))

        //TODO
        val myJobs = ProfileItem(TYPE_DROPDOWN, getString(R.string.my_jobs), null, R.drawable.r_shop, listOf(
                ProfileSubItems.MyJobs()
        ))

        //TODO get credits value etc
        val earnCredits = ProfileItem(TYPE_DROPDOWN, getString(R.string.earn_credits),null, R.drawable.r_shop, listOf(
                ProfileSubItems.EarnCredits(EARN_TYPE_SHARE_FRIENDS, 150),
                ProfileSubItems.EarnCredits(EARN_TYPE_REFER_VENUE, 300),
                ProfileSubItems.EarnCredits(EARN_TYPE_INTRODUCE_BRAND, 500)
        ))

        //TODO
        val myInterests = ProfileItem(TYPE_DROPDOWN, getString(R.string.my_interests), null, R.drawable.r_shop, listOf(
                ProfileSubItems.MyInterests()
        ))

        //TODO
        val socialChannels = ProfileItem(TYPE_DROPDOWN, getString(R.string.social_channels), null, R.drawable.r_shop, listOf(
                ProfileSubItems.BusinessSocialChannels()
        ))

        //TODO get details
        val appearanceCharacteristics = ProfileItem(TYPE_DROPDOWN, getString(R.string.appearance_characteristics), null, R.drawable.r_shop, listOf(
                ProfileSubItems.AppearanceCharacteristics(DETAIL_TYPE_DOUBLE, getString(R.string.height_), getString(R.string.height_format, "179"), getString(R.string.bust_), "90"),
                ProfileSubItems.AppearanceCharacteristics(DETAIL_TYPE_DOUBLE, getString(R.string.typology_), "Curvy", getString(R.string.waist_), "60"),
                ProfileSubItems.AppearanceCharacteristics(DETAIL_TYPE_DOUBLE, getString(R.string.skin_), "Caucasian", getString(R.string.hip_), "90"),
                ProfileSubItems.AppearanceCharacteristics(DETAIL_TYPE_DOUBLE, getString(R.string.eyes_), "Blue", getString(R.string.bra_), "B Cup"),
                ProfileSubItems.AppearanceCharacteristics(DETAIL_TYPE_DOUBLE, getString(R.string.hair_), "Brown", getString(R.string.size_), "S"),
                ProfileSubItems.AppearanceCharacteristics(DETAIL_TYPE_DOUBLE, getString(R.string.length_), "Medium", getString(R.string.shoes_), "37"),
                ProfileSubItems.AppearanceCharacteristics(DETAIL_TYPE_FULL, getString(R.string.particular_signs_),"2 tattoos on the back")
        ))

        //TODO get data
        val agencies = ProfileItem(TYPE_DROPDOWN, getString(R.string.agencies), null, R.drawable.r_shop, listOf(
                ProfileSubItems.Agency("My network",  44)
        ))

        //TODO get data
        val myPortfolio = ProfileItem(TYPE_DROPDOWN, getString(R.string.my_portfolio), null, R.drawable.r_shop, listOf(
                ProfileSubItems.Portfolio("Editorial Portfolio",  132),
                ProfileSubItems.Portfolio("Commercial Portfolio",  122),
                ProfileSubItems.Portfolio("Acting Portfolio",  1452),
                ProfileSubItems.Create(CREATE_CLICKED_TYPE_PORTFOLIO)
        ))

//        //TODO get data
//        val polaroids = ProfileItem(TYPE_DROPDOWN, getString(R.string.polaroids), null, R.drawable.r_shop, listOf(
//        ProfileSubItems.Polaroid("Album", true , 124),
//        ProfileSubItems.Create(CREATE_CLICKED_TYPE_POLAROID)
//        ), R.drawable.ic_warning_red)
//
//        //TODO get data
//        val compCard = ProfileItem(TYPE_DROPDOWN, getString(R.string.comp_card), null, R.drawable.r_shop, listOf(
//                ProfileSubItems.CompCard("Commercial Comp card",  635),
//                ProfileSubItems.CompCard("Acting Comp card",  421),
//                ProfileSubItems.Create(CREATE_CLICKED_TYPE_COMP_CARD)
//        ))
//
//        //TODO get data
//        val preferences = ProfileItem(TYPE_DROPDOWN, getString(R.string.preferences), null, R.drawable.r_shop, listOf(
//                ProfileSubItems.Preference(PREFERENCE_TYPE_SOCIAL, true),
//                ProfileSubItems.Preference(PREFERENCE_TYPE_HOSTESS, false),
//                ProfileSubItems.Preference(PREFERENCE_TYPE_NIGHT_OUT, true)
//        ))
//
//        //TODO get data
//        val modelsCom = ProfileItem(TYPE_DROPDOWN, getString(R.string.models_com_profile), null, R.drawable.r_shop, listOf(
//                ProfileSubItems.ModelsCom("")
//        ))

        businessAdapter = ProfileItemAdapter(listOf(personalInfo, myJobs, earnCredits, myInterests, socialChannels,appearanceCharacteristics, agencies, myPortfolio), this, businessHandler = this)

        rvItems.itemAnimator = null
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        rvItems.adapter = businessAdapter
    }


    override fun earnCreditsClicked(type: Int) {
        presenter.navigateToEarnMoreCredits()
    }

    override fun modelsComClicked(modelsComUserName: String) {
        //TODO open dialog - edit models.com user name?
    }

    override fun preferenceClicked(type: Int, isChecked: Boolean) {
        //TODO
    }

    override fun compCardOpenClicked(compCardId: Long) {
        //TODO
    }

    override fun agencyViewClicked(agencyId: Long) {
        //TODO
    }

    override fun portfolioOpenClicked(portfolioId: Long) {
        // TODO
    }

    override fun polaroidOpenClicked(albumId: Long) {
        // TODO
    }

    override fun createClicked(clickedType: Int) {
        // TODO
    }

    override fun clickViewClicked(position: Int) {
        businessAdapter?.setOpenedItem(position)
    }

}