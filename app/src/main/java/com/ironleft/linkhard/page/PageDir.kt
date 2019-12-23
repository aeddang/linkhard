package com.ironleft.linkhard.page


import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.ironleft.linkhard.PageID
import com.ironleft.linkhard.PageParam
import com.ironleft.linkhard.R
import com.ironleft.linkhard.model.DataList
import com.ironleft.linkhard.model.DataType
import com.ironleft.linkhard.page.viewmodel.ViewModelDir
import com.ironleft.linkhard.store.ServerDatabaseManager
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PageFragment
import com.lib.page.PagePresenter
import com.lib.view.adapter.SingleAdapter
import com.skeleton.module.ViewModelFactory
import com.skeleton.rx.RxPageFragment
import com.skeleton.view.alert.CustomAlert
import com.skeleton.view.alert.AlertDelegate
import com.skeleton.view.alert.CustomToast
import com.skeleton.view.item.ListItem
import com.skeleton.view.item.VerticalLinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.item_list.view.*
import kotlinx.android.synthetic.main.page_dir.*
import kotlinx.android.synthetic.main.ui_header.*
import javax.inject.Inject


class PageDir : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.page_dir

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ViewModelDir


    private val adapter = ListAdapter()
    private var server:ServerDatabaseManager.Row? = null
    private var folder:DataList? = null
    private var path:String = "root/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ViewModelDir::class.java)
    }

    override fun setParam(param: Map<String, Any?>): PageFragment {
        server =  param[PageParam.SERVER_DATA] as? ServerDatabaseManager.Row
        folder =  param[PageParam.FOLDER_DATA] as? DataList
        path =  param[PageParam.FOLDER_PATH] as? String ?: path
        return super.setParam(param)
    }

    override fun onCreatedView() {
        super.onCreatedView()
        viewModel.server = server
        textPageTitle.text = path

        context?.let {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = VerticalLinearLayoutManager(it)
        }
    }

    override fun onSubscribe() {
        super.onSubscribe()
        viewModel.listChangedObservable.subscribe {datas->
            adapter.setDataArray(datas.toTypedArray())
        }.apply { disposables.add(this) }

        viewModel.checkHealthObservable.subscribe {isHealth->
            context ?: return@subscribe
            if(!isHealth)  CustomToast.makeToast(context!!, R.string.notice_disable_server, Toast.LENGTH_SHORT).show()
        }.apply { disposables.add(this) }

        btnUpload.clicks().subscribe {


        }.apply { disposables.add(this) }

        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().goBack()
        }.apply { disposables.add(this) }

        btnSetting.clicks().subscribe {
            val param = HashMap<String, Any?>()
            PagePresenter.getInstance<PageID>().pageChange(PageID.SETUP_SERVER, param)
        }.apply { disposables.add(this) }


        viewModel.updateLists(folder?.id, path)
        if( folder== null ) viewModel.checkHealth()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.removeAllData()
    }


    inner class ListAdapter : SingleAdapter<DataList>(){
        override fun getListCell(parent: ViewGroup): ListItem {
            return Item(context!!)
        }
    }

    inner class Item(context: Context) : ListItem(context) {
        override fun getLayoutResId(): Int  = R.layout.item_list

        private var currentData:DataList? = null

        set(value) {
            field = value

            value?.let { data->
                textTitle.text = data.title
                imageIcon.setImageResource(data.iconResource)
                btnDelete.visibility = if(data.isDeleteAble) View.VISIBLE else View.GONE
                btnLink.visibility = if(data.isLinkAble) View.VISIBLE else View.GONE
                btnDownload.visibility = if(data.isDownLoadAble) View.VISIBLE else View.GONE
            }
        }


        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            currentData?.let {data->
                btnDelete.clicks().subscribe {
                    CustomAlert.makeAlert(context,  R.string.notice_delete, object: AlertDelegate{
                        override fun onPositiveClicked() { viewModel.deleteList(data) }
                    }).show()
                }.apply { disposables?.add(this) }

                if(data.type == DataType.Folder){

                    btnGo.clicks().subscribe {
                        val param = HashMap<String, Any?>()
                        param[PageParam.SERVER_DATA] = viewModel.server
                        param[PageParam.FOLDER_DATA] = data
                        param[PageParam.FOLDER_PATH] =  "$path${data.title}/"
                        PagePresenter.getInstance<PageID>().pageChange(PageID.DIR, param)
                    }.apply { disposables?.add(this) }
                }

                btnDownload.clicks().subscribe {

                }.apply { disposables?.add(this) }

                btnLink.clicks().subscribe {

                }.apply { disposables?.add(this) }
            }
        }

        override fun setData(data: Any?, idx:Int){
            currentData = data as DataList
        }


    }
}