package com.square.android.presentation.view.place

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.newPojo.OfferInfo
import com.square.android.data.newPojo.PlaceOffer
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.PlaceExtra
import com.square.android.presentation.view.BaseView
import java.util.*

interface PlaceView : BaseView {
    fun showData(place: Place, offers: List<PlaceOffer>, calendar: Calendar, typeImage: String?, extras: List<PlaceExtra>)
    fun showDistance(distance: Int?)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showOfferDialog(offer: OfferInfo, place: Place?)

    fun showIntervals(data: List<Place.Interval>)

    fun setSelectedDayItem(position: Int)

    fun setSelectedIntervalItem(position: Int)

    fun showProgress()

    fun hideProgress()
}