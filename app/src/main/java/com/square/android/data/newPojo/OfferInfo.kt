package com.square.android.data.newPojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class OfferInfo(
        @Json(name="_id")
        var id: Long = 0,
        var placeId: Long = 0,
        var status: String = "",
        var coverImage: String = "",
        var typology: String = "",
        var name: String = "",
        var description: String = "",
        var extraNotes: String = "",
        var valueWorth: Int = 0,
        val valueWorthCurrencyId: Int = 0,
        var userCategories: List<String>,
        var reservationApprove: Boolean = true,
        var noSquareTag: Boolean = true
): Parcelable