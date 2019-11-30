package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
class RedemptionFull(
        @Json(name="place")
        var redemption: Redemption = Redemption()
): Parcelable {
    @JsonClass(generateAdapter = true)
    @Parcelize
    class Redemption(
            @Json(name="_id")
            var id: Long = 0,
            var claimed: Boolean = false,
            var closed: Boolean = false,
            var creationDate: String? = "",
            var date: String = "",
            var endTime: String = "",
            var place: PlaceInfo = PlaceInfo(),
            var startTime: String = "",
            var user: Int = 0
    ): Parcelable
}