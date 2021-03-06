package com.jmcdale.ikea.watcher

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jmcdale.ikea.watcher.local.LocalDateAdapter
import com.jmcdale.ikea.watcher.local.LocalStorage
import com.jmcdale.ikea.watcher.local.LocalStorageTest
import com.jmcdale.ikea.watcher.remote.IkeaWatcherClient
import com.jmcdale.ikea.watcher.remote.IkeaWatcherClientImpl
import com.jmcdale.ikea.watcher.remote.IkeaWatcherService
import com.squareup.moshi.Moshi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber


class IkeaWatcherApplication : Application() {

    lateinit var client: IkeaWatcherClient
    lateinit var localStorage: LocalStorage
    lateinit var moshi: Moshi

    override fun onCreate() {
        super.onCreate()
        IkeaWatcherApplication.instance = this

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        buildMoshi()
        buildClient()
        buildLocalStorage()
    }

    // resource: https://github.com/Ephigenia/ikea-availability-checker/blob/master/source/lib/iows2.js
    private fun buildClient() {
        val okhttpBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            okhttpBuilder.addInterceptor(
                ChuckerInterceptor.Builder(this)
                    .collector(ChuckerCollector(this))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build()
            )
        }
        val okhttpClient = okhttpBuilder.build()

        val retrofit = Retrofit.Builder()
            .client(okhttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val service = retrofit.create(IkeaWatcherService::class.java)
        client = IkeaWatcherClientImpl(service)
    }

    private fun buildLocalStorage() {
        localStorage = LocalStorage(this, moshi)
    }

    private fun buildMoshi() {
        moshi = Moshi.Builder()
            .add(LocalDateAdapter)
            .build()
    }

    companion object {
        var instance: IkeaWatcherApplication? = null

        private val BASE_URL = "https://iows.ikea.com/"
    }
}
