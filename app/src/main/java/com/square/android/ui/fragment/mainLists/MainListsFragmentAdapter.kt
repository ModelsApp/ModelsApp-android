package com.square.android.ui.fragment.mainLists

import com.square.android.App
import com.square.android.R
import com.square.android.presentation.presenter.mainLists.MainData
import com.square.android.presentation.presenter.mainLists.POSITION_CAMPAIGNS
import com.square.android.presentation.presenter.mainLists.POSITION_EVENTS
import com.square.android.presentation.presenter.mainLists.POSITION_PLACES
import com.square.android.ui.fragment.campaignsList.CampaignsListFragment
import com.square.android.ui.fragment.eventsList.EventsListFragment
import com.square.android.ui.fragment.placesList.PlacesListFragment
import androidx.fragment.app.FragmentManager

private const val ITEM_COUNT = 3

private var PAGE_TITLES_RES = listOf(R.string.offers, R.string.events, R.string.jobs)

class PlacesFragmentAdapter(fragmentManager: FragmentManager, val data: MainData) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    private val titles: List<String> = PAGE_TITLES_RES.map { App.getString(it) }

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            POSITION_PLACES -> PlacesListFragment(data.placesData)
            POSITION_EVENTS -> EventsListFragment(data.eventsData)
            POSITION_CAMPAIGNS -> CampaignsListFragment(data.campaignsData)
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    override fun getCount() = ITEM_COUNT

    override fun getPageTitle(position: Int) = titles[position]
}