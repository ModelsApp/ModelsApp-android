package com.square.android.ui.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.profile.*
import com.square.android.presentation.view.profile.ProfileSocialView
import com.square.android.ui.fragment.BaseFragment
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.page_profile_social.*

const val SOCIAL_ITEM_TYPE_PLAIN = 1 // (only right text, non-clickable)
const val SOCIAL_ITEM_TYPE_DROPDOWN = 2

class SocialItem(
        var type: Int = 0, // one of SOCIAL_ITEM_TYPE_...
        var title: String = "",
        var textValue: String? = null,
        @DrawableRes
        var iconRes: Int,
        var subItems: List<Any>? = null)

class ProfileSocialFragment: BaseFragment(), ProfileSocialView, SocialAdapter.Handler {

    @InjectPresenter
    lateinit var presenter: ProfileSocialPresenter

    private var socialAdapter: SocialAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_profile_social, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tutorialsTv.setOnClickListener { presenter.navigateTutorialVideos() }

        termsTv.setOnClickListener { presenter.navigateTerms() }

        privacyTv.setOnClickListener { presenter.navigatePrivacy() }
    }

    override fun showData(user: Profile.User, actualTokenInfo: BillingTokenInfo) {

        user.mainImage?.run {
            userImg.loadImage(this,
                    roundedCornersRadiusPx = 48,
                    whichCornersToRound = listOf(RoundedCornersTransformation.CornerType.TOP_LEFT, RoundedCornersTransformation.CornerType.TOP_RIGHT, RoundedCornersTransformation.CornerType.BOTTOM_LEFT))
        }

        userName.text = user.name + "\n" + user.surname

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

        memberLabel.text = subText + " " + getString(R.string.member) + "\n" + getString(R.string.level_format, user.level)

//      TODO  show/hide icMedal(when?), change statusCircle color(when?)

//      TODO  populate rvSocials

        //TODO get credits value
        val plainCredits = SocialItem(SOCIAL_ITEM_TYPE_PLAIN, getString(R.string.credits), user.credits.toString(), R.drawable.r_shop)

        //TODO get points value(from where?)
        val plainPoints = SocialItem(SOCIAL_ITEM_TYPE_PLAIN, getString(R.string.points), "550", R.drawable.r_shop)

        //TODO when to determine if can change?
        val socialPlan = SocialItem(SOCIAL_ITEM_TYPE_DROPDOWN, getString(R.string.active_plan),null, R.drawable.r_shop, listOf(Plan(subText+" | "+subTypeText), true))

        //TODO get social "connected" value for every type of social
        val socialChannels = SocialItem(SOCIAL_ITEM_TYPE_DROPDOWN, getString(R.string.social_channels),null, R.drawable.r_shop, listOf(
                Social(SOCIAL_APP_TYPE_INSTAGRAM, true), Social(SOCIAL_APP_TYPE_FACEBOOK, false), Social(SOCIAL_APP_TYPE_GOOGLE, true),
                Social(SOCIAL_APP_TYPE_TRIPADVISOR, true),Social(SOCIAL_APP_TYPE_YELP, false)))

        //TODO get credits value etc
        val socialEarnCredits = SocialItem(SOCIAL_ITEM_TYPE_DROPDOWN, getString(R.string.earn_more_credits),null, R.drawable.r_shop, listOf(
                EarnCredits(EARN_TYPE_SHARE_FRIENDS, 150), EarnCredits(EARN_TYPE_REFER_VENUE, 300), EarnCredits(EARN_TYPE_INTRODUCE_BRAND, 500)))

        //TODO get price value etc
        val socialBuyCredits = SocialItem(SOCIAL_ITEM_TYPE_DROPDOWN, getString(R.string.buy_extra_credits),null, R.drawable.r_shop, listOf(
                BuyCredits(BUY_TYPE_500, 500, "€ 30,00"), BuyCredits(BUY_TYPE_1000, 1000, "€ 50,00")))

        //TODO
        val socialAmbassador = SocialItem(SOCIAL_ITEM_TYPE_DROPDOWN, getString(R.string.ambassador_program),null, R.drawable.r_shop, listOf(
                Ambassador(AMBASSADOR_TYPE_JOIN_TEAM, R.drawable.r_shop)))

        socialAdapter = SocialAdapter(listOf(plainCredits, plainPoints, socialPlan, socialChannels, socialEarnCredits, socialBuyCredits, socialAmbassador), this)
        rvItems.itemAnimator = null
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        rvItems.adapter = socialAdapter
    }

    override fun clickViewClicked(position: Int) {
        socialAdapter?.setOpenedItem(position)
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

    override fun showProgress() {
        rvItems.visibility = View.GONE
        statusRl.visibility = View.GONE
        bottomLl.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
        rvItems.visibility = View.VISIBLE
        statusRl.visibility = View.VISIBLE
        bottomLl.visibility = View.VISIBLE
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