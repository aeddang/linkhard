package com.lib.module

import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText




class SoftKeyboard(val inputMethodManager:InputMethodManager?, private val view:ViewGroup? = null) : View.OnFocusChangeListener {

    private val editTextList = ArrayList<EditText>()
    private var isKeyboardShow = false
    private var focusView:View? = null
    init {
        initEditTexts(view)
    }
    private fun initEditTexts(viewgroup:ViewGroup?) {
        viewgroup ?: return
        val childCount = viewgroup.childCount
        for (i in 0 until childCount) {
            val v: View = viewgroup.getChildAt(i)
            if (v is ViewGroup) {
                initEditTexts(v)
            }
            if (v is EditText) {
                v.onFocusChangeListener = this
                v.isCursorVisible = false
                editTextList.add(v)
            }
        }
    }

    fun destroy(){
        editTextList.clear()
        focusView = null
    }

    fun addEditTexts(list:ArrayList<EditText>){
        list.forEach {v->
            v.onFocusChangeListener = this
            v.isCursorVisible = false
            editTextList.add(v)
        }
    }

    fun removeEditTexts(list:ArrayList<EditText>){
        list.forEach {v->
            v.onFocusChangeListener = null
            editTextList.remove(v)
        }
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        focusView = v
        if (hasFocus && !isKeyboardShow) {
            isKeyboardShow = true
        }
    }

    fun showKeyBoard(){
        isKeyboardShow = true
        inputMethodManager?.showSoftInput(focusView, 0)
    }
    fun hideKeyBoard(){
        isKeyboardShow = false
        inputMethodManager?.hideSoftInputFromWindow(focusView?.windowToken,0)
    }

    fun showKeyBoard(view:View,flag:Int = 0){
        view.requestFocus()
        isKeyboardShow = true
        focusView = view
        inputMethodManager?.showSoftInput(view, flag)
    }
    fun hideKeyBoard(view:View,flag:Int = 0){
        isKeyboardShow = false
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken,flag)
    }

}