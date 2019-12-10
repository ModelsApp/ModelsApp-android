package com.square.android.ui.fragment.profile

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.profile.ProfileSocialPresenter
import com.square.android.presentation.presenter.profile.SUBSCRIPTION_NORMAL
import com.square.android.presentation.presenter.profile.SUBSCRIPTION_PREMIUM
import com.square.android.presentation.view.profile.ProfileSocialView
import com.square.android.ui.fragment.BaseFragment
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.page_profile_social.*

const val SOCIAL_ITEM_TYPE_PLAIN = 1 // (only right text, non-clickable)
const val SOCIAL_ITEM_TYPE_DROPDOWN = 2

data class SocialItem(
        var type: Int = 0, // one of SOCIAL_ITEM_TYPE_...
        var title: String = "",
        @DrawableRes
        var iconRes: Int,
        var subItems: List<Any>? = null)

class ProfileSocialFragment: BaseFragment(), ProfileSocialView, SocialAdapter.Handler {

    @InjectPresenter
    lateinit var presenter: ProfileSocialPresenter

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

        val memberText = when(actualTokenInfo.subscriptionType){
            SUBSCRIPTION_NORMAL -> getString(R.string.basic)
            SUBSCRIPTION_PREMIUM -> getString(R.string.premium)
            else -> ""
        }

        memberLabel.text = memberText + " " + getString(R.string.member) + "\n" + getString(R.string.level_format, user.level)

//      TODO  show/hide icMedal, change statusCircle color

//      TODO  populate rvSocials

    }

    override fun clickViewClicked(position: Int) {

//        socialAdapter.setOpenedItem(position)
    }

    override fun changePlanClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun socialConnectClicked(type: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun earnMoreClicked(type: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun buyExtraClicked(type: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun ambassadorClicked(type: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress() {
        rvItems.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
        rvItems.visibility = View.VISIBLE
    }

// from "invite your friends" button
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