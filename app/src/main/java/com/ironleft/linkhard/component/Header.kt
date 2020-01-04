package com.ironleft.linkhard.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.ironleft.linkhard.PageID
import com.ironleft.linkhard.R
import com.ironleft.linkhard.store.FileDownloadManager
import com.ironleft.linkhard.store.FileManagerStatus
import com.ironleft.linkhard.store.FileUploadManager
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PagePresenter
import com.lib.util.animateY

import com.skeleton.rx.RxFrameLayout
import kotlinx.android.synthetic.main.cp_header.view.*

class Header : RxFrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    override fun getLayoutResId(): Int { return R.layout.cp_header }
    private val appTag = javaClass.simpleName
    private lateinit var fileUploadManager: FileUploadManager
    private lateinit var fileDownloadManager: FileDownloadManager
    var title:String = ""
        set(value) {
            field = value
            textPageTitle.text = value
        }

    var useSettingButton = true
        set(value) {
            field = value
            btnSetting.visibility = if(value) View.VISIBLE else View.GONE
        }

    var useBackButton = true
        set(value) {
            field = value
            btnBack.visibility = if(value) View.VISIBLE else View.GONE
        }


    override fun onCreatedView() {
    }

    override fun onDestroyedView() {

    }

    fun injectFileUploadManager( manager: FileUploadManager) {
        fileUploadManager = manager
        setActiveUploadStatus(fileUploadManager.status)
        fileUploadManager.statusObservable.subscribe {
            setActiveUploadStatus(it)
        }.apply { disposables?.add(this) }
    }

    fun injectFileDownloadManager( manager: FileDownloadManager) {
        fileDownloadManager = manager
        setActiveDownloadStatus(fileDownloadManager.status)
        fileDownloadManager.statusObservable.subscribe {
            setActiveDownloadStatus(it)
        }.apply { disposables?.add(this) }
    }

    private fun setActiveUploadStatus(status:FileManagerStatus){
        val res = when(status){
            FileManagerStatus.Progress -> R.drawable.ic_upload_cloud_on
            FileManagerStatus.NoneProgress -> R.drawable.ic_upload_cloud
            FileManagerStatus.Empty -> R.drawable.ic_upload_cloud
        }
        val colorRes = when(status){
            FileManagerStatus.Progress -> R.color.colorAccent
            FileManagerStatus.NoneProgress -> R.color.colorPrimaryDark
            FileManagerStatus.Empty -> R.color.colorPrimaryLight
        }
        btnUploadStatus.setImageResource(res)
        btnUploadStatus.setColorFilter(ContextCompat.getColor(context, colorRes))
    }

    private fun setActiveDownloadStatus(status:FileManagerStatus){
        val res = when(status){
            FileManagerStatus.Progress -> R.drawable.ic_download_cloud_on
            FileManagerStatus.NoneProgress -> R.drawable.ic_download_cloud
            FileManagerStatus.Empty -> R.drawable.ic_download_cloud
        }
        val colorRes = when(status){
            FileManagerStatus.Progress -> R.color.colorAccent
            FileManagerStatus.NoneProgress -> R.color.colorPrimaryDark
            FileManagerStatus.Empty -> R.color.colorPrimaryLight
        }
        btnDownLoadStatus.setImageResource(res)
        btnDownLoadStatus.setColorFilter(ContextCompat.getColor(context, colorRes))
    }


    override fun onSubscribe() {
        super.onSubscribe()
        btnDownLoadStatus.clicks().subscribe {
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_DOWNLOAD)
        }.apply { disposables?.add(this) }

        btnBack.clicks().subscribe {
            PagePresenter.getInstance<PageID>().goBack()
        }.apply { disposables?.add(this) }

        btnSetting.clicks().subscribe {
            val param = HashMap<String, Any?>()
            PagePresenter.getInstance<PageID>().pageChange(PageID.SETUP_SERVER, param)
        }.apply { disposables?.add(this) }

    }

    fun onOpen(){
        animateY(0).apply {
            interpolator = AccelerateInterpolator()
            startAnimation(this)
        }
    }

    fun onClose(){

        animateY(- height).apply {
            interpolator = DecelerateInterpolator()
            startAnimation(this)
        }

    }





}