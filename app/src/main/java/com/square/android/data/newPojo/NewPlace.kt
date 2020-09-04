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
        var images: List<PlaceImage> = listOf(),
        var instapage: String? = null,
        // need model
//        var daysOffs
        var phone: String,
        var isActive: Boolean = true
): Parcelable {

    var distance: Int? = null


    fun getMainImage() = images.firstOrNull { it.isMainImage }?.url
    fun getAllPhotos() = images.map { it.url }
    fun getNotMainPhotos() = images.filter { !it.isMainImage }.map { it.url }
}

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