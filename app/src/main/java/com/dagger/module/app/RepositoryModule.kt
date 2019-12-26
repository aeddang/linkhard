package com.dagger.module.app

import android.content.Context
import com.ironleft.linkhard.store.FileUploadManager
import com.ironleft.linkhard.store.Repository
import com.ironleft.linkhard.store.ServerDatabaseManager
import com.ironleft.linkhard.store.SettingPreference
import com.skeleton.module.network.NetworkFactory

import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(@Named("appContext") ctx: Context,
                          setting:SettingPreference,
                          networkFactory: NetworkFactory,
                          serverDatabaseManager: ServerDatabaseManager,
                          fileUploadManager: FileUploadManager


    ): Repository = Repository(ctx, setting, networkFactory, serverDatabaseManager, fileUploadManager)
}