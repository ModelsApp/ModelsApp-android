package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserInvoice(
        var address: String = "",
        var city: String = "",
        var companyName: String = "",
        var country: String = "",
        var state: String = "",
        var vatNumber: String = "",
        var zip: String = ""
): Parcelable