package com.ironleft.linkhard.page.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.ironleft.linkhard.api.ApiRequest
import com.ironleft.linkhard.model.DataList
import com.ironleft.linkhard.model.DataType
import com.ironleft.linkhard.store.Repository
import com.ironleft.linkhard.store.ServerDatabaseManager
import com.lib.util.Log
import io.reactivex.subjects.PublishSubject
import java.util.*

class ViewModelDir (val repo: Repository) : ViewModel(){
    private val appTag = javaClass.simpleName
    var server:ServerDatabaseManager.Row? = null
        set(value) {
            field = value
            value?.let {
                api = try {
                    repo.networkFactory.getRetrofit(it.path).create(ApiRequest::class.java)
                } catch (e:Throwable){
                    null
                }
            }

        }
    var api:ApiRequest? = null ; private set

    var datas:List<DataList> = listOf()
        private set(value) {
            field = value
            listChangedObservable.onNext(value)
        }


    val checkHealthObservable = PublishSubject.create<Boolean>()
    fun checkHealth() {
        if(api == null){
            checkHealthObservable.onNext(false)
            return
        }
        var d = api?.checkHealth(server?.userID, server?.userPW)?.subscribe(
            {
                Log.d(appTag, "${it.data}")
                checkHealthObservable.onNext(true)
            },
            {
                Log.e(appTag, "${it.message}")
                checkHealthObservable.onNext(false)
            }
        )
    }


    val listChangedObservable = PublishSubject.create<List<DataList>>()
    fun updateLists(id:String?, path:String?){
        datas = listOf(
            DataList(UUID.randomUUID().toString(), DataType.Folder, "folder1"),
            DataList(UUID.randomUUID().toString(), DataType.Folder, "folder2"),
            DataList(UUID.randomUUID().toString(), DataType.File, "file.file"),
            DataList(UUID.randomUUID().toString(), DataType.Text, "text.txt"),
            DataList(UUID.randomUUID().toString(), DataType.Ppt, "powerpoint.ppt"),
            DataList(UUID.randomUUID().toString(), DataType.Word, "word.doc"),
            DataList(UUID.randomUUID().toString(), DataType.Excel, "excel.xlsx"),
            DataList(UUID.randomUUID().toString(), DataType.Music, "music.mp3"),
            DataList(UUID.randomUUID().toString(), DataType.Movie, "movie.mp4"),
            DataList(UUID.randomUUID().toString(), DataType.Image, "image.jpeg")
        )
    }

    fun deleteList(data:DataList){
        datas = datas.filter { it.id != data.id }
    }

    fun uploadFile(file:Uri){
        
    }



    override fun onCleared() {
        super.onCleared()
    }



}