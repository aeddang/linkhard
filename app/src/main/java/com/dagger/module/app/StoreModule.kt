package com.dagger.module.app


import android.app.Activity
import android.content.Context
import com.ironleft.linkhard.store.FileUploadManager
import com.ironleft.linkhard.store.ServerDatabaseManager
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton


@Module
class StoreModule {

    @Provides
    @Singleton
    fun provideServerDatabaseManager(@Named("appContext") ctx: Context): ServerDatabaseManager = ServerDatabaseManager(ctx)

    @Provides
    @Singleton
    fun provideFileUploadManager(activity: Activity): FileUploadManager = FileUploadManager(activity)
}