package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class CampaignInterval(
        @field:JsonProperty("_id")
        var id: Long = 0,
        var location: Location? = null,
        var slots: List<Slot>? = null
): Parcelable {

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Location(
            @field:JsonProperty("_id")
            var id: Long? = null,

            var address: String? = null,
            var city: String? = null,
            var coordinates: Coordinates? = null
    ) : Parcelable {
        fun getAddressString(): String {
            return "$address, $city"
        }

        fun latLng() = coordinates?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(0.0, 0.0)
    }

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Coordinates(
            var longitude: Double = 0.0,
            var latitude: Double = 0.0
    ) : Parcelable

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Slot(
            @field:JsonProperty("_id")
            var id: String? = null,

            var start: String = "",
            var end: String = "",
            var day: String? = null,
            @field:JsonProperty("free")
            var slots: Int = 0,

            var date: String? = null,
            var startTime: String? = null

    ) : Parcelable
}