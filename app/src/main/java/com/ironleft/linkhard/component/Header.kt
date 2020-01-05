package com.ironleft.linkhard.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
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
        setActiveStatus(fileUploadManager.status, btnUploadStatus)
        fileUploadManager.statusObservable.subscribe {
            setActiveStatus(it, btnUploadStatus)
        }.apply { disposables?.add(this) }
    }

    fun injectFileDownloadManager( manager: FileDownloadManager) {
        fileDownloadManager = manager
        setActiveStatus(fileDownloadManager.status, btnDownLoadStatus)
        fileDownloadManager.statusObservable.subscribe {
            setActiveStatus(it,btnDownLoadStatus)
        }.apply { disposables?.add(this) }
    }

    private fun setActiveStatus(status:FileManagerStatus, btn:ImageButton){
        val res = when(status){
            FileManagerStatus.Progress -> if(btn == btnUploadStatus) R.drawable.ic_upload_cloud_on else R.drawable.ic_download_cloud_on
            else -> if(btn == btnUploadStatus) R.drawable.ic_upload_cloud else R.drawable.ic_download_cloud
        }
        val colorRes = when(status){
            FileManagerStatus.Progress -> R.color.colorAccent
            else -> R.color.colorPrimaryDark
        }

        val opercity = when(status){
            FileManagerStatus.Empty -> 0.5f
            else -> 1.0f
        }
        btn.alpha = opercity
        btn.setImageResource(res)
        btn.setColorFilter(ContextCompat.getColor(context, colorRes))
    }



    override fun onSubscribe() {
        super.onSubscribe()
        btnDownLoadStatus.clicks().subscribe {
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_DOWNLOAD)
        }.apply { disposables?.add(this) }

        btnUploadStatus.clicks().subscribe {
            PagePresenter.getInstance<PageID>().openPopup(PageID.POPUP_UPLOAD)
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