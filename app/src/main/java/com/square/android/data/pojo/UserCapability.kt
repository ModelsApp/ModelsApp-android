package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserCapability(
        @Json(name = "_id")
        var id: String = "",
        var name: String = "",
        @Json(name = "userCapability")
        var innerUserCapability: InnerUserCapability? = null
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class InnerUserCapability(
        @Json(name = "_id")
        var id: String = "",
        var name: String = "",
        var userId: Long? = null
): Parcelable