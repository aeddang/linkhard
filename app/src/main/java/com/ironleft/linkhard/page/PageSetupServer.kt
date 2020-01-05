package com.ironleft.linkhard.page


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.ironleft.linkhard.PageID
import com.ironleft.linkhard.PageParam
import com.ironleft.linkhard.R
import com.ironleft.linkhard.page.viewmodel.ViewModelServer
import com.ironleft.linkhard.store.ServerDatabaseManager
import com.jakewharton.rxbinding3.view.clicks
import com.lib.module.SoftKeyboard
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
import io.reactivex.subjects.PublishSubject
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
    private val items = ArrayList<Item>()
    private val adapter = ListAdapter()
    private var keyBoard:SoftKeyboard? = null
    private var finalServerID = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ViewModelServer::class.java)
    }


    override fun onCreatedView() {
        super.onCreatedView()
        btnCurrentDataModify.visibility = View.GONE
        finalServerID = viewModel.repo.setting.getFinalServerID()
        context?.let {
            keyBoard = SoftKeyboard(  it.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = VerticalLinearLayoutManager(it)
        }


    }

    override fun onSubscribe() {
        super.onSubscribe()
        currentEditingObservable.subscribe {
            if(it){
                btnCurrentDataModify.visibility = View.VISIBLE
                btnAddList.visibility = View.GONE
            }else{
                btnCurrentDataModify.visibility = View.GONE
                btnAddList.visibility = View.VISIBLE
                keyBoard?.hideKeyBoard()
            }
        }.apply { disposables.add(this) }

        viewModel.serverObservable.subscribe {serverNum->
            currentEditingData = null
            if(serverNum == 1 && finalServerID == -1) finalServerID = viewModel.servers[0].id
            adapter.setDataArray(viewModel.servers.toTypedArray())

        }.apply { disposables.add(this) }

        btnCurrentDataModify.clicks().subscribe {
            items.forEach { it.sync() }
            currentEditingData?.let { data->
                if(!data.isModify){
                    CustomToast.makeToast(context!!, R.string.notice_not_modify, Toast.LENGTH_SHORT).show()
                }else{
                    data.sync()
                    viewModel.updateServer(data)
                    CustomToast.makeToast(context!!, R.string.notice_modify, Toast.LENGTH_SHORT).show()
                }
            }
        }.apply { disposables.add(this) }

        btnAddList.clicks().subscribe {
            viewModel.addServer(ServerDatabaseManager.Row())
            val handler = Handler()
            handler.postDelayed({ recyclerView.smoothScrollToPosition(viewModel.servers.size) }, 500)
        }.apply { disposables.add(this) }

        btnClose.clicks().subscribe {
            PagePresenter.getInstance<PageID>().goBack()
        }.apply { disposables.add(this) }


    }

    override fun onTransactionCompleted() {
        super.onTransactionCompleted()
        viewModel.syncDataBase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        items.clear()
        keyBoard?.hideKeyBoard()
        keyBoard?.destroy()
        keyBoard = null
        currentEditingData = null
        adapter.removeAllData()
    }


    inner class ListAdapter : SingleAdapter<ServerDatabaseManager.Row>(){
        override fun getListCell(parent: ViewGroup): ListItem {
            val item = Item(context!!)
            items.add(item)
            return item
        }
    }

    companion object {
        private val currentEditingObservable = PublishSubject.create<Boolean>()
        private var currentEditingData:ServerDatabaseManager.Row? = null
        set(value) {
            field = value
            currentEditingObservable.onNext(value!=null)
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
            val tintColor = if(value) ContextCompat.getColor(context,R.color.colorAccent) else ContextCompat.getColor(context,R.color.colorPrimaryLight)
            iconTitle.setColorFilter(tintColor)
            iconServer.setColorFilter(tintColor)
            iconID.setColorFilter(tintColor)
            iconPW.setColorFilter(tintColor)

            if(value){
                if(currentEditingData !== currentData) currentEditingData?.isLock = true
                currentEditingData = currentData
                areaID.visibility = View.VISIBLE
                areaPW.visibility = View.VISIBLE
                btnRefresh.visibility = View.VISIBLE
                btnHome.visibility = View.GONE
                btnModify.setImageResource(R.drawable.ic_unlock)
                this.alpha = 1.0f
            }else{
                if(currentEditingData === currentData) currentEditingData = null
                btnRefresh.visibility = View.GONE
                areaID.visibility = View.GONE
                areaPW.visibility = View.GONE
                btnHome.visibility = View.VISIBLE
                btnModify.setImageResource(R.drawable.ic_lock)
                this.alpha = 0.6f
            }
        }


        private fun reset(){
            currentData?.let { data ->
                inputTitle.setText(data.modifyTitle)
                inputServer.setText(data.modifyPath)
                inputID.setText(data.modifyUserID)
                inputPW.setText(data.modifyUserPW)
            }
        }
        internal fun sync(){
            currentData?.let { data ->
                data.modifyUserID = inputID.text.toString()
                data.modifyUserPW = inputPW.text.toString()
                data.modifyTitle = inputTitle.text.toString()
                data.modifyPath = inputServer.text.toString()
            }
        }

        private fun setHome(data:ServerDatabaseManager.Row){
            viewModel.repo.setting.putFinalServerID(data.id)
            val param = HashMap<String, Any?>()
            param[PageParam.SERVER_DATA] = data
            PagePresenter.getInstance<PageID>().pageChange(PageID.DIR, param)
        }

        override fun onCreatedView() {
            keyBoard?.addEditTexts(arrayListOf(inputServer, inputID, inputTitle, inputPW))
            currentData?.let {data->
                if(data.id == finalServerID){
                    btnHome.setImageResource(R.drawable.ic_home_on)
                    btnHome.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
                    btnDelete.visibility = View.GONE
                }
                else {
                    btnHome.setImageResource(R.drawable.ic_home)
                    btnHome.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                    btnDelete.visibility = View.VISIBLE
                }

                reset()
                isEditMode = !data.isLock
                data.lockObservable.subscribe {
                    isEditMode = !data.isLock
                }.apply { disposables?.add(this) }

                btnHome.clicks().subscribe {
                    if(!data.isCompleted){
                        CustomToast.makeToast(context, R.string.notice_disable_home, Toast.LENGTH_SHORT).show()
                        data.isLock = false
                        return@subscribe
                    }
                    if(currentEditingData != null){
                        CustomAlert.makeAlert(context,  R.string.page_setup_not_saved, object: AlertDelegate{
                            override fun onPositiveClicked() {
                                setHome(data)
                            }
                        }).show()
                    }else{
                        setHome(data)
                    }
                }.apply { disposables?.add(this) }

                btnModify.clicks().subscribe {
                    sync()
                    if(isEditMode  && data.isModify){
                        CustomAlert.makeAlert(context,  R.string.page_setup_not_saved, object: AlertDelegate{
                            override fun onPositiveClicked() {
                                data.isLock = true
                                data.reset()
                                reset()
                            }
                        }).show()
                    } else if(currentEditingData != null && currentEditingData !== currentData && !isEditMode ){
                        CustomAlert.makeAlert(context,  R.string.page_setup_change_edit, object: AlertDelegate{
                            override fun onPositiveClicked() {
                                data.isLock = !data.isLock
                            }
                        }).show()
                    }else{
                        data.isLock = !data.isLock
                    }
                    if(!data.isLock) keyBoard?.showKeyBoard(inputTitle)

                }.apply { disposables?.add(this) }


                btnRefresh.clicks().subscribe {
                    sync()
                    if(!data.isModify){
                        CustomToast.makeToast(context, R.string.notice_not_modify, Toast.LENGTH_SHORT).show()
                    }else{
                        data.sync()
                        viewModel.updateServer(data)
                        CustomToast.makeToast(context, R.string.notice_modify, Toast.LENGTH_SHORT).show()
                        val handler = Handler()
                        handler.postDelayed({ recyclerView.smoothScrollToPosition(data.idx) }, 500)
                    }
                }.apply { disposables?.add(this) }


                btnDelete.clicks().subscribe {
                    if( data.id == finalServerID ){
                        CustomToast.makeToast(context, R.string.page_setup_delete_disable, Toast.LENGTH_SHORT).show()
                        return@subscribe
                    }
                    CustomAlert.makeAlert(context,  R.string.notice_delete, object: AlertDelegate{
                        override fun onPositiveClicked() {
                            if(currentEditingData === currentData) currentEditingData = null
                            viewModel.deleteServer(data)
                        }
                    }).show()
                }.apply { disposables?.add(this) }
            }
        }

        override fun onDetached() {
            keyBoard?.removeEditTexts(arrayListOf(inputServer, inputID, inputTitle, inputPW))
            sync()
        }
        override fun setData(data: Any?, idx:Int){
            super.setData(data, idx)
            currentData = data as ServerDatabaseManager.Row
        }


    }
}