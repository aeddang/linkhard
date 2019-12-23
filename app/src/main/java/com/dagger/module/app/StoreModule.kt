package com.dagger.module.app


import android.content.Context
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


}