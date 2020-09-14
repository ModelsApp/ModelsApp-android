package com.square.android.ui.fragment.offersList

import com.square.android.data.newPojo.OfferInfo
import com.square.android.ui.fragment.dOfferInfo.DInfoFragment
import com.square.android.ui.fragment.dOffer.DOfferFragment

private const val ITEM_COUNT = 2

private const val POSITION_D_OFFER = 0
private const val POSITION_D_INFO = 1

class DinnerOfferDialogAdapter(fragmentManager: androidx.fragment.app.FragmentManager, val offerInfo: OfferInfo) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            POSITION_D_OFFER -> DOfferFragment(offerInfo)
            POSITION_D_INFO -> DInfoFragment(offerInfo)
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    override fun getCount() = ITEM_COUNT
}