package com.square.android.presentation.presenter.review

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.*
import com.square.android.domain.review.ReviewInteractor
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.main.BadgeStateChangedEvent
import com.square.android.presentation.presenter.redemptions.RedemptionsUpdatedEvent
import com.square.android.presentation.view.review.ReviewView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

class ActionExtras(var index: Int, var id: String = "", var photo: ByteArray? = null, var type: String = "")

@InjectViewState
class ReviewPresenter(private val offerId: Long,
                      private val redemptionId: Long) : BasePresenter<ReviewView>() {

    private val bus: EventBus by inject()

    private val interactor : ReviewInteractor by inject()

    private var data: Offer? = null

    private var actions: MutableList<Offer.Action> = mutableListOf()
    var subActions: List<Offer.Action> = listOf()

    private val filledActions: MutableList<ActionExtras> = mutableListOf()

    init {
        loadData()
    }

    private fun loadData() = launch {
        viewState.showProgress()

        actions = repository.getActions(offerId, redemptionId).await().toMutableList()

        //TODO no subActions for now, action picture will not be working without them
        val pictureAction = actions.firstOrNull { it.type == TYPE_PICTURE }
        pictureAction?.let {
            actions.remove(it)
        }

        data = interactor.getOffer(offerId).await()

        for(action in actions){
            action.enabled = action.active
        }

//        actions = data!!.actions

        //TODO there are no subActions for now
//        subActions = data!!.subActions

//        for(subAction in subActions){
//            if(subAction.attempts >= subAction.maxAttempts) subAction.enabled = false
//        }
//
//        for(action in actions){
//            if(action.type != TYPE_PICTURE){
//                if(action.attempts >= action.maxAttempts) action.enabled = false
//            } else{
//                var disabledCount = 0
//                for(subAction in subActions){
//                    if(!subAction.enabled) disabledCount++
//                }
//                if(disabledCount == subActions.size) action.enabled = false
//            }
//        }

        viewState.hideProgress()
        viewState.showData(data!!, actions.toList())
    }

    fun itemClicked(index: Int) {
        if(index in filledActions.map { it.index }){
            viewState.showDeleteDialog(index)
        } else{
            //TODO get fb user name from API
            viewState.showDialog(index, actions[index], subActions, data!!.instaUser, "TODO")
        }
    }

    fun deleteFromFilled(index: Int){
        filledActions.remove(filledActions.first { it.index == index })

        viewState.changeSelection(index)

        if(filledActions.isEmpty()){
            viewState.hideButton()
        }
    }

//    fun addAction(index: Int, photo: ByteArray){
//        if(filledActions.isEmpty()){
//            viewState.showButton()
//        }
//
//        filledActions.add(ActionExtras(index, actions[index].id, photo))
//
//        viewState.changeSelection(index)
//    }

    fun addAction(index: Int, photo: ByteArray?, actionType: String){
        if(filledActions.isEmpty()){
            viewState.showButton()
        }

        filledActions.add(ActionExtras(index, actions[index].id, photo, actionType))

        viewState.changeSelection(index)
    }

    fun submitClicked() = launch {
        viewState.showLoadingDialog()

        //TODO error: D/OkHttp: <-- HTTP FAILED: javax.net.ssl.SSLException: Write error: ssl=0x7b6ed76208: I/O error during system call, Broken pipe
        //TODO changed ReviewInfo to link:String in api.addReview
        for(filledAction in filledActions){
            if(filledAction.type == TYPE_INSTAGRAM_STORY){
                 interactor.addReview(offerId, redemptionId, filledAction.type, filledAction.photo).await()
            } else{
                filledAction.photo?.let {
                    interactor.addReview(offerId, redemptionId, filledAction.type, it).await()
                }
            }
        }

        //TODO this part is working
        claimRedemption()
    }

    fun skipClicked() {
        viewState.showLoadingDialog()
        claimRedemption()
    }

    private fun claimRedemption() = launch {
        interactor.claimRedemption(redemptionId, offerId).await()

        sendRedemptionsUpdatedEvent()
        sendBadgeEvent()

        viewState.hideLoadingDialog()

        viewState.showCongratulations()
    }

    private fun sendBadgeEvent() {
        val event = BadgeStateChangedEvent()

        bus.post(event)
    }

    fun finishChain() = router.finishChain()

    private fun sendRedemptionsUpdatedEvent() {
        val event = RedemptionsUpdatedEvent()
        bus.post(event)
    }

}
