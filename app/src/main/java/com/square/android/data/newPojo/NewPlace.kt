package com.square.android.data.newPojo

import android.os.Parcelable
import com.mapbox.mapboxsdk.geometry.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class NewPlace(
        @Json(name="_id")
        var id: Long = 0,
        var name: String = "",
        var type: String = "",
        var address: String = "",
        var description: String = "",
        var location: PlaceLocation? = null,
        var socials: Map<String, String?> = mapOf(),
        var level: Int? = null,
        var schedule: Map<String, String> = mapOf(),
        var slots: Int = 0,
        var credits: Int = 0,
        var offers: List<Long> = listOf(),
        var photos: List<String> = listOf(),
        var instapage: String? = null,
        // need model
//        var daysOffs
        var phone: String = "",
        var isActive: Boolean = true,

        var mainCategory: String = "",
        var genderAvailability: PlacesFiltersAvailability = PlacesFiltersAvailability(),
        var secondaryType: String = "",
        var country: String = "",
        var state: String = "",
        var city: String = "",
        var zip: String = "",
        var street: String = "",
        var houseNum: Int = 0,
        @Json(name = "referalCode")
        var referralCode: String = "",
        @Json(name = "offerDeal")
        var offerDeals: List<OfferDeal> = listOf()

): Parcelable {

    var distance: Int? = null


    fun getMainImage() = photos.firstOrNull()
}

@Parcelize
@JsonClass(generateAdapter = true)
data class OfferDeal(
        @Json(name="_id")
        var id: String = "",
        @Json(name="chanelCreditId")
        var channelCreditId: String = "",
        var offerId: Long = 0,
        var activeInApp: Boolean = true,
        var currencyId: Long = 0,
        var dealOptions: List<DealOption> = listOf(),
        var price: Int = 0,
        var quantity: Int = 0,
        var requiresConfirmation: Boolean = true,
        var type: String = "",
        var userLevelId: Int = 0
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DealOption(
        @Json(name="chanelCreditId")
        var channelCreditId: String = "",
        var quantity: Int = 0
): Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class PlaceLocation(
        var type: String = "",
        var coordinates: List<Double> = listOf()
): Parcelable {

    fun getLatLng(): LatLng? = if(coordinates.size > 1) LatLng(coordinates[0], coordinates[1]) else null
}

@Parcelize
@JsonClass(generateAdapter = true)
data class PlaceImage(
        var url: String = "",
        var isMainImage: Boolean = false
): Parcelable