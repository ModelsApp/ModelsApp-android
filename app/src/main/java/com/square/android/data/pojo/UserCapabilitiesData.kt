package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserCapabilitiesData(
        var capabilities: List<Capability>
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Capability(
        var name: String = ""
): Parcelable