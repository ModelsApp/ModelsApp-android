package com.square.android.data.newPojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PlaceOffer(
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
        var noSquareTag: Boolean = true,
        @Json(name="offerCoupons")
        var coupons: List<OfferCoupon> = listOf(),
        var offerDeal: List<OfferDeal> = listOf(),
        var offerPlace: NewPlace? = null
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class OfferCoupon(
        @Json(name="_id")
        var id: String = "",
        var offerId: Long = 0,
        var from: String = "",
        var to: String = "",
        var date: String = "",
        var days: List<String>? = listOf(),
        var quantity: Int = 0,
        var walkIn: Boolean? = null,
        var status: String = "", // active
        var takeawayOption: String = "" // Allowed/NotAllowed(was NoTAllowed in example)
): Parcelable
