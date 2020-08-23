package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ProfessionData(
        @Json(name = "professionId")
        var id: String = "",
        var main: Boolean = false
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ProfessionDelete1Data(
        @Json(name = "professionId")
        var id: String = ""
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ProfessionDelete2Data(
        @Json(name = "professionId")
        var id: Int = 0
): Parcelable