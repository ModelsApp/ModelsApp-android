package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserSkill(
        var type: String = "",
        var exists: Boolean = false,
        var level: Int = 1
): Parcelable