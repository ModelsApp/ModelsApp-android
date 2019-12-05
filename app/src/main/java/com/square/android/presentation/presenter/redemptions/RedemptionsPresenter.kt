package com.square.android.presentation.presenter.redemptions

import android.location.Location
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.square.android.App
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.pojo.CampaignBooking
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.extensions.relativeTimeString
import com.square.android.extensions.toDate
import com.square.android.extensions.toDateBooking
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
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class RedemptionsUpdatedEvent

private const val MAXIMAL_DISTANCE = 75_000_000 // TODO change before release to the 75 m

@Suppress("unused")
@InjectViewState
class RedemptionsPresenter : BasePresenter<RedemptionsView>() {
    private val eventBus: EventBus by inject()

    var data: MutableList<Any>? = null

    var redemptions: MutableList<RedemptionInfo>? = null
    var campaignRedemptions: MutableList<CampaignBooking>? = null

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
            viewState.showProgress()

            redemptions = repository.getRedemptions().await().toMutableList()
            campaignRedemptions = repository.getCampaignBookings().await().toMutableList()

            if (!campaignRedemptions.isNullOrEmpty()) {
                val iterate = campaignRedemptions!!.toMutableList().listIterator()
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

            campaignRedemptions!!.sortedBy { it.pickUpDate?.toDate() }

            if (data != null) {
                if (data!!.filterIsInstance<RedemptionInfo>().isEmpty()) {
                    changeDataType(true)
                } else {
                    changeDataType(false)
                }
            } else {
                changeDataType(false)
            }
        }
    }

    fun campaignClicked(id: Long){
        val item = data!!.filterIsInstance<CampaignBooking>().firstOrNull { it.campaignId == id } ?: return

        router.navigateTo(SCREENS.CAMPAIGN_DETAILS, item.campaignId)
    }

    fun changeDataType(areCampaignRedemptions: Boolean) = launch {
        viewState.showProgress()

        if(!areCampaignRedemptions){
            data = addHeaders(redemptions!!).await().toMutableList()
            viewState.hideProgress()
            viewState.showData(data!!)
        } else{
            data = addHeaders(campaignRedemptions!!).await().toMutableList()
            viewState.hideProgress()
            viewState.showData(data!!)
        }
    }

    fun claimClicked(id: Long) {
        val item = data!!.filterIsInstance<RedemptionInfo>().firstOrNull { it.id == id } ?: return

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

        try {
            val s = item.date.split("-")

            val sStart = item.startTime.split(".")
            val sEnd = item.endTime.split(".")

            // 10 minutes in milliseconds
            val claimTime = 10*60000

            val actualCal = Calendar.getInstance()

            val redemptionCalBeginning = Calendar.getInstance()
            redemptionCalBeginning.set(Calendar.YEAR, s[2].toInt())
            redemptionCalBeginning.set(Calendar.MONTH, s[1].toInt() -1)
            redemptionCalBeginning.set(Calendar.DAY_OF_MONTH, s[0].toInt())
            redemptionCalBeginning.set(Calendar.HOUR_OF_DAY, sStart[0].toInt())
            redemptionCalBeginning.set(Calendar.MINUTE, sStart[1].toInt())

            val redemptionCalEnding = Calendar.getInstance()
            redemptionCalEnding.set(Calendar.YEAR, s[2].toInt())
            redemptionCalEnding.set(Calendar.MONTH, s[1].toInt() -1)
            redemptionCalEnding.set(Calendar.DAY_OF_MONTH, s[0].toInt())
            redemptionCalEnding.set(Calendar.HOUR_OF_DAY, sEnd[0].toInt())
            redemptionCalEnding.set(Calendar.MINUTE, sEnd[1].toInt())

            if(((redemptionCalBeginning.timeInMillis - actualCal.timeInMillis) > 0) && ((redemptionCalBeginning.timeInMillis - actualCal.timeInMillis) > claimTime) ){
                viewState.showMessage(R.string.claim_too_early)
            } else if(((redemptionCalEnding.timeInMillis - actualCal.timeInMillis) < 0) && ((actualCal.timeInMillis - redemptionCalEnding.timeInMillis) > claimTime)){
                viewState.showMessage(R.string.claim_too_late)
            } else{
                router.navigateTo(SCREENS.SELECT_OFFER, item)
            }

        } catch (e: Exception){
            viewState.showMessage(R.string.error_occurred)
        }

//        AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.OFFER_SELECT.apply { venueName = item.place.name },
//                hashMapOf("id" to item.id.toString())), repository)
    }

    fun claimedInfoClicked(id: Long) {
        val item = data!!.filterIsInstance<RedemptionInfo>().firstOrNull { it.id == id } ?: return

        router.navigateTo(SCREENS.SELECT_OFFER, item)
    }

    fun redemptionDetailsClicked(placeId: Long){
        router.navigateTo(SCREENS.PLACE, placeId)
    }

    fun cancelRedemptionClicked(id: Long) {
        val item = data!!.filterIsInstance<RedemptionInfo>().firstOrNull { it.id == id } ?: return

        //TODO check if working
        val position = data!!.indexOf(item)

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

            val r = redemptions!!.firstOrNull { it.id == id}
            r?.let {
                redemptions!!.removeAt(redemptions!!.indexOf(it))
            }

            viewState.removeItem(position)

            if (removeHeader) {
                data!!.removeAt(position - 1)
                viewState.removeItem(position - 1)
            }

            sendBadgeEvent()

            viewState.showMessage(result.message)
        }
    }

    fun cancelCampaignClicked(id: Long) {
        //TODO
    }

    private fun sendBadgeEvent() {
        val event = BadgeStateChangedEvent()

        eventBus.post(event)
    }

    private fun addHeaders(data: List<Any>): Deferred<List<Any>> = GlobalScope.async {
        val today = Calendar.getInstance()
        val itemCalendar = Calendar.getInstance()

        val result = ArrayList<Any>()

//       redemption
        val closedTitle = App.getString(R.string.closed)
        val claimedTitle = App.getString(R.string.claimed)

//       campaign
        val activeTitle = App.getString(R.string.active_campaigns)
        val completedTitle = App.getString(R.string.completed_campaigns)
        
//        val data2 = listOf(RedemptionInfo().apply { date = "2019-11-22"; claimed = true; startTime ="14.00"; endTime="15.00"; place.name = "Nice redemption"; place.address = "Via Giorgio 2, 324 Milano"; }, RedemptionInfo().apply {date = "2019-11-22"; startTime ="14.00"; endTime="15.00"; place.name = "Nice redemption"; place.address = "Via Giorgio 2, 324 Milano"; }, RedemptionInfo().apply { date = "2019-11-26"; startTime ="14.00"; endTime="15.00"; place.name = "Nice redemption"; place.address = "Via Giorgio 2, 324 Milano"; },
//                CampaignBooking().apply { pickUpDate = "2019-11-25" }, CampaignBooking().apply { pickUpDate = "2019-11-25" }, CampaignBooking().apply { pickUpDate = "2019-11-28" },
//                CampaignBooking().apply { pickUpDate = "2019-11-27"; time = "14.00 - 15.00"; title = "Nice c redemption" }, CampaignBooking().apply { pickUpDate = "2019-11-30" }, CampaignBooking().apply { pickUpDate = "2019-11-29" },
//                CampaignBooking().apply { pickUpDate = "2019-11-27" }, CampaignBooking().apply { pickUpDate = "2019-12-12" }, CampaignBooking().apply { pickUpDate = "2019-11-24" },
//                CampaignBooking().apply { pickUpDate = "2019-11-21" }, CampaignBooking().apply { pickUpDate = "2019-11-22" })


        groups = data.groupByTo(mutableMapOf()) {
           if(it is RedemptionInfo) {
               if (it.closed) {
                   return@groupByTo closedTitle
               }

               if (it.claimed) {
                   return@groupByTo claimedTitle
               }

               val date = "${it.date} ${it.endTime}".toDateBooking()
               itemCalendar.time = date
           }

            if(it is CampaignBooking){
                //TODO (no info for now)
//                if (it.active) {
//                    return@groupByTo activeTitle
//                }
//                if (it.completed) {
//                    return@groupByTo completedTitle
//                }

                if(it.pickUpDate != null){
                    val date = it.pickUpDate!!.toDate()
                    itemCalendar.time = date
                }
            }
            val determinedTime = itemCalendar.relativeTimeString(today)

            Log.e("LOL", "BOOKING: " + determinedTime)
            return@groupByTo determinedTime
        }

        val sorted = groups!!.toSortedMap(compareByDescending<String> { titleToIndex(it) }.thenByDescending {
            if(titleToIndex(it) == 2){
                it.toDate()
            } else{
                it
            }
        })

        sorted.forEach { (title, list) ->
                result.addAll(0, list.sortedBy {
                    if(it is RedemptionInfo){
                        "${it.date} ${it.endTime}".toDateBooking()
                    } else{
                        (it as CampaignBooking).pickUpDate?.toDate()
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
            App.getString(R.string.active_campaigns) -> 6
            App.getString(R.string.completed_campaigns) -> 7
            else -> 2
        }
    }

}
