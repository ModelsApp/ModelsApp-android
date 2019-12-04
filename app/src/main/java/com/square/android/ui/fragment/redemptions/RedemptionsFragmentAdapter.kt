package com.square.android.ui.fragment.redemptions

import com.square.android.App
import com.square.android.R

private const val ITEM_COUNT = 2

private var PAGE_TITLES_RES = listOf(R.string.lifestyle, R.string.campaigns)

private const val POSITION_LIFESTYLE = 0
private const val POSITION_CAMPAIGNS = 1

class RedemptionsFragmentAdapter(fragmentManager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {
    private val titles: List<String> = PAGE_TITLES_RES.map { App.getString(it) }

    override fun getItem(position: Int) =
            when (position) {
                POSITION_LIFESTYLE -> null
                POSITION_CAMPAIGNS -> null
                else -> throw IllegalArgumentException("Illegal position: $position")
            }

    override fun getCount() = ITEM_COUNT

    override fun getPageTitle(position: Int) = titles[position]
}