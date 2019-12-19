package com.square.android.ui.fragment.profile

import android.view.ViewGroup
import com.square.android.App
import com.square.android.R
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile
import androidx.fragment.app.Fragment
import com.square.android.utils.widget.WrapContentPagingViewPager

private var PAGE_TITLES_RES = listOf(R.string.social, R.string.business, R.string.wallet)

private const val ITEM_COUNT = 3

const val POSITION_SOCIAL = 0
const val POSITION_BUSINESS = 1
const val POSITION_WALLET = 2

class ProfileFragmentAdapter(fragmentManager: androidx.fragment.app.FragmentManager, var user: Profile.User, var actualTokenInfo: BillingTokenInfo) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    private var mCurrentPosition = -1

    private val titles: List<String> = PAGE_TITLES_RES.map { App.getString(it) }

    override fun getItem(position: Int): Fragment{
        return when (position) {
            POSITION_SOCIAL -> ProfileSocialFragment.newInstance(user, actualTokenInfo)
            POSITION_BUSINESS -> ProfileBusinessFragment.newInstance(user)
            POSITION_WALLET -> ProfileWalletFragment.newInstance(user)
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    //TODO used only with WrapContentPagingViewPager to measure height of every fragment
    override fun setPrimaryItem(container: ViewGroup, position: Int, objc: Any) {
        super.setPrimaryItem(container, position, objc)
        if (position != mCurrentPosition) {
            val fragment = objc as Fragment
            val pager = container as WrapContentPagingViewPager
            if (fragment.view != null) {
                mCurrentPosition = position
                pager.measureCurrentView(fragment.view!!)
            }
        }
    }

override fun getCount() = ITEM_COUNT

override fun getPageTitle(position: Int) = titles[position]

}
