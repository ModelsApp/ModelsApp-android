package com.square.android.ui.fragment.claimedRedemption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionFull
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.presentation.presenter.claimedRedemption.ClaimedRedemptionPresenter
import com.square.android.presentation.view.claimedRedemption.ClaimedRedemptionView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.review.EXTRA_REDEMPTION
import kotlinx.android.synthetic.main.fragment_claimed_redemption.*
import org.jetbrains.anko.bundleOf

class ClaimedRedemptionFragment: BaseFragment(), ClaimedRedemptionView{

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(redemptionInfo: RedemptionInfo): ClaimedRedemptionFragment {
            val fragment = ClaimedRedemptionFragment()

            val args = bundleOf(EXTRA_REDEMPTION to redemptionInfo)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ClaimedRedemptionPresenter

    @ProvidePresenter
    fun providePresenter(): ClaimedRedemptionPresenter = ClaimedRedemptionPresenter( arguments?.getParcelable(EXTRA_REDEMPTION) as RedemptionInfo)

    override fun showData(redemptionFull: RedemptionFull, redemptionInfo: RedemptionInfo, user: Profile.User) {
        initializePager(redemptionFull, redemptionInfo, user)

        claimedRedemptionTitle.text = redemptionFull.redemption.place.name
    }

    override fun showProgress() {
        claimedRedemptionProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        claimedRedemptionProgress.visibility = View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_claimed_redemption, container, false)
    }

    private fun initializePager(redemptionFull: RedemptionFull, redemptionInfo: RedemptionInfo, user: Profile.User) {

        val adapter = ClaimedRedemptionAdapter(childFragmentManager, redemptionFull, redemptionInfo, user)

        claimedRedemptionPager.adapter = adapter

        claimedRedemptionTab.setupWithViewPager(claimedRedemptionPager)

        //(claimedRedemptionPager.adapter?.instantiateItem(claimedRedemptionPager, 0) as BaseFragment).visibleNow()

        claimedRedemptionPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                (claimedRedemptionPager.adapter?.instantiateItem(claimedRedemptionPager, position) as BaseFragment).visibleNow()
            }
        })
    }

}