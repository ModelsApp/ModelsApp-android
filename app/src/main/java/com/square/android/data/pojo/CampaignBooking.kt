package com.square.android.data.pojo

import com.square.android.presentation.presenter.agenda.ScheduleDivider
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class CampaignBooking(
        var title: String? = null,
        var campaignId: Long = 0,
        var pickUpDate: String? = null,
        var mainImage: String? = null,


        //Used only in app
        var time: String? = null
): ScheduleDivider()