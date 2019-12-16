package com.square.android.ui.fragment.profile

import com.square.android.App
import com.square.android.R
import com.square.android.data.pojo.BillingTokenInfo
import com.square.android.data.pojo.Profile

private var PAGE_TITLES_RES = listOf(R.string.social, R.string.business, R.string.wallet)

//TODO change to 3
private const val ITEM_COUNT = 2

const val POSITION_SOCIAL = 0
const val POSITION_BUSINESS = 1
const val POSITION_WALLET = 2

class ProfileFragmentAdapter(fragmentManager: androidx.fragment.app.FragmentManager, var user: Profile.User, var actualTokenInfo: BillingTokenInfo) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    private val titles: List<String> = PAGE_TITLES_RES.map { App.getString(it) }

    override fun getItem(position: Int): androidx.fragment.app.Fragment{
        return when (position) {
            POSITION_SOCIAL -> ProfileSocialFragment.newInstance(user, actualTokenInfo)
            POSITION_BUSINESS -> ProfileBusinessFragment.newInstance(user)

//            TODO implement 1 remaining fragment
//            POSITION_WALLET -> null

            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

override fun getCount() = ITEM_COUNT

override fun getPageTitle(position: Int) = titles[position]

}
