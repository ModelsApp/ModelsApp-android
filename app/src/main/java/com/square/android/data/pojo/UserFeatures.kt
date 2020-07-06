package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserFeatures(
        var bodyType: String = "",
        var eyesColor: String = "",
        var hairColor: String = ""
): Parcelable