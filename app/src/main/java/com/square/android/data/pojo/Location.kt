package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.parcel.Parcelize

//@Parcelize
//@JsonClass(generateAdapter = true)
//class Location: MutableList<Double> by mutableListOf(), Parcelable {
//    fun latLng()
//}
fun List<Double>.latLng(): LatLng {
    return LatLng(this[0], this[1])
}