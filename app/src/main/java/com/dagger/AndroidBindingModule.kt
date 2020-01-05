package com.dagger


import com.dagger.module.view.ActivityModule
import com.dagger.module.view.MainActivityModule
import com.dagger.module.view.PageModule
import com.ironleft.linkhard.MainActivity
import com.ironleft.linkhard.page.*
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
    internal abstract fun bindPageSetupInit(): PageSetupInit

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPageDir(): PageDir

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPageSetupServer(): PageSetupServer

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPopupDownLoad(): PopupDownLoad

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPopupUpLoad(): PopupUpLoad

    @PageScope
    @ContributesAndroidInjector(modules = [PageModule::class])
    internal abstract fun bindPopupWebView(): PopupWebView
}
