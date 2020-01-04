package com.ironleft.linkhard.store


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.ironleft.linkhard.PageID
import com.ironleft.linkhard.R
import com.lib.page.PagePresenter
import com.skeleton.view.alert.CustomToast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit


class FileUploadManager(ctx:Context): FileManager(ctx){
    private val appTag = javaClass.simpleName
    private val REQUEST_CODE = 10

    override fun onDestroyed() {
        cancelAll()
    }
    fun openFileFinder(server:String, mimeType:String = "*/*", title:String = context.getString(R.string.title_file_select)) {
        serverID = server
        val intent: Intent = Intent()
            .setType(mimeType)
            .setAction(Intent.ACTION_GET_CONTENT)

        PagePresenter.getInstance<PageID>().activity?.getCurrentActivity()?.startActivityForResult(
            Intent.createChooser(intent, title), REQUEST_CODE )
    }


    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {

        if (requestCode == REQUEST_CODE  && resultCode == Activity.RESULT_OK) {
            val selectedfile = data.data //The uri with the location of the file
            selectedfile?.let {
                val fileData = FileData(serverID)
                fileData.fileUri = it
                fileData.fileName = it.lastPathSegment
                excute(fileData)
            }
        }
    }



    override fun onExcute(data: FileData) {

        val initMsg = "${data.fileName} ${context.getString(R.string.notice_upload_init)}"
        CustomToast.makeToast(context, initMsg, Toast.LENGTH_SHORT).show()
        data.disposable = Observable.interval(50, TimeUnit.MILLISECONDS)
            .take(20)
            .observeOn(AndroidSchedulers.mainThread()).subscribe (
                {
                    data.progress = it/20.0f
                    onProgress(data)
                },
                {
                    onError(data)
                    val msg = "${data.fileName} ${context.getString(R.string.notice_upload_error)}"
                    CustomToast.makeToast(context, msg, Toast.LENGTH_SHORT).show()
                },
                {

                    onComplete(data)
                    val msg = "${data.fileName} ${context.getString(R.string.notice_uploaded)}"
                    CustomToast.makeToast(context, msg, Toast.LENGTH_SHORT).show()
                }
            )
    }

}