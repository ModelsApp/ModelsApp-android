package com.square.android

import com.google.android.gms.wallet.WalletConstants

object SCREENS {
    const val MAIN = "MAIN"
    const val START = "START"

    const val INTRO = "INTRO"
    const val AUTH = "AUTH"

    const val FILL_PROFILE_FIRST = "FILL_PROFILE_FIRST"
    const val FILL_PROFILE_SECOND = "FILL_PROFILE_SECOND"
    const val FILL_PROFILE_THIRD = "FILL_PROFILE_THIRD"
    const val FILL_PROFILE_REFERRAL = "FILL_PROFILE_REFERRAL"

    const val REDEMPTIONS = "REDEMPTIONS"
    const val MAP = "MAP"
    const val PLACES = "PLACES"
    const val PROFILE = "PROFILE"

    const val PLACE_DETAIL = "PLACE_DETAIL"
    const val EDIT_PROFILE = "EDIT_PROFILE"
    const val GALLERY = "GALLERY"

    const val SELECT_OFFER = "SELECT_OFFER"
    const val REVIEW = "REVIEW"

    const val CLAIMED_REDEMPTION = "CLAIMED_REDEMPTION"

    const val NO_CONNECTION = "NO_CONNECTION"

    const val TUTORIAL_VIDEOS = "TUTORIAL_VIDEOS"

    const val CAMPAIGNS = "CAMPAIGNS"

    const val CAMPAIGN_DETAILS = "CAMPAIGN_DETAILS"
    const val CAMPAIGN_FINISHED = "CAMPAIGN_FINISHED"

    const val NOT_APPROVED = "NOT_APPROVED"
    const val UPLOAD_PICS = "UPLOAD_PICS"
    const val ADD_PHOTO = "ADD_PHOTO"
    const val APPROVAL = "APPROVAL"

    const val PICK_UP_SPOT = "PICK_UP_SPOT"
    const val PICK_UP_MAP = "PICK_UP_MAP"

    const val PASS_ELIGIBLE = "PASS_ELIGIBLE"
}

object Network {
    //TODO change dev to test later
    const val BASE_API_URL = "https://square-app-dev-api.herokuapp.com/api/"
    const val MIXPANEL_TOKEN = "2529780c1354ad1945e06330161ac446"

}

object SOCIAL {
    const val SOCIAL_LINK_FORMAT = "https://www.instagram.com/%s"
}

object GOOGLEPAY{

    //TODO change to ENVIRONMENT_PRODUCTION later
    const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST

    const val PRICE_PER_WEEK = 10.0f

    val SUPPORTED_NETWORKS = listOf(
            "AMEX",
            "DISCOVER",
            "JCB",
            "MASTERCARD",
            "VISA")

    val SUPPORTED_METHODS = listOf(
            "PAN_ONLY",
            "CRYPTOGRAM_3DS")

    const val CURRENCY_CODE = "EUR"

    const val MERCHAT_NAME = "example"

    val SHIPPING_SUPPORTED_COUNTRIES = listOf("US", "GB")


    /** GATEWAY tokenization */
    const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "example"
    const val GATEWAY_MERCHANT_ID = "exampleGatewayMerchantId"
    val PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS = mapOf(
            "gateway" to PAYMENT_GATEWAY_TOKENIZATION_NAME,
            "gatewayMerchantId" to GATEWAY_MERCHANT_ID
    )


    /** Only used for DIRECT tokenization */
    const val DIRECT_TOKENIZATION_PUBLIC_KEY = "REPLACE_ME"
    const val PROTOCOL_VERSION = "ECv1"
    val DIRECT_TOKENIZATION_PARAMETERS = mapOf(
            "protocolVersion" to PROTOCOL_VERSION,
            "publicKey" to DIRECT_TOKENIZATION_PUBLIC_KEY
    )


}