package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class SignUpData(

        //TODO these two will be added later?
        @Transient
        @JsonIgnore
        var name: String = "",
        @Transient
        @JsonIgnore
        var surname: String = "",

        var nationality: String = "",
        @Json(name = "birthdate")
        var birthDate: String = "",
        var gender: String = "",
        var phone: String = "",
        var email: String = "",
        var password: String = "",

        @Json(name = "refferal_code")
        var referral: String = "",
        @Json(name = "terms_of_use")
        var termsOfUse: Boolean = true,
        var profession: String = "",
        @Json(name = "main_specialty")
        var mainSpeciality: String = "",
        @Json(name = "addition_specialities")
        var additionalSpecialities: List<String> = listOf(),
        var capabilities: List<String> = listOf(),
        var preferences: List<String> = listOf(),
        var representation: String = "",
        @Json(name = "vat_number")
        var vatNumber: String = "",
        @Json(name = "instagram_account")
        var instagramName: String = "",
        var socials: List<String> = listOf(),

        @Transient
        @JsonIgnore
        var image: ByteArray? = null,

        @Transient
        @JsonIgnore
        var fbToken: String? = null

): Parcelable
