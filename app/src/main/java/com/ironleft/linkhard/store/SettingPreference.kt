package com.ironleft.linkhard.store

import android.content.Context

import com.ironleft.linkhard.PreferenceName
import com.lib.module.CachedPreference

class SettingPreference(context: Context) : CachedPreference(context, PreferenceName.SETTING) {
    companion object {
        private const val FINAL_SERVER_ID = "finalServerID"
    }

    fun putFinalServerID(id: Int) = put(FINAL_SERVER_ID, id)
    fun getFinalServerID(): Int = get(FINAL_SERVER_ID, -1) as Int

    //fun putViewGesture(bool: Boolean) = put(VIEW_GESTURE, bool)
    //fun getViewGesture(): Boolean = get(VIEW_GESTURE, false) as Boolean


}