package com.square.android.presentation.presenter.agenda

import android.os.Parcelable
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.agenda.ScheduleBookingView
import kotlinx.android.parcel.Parcelize
import java.util.*

//TODO change when actual model is known and move to newPojo folder
@Parcelize
data class ScheduleBooking(
        var timeframe: String = "",
        // in: 51.9335342, 15.4780203
        // out: 51.9455313, 15.4879493
        var latlng: LatLng = LatLng(51.9455313, 15.4879493),
        var placeName: String = "La Perla dOro",
        var placeAddress: String = "2875 Robinson Rd",
        var placeImg: String = "",
        var date: String = "Dec 14th",
        var code: String = "35dgnj37234",
        var type: String = "Complimentary",
        var userName: String = "Rosemary Edwards",
        var userGender: Int = 1,
        var userImg: String = "",
        var offerName: String = "Breakfast",
        var offerTip: String = "$14.99",
        var ofeferTakeaway: String = "Available",
        var offerDescription: String = "Desc desc desc desc desc desc desc desc desc",
        var notes: String = "Notes notes notes notes notes notes notes notes notes notes notes notes notes notes notes notes notes notes notes notes notes" +
                " notes notes notes notes notes notes notes notes notes notes notes notes",
        var userCheckedIn: Boolean = false
        ): Parcelable

@InjectViewState
class ScheduleBookingPresenter(): BasePresenter<ScheduleBookingView>() {

    var data: ScheduleBooking? = null

    var lastUserLocation: LatLng? = null

    //TODO get these from data returned from api
    var placeLocationPoint: LatLng? = null
    //TODO change when timeframe model is known
    var placeTimeframe: String? = null
    var isUserCheckedIn: Boolean = false
    //TODO change when model is known
    var offerDate: String = ""

    //TODO change when timeframe model is known
    //TODO pass and check date too
    fun doTimeFrameMatchActualTime(timeframe: String): Boolean{
                 try {
                     //TODO uncomment and change when timeframe model is known
//                    val s = item.date.split("-")
//
//                    val sStart = item.startTime.split(".")
//                    val sEnd = item.endTime.split(".")
//
//                    // 10 minutes in milliseconds
//                    val claimTime = 10*60000
//
//                    val actualCal = Calendar.getInstance()
//
//                    val redemptionCalBeginning = Calendar.getInstance()
//                    redemptionCalBeginning.set(Calendar.YEAR, s[2].toInt())
//                    redemptionCalBeginning.set(Calendar.MONTH, s[1].toInt() -1)
//                    redemptionCalBeginning.set(Calendar.DAY_OF_MONTH, s[0].toInt())
//                    redemptionCalBeginning.set(Calendar.HOUR_OF_DAY, sStart[0].toInt())
//                    redemptionCalBeginning.set(Calendar.MINUTE, sStart[1].toInt())
//
//                    val redemptionCalEnding = Calendar.getInstance()
//                    redemptionCalEnding.set(Calendar.YEAR, s[2].toInt())
//                    redemptionCalEnding.set(Calendar.MONTH, s[1].toInt() -1)
//                    redemptionCalEnding.set(Calendar.DAY_OF_MONTH, s[0].toInt())
//                    redemptionCalEnding.set(Calendar.HOUR_OF_DAY, sEnd[0].toInt())
//                    redemptionCalEnding.set(Calendar.MINUTE, sEnd[1].toInt())
//
//                     return if(((redemptionCalBeginning.timeInMillis - actualCal.timeInMillis) > 0) && ((redemptionCalBeginning.timeInMillis - actualCal.timeInMillis) > claimTime) ){
//                         false
//                     } else !(((redemptionCalEnding.timeInMillis - actualCal.timeInMillis) < 0) && ((actualCal.timeInMillis - redemptionCalEnding.timeInMillis) > claimTime))

                } catch (e: Exception){
                    viewState.showMessage(R.string.error_occurred)
                }
        return false
    }



    init {
        loadData()
    }

    fun loadData() = launch{

        //TODO get data from api
        data = ScheduleBooking()

        placeLocationPoint = data!!.latlng
        placeTimeframe = data!!.timeframe
        isUserCheckedIn = data!!.userCheckedIn
        offerDate = data!!.date

        viewState.showData(data!!)
    }

}
