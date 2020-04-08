package com.square.android.data.pojo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendPhoneCodeData(
        val phone: String
)