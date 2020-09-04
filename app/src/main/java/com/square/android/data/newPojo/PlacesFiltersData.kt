package com.square.android.data.newPojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PlacesFiltersData(
        var city: String = "",

        var mainCategory: String = "",
//        var categories: List<String>,

        var availability: PlacesFiltersAvailability = PlacesFiltersAvailability(),
        var typology: String = "",

        // Booking type
        // Both true == All
        var walkIn: Boolean = true, // Reservation needed
        var reservationApprove: Boolean = true, // Walk-in

        var timeSlots: PlacesFiltersTimeSlots = PlacesFiltersTimeSlots()


        // No Show me places
        // No Offers level
        // No Takeaway option
        //
        // Categories should be a list

): Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class PlacesFiltersTimeSlots(
        var from: String = "00:00", // "07:00"
        var to: String = "24:00"    // "09:00"
): Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class PlacesFiltersAvailability(
        var male: Boolean= true,
        var female: Boolean = true
): Parcelable
