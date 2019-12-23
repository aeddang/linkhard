package com.ironleft.linkhard.page.viewmodel

import androidx.lifecycle.ViewModel
import com.ironleft.linkhard.api.ApiRequest
import com.ironleft.linkhard.store.Repository
import com.ironleft.linkhard.store.ServerDatabaseManager
import io.reactivex.subjects.PublishSubject

class ViewModelServer (val repo: Repository) : ViewModel(){

    var servers = ArrayList< ServerDatabaseManager.Row>() ; private set


    val serverObservable = PublishSubject.create<Int>()
    fun syncDataBase(){
        servers = repo.serverDatabaseManager.getDatas()
        /*
        apis = servers.map {
            repo.networkFactory.getRetrofit(it.path).create(ApiRequest::class.java)
        }
        */

        serverObservable.onNext(servers.size)
    }

    fun addServer(row:ServerDatabaseManager.Row){
        repo.serverDatabaseManager.insert(row)
        syncDataBase()
    }

    fun deleteServer(row:ServerDatabaseManager.Row){
        repo.serverDatabaseManager.delete(row)
        syncDataBase()
    }

    fun updateServer(row:ServerDatabaseManager.Row){
        repo.serverDatabaseManager.update(row)
        syncDataBase()
    }

    override fun onCleared() {
        super.onCleared()
    }

}