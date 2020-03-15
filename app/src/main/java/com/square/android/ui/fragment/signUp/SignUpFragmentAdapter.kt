package com.square.android.ui.fragment.signUp

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.square.android.utils.widget.WrapContentPagingViewPager
import android.util.SparseArray
import com.square.android.data.pojo.SignUpData
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.MvpFragment

private const val ITEM_COUNT = 3

const val POSITION_ONE = 0
const val POSITION_TWO = 1
const val POSITION_THREE = 2

class SignUpFragmentAdapter(fragmentManager: androidx.fragment.app.FragmentManager, var signUpData: SignUpData): androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    private var mCurrentPosition = -1

    private var registeredFragments = SparseArray<BaseFragment>()

    override fun getItem(position: Int): Fragment{
        return when (position) {
            POSITION_ONE -> {
                val fragment: BaseFragment = SignUpOneFragment.newInstance(signUpData)
                registeredFragments.put(position, fragment)
                fragment
            }

            POSITION_TWO -> {
                val fragment: BaseFragment = SignUpTwoFragment.newInstance(signUpData)
                registeredFragments.put(position, fragment)
                fragment
            }

            POSITION_THREE -> {
                val fragment: BaseFragment = SignUpThreeFragment.newInstance(signUpData)
                registeredFragments.put(position, fragment)
                fragment
            }

            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    fun getRegisteredFragment(position: Int): MvpFragment = registeredFragments.get(position)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)

        super.destroyItem(container, position, `object`)
    }

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
}