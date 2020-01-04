package com.ironleft.linkhard.page
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.ironleft.linkhard.PageID
import com.ironleft.linkhard.PageParam
import com.ironleft.linkhard.R
import com.ironleft.linkhard.model.DataList
import com.ironleft.linkhard.store.FileOpenController
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PageFragment
import com.lib.page.PagePresenter
import com.skeleton.rx.RxPageFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.popup_webview.*
import kotlinx.android.synthetic.main.popup_webview.loadingBar
import kotlinx.android.synthetic.main.ui_header.*
import javax.inject.Inject
import kotlin.Exception


class PopupWebView : RxPageFragment() {
    @Inject
    lateinit var fileOpenController: FileOpenController
    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.popup_webview
    private var currentData:DataList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }


    override fun setParam(param: Map<String, Any?>): PageFragment {
        currentData =  param[PageParam.FILE_DATA] as? DataList
        return super.setParam(param)

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreatedView() {
        super.onCreatedView()
        loadingBar.visibility = View.VISIBLE
        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
            allowFileAccess = true
            allowContentAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            loadWithOverviewMode = true
            useWideViewPort = true
            @SuppressLint("ObsoleteSdkInt")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// html5에서 https 이미지 안나올때
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                view ?: return
                loadingBar.visibility = View.VISIBLE
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                view ?: return
                loadingBar.visibility = View.GONE
            }
            @SuppressLint("DefaultLocale")
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                webView.loadUrl(url)
                return true
            }
        }
        context?.let {  webView.addJavascriptInterface(WebAppInterface(it), "") }

    }
    inner class WebAppInterface(private val mContext: Context) {
        /** Show a toast from the web page  */
        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }
    }
    override fun onTransactionCompleted() {
        super.onTransactionCompleted()
        currentData?.let {
            webView.loadUrl(it.linkPath)
        }

    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().goBack()
        }.apply { disposables.add(this) }

        btnShare.clicks().subscribe {
            fileOpenController.shareFile(currentData)
        }.apply { disposables.add(this) }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            webView.stopLoading()
            webView.webViewClient = null
            webView.clearCache(true)
            webView.clearHistory()
        } catch ( e:Exception){

        }
        currentData = null
    }



}