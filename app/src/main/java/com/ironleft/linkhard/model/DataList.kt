package com.ironleft.linkhard.model

import com.ironleft.linkhard.R

enum class DataType(val minetype:String = ""){
    File("text/*"),
    Folder,
    Text("text/*"),
    Word("application/msword"),
    Ppt("application/vnd.ms-powerpoint"),
    Excel("application/vnd.ms-excel"),
    Pdf("application/pdf"),
    Movie("video/mpeg"),
    Music("audio/*"),
    Image("image/*")
}

data class DataList(val id:String, val type:DataType, val fileName:String, val filePath:String? = null, val linkPath:String? = null){

    companion object {
        fun getDataType(file:String) : DataType{
            val fileName = file.toLowerCase()
            return if (fileName.endsWith("mp3")) DataType.Music
            else if (fileName.endsWith("mp4")) DataType.Movie
            else if (fileName.endsWith("jpg") || fileName.endsWith("jpeg") || fileName.endsWith("gif") || fileName.endsWith("png") || fileName.endsWith("bmp")) DataType.Image
            else if (fileName.endsWith("txt")) DataType.Text
            else if (fileName.endsWith("doc") || fileName.endsWith("docx")) DataType.Word
            else if (fileName.endsWith("xls") || fileName.endsWith("xlsx")) DataType.Excel
            else if (fileName.endsWith("ppt") || fileName.endsWith("pptx")) DataType.Ppt
            else if (fileName.endsWith("pdf")) DataType.Pdf
            else DataType.File
        }

        fun getIconResource(file:String) : Int = getIconResource(getDataType(file))
        fun getIconResource(type:DataType) : Int{
            return when(type){
                DataType.Folder -> R.drawable.ic_folder
                DataType.File -> R.drawable.ic_file
                DataType.Text -> R.drawable.ic_file_text
                DataType.Word -> R.drawable.ic_file_word
                DataType.Ppt -> R.drawable.ic_file_ppt
                DataType.Excel -> R.drawable.ic_file_excel
                DataType.Pdf -> R.drawable.ic_file_pdf
                DataType.Movie -> R.drawable.ic_movie
                DataType.Music -> R.drawable.ic_music
                DataType.Image -> R.drawable.ic_image
            }
        }
    }


    val isLinkAble:Boolean
        get() {
            return linkPath != null
        }
    val isDownLoadAble:Boolean
        get() {
            return filePath != null
}
    val isDeleteAble:Boolean
        get() {
            return type != DataType.Folder
        }
    val iconResource:Int
        get() {
            return DataList.getIconResource(type)
        }
}