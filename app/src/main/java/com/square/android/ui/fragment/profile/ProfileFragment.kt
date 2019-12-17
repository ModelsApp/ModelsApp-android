package com.square.android.ui.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.profile.*
import com.square.android.presentation.view.profile.ProfileView
import com.square.android.ui.fragment.BaseFragment
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.memberLabel
import kotlinx.android.synthetic.main.fragment_profile.privacyTv
import kotlinx.android.synthetic.main.fragment_profile.rvSocials
import kotlinx.android.synthetic.main.fragment_profile.termsTv
import kotlinx.android.synthetic.main.fragment_profile.tutorialsTv

const val EXTRA_USER = "EXTRA_USER"
const val EXTRA_BILLING_TOKEN_INFO = "EXTRA_BILLING_TOKEN_INFO"

const val TYPE_PLAIN = 1 // (only right text, non-clickable)
const val TYPE_DROPDOWN = 2

class ProfileItem(
        var type: Int = 0, // one of TYPE_...
        var title: String = "",
        var textValue: String? = null,
        @DrawableRes
        var iconRes: Int,
        var subItems: List<Any>? = null,
        @DrawableRes
        var rightIconRes: Int? = null)

class ProfileFragment: BaseFragment(), ProfileView {

    private var currentPagerPosition = 0

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iconSettings.setOnClickListener { presenter.openSettings() }

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

        memberLabel.text = subText + " " + getString(R.string.member) + "\n" + getString(R.string.level_format, user.level)

//      TODO  show/hide icMedal(when?), change statusCircle color(when?)

//      TODO  populate rvSocials

//      TODO fill business data(businessInfoLl - heightTv, sizeTv, cityTv)

        setupFragmentAdapter(user, actualTokenInfo)
    }

    private fun setupFragmentAdapter(user: Profile.User, actualTokenInfo: BillingTokenInfo) {
        viewPager.isPagingEnabled = false
        viewPager.animationEnabled = false
        viewPager.adapter = ProfileFragmentAdapter(childFragmentManager, user, actualTokenInfo )
        tabLayout.setupWithViewPager(viewPager)

        viewPager.offscreenPageLimit = 2

        viewPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                currentPagerPosition = position
                setUpPage(currentPagerPosition)
            }
        })
    }

    fun setUpPage(position: Int){
        memberLabel.visibility = if(position == POSITION_SOCIAL) View.VISIBLE else View.INVISIBLE
        rvSocials.visibility = if(position == POSITION_SOCIAL) View.VISIBLE else View.INVISIBLE

        businessInfoLl.visibility = if(position == POSITION_BUSINESS) View.VISIBLE else View.GONE
    }

    override fun showProgress() {
        statusRl.visibility = View.GONE
        bottomLl.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
        statusRl.visibility = View.VISIBLE
        bottomLl.visibility = View.VISIBLE
    }
}