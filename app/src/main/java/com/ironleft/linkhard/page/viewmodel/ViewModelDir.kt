package com.ironleft.linkhard.page.viewmodel


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
            DataList(UUID.randomUUID().toString(), DataType.Pdf,
                "dummy.pdf",
                "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/",
                "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"),
            DataList(UUID.randomUUID().toString(), DataType.Music, "music.mp3"),
            DataList(UUID.randomUUID().toString(), DataType.Movie,
                "big_buck_bunny_720p_1mb.mp4",
                "https://www.sample-videos.com/video123/mp4/720/",
                "https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4"),
            DataList(UUID.randomUUID().toString(), DataType.Image,
                "Sample-jpg-image-50kb.jpg",
                "https://sample-videos.com/img/",
                "https://sample-videos.com/img/Sample-jpg-image-50kb.jpg"),

            DataList(UUID.randomUUID().toString(), DataType.Image,
                "Frog_on_river_4000x3000_26-09-2010_11-01am_2mb.jpg",
                "http://upload.wikimedia.org/wikipedia/commons/c/cf/",
                "http://upload.wikimedia.org/wikipedia/commons/c/cf/Frog_on_river_4000x3000_26-09-2010_11-01am_2mb.jpg")

        )
    }

    fun deleteList(data:DataList){
        datas = datas.filter { it.id != data.id }
    }


    override fun onCleared() {
        super.onCleared()
    }



}