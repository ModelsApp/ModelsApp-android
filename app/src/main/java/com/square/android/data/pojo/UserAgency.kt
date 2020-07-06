package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserAgency(
        var id: String = "",
        var name: String = "",
        var address: String = "",
        var city: String = "",
        var country: String = "",
        var main: Boolean = false
): Parcelable