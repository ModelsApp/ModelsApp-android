package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserRate(
        var type: String = "",
        var perDay: Double = 0.0,
        var perHour: Double = 0.0
): Parcelable