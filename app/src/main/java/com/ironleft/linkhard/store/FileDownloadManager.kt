package com.ironleft.linkhard.store


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.ironleft.linkhard.PageID
import com.ironleft.linkhard.R
import com.lib.page.PagePresenter
import com.lib.util.Log
import com.lib.util.getFileName
import com.skeleton.module.network.DownloadEventBus
import com.skeleton.module.network.ProgressEvent

import com.skeleton.view.alert.CustomToast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.io.File
import java.util.concurrent.TimeUnit

class FileDownloadManager(ctx:Context): FileManager(ctx){


    private val appTag = javaClass.simpleName
    val downloadEventBus = DownloadEventBus()
    private val dirPath = ctx.getExternalFilesDir(null)?.absolutePath

    private val disposable:Disposable
    init {

        disposable = downloadEventBus.filteredObservable(ProgressEvent::class.java).subscribe {
            Log.i(appTag,"progress ${it.downloadIdentifier} : ${it.progress}")
        }
    }

    fun addDownLoad(path:String, name:String){
        val id = "$path$name".hashCode().toString()
        val data = FileData(serverID,id)
        data.filePath = path
        data.file = File("$dirPath$name")
        excute(data)

    }

    override fun onExcute(data: FileData) {

    }





}