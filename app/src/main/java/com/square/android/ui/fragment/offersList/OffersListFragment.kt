package com.square.android.ui.fragment.offersList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionFull
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.presentation.presenter.offersList.OffersListPresenter
import com.square.android.presentation.view.offersList.OffersListView
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.explore.GridItemDecoration
import com.square.android.ui.fragment.review.EXTRA_REDEMPTION
import kotlinx.android.synthetic.main.fragment_offers_list.*
import org.jetbrains.anko.bundleOf

class OffersListFragment: BaseFragment(), OffersListView, OffersListAdapter.Handler, SelectOfferDialog.Handler, CouponDialog.Handler {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(redemptionInfo: RedemptionInfo): OffersListFragment {
            val fragment = OffersListFragment()

            val args = bundleOf(EXTRA_REDEMPTION to redemptionInfo)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: OffersListPresenter

    @ProvidePresenter
    fun providePresenter(): OffersListPresenter = OffersListPresenter( arguments?.getParcelable(EXTRA_REDEMPTION) as RedemptionInfo)

    private var adapter: OffersListAdapter? = null

    private var dialog: SelectOfferDialog? = null

    private var currentId: Long? = null

    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_offers_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(activity!!)
        offersListBack.setOnClickListener {presenter.backPressed()}
    }

    override fun showProgress() {
        offersListRv.visibility = View.GONE
        offersListProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        offersListProgress.visibility = View.GONE
        offersListRv.visibility = View.VISIBLE
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showCouponDialog(redemptionFull: RedemptionFull, userData: Profile.User, offerInfo: OfferInfo) {
        val dialog = CouponDialog(activity!!, true)
        dialog.show(redemptionFull, userData, offerInfo, this)
    }

    override fun dialogCancelled() {
        presenter.navigateToClaimed()
    }

    override fun showOfferDialog(offer: OfferInfo) {
        currentId = offer.id

        dialog = SelectOfferDialog(offer, this)

        dialog!!.show(fragmentManager, "")
    }

    override fun confirmClicked(id: Long) {
        presenter.dialogSubmitClicked(id)
    }

    override fun showData(data: List<OfferInfo>, redemptionFull: RedemptionFull) {
        adapter = OffersListAdapter(data, this, redemptionFull)

        offersListRv.adapter = adapter
        offersListRv.layoutManager = GridLayoutManager(activity, 2)
        offersListRv.adapter = adapter
        offersListRv.addItemDecoration(GridItemDecoration(2,offersListRv.context.resources.getDimension(R.dimen.value_24dp).toInt(), false))

        offersListDate.text = redemptionFull.redemption.date.replace("-",".")
        offersListInterval.text = redemptionFull.redemption.startTime.replace(" ","")+"\n"+redemptionFull.redemption.endTime.replace(" ","")
        offersListTitle.text = redemptionFull.redemption.place.name

        offersListCheckBtn.setOnClickListener {presenter.checkIn()}

        visibleNow()
    }

    override fun setSelectedItem(position: Int) {
        adapter?.setSelectedItem(position)

        offersListCheckBtn.visibility = View.VISIBLE
    }

    override fun itemClicked(position: Int) {
        presenter.dialogAllowed = true

        presenter.itemClicked(position)
    }

//    override val PERMISSION_REQUEST_CODE: Int?
//        get() = 1341
//
//    override val tutorial: Tutorial?
//        get() =  Tutorial.Builder(tutorialKey = TutorialService.TutorialKey.SELECT_OFFER)
//                .addNextStep(TutorialStep(
//                        // width percentage, height percentage for text with arrow
//                        floatArrayOf(0.35f, 0.50f),
//                        getString(R.string.tut_4_1),
//                        TutorialStep.ArrowPos.TOP,
//                        R.drawable.arrow_bottom_left_x_top_right,
//                        0.60f,
//                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
//                        // 0f,0f,0f,0f for covering entire screen
//                        floatArrayOf(0f,0f,0.15f,312f),
//                        1,
//                        // delay before showing view in ms
//                        500f))
//                .addNextStep(TutorialStep(
//                        // width percentage, height percentage for text with arrow
//                        floatArrayOf(0.35f, 0.50f),
//                        "",
//                        TutorialStep.ArrowPos.TOP,
//                        R.drawable.arrow_bottom_left_x_top_right,
//                        0.60f,
//                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
//                        // 0f,0f,0f,0f for covering entire screen
//                        floatArrayOf(0f,0f,0.0f,0f),
//                        0,
//                        // delay before showing view in ms
//                        500f))
//
//                .setOnNextStepIsChangingListener {
//                    if(it == 2){
//                        presenter.itemClicked(0)
//                    }
//                }
//                .setOnContinueTutorialListener {
//                    dialog?.cancel()
//                    currentId?.run {presenter.dialogSubmitClicked(this)}
//                }
//                .build()
}
