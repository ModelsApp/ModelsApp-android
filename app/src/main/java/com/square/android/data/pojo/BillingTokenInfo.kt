package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class BillingTokenInfo(
        @Json(name="id")
        var subscriptionId: String? = null,
        var token: String? = null,

        @Transient
        @JsonIgnore
        var subscriptionType: Int = 0,

        @Transient
        @JsonIgnore
        var planType: Int = 0

): Parcelable
