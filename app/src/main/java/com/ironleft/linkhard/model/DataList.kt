package com.ironleft.linkhard.model

import com.ironleft.linkhard.R

enum class DataType{
    File,
    Folder,
    Text,Word,Ppt,Excel,
    Movie,Music,Image
}


data class DataList(val id:String, val type:DataType, val title:String){

    val isLinkAble:Boolean
        get() {
            return type != DataType.Folder && type != DataType.File
        }
    val isDownLoadAble:Boolean
        get() {
            return type != DataType.Folder
        }
    val isDeleteAble:Boolean
        get() {
            return type != DataType.Folder
        }
    val iconResource:Int
        get() {
            return when(type){
                DataType.Folder -> R.drawable.ic_folder
                DataType.File -> R.drawable.ic_file
                DataType.Text -> R.drawable.ic_file_text
                DataType.Word -> R.drawable.ic_file_word
                DataType.Ppt -> R.drawable.ic_file_ppt
                DataType.Excel -> R.drawable.ic_file_excel
                DataType.Movie -> R.drawable.ic_movie
                DataType.Music -> R.drawable.ic_music
                DataType.Image -> R.drawable.ic_image
            }
        }
}