package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class SpecialitiesResult(
        var list: List<Speciality>,
        var userSpecialities: List<UserSpeciality>
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UserSpeciality(
        @Json(name = "_id")
        var id: String? = "",
        var specialityId: String? = "",
        var userId: Long = 0,
        var main: Boolean = false,
        var name: String = ""
): Parcelable