package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserSocialChannel(
        var id: String = "",
        var name: String = "",
        @Json(name = "accountname")
        var accountName: String = ""
): Parcelable