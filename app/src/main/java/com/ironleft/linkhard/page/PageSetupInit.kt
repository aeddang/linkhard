package com.ironleft.linkhard.page

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast

import androidx.lifecycle.ViewModelProviders
import com.ironleft.linkhard.PageID
import com.ironleft.linkhard.PageParam
import com.ironleft.linkhard.R
import com.ironleft.linkhard.page.viewmodel.ViewModelServer
import com.ironleft.linkhard.store.ServerDatabaseManager
import com.jakewharton.rxbinding3.view.clicks
import com.lib.page.PagePresenter

import com.skeleton.module.ViewModelFactory
import com.skeleton.rx.RxPageFragment
import com.skeleton.view.alert.CustomToast
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.page_setup_init.*
import javax.inject.Inject

class PageSetupInit : RxPageFragment() {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId() = R.layout.page_setup_init

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ViewModelServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ViewModelServer::class.java)
    }


    override fun onCreatedView() {
        super.onCreatedView()
        inputTitle.isEnabled = true
        inputServer.isEnabled = true
        inputID.isEnabled = true
        inputPW.isEnabled = true
    }

    override fun onSubscribe() {
        super.onSubscribe()

        btnInit.clicks().subscribe {

            val data = ServerDatabaseManager.Row()
            data.title = inputTitle.text.toString()
            data.path = inputServer.text.toString()
            if(data.path == "") {
                setFocus(inputServer)
                return@subscribe
            }
            data.userID = inputID.text.toString()
            if(data.userID == "") {
                setFocus(inputID)
                return@subscribe
            }
            data.userPW = inputPW.text.toString()
            if(data.userPW == "") {
                setFocus(inputPW)
                return@subscribe
            }

            viewModel.addServer(data)

        }.apply { disposables.add(this) }

        viewModel.serverObservable.subscribe {
            if(viewModel.servers.isEmpty()) return@subscribe
            val data =  viewModel.servers[0]
            viewModel.repo.setting.putFinalServerID(data.id)
            val param = HashMap<String, Any?>()
            param[PageParam.SERVER_DATA] = data
            PagePresenter.getInstance<PageID>().pageChange(PageID.DIR, param)

        }.apply { disposables.add(this) }

    }

    private fun setFocus(input:EditText){
        context?.let {

            CustomToast.makeToast(it, R.string.notice_not_input, Toast.LENGTH_SHORT).show()
            input.requestFocus()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
    }


}