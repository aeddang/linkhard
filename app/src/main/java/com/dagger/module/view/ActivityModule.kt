package com.dagger.module.view

import android.app.Activity
import com.dagger.ActivityScope
import com.ironleft.linkhard.store.FileUploadManager
import com.ironleft.linkhard.store.Repository
import com.skeleton.module.ViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {
    @Provides
    @ActivityScope
    fun provideViewModelFactory(repository: Repository): ViewModelFactory = ViewModelFactory(repository)


}
