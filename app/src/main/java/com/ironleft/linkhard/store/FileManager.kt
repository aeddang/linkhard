package com.ironleft.linkhard.store
import android.content.Context
import android.net.Uri
import androidx.annotation.CallSuper
import com.lib.util.Log
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.*
import kotlin.collections.ArrayList

enum class FileEventType{
    Excute, Completed, Progress, Error, Cancel , Resume
}
enum class FileManagerStatus{
    Progress, NoneProgress, Empty
}
enum class FileStatus{
    Progress, Error, Completed, Cancel, Resume
}

data class FileEvent(val type:FileEventType, val data:FileData)
data class FileData(val serverID:String, val fileID:String = UUID.randomUUID().toString()){
    var fileUri: Uri? = null ;internal set
    var filePath: String? = null ;internal set
    var fileName: String? = null ;internal set

    var progress:Float = 0.0f ;internal set
    var disposable: Disposable? = null ;internal set
    var downLoadID: Long = 0L ;internal set

    var fileStatus:FileStatus = FileStatus.Progress
        internal set(value){
            if(value != FileStatus.Progress && field == value) return
            field = value
            statusObservable.onNext(value)
            if(value != FileStatus.Resume && value != FileStatus.Progress) downLoadID = 0L
        }

    val statusObservable = PublishSubject.create<FileStatus>()
}

abstract class FileManager(val context: Context){
    private val appTag = javaClass.simpleName
    val statusObservable = PublishSubject.create<FileManagerStatus>()
    val fileObservable = PublishSubject.create<FileEvent>()
    val datasObservable = PublishSubject.create<Int>()
    val datas = ArrayList<FileData>()
    var status:FileManagerStatus = FileManagerStatus.Empty ;private set

    var server:ServerDatabaseManager.Row? = null
    var serverPath:String? = null

    protected fun syncStatus(){
        val willStatus = if(datas.isEmpty()) FileManagerStatus.Empty
        else{
            if(datas.find { it.fileStatus == FileStatus.Progress || it.fileStatus == FileStatus.Resume } == null) FileManagerStatus.NoneProgress else FileManagerStatus.Progress
        }
        if(willStatus != status){
            status = willStatus
            statusObservable.onNext(status)
        }
    }

    abstract fun onDestroyed()

    fun cancelAll(){
        datas.forEach { cancel(it, false) }
        syncStatus()
    }
    fun resumeAll(){
        datas.forEach { resume(it) }
    }

    fun removeAll(){
        datas.forEach { cancel(it, false) }
        datas.clear()
        datasObservable.onNext(datas.size)
        syncStatus()
    }

    fun remove(data:FileData){
        cancel(data, false)
        datas.remove(data)
        datasObservable.onNext(datas.size)
        syncStatus()
    }

    fun cancel(data:FileData,isSync:Boolean = true){
        data.disposable?.dispose()
        data.disposable = null
        if(data.fileStatus != FileStatus.Progress) return
        Log.d(appTag, "cancel ${data.filePath}")
        data.fileStatus = FileStatus.Cancel
        onCancel(data)
        fileObservable.onNext(FileEvent(FileEventType.Cancel, data))
        if(isSync) syncStatus()
    }

    fun resume(data:FileData){
        if(data.fileStatus == FileStatus.Completed) return
        if(data.fileStatus == FileStatus.Resume) return
        Log.d(appTag, "resume ${data.filePath}")
        data.fileStatus = FileStatus.Resume
        onResume(data)
        fileObservable.onNext(FileEvent(FileEventType.Resume, data))
        syncStatus()
    }

    fun excute(data:FileData){
        Log.d(appTag, "excute ${data.filePath}")
        datas.add(data)
        data.fileStatus = FileStatus.Resume
        onExcute(data)
        fileObservable.onNext(FileEvent(FileEventType.Excute, data))
        syncStatus()
        datasObservable.onNext(datas.size)
    }

    protected  fun onProgress(data: FileData){
        data.fileStatus = FileStatus.Progress
        fileObservable.onNext(FileEvent(FileEventType.Progress, data))
    }

    @CallSuper
    open protected fun onError(data: FileData){
        data.disposable?.dispose()
        data.disposable = null
        data.fileStatus = FileStatus.Error
        fileObservable.onNext(FileEvent(FileEventType.Error, data))
        syncStatus()
    }
    @CallSuper
    open protected fun onComplete(data: FileData){
        data.disposable?.dispose()
        data.disposable = null
        data.fileStatus = FileStatus.Completed
        fileObservable.onNext(FileEvent(FileEventType.Completed, data))
        syncStatus()
    }

    open protected fun onCancel(data: FileData){}
    open protected fun onResume(data: FileData){ onExcute(data) }
    abstract fun onExcute(data: FileData)
}