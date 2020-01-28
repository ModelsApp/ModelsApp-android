package com.square.android

import com.google.android.gms.wallet.WalletConstants
import com.square.android.GOOGLEBILLING.PACKAGE_NAME

object SCREENS {
    const val MAIN = "MAIN"
    const val START = "START"

    const val INTRO = "INTRO"
    const val AUTH = "AUTH"
    const val LOGIN = "LOGIN"
    const val RESET_PASSWORD = "RESET_PASSWORD"
    const val SIGN_UP = "SIGN_UP"

    const val REDEMPTIONS = "REDEMPTIONS"
    const val PLACES = "PLACES"
    const val PROFILE = "PROFILE"

    const val PLACE= "PLACE"

    const val EDIT_PROFILE = "EDIT_PROFILE"
    const val GALLERY = "GALLERY"

    const val SELECT_OFFER = "SELECT_OFFER"
    const val REVIEW = "REVIEW"
    const val OFFERS_LIST = "OFFERS_LIST"

    const val CLAIMED_REDEMPTION = "CLAIMED_REDEMPTION"

    const val NO_CONNECTION = "NO_CONNECTION"

    const val SUBSCRIPTION_ERROR = "SUBSCRIPTIONS_ERROR"

    const val TUTORIAL_VIDEOS = "TUTORIAL_VIDEOS"

    const val CAMPAIGNS = "CAMPAIGNS"

    const val CAMPAIGN_DETAILS = "CAMPAIGN_DETAILS"
    const val CAMPAIGN_FINISHED = "CAMPAIGN_FINISHED"

    const val NOT_APPROVED = "NOT_APPROVED"
    const val WINNER = "WINNER"
    const val UPLOAD_PICS = "UPLOAD_PICS"
    const val ADD_PHOTO = "ADD_PHOTO"
    const val APPROVAL = "APPROVAL"

    const val PICK_UP_SPOT = "PICK_UP_SPOT"
    const val PICK_UP_INTERVAL = "PICK_UP_INTERVAL"
    const val PICK_UP_LOCATION = "PICK_UP_LOCATION"

    const val PASS_ELIGIBLE = "PASS_ELIGIBLE"

    const val ACTIVE_PLAN = "ACTIVE_PLAN"
    const val EARN_MORE_CREDITS = "EARN_MORE_CREDITS"

    const val SCAN_QR = "SCAN_QR"

    const val EVENT = "EVENT"

    const val EVENT_DETAILS = "EVENT_DETAILS"
    const val EVENT_PLACE = "EVENT_PLACE"

}

object Network {
    const val BASE_API_URL = "https://square-app-staging-api.herokuapp.com/api/"
    const val MIXPANEL_TOKEN = "2529780c1354ad1945e06330161ac446"


    const val GOOGLE_BILLING_API_URL = "https://www.googleapis.com/androidpublisher/v3/applications/$PACKAGE_NAME/"

    const val OAUTH_API_URL = "https://accounts.google.com/o/oauth2/"

    const val OAUTH_CLIENT_ID = "221995887985-g17rh0796sh4g1nvt7p8oinok5l83tri.apps.googleusercontent.com"



    const val GOOGLE_PLACES_KEY = "AIzaSyAfoqnBahxyDd-5la1JpPCS2qKBicKg7L0"
}

object SOCIAL {
    const val SOCIAL_LINK_FORMAT = "https://www.instagram.com/%s"
}

object GOOGLEPAY {

    //change to ENVIRONMENT_PRODUCTION later
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


object GOOGLEBILLING{

    const val PACKAGE_NAME = "com.squaremm.android"

    const val SUBSCRIPTION_PER_WEEK_NAME = "square_weekly_premium"

    const val SUBSCRIPTION_PER_MONTH_NAME = "square_monthly_premium"

    const val SUBSCRIPTION_PER_WEEK_PREMIUM_NAME = "square_weekly_premium_extra"

    const val SUBSCRIPTION_PER_MONTH_PREMIUM_NAME = "square_monthly_premium_extra"

    const val GOOGLE_CODE = "4/iQHOcHcwK4LEy01sMsqFyvoK_dObDw6qiE5JP-U8INPkGmYyTe_3tQXpryRgcOW3iZrn8xoAdkmApwL6KtGYc-I"

    const val CLIENT_SECRET = "qqJusPDEfqaXS4IomodNhZfC"

    const val REFRESH_TOKEN = "1/bHjM8CeWmNwTT2RHqfw_GzroIWiqbbtIPQFfo9nBBQhPthdFqOsxyktJ0cbxLbaZ"

    const val APP_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh8idDiOcP/cvl2nhlFjURSkwbflO24NGfXepXjIvijo2XHeJDx8tSqwqdH6aOrpxNo5y7lG1Jj22vpg0VOPfEJ/ETQOUE5jCRpTrIc4tGaWuiTjbSdXEtA0oYnn9YHudAcxtjOn02hvQIkTyPmmKx/XGK9mG2D4+XTo8EV/sLJesxCI/VY9cdg/IzPJ+S+otEHQY5r3a2/exjH/uBd/nXCX0JUSRtKcPTFrPIefZ3XpYZztEPUx62sY+FWYbyv1HbXcgofYhNyGD43/TTqXu3Ke49Xbq1mK9Xl3nJicQPFIh+hkx8KC44FuxQM+y2iY8Xu+TX+kKT5GtLfrZ4NLJfwIDAQAB"

}


