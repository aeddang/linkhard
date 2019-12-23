package com.dagger


import com.dagger.module.view.ActivityModule
import com.dagger.module.view.MainActivityModule
import com.dagger.module.view.PageModule
import com.ironleft.linkhard.MainActivity
import com.ironleft.linkhard.page.PageDir
import com.ironleft.linkhard.page.PageIntro
import com.ironleft.linkhard.page.PageSetupServer
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class AndroidBindingModule {


    /**
     * Main Activity
     */

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class, ActivityModule::class])
    internal abstract fun bindMainActivity(): MainActivity

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPageIntro(): PageIntro

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPageSetupServer(): PageSetupServer

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPageDir(): PageDir
}
