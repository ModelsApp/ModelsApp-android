package com.square.android.data.newPojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class CoordinatesData(
        var coordinates: Coordinates,
        var radius: Int,
        var search: String
//        ,var created: ???
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Coordinates(
        var lat: Double,
        @Json(name="lon")
        var lng: Double
): Parcelable