package com.ironleft.linkhard.store
import android.content.Context
import android.net.Uri
import androidx.annotation.CallSuper
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

enum class FileEventType{
    Excute, Completed, Progress, Pause,Resume, Error, Cancel
}
enum class FileManagerStatus{
    Progress, NoneProgress
}
enum class FileStatus{
    Progress, Pause, Error, Completed
}

data class FileEvent(val type:FileEventType, val data:FileData)
data class FileData(val serverID:String, val fileID:String = UUID.randomUUID().toString()){
    var fileUri: Uri? = null ;internal set
    var filePath: String? = null ;internal set
    var file:File? = null ;internal set
    var fileStatus:FileStatus = FileStatus.Progress ;internal set
    var progress:Float = 0.0f ;internal set
    var disposable: Disposable? = null ;internal set
}

abstract class FileManager(val context: Context){
    private val appTag = javaClass.simpleName
    val statusObservable = PublishSubject.create<FileManagerStatus>()
    val fileObservable = PublishSubject.create<FileEvent>()
    val datas = ArrayList<FileData>()
    var status:FileManagerStatus = FileManagerStatus.NoneProgress ;private set
    protected var serverID:String = ""

    protected fun syncStatus(){
        val willStatus = if(datas.isEmpty()) FileManagerStatus.NoneProgress else FileManagerStatus.Progress
        if(willStatus != status){
            status = willStatus
            statusObservable.onNext(status)
        }
    }

    @CallSuper
    fun onDestroyed(){
        cancelAll()
    }

    fun cancelAll(){
        datas.forEach { cancel(it) }
    }

    fun pauseAll(){
        datas.forEach { pause(it) }
    }

    fun cancel(data:FileData){
        data.disposable?.dispose()
        data.disposable = null
        datas.remove(data)
        fileObservable.onNext(FileEvent(FileEventType.Cancel, data))
        syncStatus()
    }

    fun pause(data:FileData){
        data.disposable?.dispose()
        data.disposable = null
        data.fileStatus = FileStatus.Pause
        fileObservable.onNext(FileEvent(FileEventType.Pause, data))
    }


    fun resume(data:FileData){
        data.disposable?.dispose()
        data.disposable = null
        onResume(data)
        onExcute(data)
    }

    fun excute(data:FileData){
        fileObservable.onNext(FileEvent(FileEventType.Excute, data))
        datas.add(data)
        syncStatus()
        onExcute(data)
    }
    open fun onResume(data: FileData){}
    abstract fun onExcute(data: FileData)
}