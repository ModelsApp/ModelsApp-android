package com.square.android.ui.fragment.profile

import androidx.annotation.DrawableRes

// SOCIAL //
class Plan(
        var text: String = "",
        var canChange: Boolean = true)

const val SOCIAL_APP_TYPE_INSTAGRAM = 1
const val SOCIAL_APP_TYPE_FACEBOOK = 2
const val SOCIAL_APP_TYPE_GOOGLE = 3
const val SOCIAL_APP_TYPE_TRIPADVISOR = 4
const val SOCIAL_APP_TYPE_YELP = 5
class Social(
        var type: Int = 0, // one of SOCIAL_APP_TYPE...
        var connected: Boolean = true)

const val EARN_TYPE_SHARE_FRIENDS = 1
const val EARN_TYPE_REFER_VENUE = 2
const val EARN_TYPE_INTRODUCE_BRAND = 3
class EarnCredits(
        var type: Int = 0, // one of EARN_TYPE...
        var credits: Int = 0)

const val BUY_TYPE_500 = 1
const val BUY_TYPE_1000 = 2
class BuyCredits(
        var type: Int = 0, // one of BUY_TYPE...
        var credits: Int = 0,
        var priceText: String = "")

const val AMBASSADOR_TYPE_JOIN_TEAM = 1
class Ambassador(
        var type: Int = 0, // one of AMBASSADOR_TYPE...
        @DrawableRes
        var smallIconRes: Int)


// BUSINESS //
const val DETAIL_TYPE_DOUBLE = 1
const val DETAIL_TYPE_FULL = 2
class Detail(
        var type: Int = 0, // one of DETAIL_TYPE...
        var firstTitle: String = "",
        var firstText: String = "",
        var secondTitle: String = "",
        var secondText: String = "")