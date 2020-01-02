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
import com.ironleft.linkhard.page.viewmodel.ViewModelServer
import com.ironleft.linkhard.store.ServerDatabaseManager
import com.jakewharton.rxbinding3.view.clicks
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
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_setup_server.view.*
import kotlinx.android.synthetic.main.page_setup_server.*
import kotlinx.android.synthetic.main.ui_header.*
import javax.inject.Inject


class PageSetupServer : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.page_setup_server

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ViewModelServer

    private val adapter = ListAdapter()
    private var finalServerID = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ViewModelServer::class.java)
    }


    override fun onCreatedView() {
        super.onCreatedView()
        finalServerID = viewModel.repo.setting.getFinalServerID()
        context?.let {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = VerticalLinearLayoutManager(it)
        }
    }

    override fun onSubscribe() {
        super.onSubscribe()
        viewModel.serverObservable.subscribe {serverNum->
            if(serverNum == 1 && finalServerID == -1) finalServerID = viewModel.servers[0].id
            adapter.setDataArray(viewModel.servers.toTypedArray())

        }.apply { disposables.add(this) }

        btnAddList.clicks().subscribe {
            viewModel.addServer(ServerDatabaseManager.Row())
        }.apply { disposables.add(this) }

        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().goBack()
        }.apply { disposables.add(this) }

        viewModel.syncDataBase()

    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.removeAllData()
    }


    inner class ListAdapter : SingleAdapter<ServerDatabaseManager.Row>(){
        override fun getListCell(parent: ViewGroup): ListItem {
            return Item(context!!)
        }
    }

    inner class Item(context: Context) : ListItem(context) {
        override fun getLayoutResId(): Int  = R.layout.item_setup_server

        private var currentData:ServerDatabaseManager.Row? = null
        private var isEditMode:Boolean = false
        set(value) {
            field = value
            inputTitle.isEnabled = value
            inputServer.isEnabled = value
            inputID.isEnabled = value
            inputPW.isEnabled = value
            if(value){
                areaID.visibility = View.VISIBLE
                areaPW.visibility = View.VISIBLE
                if(currentData?.id != finalServerID) btnDelete.visibility = View.VISIBLE
                btnRefresh.visibility = View.VISIBLE
                btnHome.visibility = View.GONE
                btnModify.setImageResource(R.drawable.ic_unlock)
            }else{
                btnDelete.visibility = View.GONE
                btnRefresh.visibility = View.GONE
                areaID.visibility = View.GONE
                areaPW.visibility = View.GONE
                btnHome.visibility = View.VISIBLE
                btnModify.setImageResource(R.drawable.ic_lock)
            }
        }

        private val isChanged:Boolean
        get() {
            if( currentData?.title != inputTitle.text.toString() ) return true
            if( currentData?.path != inputServer.text.toString() ) return true
            if( currentData?.userID != inputID.text.toString() ) return true
            if( currentData?.userPW != inputPW.text.toString() ) return true
            return false
        }

        private fun reset(){
            currentData?.let { data ->
                inputTitle.setText(data.title)
                inputServer.setText(data.path)
                inputID.setText(data.userID)
                inputPW.setText(data.userPW)
            }
        }

        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            currentData?.let {data->
                if(data.id == finalServerID) btnHome.setImageResource(R.drawable.ic_home_on)
                else btnHome.setImageResource(R.drawable.ic_home)
                reset()

                isEditMode = !data.isLock
                disposables = CompositeDisposable()
                btnHome.clicks().subscribe {
                    viewModel.repo.setting.putFinalServerID(data.id)
                    val param = HashMap<String, Any?>()
                    param[PageParam.SERVER_DATA] = data
                    PagePresenter.getInstance<PageID>().pageChange(PageID.DIR, param)
                }

                btnModify.clicks().subscribe {
                    if(isEditMode  && isChanged){
                        CustomAlert.makeAlert(context,  R.string.notice_not_saved, object: AlertDelegate{
                            override fun onPositiveClicked() {
                                isEditMode = false
                                data.isLock = true
                                reset()
                            }
                        }).show()
                    }else{
                        isEditMode = !isEditMode
                        data.isLock = !isEditMode
                    }
                }.apply { disposables?.add(this) }


                btnRefresh.clicks().subscribe {
                    if(!isChanged){
                        CustomToast.makeToast(context, R.string.notice_not_modify, Toast.LENGTH_SHORT).show()
                    }else{
                        data.userID = inputID.text.toString()
                        data.userPW = inputPW.text.toString()
                        data.title = inputTitle.text.toString()
                        data.path = inputServer.text.toString()
                        viewModel.updateServer(data)
                        CustomToast.makeToast(context, R.string.notice_modify, Toast.LENGTH_SHORT).show()
                    }


                }.apply { disposables?.add(this) }


                btnDelete.clicks().subscribe {
                    if( data.id == finalServerID ){
                        CustomToast.makeToast(context, R.string.page_setup_delete_disable, Toast.LENGTH_SHORT).show()
                        return@subscribe
                    }
                    CustomAlert.makeAlert(context,  R.string.notice_delete, object: AlertDelegate{
                        override fun onPositiveClicked() { viewModel.deleteServer(data) }
                    }).show()
                }.apply { disposables?.add(this) }
            }
        }

        override fun setData(data: Any?, idx:Int){
            currentData = data as ServerDatabaseManager.Row
        }


    }
}