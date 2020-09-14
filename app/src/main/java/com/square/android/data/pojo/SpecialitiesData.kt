package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class SpecialitiesData(
        var specialities: List<UserPostSpeciality>
): Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class UserPostSpeciality(
        var specialityId: String? = "",
        var main: Boolean = false,
        var name: String = ""
): Parcelable