package com.ironleft.linkhard.store


import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.ironleft.linkhard.R
import com.lib.page.PagePresenter
import com.lib.page.PageRequestPermission
import com.lib.util.Log
import com.skeleton.view.alert.CustomToast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File
import java.util.concurrent.TimeUnit


class FileDownloadManager(ctx:Context): FileManager(ctx) {


    private val appTag = javaClass.simpleName
    val downloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    override fun onDestroyed() {}

    fun addDownLoad(path:String?, fileName:String?){
        path ?: return
        fileName ?: return
        val fullPath = "$path$fileName"
        val uri = Uri.parse(fullPath)
        val id = fullPath.hashCode().toString()
        val data = FileData(server?.id.toString(),id)
        data.filePath = fullPath
        data.fileUri = uri
        data.fileName = fileName
        excute(data)

    }

    override fun onCancel(data: FileData) {
        super.onCancel(data)
        downloadManager.remove(data.downLoadID)
    }

    override fun onExcute(data: FileData) {
        PagePresenter.getInstance<Any>().requestPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
        object : PageRequestPermission{
            override fun onRequestPermissionResult(
                resultAll: Boolean,
                permissions: List<Boolean>?
            ) {
                if(!resultAll){
                    Toast.makeText(context, R.string.notice_need_permission, Toast.LENGTH_LONG).show()
                    return
                }
                val newFilename = data.fileName ?: ""
                val initMsg = "${data.fileName} ${context.getString(R.string.notice_download_init)}"
                CustomToast.makeToast(context, initMsg, Toast.LENGTH_SHORT).show()

                val request = DownloadManager.Request(data.fileUri)
                    .apply {
                        setAllowedOverRoaming(false)
                        setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                        setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
                        setTitle(newFilename)
                    }
                if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, newFilename
                    )
                }

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                data.fileStatus = FileStatus.Progress
                data.downLoadID = downloadManager.enqueue(request)
                data.disposable = Observable.interval(100, TimeUnit.MILLISECONDS).subscribe {
                    val query = DownloadManager.Query()
                    query.setFilterById(data.downLoadID)

                    val cursor = downloadManager.query(query)
                    if(cursor.moveToFirst()){
                        val statusColumn = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if( cursor.getInt(statusColumn) != DownloadManager.STATUS_RUNNING) return@subscribe
                        val loaded = cursor.getFloat(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val total = cursor.getFloat(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        data.progress = loaded/total
                        Log.d(appTag, "progress $loaded $total")
                        onProgress(data)
                    }
                }
            }
        })

    }

    fun onReceived(id:Long){
        val data = datas.find { it.downLoadID == id }
        data ?: return
        val query = DownloadManager.Query()
        query.setFilterById(id)

        val cursor = downloadManager.query(query)
        if(cursor.moveToFirst()){
            val statusColumn = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            when(cursor.getInt(statusColumn)){
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    data.filePath = cursor.getString(uriIndex)
                    data.fileUri = Uri.parse(data.filePath)
                    onComplete(data)
                    val msg = "${data.fileName} ${context.getString(R.string.notice_downloaded)}"
                    CustomToast.makeToast(context, msg, Toast.LENGTH_SHORT).show()
                }
                DownloadManager.STATUS_FAILED -> {
                    onError(data)
                    val msg = "${data.fileName} ${context.getString(R.string.notice_download_error)}"
                    CustomToast.makeToast(context, msg, Toast.LENGTH_SHORT).show()
                }
                else ->{}
            }

        }else {
            // 다운로드 캔슬시
        }
    }





}