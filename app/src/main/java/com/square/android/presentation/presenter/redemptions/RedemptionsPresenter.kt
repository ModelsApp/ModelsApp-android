package com.square.android.presentation.presenter.redemptions

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.square.android.App
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.pojo.CampaignBooking
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.extensions.relativeTimeString
import com.square.android.extensions.toDateYMD
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.main.BadgeStateChangedEvent
import com.square.android.presentation.view.redemptions.RedemptionsView
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.util.*
import kotlin.collections.ArrayList

class RedemptionsUpdatedEvent

class ClaimedExtras(val offerId: Long, val redemptionId: Long)

private const val MAXIMAL_DISTANCE = 75_000_000 // TODO change before release to the 75 m

@Suppress("unused")
@InjectViewState
class RedemptionsPresenter : BasePresenter<RedemptionsView>() {
    private val eventBus: EventBus by inject()

    var data: MutableList<Any>? = null

    private var groups: MutableMap<String, MutableList<Any>>? = null

    private var lastLocation: Location? = null

    init {
        eventBus.register(this)
    }

    override fun onDestroy() {
        eventBus.unregister(this)
    }

    override fun attachView(view: RedemptionsView?) {
        super.attachView(view)

        if (data == null) loadData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedemptionsUpdatedEvent(event: RedemptionsUpdatedEvent) {
        loadData()
    }

    private fun loadData() {
        launch {
            val redemptions = repository.getRedemptions().await()
            val campaignRedemptions = repository.getCampaignBookings().await()

            if(!campaignRedemptions.isNullOrEmpty()){
                val iterate = campaignRedemptions.toMutableList().listIterator()
                while (iterate.hasNext()) {
                    val oldValue = iterate.next()

                    oldValue.pickUpDate?.let {
                        val split = it.split(" ")

                        oldValue.pickUpDate = split[0]
                        oldValue.time = split[1]

                        iterate.set(oldValue)
                    }
                }
            }

            val allRedemptions: List<Any> = if(!campaignRedemptions.isNullOrEmpty() && !redemptions.isNullOrEmpty()){
                redemptions + campaignRedemptions.toList()
            } else if(!campaignRedemptions.isNullOrEmpty()){
                campaignRedemptions.toList()
            } else if (!redemptions.isNullOrEmpty()){
                redemptions
            } else{
                listOf()
            }

            data = addHeaders(allRedemptions).await().toMutableList()

            viewState.hideProgress()
            viewState.showData(data!!)
        }
    }

    fun campaignClicked(position: Int){
        val item = data!![position] as? CampaignBooking ?: return

        router.navigateTo(SCREENS.CAMPAIGN_DETAILS, item.campaignId)
    }

    fun claimClicked(position: Int) {

        val item = data!![position] as? RedemptionInfo ?: return

        if (lastLocation == null) {
            viewState.showMessage(R.string.cannot_obtain_location)
            return
        }

        //TODO IMPORTANT - no place.location in RedemptionInfo
//        val distance = lastLocation!!.distanceTo(item.place.location)
//
//        if (distance > MAXIMAL_DISTANCE) {
//            viewState.showMessage(R.string.too_far_from_book)
//            return
//        }

        router.navigateTo(SCREENS.SELECT_OFFER, item)

//        AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.OFFER_SELECT.apply { venueName = item.place.name },
//                hashMapOf("id" to item.id.toString())), repository)
    }

    fun claimedInfoClicked(position: Int) {
        val item = data!![position] as? RedemptionInfo ?: return

        router.navigateTo(SCREENS.SELECT_OFFER, item)
    }

    fun cancelClicked(position: Int) {
        val item = data!![position] as? RedemptionInfo ?: return

        launch {
            val result = repository.deleteRedemption(item.id).await()

            var removeHeader: Boolean

            val previous = data!![position - 1]
            removeHeader = previous is String

            val isNotLast = position < data!!.size - 1

            if (isNotLast) {
                val next = data!![position + 1]

                val isNextHeader = next is String

                removeHeader = removeHeader && isNextHeader
            }

            data!!.removeAt(position)
            viewState.removeItem(position)

            if (removeHeader) {
                data!!.removeAt(position - 1)
                viewState.removeItem(position - 1)
            }

            sendBadgeEvent()

            viewState.showMessage(result.message)
        }
    }

    private fun sendBadgeEvent() {
        val event = BadgeStateChangedEvent()

        eventBus.post(event)
    }

    private fun addHeaders(data: List<Any>): Deferred<List<Any>> = GlobalScope.async {
        val today = Calendar.getInstance()
        val itemCalendar = Calendar.getInstance()

        val result = ArrayList<Any>()

        val closedTitle = App.getString(R.string.closed)
        val claimedTitle = App.getString(R.string.claimed)
        
//        val data2 = listOf(RedemptionInfo().apply { date = "2019-11-22"; claimed = true; startTime ="14.00"; endTime="15.00"; place.name = "Nice redemption"; place.address = "Via Giorgio 2, 324 Milano"; }, RedemptionInfo().apply {date = "2019-11-22"; startTime ="14.00"; endTime="15.00"; place.name = "Nice redemption"; place.address = "Via Giorgio 2, 324 Milano"; }, RedemptionInfo().apply { date = "2019-11-26"; startTime ="14.00"; endTime="15.00"; place.name = "Nice redemption"; place.address = "Via Giorgio 2, 324 Milano"; },
//                CampaignBooking().apply { pickUpDate = "2019-11-25" }, CampaignBooking().apply { pickUpDate = "2019-11-25" }, CampaignBooking().apply { pickUpDate = "2019-11-28" },
//                CampaignBooking().apply { pickUpDate = "2019-11-27"; time = "14.00 - 15.00"; title = "Nice c redemption" }, CampaignBooking().apply { pickUpDate = "2019-11-30" }, CampaignBooking().apply { pickUpDate = "2019-11-29" },
//                CampaignBooking().apply { pickUpDate = "2019-11-27" }, CampaignBooking().apply { pickUpDate = "2019-12-12" }, CampaignBooking().apply { pickUpDate = "2019-11-24" },
//                CampaignBooking().apply { pickUpDate = "2019-11-21" }, CampaignBooking().apply { pickUpDate = "2019-11-22" })

        groups = data.groupByTo(mutableMapOf()) {
           if(it is RedemptionInfo){
               if (it.closed) {
                   return@groupByTo closedTitle
               }

               if (it.claimed) {
                   return@groupByTo claimedTitle
               }

               val date = it.date.toDateYMD()
               itemCalendar.time = date
           }

            if(it is CampaignBooking){
                if(it.pickUpDate != null){
                    val date = it.pickUpDate!!.toDateYMD()
                    itemCalendar.time = date
                }
            }
            itemCalendar.relativeTimeString(today)
        }

        val sorted = groups!!.toSortedMap(compareByDescending<String> { titleToIndex(it) }.thenByDescending {
            if(titleToIndex(it) == 2){
                it.toDateYMD()
            } else{
                it
            }
        })

        sorted.forEach { (title, list) ->
                result.addAll(0, list.sortedByDescending {
                    if(it is RedemptionInfo){
                        it.date.toDateYMD()
                    } else{
                        (it as CampaignBooking).pickUpDate?.toDateYMD()
                    }
                })
                result.add(0, title)
        }

        result
    }

    fun locationGotten(location: Location?) {
        lastLocation = location
    }

    private fun titleToIndex(title: String): Int {
        return when(title){
            App.getString(R.string.today) -> 0
            App.getString(R.string.tomorrow) -> 1
            App.getString(R.string.past) -> 3
            App.getString(R.string.claimed) -> 4
            App.getString(R.string.closed) -> 5
            else -> 2
        }
    }
}
