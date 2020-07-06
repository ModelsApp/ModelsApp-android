package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserDeliveryPoint(
        var id: String = "",
        var name: String = "",
        var address: String = "",
        var zip: String = "",
        var city: String = "",
        var state: String = "",
        var country: String = ""
): Parcelable