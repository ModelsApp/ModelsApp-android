package com.square.android.data.pojo

import android.os.Parcelable
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.data.network.IgnoreObjectIfIncorrect
import com.square.android.utils.SingleToArray
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@JsonClass(generateAdapter = true)
class Place(
        @Json(name="_id")
        var id: Long = 0,
        var mainImage: String? = null,
        var address: String = "",
        var type: String = "",
        var name: String = "",

        @Json(name = "location")
        @SingleToArray
        var rawStringLocation: @RawValue List<Any?> = listOf(),
        @Transient
        private var locationLatLng: LatLng? = null,
        var access: String = "",
        var icons: Icons? = null,
        var freeSpots: Int = 0,

//        var bookings: List<Booking> = listOf(),
        var intervals: List<Interval> = listOf(),
        var credits: Int = 0,
        var description: String = "",
        var level: Int? = 0,
        var offers: List<OfferInfo> = listOf(),
        var photos: List<String>? = listOf(),

        @IgnoreObjectIfIncorrect.IgnoreJsonObjectError
        @Transient
        var schedule: Map<String, ScheduleDay> = mapOf(),

        var city: String = "",

        //set this to slots from PlaceOffers when making a list of Place in event
        @Transient
        var slots: Int = 0,

        //TODO for getting event from place
        @Transient
        var isEventPlace: Boolean = false,
        @Transient
        var event: Event? = null,


        // Availability label data
        var availableOfferDay: String? = null

) : Parcelable {

    init {
        location()
    }

    fun location(): LatLng {

        if(locationLatLng == null){

            var loc = LatLng(0.0,0.0)

            runCatching{
                if(rawStringLocation.size > 1 && (rawStringLocation[0] != null && rawStringLocation[1] != null)){

                    loc = LatLng((rawStringLocation[0] as Double), (rawStringLocation[1] as Double))
                } else {
                    val str = rawStringLocation.toString()
                    val coordinates = str.replace("[{type=Point, coordinates=[", "")
                            .replace("]}]", "")
                            .replace(", ", ",")
                            .split(",")
                    loc = LatLng(coordinates[0].toDouble(), coordinates[1].toDouble())
                }
            }

            locationLatLng = loc

            return loc
        } else return locationLatLng!!
    }

    @Parcelize
    @JsonClass(generateAdapter = true)
    class PlaceLocation(
            var type: String = "",
            var coordinates: List<Double> = listOf()
    ) : Parcelable

    @Parcelize
    @JsonClass(generateAdapter = true)
    class Icons(
            var typology: List<String> = listOf(),
            var extras: List<String> = listOf()
    ) : Parcelable

    var distance: Int? = null

    var award: Int = 0

    fun stringDays() = schedule.keys
            .filterNot { it.isEmpty() }
            .joinToString(separator = "\n", transform = String::capitalize)

    fun stringTime() = schedule.values
            .map { it.start + " " + it.end }
            .joinToString(separator = "\n")


    @Parcelize
    @JsonClass(generateAdapter = true)
    class Interval(
            @Json(name="_id")
            var id: String? = null,
            var start: String = "",
            var end: String = "",
            var offers: List<Long> = listOf(),
            @Json(name="free")
            var slots: Int = 0,
            var description: String = ""
    ) : Parcelable

    data class Booking(
            @Json(name="_id")
            var id: Int = 0,
            var closed: Boolean = false,
            var date: String = "",
            var endTime: String = "",
            var name: String = "",
            var place: Int = 0,
            var startTime: String = "",
            var user: Int = 0
    )

}


