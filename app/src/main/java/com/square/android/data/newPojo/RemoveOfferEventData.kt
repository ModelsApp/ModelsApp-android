package com.square.android.data.newPojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class RemoveOfferEventData(
        var eventId: String,
        var couponId: String
): Parcelable