package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ProfessionsResult(
        var professions: List<Profession>,
        var userProfessions: List<UserProfession>
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UserProfession(
        @Json(name = "_id")
        var id: String? = "",
        var professionId: String? = "",
        var userId: Long = 0,
        var main: Boolean = false,
        var name: String = ""
): Parcelable