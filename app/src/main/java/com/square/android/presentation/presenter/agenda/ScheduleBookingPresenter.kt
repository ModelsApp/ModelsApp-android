package com.square.android.presentation.presenter.agenda

import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.agenda.ScheduleBookingView
import java.util.*

@InjectViewState
class ScheduleBookingPresenter(): BasePresenter<ScheduleBookingView>() {

    //TODO get these from data returned from api
    var placeLocationPoint: LatLng? = null
    //TODO change if timeframe model is known
    var placeTimeframe: String? = null
    var isUserCheckedIn: Boolean = false

    //TODO change if timeframe model is known
    fun doTimeFrameMatchActualTime(timeframe: String): Boolean{
                 try {
                     //TODO uncomment and change if timeframe model is known
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

    }

    fun loadData() {
//        viewState.showPlaces(data)
    }


    fun exit(){
        router.exit()
    }

}
