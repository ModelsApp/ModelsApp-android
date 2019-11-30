package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
class RedemptionInfo(
        @Json(name="_id")
        var id: Long = 0,
        var closed: Boolean = false,
        var date: String = "",
        var endTime: String = "",
        var offers: List<Long> = listOf(),
        var place: PlaceInfo = PlaceInfo(),
        var claimed: Boolean = false,
        var startTime: String = "",
        var user: Int = 0
) : Parcelable