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
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.profile.*
import com.square.android.presentation.view.profile.ProfileSocialView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.page_profile_social.*
import org.jetbrains.anko.bundleOf

class ProfileSocialFragment: BaseFragment(), ProfileSocialView {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(user: Profile.User, actualTokenInfo: BillingTokenInfo): ProfileSocialFragment {
            val fragment = ProfileSocialFragment()

            val args = bundleOf(EXTRA_USER to user, EXTRA_BILLING_TOKEN_INFO to actualTokenInfo)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ProfileSocialPresenter

    @ProvidePresenter
    fun providePresenter() = ProfileSocialPresenter(arguments?.getParcelable(EXTRA_USER) as Profile.User, arguments?.getParcelable(EXTRA_BILLING_TOKEN_INFO) as BillingTokenInfo)

    private var profileItemAdapter: ProfileItemAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_profile_social, container, false)
    }

    override fun showData(user: Profile.User, actualTokenInfo: BillingTokenInfo) {

        val subTextSubscription = when(actualTokenInfo.subscriptionType){
            SUBSCRIPTION_NORMAL -> getString(R.string.basic)
            SUBSCRIPTION_PREMIUM -> getString(R.string.premium)
            else -> ""
        }

        //TODO load data from API

        val editProfile = ProfileItem(TYPE_BUTTON, getString(R.string.edit_profile), onClick = { presenter.openEditProfile() })

        val activePlan = ProfileItem(title = getString(R.string.active_plan), iconRes = R.drawable.r_shop, subText = subTextSubscription, subTextColor = R.color.status_yellow, onClick = { presenter.navigateToActivePlan() })
        val credits = ProfileItem(title = getString(R.string.credits), iconRes = R.drawable.r_shop, subText = "1.534", subTextColor = android.R.color.black)
        val experiencePoints = ProfileItem(title = getString(R.string.experience_points),iconRes =  R.drawable.r_shop, subText = "435", subTextColor = android.R.color.black, dividerVisible = true)

        val socialChannels = ProfileItem(title = getString(R.string.social_channels), iconRes = R.drawable.r_shop, subText = "Instagram, +1 more", onClick = { presenter.navigateToSocialChannels() })
        val myProfession = ProfileItem(title = getString(R.string.my_profession), iconRes = R.drawable.r_shop, subText = "Content creator")
        val specialities = ProfileItem(title = getString(R.string.specialities), iconRes = R.drawable.r_shop, subText = "Fashion, +3 more")
        val capabilities = ProfileItem(title = getString(R.string.capabilities), iconRes = R.drawable.r_shop, subText = "Writing, +2 more", dividerVisible = true)

        val earnMoreCredits = ProfileItem(title = getString(R.string.earn_more_credits), iconRes = R.drawable.r_shop, onClick = { presenter.navigateToEarnMoreCredits() })
        val ambassadorProgram = ProfileItem(title = getString(R.string.ambassador_program), iconRes = R.drawable.r_shop)
        val profileVerification = ProfileItem(title = getString(R.string.profile_verification), iconRes = R.drawable.r_shop)

        profileItemAdapter = ProfileItemAdapter(listOf(editProfile, activePlan, credits, experiencePoints, socialChannels, myProfession, specialities, capabilities,
                earnMoreCredits, ambassadorProgram, profileVerification))
        rvItems.itemAnimator = null
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvItems.adapter = profileItemAdapter
    }

}