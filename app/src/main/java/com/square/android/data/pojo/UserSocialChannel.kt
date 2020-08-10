package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserSocialChannel(
        @Json(name = "_id")
        var id: String = "",
        var name: String = "",
        @Json(name = "accountname")
        var accountName: String = "",

        //TODO why not just leave account name in UserSocialChannel?
        @Json(name = "userChanel")
        var userChannel: UserChannel? = null
): Parcelable

//TODO is this additional model really useful?
@Parcelize
@JsonClass(generateAdapter = true)
data class UserChannel(
        @Json(name = "_id")
        var id: String = "",
        var name: String = "",
        @Json(name = "accountname")
        var accountName: String = ""
): Parcelable