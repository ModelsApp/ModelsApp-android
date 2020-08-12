package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserSocialChannelData(
        @Json(name = "chanel")
        var channel: ModelSocialChannel
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ModelSocialChannel(
        @Json(name = "_id")
        var id: String = "",
        var name: String = "",

        @Json(name = "accountname")
        var accountName: String = "",

        var userId: Long = 0
): Parcelable
