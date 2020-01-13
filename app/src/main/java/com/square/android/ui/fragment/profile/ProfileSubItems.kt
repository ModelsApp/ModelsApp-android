package com.square.android.ui.fragment.profile

import androidx.annotation.DrawableRes

const val SOCIAL_APP_TYPE_INSTAGRAM = 1
const val SOCIAL_APP_TYPE_FACEBOOK = 2
const val SOCIAL_APP_TYPE_GOOGLE = 3
const val SOCIAL_APP_TYPE_TRIPADVISOR = 4
const val SOCIAL_APP_TYPE_YELP = 5

const val EARN_TYPE_SHARE_FRIENDS = 1
const val EARN_TYPE_REFER_VENUE = 2
const val EARN_TYPE_INTRODUCE_BRAND = 3

const val BUY_TYPE_500 = 1
const val BUY_TYPE_1000 = 2

const val AMBASSADOR_TYPE_JOIN_TEAM = 1

const val DETAIL_TYPE_DOUBLE = 1
const val DETAIL_TYPE_FULL = 2

const val CREATE_CLICKED_TYPE_POLAROID = 1
const val CREATE_CLICKED_TYPE_PORTFOLIO = 2
const val CREATE_CLICKED_TYPE_COMP_CARD = 3
const val CREATE_CLICKED_TYPE_ACCOUNT = 4

const val PREFERENCE_TYPE_SOCIAL = 1
const val PREFERENCE_TYPE_HOSTESS = 2
const val PREFERENCE_TYPE_NIGHT_OUT = 3

const val BANK_ACCOUNT_TYPE_VISA = 1
const val BANK_ACCOUNT_TYPE_MASTER_CARD = 2

class ProfileSubItems {

// SOCIAL //
        class Plan(
                var text: String = "",
                var canChange: Boolean = true)


        class Social(
                var type: Int = 0, // one of SOCIAL_APP_TYPE...
                var connected: Boolean = true)

        class YourActivity()

        class Specialities()

        class Capabilities()

// TODO not used?
        class BuyCredits(
                var type: Int = 0, // one of BUY_TYPE...
                var credits: Int = 0,
                var priceText: String = "")
        class Ambassador(
                var type: Int = 0, // one of AMBASSADOR_TYPE...
                @DrawableRes
                var smallIconRes: Int)

// BUSINESS //
        class PersonalInfo()

        class MyJobs()

        class MyInterests()

        class BusinessSocialChannels()

        class Agency(
                var title: String = "",
                var agencyId: Long)

        class AppearanceCharacteristics(
                var type: Int = 0, // one of DETAIL_TYPE...
                var firstTitle: String = "",
                var firstText: String = "",
                var secondTitle: String = "",
                var secondText: String = "")

        class EarnCredits(
                var type: Int = 0, // one of EARN_TYPE...
                var credits: Int = 0)

// TODO not used?
        class Polaroid(
                var title: String = "",
                var expired: Boolean = false,
                var albumId: Long)
        class Create(
                var clickedType: Int = 0 // one of CREATE_CLICKED_TYPE...
                 )
        class Portfolio(
                var title: String = "",
                var portfolioId: Long)
        class CompCard(
                var title: String = "",
                var compCardId: Long)
        class Preference(
                var type: Int = 0, // one of PREFERENCE_TYPE...
                var checked: Boolean)
        class ModelsCom(
                var modelsComUserName: String = "")

// WALLET //
        class BankAccount(
                // if type: display - profile_subitem_account, edit - profile_subitem_account_edit
                var type: Int = 0, // one of BANK_ACCOUNT_TYPE...
                var number: String = "",
                var isPrimary: Boolean)

        class PaypalAccount()

}


