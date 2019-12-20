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

class ProfileSocialFragment: BaseFragment(), ProfileSocialView, ProfileItemAdapter.Handler, ProfileItemAdapter.SocialHandler {

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

        val subText = when(actualTokenInfo.subscriptionType){
            SUBSCRIPTION_NORMAL -> getString(R.string.basic)
            SUBSCRIPTION_PREMIUM -> getString(R.string.premium)
            else -> ""
        }

        val subTypeText = when(actualTokenInfo.subscriptionType){
            SUBSCRIPTION_TYPE_WEEKLY -> getString(R.string.weekly)
            SUBSCRIPTION_TYPE_MONTHLY -> getString(R.string.monthly)
            SUBSCRIPTION_TYPE_NO_LIMIT -> getString(R.string.unlimited)
            else -> ""
        }

        //TODO get credits value
        val plainCredits = ProfileItem(TYPE_PLAIN, getString(R.string.credits), user.credits.toString(), R.drawable.r_shop)

        //TODO get points value(from where?)
        val plainPoints = ProfileItem(TYPE_PLAIN, getString(R.string.points), "550", R.drawable.r_shop)

        //TODO how to determine if user can change plan?
        val socialPlan = ProfileItem(TYPE_DROPDOWN, getString(R.string.active_plan),null, R.drawable.r_shop, listOf(
                ProfileSubItems.Plan(subText+" | "+subTypeText)
        , true))

        //TODO get social "connected" value for every type of social
        val socialChannels = ProfileItem(TYPE_DROPDOWN, getString(R.string.social_channels),null, R.drawable.r_shop, listOf(
                ProfileSubItems.Social(SOCIAL_APP_TYPE_INSTAGRAM, true),
                ProfileSubItems.Social(SOCIAL_APP_TYPE_FACEBOOK, false),
                ProfileSubItems.Social(SOCIAL_APP_TYPE_GOOGLE, true),
                ProfileSubItems.Social(SOCIAL_APP_TYPE_TRIPADVISOR, true),
                ProfileSubItems.Social(SOCIAL_APP_TYPE_YELP, false)
        ))

        //TODO get credits value etc
        val socialEarnCredits = ProfileItem(TYPE_DROPDOWN, getString(R.string.earn_more_credits),null, R.drawable.r_shop, listOf(
                ProfileSubItems.EarnCredits(EARN_TYPE_SHARE_FRIENDS, 150),
                ProfileSubItems.EarnCredits(EARN_TYPE_REFER_VENUE, 300),
                ProfileSubItems.EarnCredits(EARN_TYPE_INTRODUCE_BRAND, 500)
        ))

        //TODO get price value etc
        val socialBuyCredits = ProfileItem(TYPE_DROPDOWN, getString(R.string.buy_extra_credits),null, R.drawable.r_shop, listOf(
                ProfileSubItems.BuyCredits(BUY_TYPE_500, 500, "€ 30,00"),
                ProfileSubItems.BuyCredits(BUY_TYPE_1000, 1000, "€ 50,00")
        ))

        //TODO
        val socialAmbassador = ProfileItem(TYPE_DROPDOWN, getString(R.string.ambassador_program),null, R.drawable.r_shop, listOf(
                ProfileSubItems.Ambassador(AMBASSADOR_TYPE_JOIN_TEAM, R.drawable.r_shop)
        ))

        profileItemAdapter = ProfileItemAdapter(listOf(plainCredits, plainPoints, socialPlan, socialChannels, socialEarnCredits, socialBuyCredits, socialAmbassador), this, socialHandler = this)
        rvItems.itemAnimator = null
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvItems.adapter = profileItemAdapter
    }

    override fun clickViewClicked(position: Int) {
        profileItemAdapter?.setOpenedItem(position)
    }

    override fun changePlanClicked() {
        // TODO

        println("ProfileSocialFragment: changePlanClicked()")
    }

    override fun socialConnectClicked(type: Int, isConnected: Boolean) {
        // TODO

        println("ProfileSocialFragment: socialConnectClicked() type = "+type+", isConnecte = "+isConnected)
    }

    override fun earnMoreClicked(type: Int) {
        // TODO

        println("ProfileSocialFragment: earnMoreClicked() type = "+type)
    }

    override fun buyExtraClicked(type: Int) {
        // TODO

        println("ProfileSocialFragment: buyExtraClicked() type = "+type)
    }

    override fun ambassadorClicked(type: Int) {
        // TODO

        println("ProfileSocialFragment: ambassadorClicked() type = "+type)
    }

    override fun createClicked(clickedType: Int) {

    }

// from old "invite your friends" button
//    private fun share(referralCode: String) {
//        val text = getString(R.string.shareContent, referralCode)
//
//        val shareIntent = Intent(Intent.ACTION_SEND)
//        shareIntent.type = "text/plain"
//        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
//
//        startActivity(shareIntent)
//    }

}