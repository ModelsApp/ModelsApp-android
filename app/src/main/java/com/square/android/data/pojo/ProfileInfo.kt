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
                       var instagramName: String = "",
                       var phone: String = "",
                       var referral: String? = null,

                       //TODO:F will be deleted
                       var motherAgency: String = "",

                       @Transient
                       @JsonIgnore
                       var image: ByteArray? = null,

                       @Transient
                       @JsonIgnore
                       var email: String? = null,
                       @Transient
                       @JsonIgnore
                       var password: String? = null,
                       @Transient
                       @JsonIgnore
                       var fbToken: String? = null


//                       @Transient
//                       @JsonIgnore
//                       var agency: String? = null,
//                       @Transient
//                       @JsonIgnore
//                       var city: String? = null,
//                       @Transient
//                       @JsonIgnore
//                       var agency2: String? = null,
//                       @Transient
//                       @JsonIgnore
//                       var city2: String? = null,
//                       @Transient
//                       @JsonIgnore
//                       var agency3: String? = null,
//                       @Transient
//                       @JsonIgnore
//                       var city3: String? = null,
//                       @JsonIgnore
//                       var imagesUri: List<Uri>? = null,
//                       @Transient
//                       @JsonIgnore
//                       var images: List<ByteArray>? = null,
//                       @Transient
//                       @JsonIgnore
//                       var displayBirthday: String = "",
//                       @Transient
//                       @JsonIgnore
//                       var phoneN: String = "",
//                       @Transient
//                       @JsonIgnore
//                       var phoneC: String = "",
//                       @Transient
//                       @JsonIgnore
//                       var flagCode: Int = -1
) : Parcelable