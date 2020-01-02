package com.dagger.module.app


import android.app.Activity
import android.content.Context
import com.dagger.ActivityScope
import com.ironleft.linkhard.store.FileOpenController
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
    fun provideFileUploadManager(@Named("appContext") ctx: Context): FileUploadManager = FileUploadManager(ctx)

    @Provides
    @Singleton
    fun provideFileOpenController(@Named("appContext") ctx: Context): FileOpenController = FileOpenController(ctx)
}