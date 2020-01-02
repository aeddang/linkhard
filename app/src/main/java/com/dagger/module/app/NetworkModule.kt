package com.dagger.module.app

import android.app.Application
import com.lib.util.Log
import com.skeleton.module.network.NetworkFactory
import com.skeleton.module.network.Rx2ErrorHandlingCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
open class NetworkModule {
    @Provides
    @Singleton
    fun provideNetworkFactory(application: Application): NetworkFactory {
        return NetworkFactory(application)
    }


}