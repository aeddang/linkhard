package com.ironleft.linkhard.page

import android.os.Bundle
import com.ironleft.linkhard.PageID
import com.ironleft.linkhard.PageParam
import com.ironleft.linkhard.R
import com.ironleft.linkhard.store.ServerDatabaseManager
import com.ironleft.linkhard.store.SettingPreference
import com.lib.page.PagePresenter
import com.skeleton.module.ViewModelFactory
import com.skeleton.rx.RxPageFragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class PageIntro  : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.page_intro

    @Inject
    lateinit var setting: SettingPreference
    @Inject
    lateinit var dbManager:ServerDatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreatedView() {
        super.onCreatedView()
        Observable.interval(1500, TimeUnit.MILLISECONDS)
            .take(1)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                val serverID = setting.getFinalServerID()
                if( serverID == -1){
                    moveSetup()
                }else{
                    val data =dbManager.getData(serverID)
                    if( data == null) moveSetup() else moveDir(data)
                }

            }.apply { disposables.add(this) }

    }


    private fun moveSetup(){
        val param = HashMap<String, Any?>()
        param[PageParam.USE_HEADER] = false
        PagePresenter.getInstance<PageID>().pageChange(PageID.SETUP_SERVER, param)
    }

    private fun moveDir(server:ServerDatabaseManager.Row){
        val param = HashMap<String, Any?>()
        param[PageParam.SERVER_DATA] = server
        PagePresenter.getInstance<PageID>().pageChange(PageID.DIR, param)
    }

    override fun onDestroy() {
        super.onDestroy()
    }



}