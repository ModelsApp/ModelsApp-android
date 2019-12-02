package com.square.android.ui.fragment.claimedCoupon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.RedemptionFull
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.claimedCoupon.ClaimedCouponPresenter
import com.square.android.presentation.view.claimedCoupon.ClaimedCouponView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.review.EXTRA_REDEMPTION_FULL
import com.square.android.ui.fragment.review.EXTRA_USER
import kotlinx.android.synthetic.main.fragment_claimed_coupon.*
import org.jetbrains.anko.bundleOf
import java.util.*

class ClaimedCouponFragment : BaseFragment(), ClaimedCouponView {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(redemptionFull: RedemptionFull, user: Profile.User): ClaimedCouponFragment {
            val fragment = ClaimedCouponFragment()

            val args = bundleOf(EXTRA_REDEMPTION_FULL to redemptionFull, EXTRA_USER to user)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ClaimedCouponPresenter

    @ProvidePresenter
    fun providePresenter(): ClaimedCouponPresenter = ClaimedCouponPresenter(arguments?.getParcelable(EXTRA_REDEMPTION_FULL) as RedemptionFull, arguments?.getParcelable(EXTRA_USER) as Profile.User)

    override fun showData(redemptionFull: RedemptionFull, user: Profile.User) {

        dateTv.text = redemptionFull.redemption.date
        timeTv.text = redemptionFull.redemption.startTime +" - " +redemptionFull.redemption.endTime
        placeTv.text = redemptionFull.redemption.place.name
        addressTv.text = redemptionFull.redemption.place.address

        //TODO no offerInfo in redemptionFull
//        val numberList: MutableList<Int> = mutableListOf()
//        val names = redemptionFull.offer.compositionAsStr()
//
//        val p = Pattern.compile("\\d+")
//        val m = p.matcher(redemptionFull.offer.compositionAsString())
//        while (m.find()) {
//            numberList.add(m.group().toInt())
//        }
//        offerNames.text = names
//        offerNumbers.text = numberList.joinToString(separator = "\n")

        //TODO what should be here?
//      notesTv.text =

        userImg.loadImage(url = (user.mainImage ?: user.photo)?: "", roundedCornersRadiusPx = 10)
        userName.text = user.name + user.surname[0] +"."

        //TODO offer img and name

        val calendar = Calendar.getInstance()
        val hour: String = if(calendar.get(Calendar.HOUR_OF_DAY) < 10) "0"+calendar.get(Calendar.HOUR_OF_DAY) else calendar.get(Calendar.HOUR_OF_DAY).toString()
        val minute: String = if(calendar.get(Calendar.MINUTE) < 10) "0"+calendar.get(Calendar.MINUTE) else calendar.get(Calendar.MINUTE).toString()

        checkedAt.text = "$hour:$minute"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_claimed_coupon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
