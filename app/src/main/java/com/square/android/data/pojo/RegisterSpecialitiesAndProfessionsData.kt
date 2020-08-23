package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class RegisterSpecialitiesAndProfessionsData(
        @Json(name = "categories")
        var specialities: List<Speciality> = listOf(),
        @Json(name = "interests")
        var professions: List<Profession> = listOf()
): Parcelable



@Parcelize
@JsonClass(generateAdapter = true)
data class RegisterCapabilitiesData(
        var capabilities: List<RegisterCapability> = listOf()
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class RegisterCapability(
        @Json(name = "_id")
        var id: String = "",
        var name: String = ""
): Parcelable