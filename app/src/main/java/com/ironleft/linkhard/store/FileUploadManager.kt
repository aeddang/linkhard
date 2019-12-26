package com.ironleft.linkhard.store


import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.ironleft.linkhard.R
import io.reactivex.subjects.PublishSubject


class FileUploadManager(val activity:Activity){

    private val appTag = javaClass.simpleName
    private val REQUEST_CODE = 1000001


    fun openFileFinder(mimeType:String = "*/*", title:String = activity.getString(R.string.title_file_select)) {
        val intent: Intent = Intent()
            .setType(mimeType)
            .setAction(Intent.ACTION_GET_CONTENT)
        activity.startActivityForResult(Intent.createChooser(intent, title), REQUEST_CODE )
    }


    val selectedFileObservable = PublishSubject.create<Uri>()
    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {

        if (requestCode == REQUEST_CODE  && resultCode == Activity.RESULT_OK) {
            val selectedfile = data.data //The uri with the location of the file
            selectedfile?.let { selectedFileObservable.onNext(it) }
        }
    }

}