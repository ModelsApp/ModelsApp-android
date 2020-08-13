package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Speciality(
        @Json(name = "_id")
        var id: String? = "",
        var main: Boolean = false,
        var name: String = ""
): Parcelable