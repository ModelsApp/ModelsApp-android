package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

//TODO delete? not used?

@Parcelize
@JsonClass(generateAdapter = true)
data class CapabilitiesResult(
        var list: List<Capability>
//        var userCapabilities: List<UserCapability>
): Parcelable

//@Parcelize
//@JsonClass(generateAdapter = true)
//data class UserCapability(
//        @Json(name = "_id")
//        var id: String? = "",
//        var specialityId: String? = "",
//        var userId: Long = 0,
//        var main: Boolean = false,
//        var name: String = ""
//): Parcelable