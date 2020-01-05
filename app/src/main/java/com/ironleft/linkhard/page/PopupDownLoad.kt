package com.ironleft.linkhard.page


import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ironleft.linkhard.PageID
import com.ironleft.linkhard.R
import com.ironleft.linkhard.model.DataList
import com.ironleft.linkhard.store.*
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PageFragment
import com.lib.page.PagePresenter
import com.lib.view.adapter.SingleAdapter
import com.skeleton.rx.RxPageFragment
import com.skeleton.view.alert.CustomAlert
import com.skeleton.view.alert.AlertDelegate
import com.skeleton.view.alert.CustomToast
import com.skeleton.view.item.ListItem
import com.skeleton.view.item.VerticalLinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_progress.view.*
import kotlinx.android.synthetic.main.popup_download.*
import kotlinx.android.synthetic.main.popup_download.recyclerView
import kotlinx.android.synthetic.main.ui_header.*
import javax.inject.Inject
import kotlin.math.roundToInt


class PopupDownLoad : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.popup_download


    @Inject
    lateinit var fileDownloadManager: FileDownloadManager
    @Inject
    lateinit var fileOpenController: FileOpenController

    private val adapter = ListAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun setParam(param: Map<String, Any?>): PageFragment {
        return super.setParam(param)
    }

    override fun onCreatedView() {
        super.onCreatedView()
        context?.let {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = VerticalLinearLayoutManager(it)
            adapter.setDataArray(fileDownloadManager.datas.toTypedArray())
        }
    }

    override fun onSubscribe() {
        super.onSubscribe()
        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().goBack()
        }.apply { disposables.add(this) }

        btnDeleteAll.clicks().subscribe {
            context ?: return@subscribe
            if(fileDownloadManager.status == FileManagerStatus.Empty){
                CustomToast.makeToast(context!!, R.string.popup_download_empty, Toast.LENGTH_SHORT).show()
                return@subscribe
            }
            CustomAlert.makeAlert(context!!,  R.string.notice_remove_all_download, object: AlertDelegate{
                override fun onPositiveClicked() {
                    fileDownloadManager.removeAll()
                }
            }).show()
        }.apply { disposables.add(this) }

        btnOpenFolder.clicks().subscribe {
           fileOpenController.openDownloadFolder()
        }.apply { disposables.add(this) }

        fileDownloadManager.datasObservable.subscribe {
            adapter.setDataArray(fileDownloadManager.datas.toTypedArray())

        }.apply { disposables.add(this) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeAllData()
    }


    inner class ListAdapter : SingleAdapter<FileData>(){
        override fun getListCell(parent: ViewGroup): ListItem {
            return Item(context!!)
        }
    }

    inner class Item(context: Context) : ListItem(context) {
        override fun getLayoutResId(): Int  = R.layout.item_progress

        private var currentData:FileData? = null

        set(value) {
            field = value

            value?.let { data->
                textTitle.text = data.fileName
                setStatus(data.fileStatus)
            }
        }

        private fun setStatus(status:FileStatus){
            currentData ?: return
            when(status){
                FileStatus.Progress -> {
                    progressBar.progress = (progressBar.max.toFloat() * currentData!!.progress).roundToInt()
                }
                FileStatus.Resume -> {
                    progressBar.visibility = ViewGroup.VISIBLE
                    progressBar.progress =  (progressBar.max.toFloat() * currentData!!.progress).roundToInt()
                    btnOpen.setImageResource(R.drawable.ic_file)
                    btnRetry.visibility = View.GONE
                    btnCancel.visibility =  View.VISIBLE
                }
                FileStatus.Cancel -> {
                    progressBar.visibility = ViewGroup.GONE
                    btnCancel.visibility =  View.GONE
                    btnRetry.visibility = View.VISIBLE
                }
                FileStatus.Error -> {
                    btnCancel.visibility =  View.GONE
                    progressBar.visibility = ViewGroup.GONE
                    btnRetry.visibility = View.VISIBLE
                    btnOpen.setImageResource(R.drawable.ic_error)  }
                FileStatus.Completed -> {
                    btnCancel.visibility =  View.GONE
                    btnRetry.visibility = View.GONE
                    progressBar.visibility = ViewGroup.GONE
                    btnOpen.setImageResource(DataList.getIconResource(currentData!!.fileName ?: ""))
                }
            }
        }


        override fun onCreatedView() {
            currentData?.let {data->
                data.statusObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {status->
                    setStatus(status)
                }.apply { disposables?.add(this) }

                btnDelete.clicks().subscribe {
                    val msg = "${data.fileName}${context.getString(R.string.notice_remove_download)}"
                    CustomAlert.makeAlert(context,  msg, object: AlertDelegate{
                        override fun onPositiveClicked() {
                            fileDownloadManager.remove(data)
                        }
                    }).show()
                }.apply { disposables?.add(this) }

                btnCancel.clicks().subscribe {
                    val msg = "${data.fileName}${context.getString(R.string.notice_stop_download)}"
                    CustomAlert.makeAlert(context,  msg , object: AlertDelegate{
                        override fun onPositiveClicked() {
                            fileDownloadManager.cancel(data)
                        }
                    }).show()
                }.apply { disposables?.add(this) }

                btnOpen.clicks().subscribe {
                    if(data.fileStatus != FileStatus.Completed) {
                        CustomToast.makeToast(context!!, R.string.notice_wait_progress, Toast.LENGTH_SHORT).show()
                        return@subscribe
                    }
                    fileOpenController.showDocumentFile(data)
                }.apply { disposables?.add(this) }

                btnRetry.clicks().subscribe {
                    if(data.fileStatus != FileStatus.Cancel && data.fileStatus != FileStatus.Error) return@subscribe
                    fileDownloadManager.resume(data)
                }.apply { disposables?.add(this) }
            }
        }

        override fun setData(data: Any?, idx:Int){
            super.setData(data, idx)
            currentData = data as FileData
        }


    }
}