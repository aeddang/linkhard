package com.ironleft.linkhard.store

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

class FileOpenController (val ctx:Context){

    fun showDocumentFile(path:String, fileName:String)
    {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val file = File("$path/$fileName")

        if (fileName.endsWith("mp3"))
        {
            intent.setDataAndType(Uri.fromFile(file), "audio/*");
        }
        else if (fileName.endsWith("mp4"))
        {
            intent.setDataAndType(Uri.fromFile(file), "vidio/*");
        }
        else if (fileName.endsWith("jpg") || fileName.endsWith("jpeg") ||
            fileName.endsWith("JPG") || fileName.endsWith("gif") ||
            fileName.endsWith("png") || fileName.endsWith("bmp"))
        {
            intent.setDataAndType(Uri.fromFile(file), "image/*");
        }
        else if (fileName.endsWith("txt"))
        {
            intent.setDataAndType(Uri.fromFile(file), "text/*");
        }
        else if (fileName.endsWith("doc") || fileName.endsWith("docx"))
        {
            intent.setDataAndType(Uri.fromFile(file), "application/msword");
        }
        else if (fileName.endsWith("xls") || fileName.endsWith("xlsx"))
        {
            intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.ms-excel");
        }
        else if (fileName.endsWith("ppt") || fileName.endsWith("pptx"))
        {
            intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.ms-powerpoint");
        }
        else if (fileName.endsWith("pdf")) {
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    }


}