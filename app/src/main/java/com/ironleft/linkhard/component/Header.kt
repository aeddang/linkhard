package com.ironleft.linkhard.component

import android.content.Context
import android.os.AsyncTask
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.ironleft.linkhard.PageID
import com.ironleft.linkhard.R
import com.ironleft.linkhard.store.FileUploadManager
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PagePresenter
import com.lib.util.animateY

import com.skeleton.rx.RxFrameLayout
import kotlinx.android.synthetic.main.cp_header.*
import kotlinx.android.synthetic.main.cp_header.view.*

class Header : RxFrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    override fun getLayoutResId(): Int { return R.layout.cp_header }
    private val appTag = javaClass.simpleName
    private lateinit var fileUploadManager: FileUploadManager

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
        btnUploadStatus.visibility = View.GONE
        btnDownLoadStatus.visibility = View.GONE
    }

    override fun onDestroyedView() {

    }

    fun injectFileUploadManager( manager: FileUploadManager) {
        fileUploadManager = manager
        fileUploadManager.statusObservable.subscribe {
            btnUploadStatus.visibility = if(it == FileUploadManager.Status.Progress) View.VISIBLE else View.GONE
        }.apply { disposables?.add(this) }

    }

    override fun onSubscribe() {
        super.onSubscribe()
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