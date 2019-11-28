package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileInfo(var name: String = "",
                       var surname: String = "",
                       var birthDate: String = "",
                       var gender: String = "",
                       var nationality: String = "",
                       var email: String? = null,
                       var instagramName: String = "",
                       var phone: String = "",
                       var motherAgency: String = "",
                       @Transient
                       @JsonIgnore
                       var agency: String? = null,
                       @Transient
                       @JsonIgnore
                       var city: String? = null,
                       @Transient
                       @JsonIgnore
                       var agency2: String? = null,
                       @Transient
                       @JsonIgnore
                       var city2: String? = null,
                       @Transient
                       @JsonIgnore
                       var agency3: String? = null,
                       @Transient
                       @JsonIgnore
                       var city3: String? = null,
                       @Transient
                       @JsonIgnore
                       var referral: String? = null,

//                  @JsonIgnore
//                  var imagesUri: List<Uri>? = null,
                       @Transient
                       @JsonIgnore
                       var images: List<ByteArray>? = null,
                       @Transient
                       @JsonIgnore
                       var displayBirthday: String = "",
                       @Transient
                       @JsonIgnore
                       var phoneN: String = "",
                       @Transient
                       @JsonIgnore
                       var phoneC: String = "",
                       @Transient
                       @JsonIgnore
                       var flagCode: Int = -1
) : Parcelable