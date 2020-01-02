package com.skeleton.module.network

import android.app.Application
import com.lib.util.Log
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit


class NetworkFactory (val app: Application){
    private val appTag = javaClass.simpleName

    private val CONNECT_TIMEOUT: Long = 15
    private val WRITE_TIMEOUT: Long = 15
    private val READ_TIMEOUT: Long = 15

    val cache = Cache( app.cacheDir,  10L * 1024L * 1024L )
    val callAdapterFactory = Rx2ErrorHandlingCallAdapterFactory.create()

    fun getLogger():HttpLoggingInterceptor{
        return HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { message ->
                var parseMessage = message
                Log.d(appTag, parseMessage)
                if (parseMessage.contains("END")) {
                    Log.d(appTag, "\n")
                    parseMessage += "\n"
                }
            })

    }

    fun getHttpClient(interceptors:List<Interceptor>? = null): OkHttpClient {
        val logger = getLogger()
        logger.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .cookieJar(JavaNetCookieJar(CookieManager(null, CookiePolicy.ACCEPT_ALL)))
            .addInterceptor(logger)
        interceptors?.let {lists->
            lists.forEach { builder.addInterceptor(it) }
        }
        return builder.build()
    }

    fun getRetrofit( address:String  ): Retrofit {
        return Retrofit.Builder()
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(address)
            .client(getHttpClient())
            .build()
    }

    fun getRetrofitDownloader( address:String  ): Retrofit {
        val eventBus = DownloadEventBus()
        val downloadInterceptor = DownloadProgressInterceptor(eventBus)
        val client = getHttpClient(listOf(downloadInterceptor))
        return Retrofit.Builder()
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(address)
            .client(client)
            .build()
    }
}

