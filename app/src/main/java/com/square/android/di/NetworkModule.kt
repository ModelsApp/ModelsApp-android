package com.square.android.di

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.square.android.Network.BASE_API_URL
import com.square.android.Network.GOOGLE_BILLING_API_URL
import com.square.android.data.network.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

private const val MAX_TIMEOUT = 15L

class PurchasesUpdatedEvent(val data: MutableList<Purchase>?)

val networkModule = module {

//    single { OauthTokenInterceptor(manager = get()) }
    single { TokenAuthenticator(manager = get()) }
    single(name = "billing_okhttp") { createClientBilling(tokenAuthenticator = get()) }
    single(name = "billing_retrofit") { createRetrofit(get(name = "billing_okhttp"), GOOGLE_BILLING_API_URL) }
    single(name = "billing_api") { get<Retrofit>(name = "billing_retrofit").create(BillingApiService::class.java) }

    single { AuthInterceptor(manager = get()) }
    single(name = "base_okhttp") { createClientBase(interceptor = get()) }
    single(name = "base_retrofit") { createRetrofit(get(name = "base_okhttp"), BASE_API_URL) }
    single(name = "base_api") { get<Retrofit>(name = "base_retrofit").create(ApiService::class.java) }

    single { createBillingClient(context = get(), eventBus = get()) }
}

private fun createClientBilling(tokenAuthenticator: TokenAuthenticator) = OkHttpClient.Builder()
        .authenticator(tokenAuthenticator)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .connectTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)
        .build()

private fun createClientBase(interceptor: AuthInterceptor) = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .connectTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)
        .build()

private fun createMoshi() = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .add(IgnoreStringForArrays())
        .add(IgnoreObjectIfIncorrect())
        .add(KotlinJsonAdapterFactory())
        .build()

private fun createRetrofit(okHttp: OkHttpClient, baseUrl: String) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(createMoshi()).asLenient())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttp)
        .build()

private fun createBillingClient(context: Context, eventBus: EventBus) = BillingClient.newBuilder(context)
        .setListener(object: PurchasesUpdatedListener{
            override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: List<Purchase>?) {
                billingResult?.let {
                    if (it.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                        eventBus.post(PurchasesUpdatedEvent(data = purchases.toMutableList()))
                    } else if (it.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                        Log.d("BILLING","| onPurchasesUpdated | responseCode == BillingClient.BillingResponseCode.USER_CANCELED")
                    } else {
                        Log.d("BILLING","| onPurchasesUpdated | responseCode == other error code: ${it.responseCode}")

                        eventBus.post(PurchasesUpdatedEvent(data = null))
                    }
                } ?: run {
                    Log.d("BILLING","| onPurchasesUpdated | billingResult == null")
                    eventBus.post(PurchasesUpdatedEvent(data = null))
                }
            }
        })
        .enablePendingPurchases()
        .build()