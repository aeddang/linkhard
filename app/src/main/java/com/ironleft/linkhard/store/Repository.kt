package com.ironleft.linkhard.store
import android.content.Context
import com.skeleton.module.network.NetworkFactory


class Repository(
    val ctx: Context,
    val setting:SettingPreference,
    val networkFactory: NetworkFactory,
    val serverDatabaseManager: ServerDatabaseManager,
    val fileUploadManager: FileUploadManager
) {

    private val appTag = "Repository"

    init {

    }


}