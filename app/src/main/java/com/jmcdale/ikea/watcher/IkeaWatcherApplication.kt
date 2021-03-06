package com.jmcdale.ikea.watcher

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jmcdale.ikea.watcher.remote.IkeaWatcherClient
import com.jmcdale.ikea.watcher.remote.IkeaWatcherClientImpl
import com.jmcdale.ikea.watcher.remote.IkeaWatcherService
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class IkeaWatcherApplication : Application() {

    lateinit var client: IkeaWatcherClient

    override fun onCreate() {
        super.onCreate()
        IkeaWatcherApplication.instance = this

        buildClient()
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

        val moshi = Moshi.Builder().build()
        val retrofit = Retrofit.Builder()
            .client(okhttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val service = retrofit.create(IkeaWatcherService::class.java)
        client = IkeaWatcherClientImpl(service)
    }

    companion object {
        var instance: IkeaWatcherApplication? = null

        private val BASE_URL = "https://iows.ikea.com/"
    }
}
