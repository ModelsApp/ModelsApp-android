package com.square.android.ui.fragment.redemptions

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.square.android.R
import com.square.android.presentation.presenter.redemptions.RedemptionsPresenter
import com.square.android.presentation.view.redemptions.RedemptionsView
import com.square.android.ui.base.tutorial.Tutorial
import com.square.android.ui.base.tutorial.TutorialService
import com.square.android.ui.base.tutorial.TutorialStep
import com.square.android.ui.fragment.LocationFragment
import kotlinx.android.synthetic.main.fragment_redemptions.*

class RedemptionsFragment: LocationFragment(), RedemptionsView, RedemptionsAdapter.Handler {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "RedemptionsPresenter")
    lateinit var presenter: RedemptionsPresenter

    private var adapter : RedemptionsAdapter? = null

    var initialized = false

    override fun showData(ordered: List<Any>) {
        adapter = RedemptionsAdapter(ordered, this)
        redemptionsList.adapter = adapter

        if(!initialized){
            initialized = true
            visibleNow()
        }

        //TODO where to get tickets data(ticketLl)
    }

    private fun initializePager() {
        val fragAdapter = RedemptionsFragmentAdapter(childFragmentManager)

        redemptionsPager.isPagingEnabled = false

        redemptionsPager.adapter = fragAdapter
        redemptionsTab.setupWithViewPager(redemptionsPager)

        redemptionsPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                if(position == 0){
                    presenter.changeDataType(false)
                } else{
                    presenter.changeDataType(true)
                }

                ticketLl.visibility = if(position == 0) View.VISIBLE else View.GONE
            }
        })
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showProgress() {
        redemptionsList.visibility = View.INVISIBLE
        redemptionsProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        redemptionsProgress.visibility = View.GONE
        redemptionsList.visibility = View.VISIBLE
    }

    override fun claimClicked(id: Long) {
        presenter.claimClicked(id)
    }

    override fun claimedItemClicked(id: Long) {
        presenter.claimedInfoClicked(id)
    }

    override fun removeItem(position: Int) {
        adapter?.removeItem(position)
    }

    override fun cancelRedemptionClicked(id: Long) {
        presenter.cancelRedemptionClicked(id)
    }

    override fun cancelCampaignClicked(id: Long) {
        presenter.cancelCampaignClicked(id)
    }

    override fun campaignItemClicked(id: Long) {
        presenter.campaignClicked(id)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_redemptions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        redemptionsList.setHasFixedSize(true)

        initializePager()
    }

//    override val PERMISSION_REQUEST_CODE: Int?
//        get() = 1340

//    override val tutorial: Tutorial?
//        get() = when {
//            presenter.data
//                    ?.filterIsInstance<RedemptionInfo>()
//                    ?.isNullOrEmpty() == true -> null
//            else -> Tutorial.Builder(tutorialKey = TutorialService.TutorialKey.REDEMPTIONS)
//                    .addNextStep(TutorialStep(
//                            // width percentage, height percentage for text with arrow
//                            floatArrayOf(0.50f, 0.78f),
//                            getString(R.string.tut_3_1),
//                            TutorialStep.ArrowPos.TOP,
//                            R.drawable.arrow_bottom_right_x_top_left,
//                            0.35f,
//                            // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
//                            // 0f,0f,0f,0f for covering entire screen
//                            floatArrayOf(0f, 0f, 0.30f, 500f),
//                            1,
//                            // delay before showing view in ms
//                            0f))
//                    .addNextStep(TutorialStep(
//                            // width percentage, height percentage for text with arrow
//                            floatArrayOf(0.50f, 0.38f),
//                            getString(R.string.tut_3_2),
//                            TutorialStep.ArrowPos.TOP,
//                            R.drawable.arrow_bottom_right_x_top_left,
//                            0.35f,
//                            // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
//                            // 0f,0f,0f,0f for covering entire screen
//                            floatArrayOf(0f, 0f, 0.1f, 190f),
//                            1,
//                            // delay before showing view in ms
//                            500f,
//                            0))
//                    .setOnNextStepIsChangingListener {
//
//                    }
//                    .setOnContinueTutorialListener {
//                        presenter.claimClicked(1)
//                    }
//                    .build()
//        }
}
