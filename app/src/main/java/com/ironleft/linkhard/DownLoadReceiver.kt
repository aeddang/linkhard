package com.ironleft.linkhard

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lib.page.PagePresenter
import com.lib.util.Log

class DownLoadReceiver : BroadcastReceiver() {
    private val appTag =  javaClass.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {

        val id = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L)
        Log.d(appTag, "onReceived$id")
        PagePresenter.currentInstance ?: return
        val mainAc = PagePresenter.getInstance<Any>().activity as? MainActivity
        mainAc?.fileDownloadManager?.onReceived(id)
    }
}
