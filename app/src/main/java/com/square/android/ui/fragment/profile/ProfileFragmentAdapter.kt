package com.square.android.ui.fragment.profile

import com.square.android.App
import com.square.android.R

private var PAGE_TITLES_RES = listOf(R.string.social, R.string.business, R.string.wallet)

//TODO change to 3
private const val ITEM_COUNT = 1

private const val POSITION_SOCIAL = 0
private const val POSITION_BUSINESS = 1
private const val POSITION_WALLET = 2

class ProfileFragmentAdapter(fragmentManager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    private val titles: List<String> = PAGE_TITLES_RES.map { App.getString(it) }

    override fun getItem(position: Int): androidx.fragment.app.Fragment{
        return when (position) {
            POSITION_SOCIAL -> ProfileSocialFragment()

//            TODO implement 2 remaining fragments
//            POSITION_BUSINESS -> null
//            POSITION_WALLET -> null
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

override fun getCount() = ITEM_COUNT

override fun getPageTitle(position: Int) = titles[position]

}
