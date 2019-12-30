package com.ironleft.linkhard.store


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.ironleft.linkhard.PageID
import com.ironleft.linkhard.R
import com.lib.page.PagePresenter
import com.lib.util.getFileName

import com.skeleton.view.alert.CustomToast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit




class FileUploadManager(val context:Context){
    enum class EventType{
        FileSelected, Completed, Progress, Pause,Resume, Error, Cancel
    }
    enum class Status{
        Progress, NoneProgress
    }
    enum class FileStatus{
        Progress, Pause, Error
    }

    data class Event(val type:EventType, val data:Data)
    data class Data(val serverID:String, val file:Uri){
        var fileStatus:FileStatus = FileStatus.Progress ;internal set
        var progress:Float = 0.0f  ;internal set
        var disposable:Disposable? = null ;internal set
    }


    private val appTag = javaClass.simpleName
    private val REQUEST_CODE = 10

    val statusObservable = PublishSubject.create<Status>()
    val uploadObservable = PublishSubject.create<Event>()
    val datas = ArrayList<Data>()
    var status:Status = Status.NoneProgress ;private set
    private fun syncStatus(){
        val willStatus = if(datas.isEmpty()) Status.NoneProgress else Status.Progress
        if(willStatus != status){
            status = willStatus
            statusObservable.onNext(status)
        }
    }

    private var serverID:String = ""
    fun openFileFinder(server:String, mimeType:String = "*/*", title:String = context.getString(R.string.title_file_select)) {
        serverID = server
        val intent: Intent = Intent()
            .setType(mimeType)
            .setAction(Intent.ACTION_GET_CONTENT)

        PagePresenter.getInstance<PageID>().activity?.getCurrentActivity()?.startActivityForResult(
            Intent.createChooser(intent, title), REQUEST_CODE )
    }

    fun onDestroyed(){
        cancelAllUpload()
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {

        if (requestCode == REQUEST_CODE  && resultCode == Activity.RESULT_OK) {
            val selectedfile = data.data //The uri with the location of the file
            selectedfile?.let {
                val fileData = Data(serverID, it)
                uploadObservable.onNext(Event(EventType.FileSelected, fileData))
                datas.add(fileData)
                syncStatus()
                uploadFile(fileData)
            }
        }
    }

    fun cancelAllUpload(){
        datas.forEach { cancelUpload(it) }
    }

    fun pauseAllUpload(){
        datas.forEach { pauseUpload(it) }
    }

    fun cancelUpload(data:Data){
        data.disposable?.dispose()
        data.disposable = null
        datas.remove(data)
        uploadObservable.onNext(Event(EventType.Cancel, data))
        syncStatus()
    }

    fun pauseUpload(data:Data){
        data.disposable?.dispose()
        data.disposable = null
        data.fileStatus = FileStatus.Pause
        uploadObservable.onNext(Event(EventType.Pause, data))
    }

    fun resumeUpload(data:Data){
        data.disposable?.dispose()
        data.disposable = null
        uploadFile(data)
    }

    private fun uploadFile(data:Data){
        val fileName =data.file.getFileName(context) ?: "noname"
        uploadObservable.onNext(Event(EventType.Resume, data))
        val initMsg = "$fileName ${context.getString(R.string.notice_upload_init)}"
        CustomToast.makeToast(context, initMsg, Toast.LENGTH_SHORT).show()
        data.disposable = Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(20)
            .observeOn(AndroidSchedulers.mainThread()).subscribe (
                {
                    data.progress = it/20.0f
                    uploadObservable.onNext(Event(EventType.Progress, data))
                },
                {
                    data.fileStatus = FileStatus.Error
                    uploadObservable.onNext(Event(EventType.Error, data))
                    val msg = "$fileName ${context.getString(R.string.notice_upload_error)}"
                    CustomToast.makeToast(context, msg, Toast.LENGTH_SHORT).show()
                },
                {
                    datas.remove(data)
                    uploadObservable.onNext(Event(EventType.Completed, data))
                    syncStatus()
                    val msg = "$fileName ${context.getString(R.string.notice_uploaded)}"
                    CustomToast.makeToast(context, msg, Toast.LENGTH_SHORT).show()
                }
            )
    }

}