package com.square.android.presentation.presenter.offersList

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.newPojo.OfferInfo
import com.square.android.data.pojo.RedemptionFull
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.agenda.RedemptionsUpdatedEvent
import com.square.android.presentation.presenter.main.BadgeStateChangedEvent
import com.square.android.presentation.view.offersList.OffersListView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class OffersListPresenter(private val redemptionInfo: RedemptionInfo) : BasePresenter<OffersListView>(){

    private var data: RedemptionFull? = null
    private var offers: List<OfferInfo>? = null

    private var currentPosition = 0

    private val bus: EventBus by inject()

    var dialogAllowed = true

    init {
        loadData()
    }

    private fun loadData() = launch {
        viewState.showProgress()

        data = repository.getRedemption(redemptionInfo.id).await()
        offers = repository.getOffersForBooking(data!!.redemption.place.id, redemptionInfo.id).await()

        viewState.hideProgress()

        viewState.showData(offers!!, data!!)
    }

    fun backPressed(){
        router.exit()
    }

    fun itemClicked(position: Int) {
        currentPosition = position

        if(dialogAllowed){
            dialogAllowed = false

            val offer = offers!![currentPosition]

            viewState.showOfferDialog(offer)
        }
    }

    fun checkIn() = launch  {
        viewState.showLoadingDialog()

        repository.claimOffer(redemptionInfo.id).await()
        repository.addOfferToBook(redemptionInfo.id, offers!![currentPosition].id).await()

        sendRedemptionsUpdatedEvent()
        sendBadgeEvent()

        viewState.hideLoadingDialog()

        val user = repository.getCurrentUser().await()

        viewState.showCouponDialog(data!!, user, offers!![currentPosition])
    }

    fun navigateToClaimed(){
        //TODO check if working correctly
        router.replaceScreen(SCREENS.CLAIMED_REDEMPTION, redemptionInfo)
    }

    private fun sendBadgeEvent() {
        val event = BadgeStateChangedEvent()

        bus.post(event)
    }

    private fun sendRedemptionsUpdatedEvent() {
        val event = RedemptionsUpdatedEvent()
        bus.post(event)
    }

    fun dialogSubmitClicked(id: Long) {
        viewState.setSelectedItem(currentPosition)
    }

}
