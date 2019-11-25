package com.square.android

import android.app.Application
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.mapbox.mapboxsdk.Mapbox
import com.square.android.di.dataModule
import com.square.android.di.interactorsModule
import com.square.android.di.navigationModule
import com.square.android.di.networkModule
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.square.android.utils.AppLifecycleTracker
import com.squareup.picasso.OkHttp3Downloader
import org.greenrobot.eventbus.EventBus
import com.squareup.picasso.Picasso
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class App : Application() {

    lateinit var mixpanel: MixpanelAPI

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        startKoin(this, listOf(navigationModule, networkModule, dataModule, interactorsModule))

        Mapbox.getInstance(this, BuildConfig.MAPBOX_TOKEN)
        Fabric.with(this, Crashlytics())
        mixpanel = MixpanelAPI.getInstance(this, Network.MIXPANEL_TOKEN)
        registerActivityLifecycleCallbacks((AppLifecycleTracker(EventBus.getDefault())))

        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
        val built = builder.build()
        Picasso.setSingletonInstance(built)

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.font_opensans_regular))
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

    }

    companion object {
        lateinit var INSTANCE: App

        fun getString(@StringRes stringRes: Int): String {
            return INSTANCE.getString(stringRes)
        }

        fun getColor(@ColorRes colorRes: Int): Int {
            return ContextCompat.getColor(INSTANCE, colorRes)
        }
    }
}