package com.ironleft.linkhard.store

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.ironleft.linkhard.R
import com.ironleft.linkhard.model.DataList
import com.ironleft.linkhard.model.DataType
import com.lib.util.Log
import com.skeleton.view.alert.CustomToast
import java.io.File


class FileOpenController (val ctx:Context){
    private val appTag = javaClass.simpleName
    fun showDocumentFile(data: FileData?)
    {
        data ?: return
        val fileName = data.fileUri?.lastPathSegment ?: ""
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val file = data.fileUri?.toFile()
        file ?: return
        if(!file.exists()) {
            CustomToast.makeToast(ctx, R.string.notice_nofile, Toast.LENGTH_SHORT).show()
            return
        }
        val contentUri: Uri = FileProvider.getUriForFile(ctx, "com.ironleft.linkhard.fileProvider", file)
        when(val type= DataList.getDataType(fileName)){
            DataType.Text -> intent.setDataAndType(contentUri, type.minetype)
            DataType.Word -> intent.setDataAndType(contentUri, type.minetype)
            DataType.Ppt -> intent.setDataAndType(contentUri, type.minetype)
            DataType.Excel -> intent.setDataAndType(contentUri, type.minetype)
            DataType.Pdf -> intent.setDataAndType(contentUri, type.minetype)
            DataType.Movie -> intent.setDataAndType(contentUri, type.minetype)
            DataType.Music -> intent.setDataAndType(contentUri, type.minetype)
            DataType.Image -> intent.setDataAndType(contentUri, type.minetype)
            else -> {
                CustomToast.makeToast(ctx, R.string.notice_disable_openfile, Toast.LENGTH_SHORT).show()
                openDownloadFolder()
                return
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            ctx.startActivity(intent)
        } catch ( e:Exception){
            Log.e(appTag, "${e.message}")
            CustomToast.makeToast(ctx, R.string.notice_disable_openfile, Toast.LENGTH_SHORT).show()
            openDownloadFolder()
        }

    }
    fun openDownloadFolder(){
        ctx.startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
    }

    fun shareFile(data: DataList?) {
        data ?: return
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TITLE, data.fileName)
        shareIntent.putExtra(Intent.EXTRA_TEXT, data.linkPath)

        try {
            ctx.startActivity(shareIntent)
        } catch ( e:Exception){

            Log.e(appTag, "${e.message}")
            CustomToast.makeToast(ctx, R.string.notice_disable_sharefile, Toast.LENGTH_SHORT).show()
        }

    }
}