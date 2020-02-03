package com.square.android.ui.fragment.profile

import android.app.Activity
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.material.appbar.AppBarLayout
import com.square.android.R
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.profile.*
import com.square.android.presentation.view.profile.ProfileView
import com.square.android.ui.fragment.BaseFragment
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlin.math.abs

const val EXTRA_USER = "EXTRA_USER"
const val EXTRA_BILLING_TOKEN_INFO = "EXTRA_BILLING_TOKEN_INFO"

const val TYPE_PLAIN = 1 // (only right text)
const val TYPE_ARROW = 2
const val TYPE_ADD = 3
const val TYPE_BUTTON = 4
const val TYPE_CUSTOM = 0

const val CUSTOM_TYPE_BALANCE = 2

class ProfileItem(
        var type: Int = TYPE_ARROW,
        var title: String = "",
        @DrawableRes
        var iconRes: Int? = null,
        var subText: String? = null,
        var dividerVisible: Boolean = false,
        @ColorRes
        var arrowTint: Int = R.color.text_gray,
        @ColorRes
        var subTextColor: Int = R.color.text_gray,
        var addType: Int? = null,
        var customType: Int? = null,
        var onClick: () -> Unit = {},
        var onAdd: () -> Unit = {})

class ProfileFragment: BaseFragment(), ProfileView {

    private var currentPagerPosition = 0
    private var isStatusBarLight: Boolean = true

    private var screenWidth: Int = 0

    private var isCalculated = false

    private var imageMovePoint: Float = 0F

    private var separatorHidePoint: Float = 0F

    private var roundedResizePoint: Float = 0F
    private var roundedResizeWeight: Float = 0F

    private var profileBgAlphaPoint = 0f
    private var profileBgAlphaWeight = 0f

    private var imageMoveWeight: Float = 0F

    private var firstContentHidePoint: Float = 0f

    private var secondContentShowPoint: Float = 0f

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iconSettings.setOnClickListener { presenter.navigateToSettings() }

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels

        profileAppBar.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                    if(!isCalculated){
                        imageMovePoint = 0f
                        imageMoveWeight =  1 / (1 - imageMovePoint)

                        firstContentHidePoint = 0.1f

                        secondContentShowPoint = 0.95f

                        roundedResizePoint = 0f

                        roundedResizeWeight = 1 / (1 - roundedResizePoint)

                        profileBgAlphaPoint = 0.3f
                        profileBgAlphaWeight = 1 / (1 - profileBgAlphaPoint)

                        separatorHidePoint = 0.9999f

                        isCalculated = true
                    }
                    updateViews(Math.abs(i / appBarLayout.totalScrollRange.toFloat()))
                })
    }

    private fun updateViews(offset: Float) {
        var defaultImagePaddingStart = Math.round((screenWidth / 2) - (resources.getDimension(R.dimen.profile_imageSize) / 2))

        when (offset) {
            in 0.74F..1F -> {
                if (!isStatusBarLight) {
                    isStatusBarLight = true
                    setLightStatusBar((activity as Activity))
                }

                iconSettings.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.black))
                iconNotifications.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.black))
            }
            in 0F..0.74F -> {
                if (isStatusBarLight) {
                    isStatusBarLight = false
                    clearLightStatusBar((activity as Activity))
                }

                iconSettings.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.white))
                iconNotifications.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.white))
            }
        }

        userImg.apply {
            when {
                offset > imageMovePoint -> {
                    imageContainer.gravity = Gravity.START

                    val imageOffset = (offset - imageMovePoint) * imageMoveWeight

                    val measuredMarginTop = Math.round(resources.getDimension(R.dimen.profile_imageInitialMarginTop) + (abs(resources.getDimension(R.dimen.profile_imageInitialMarginTop)) * imageOffset))
                    val measuredMarginStart = Math.round(defaultImagePaddingStart - ((defaultImagePaddingStart - resources.getDimension(R.dimen.profile_defaultPadding)) * imageOffset))

                    val measuredSize = Math.round(resources.getDimension(R.dimen.profile_imageSize) - ((resources.getDimension(R.dimen.profile_imageSize) - resources.getDimension(R.dimen.profile_resizedImageSize)) * imageOffset))

                    this.layoutParams.also {

                        (it as LinearLayout.LayoutParams).setMargins(measuredMarginStart, measuredMarginTop, 0, 0)
                        it.height = measuredSize
                        it.width = measuredSize
                        this.requestLayout()
                    }
                }

                else -> {
                    imageContainer.gravity = Gravity.CENTER_HORIZONTAL
                    this.layoutParams.also {
                        (it as LinearLayout.LayoutParams).setMargins(0, Math.round(resources.getDimension(R.dimen.profile_imageInitialMarginTop)), 0, 0)
                    }
                }

            }
        }

        userLabel.visibility = if(offset > firstContentHidePoint) View.GONE else View.VISIBLE

        secondaryLabel.visibility = if(offset > secondContentShowPoint) View.VISIBLE else View.GONE

        roundedView.apply {
            when {
                offset > roundedResizePoint -> {
                    val roundedOffset = (offset - roundedResizePoint) * roundedResizeWeight

                    val roundedMeasuredHeight = Math.round( resources.getDimension(R.dimen.profile_roundedViewHeight) - (resources.getDimension(R.dimen.profile_roundedViewHeight) * (roundedOffset * 1.25f))   )

                    this.layoutParams.also {
                        it.height = roundedMeasuredHeight
                    }
                }

                else -> {
                    this.layoutParams.also {
                        it.height = Math.round(resources.getDimension(R.dimen.profile_roundedViewHeight))
                    }
                }
            }
        }

        profileBgImage.apply {
            when {
                offset > profileBgAlphaPoint -> {
                    val imageOffset = (offset - profileBgAlphaPoint) * profileBgAlphaWeight

                    alpha = 1f - (1f * imageOffset * 1.25f)
                }
                else ->{
                    alpha = 1f
                }
            }
        }

        separator.visibility = if(offset > separatorHidePoint) View.INVISIBLE else View.VISIBLE
    }

    override fun showData(user: Profile.User, actualTokenInfo: BillingTokenInfo) {
        userName.text = user.name + " " + user.surname
        secondaryName.text = user.name + " " + user.surname

        //TODO get active from API
        activeLabel.text = "Active user"
        secondaryActiveLabel.text = "Active user"

        //TODO activeIcon and secondaryActiveIcon
        activeIcon.visibility = View.VISIBLE
        secondaryActiveIcon.visibility = View.VISIBLE

        //TODO userIcon and secondaryUserIcon
        userIcon.visibility = View.VISIBLE
        secondaryUserIcon.visibility = View.VISIBLE

        user.mainImage?.run {
            userImg.loadImage(this,
                    placeholder = android.R.color.transparent,
                    roundedCornersRadiusPx = 360,
                    whichCornersToRound = listOf(RoundedCornersTransformation.CornerType.ALL))
        }

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
            }
        })
    }

    override fun showProgress() { }

    override fun hideProgress() { }

    override fun onStart() {
        super.onStart()

        if(isStatusBarLight){
            setLightStatusBar((activity as Activity))
        } else{
            clearLightStatusBar((activity as Activity))
        }
    }

    override fun onStop() {
        super.onStop()

        if(!isStatusBarLight){
            setLightStatusBar((activity as Activity))
        }
    }

    private fun setLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.decorView.systemUiVisibility = flags
        }
    }

    private fun clearLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.decorView.systemUiVisibility = flags
        }
    }
}