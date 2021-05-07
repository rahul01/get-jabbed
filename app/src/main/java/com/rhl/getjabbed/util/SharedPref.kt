package com.rhl.getjabbed.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences


object SharedPref {
    private var mSharedPref: SharedPreferences? = null
    const val MOBILE = "MOBILE"
    const val STATE = "STATE"
    const val DIST = "DIST"
    const val TOKEN = "TOKEN"
//    const val SELECTION = "SELECTION"
    const val PINFILTER = "PINFILTER"

    fun init(context: Context) {
        if (mSharedPref == null) mSharedPref =
            context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    }

    fun getString(key: String, defValue: String = ""): String {
        return mSharedPref!!.getString(key, defValue)!!
    }

    fun setValue(key: String?, value: String?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return mSharedPref!!.getBoolean(key, defValue)
    }

    fun setValue(key: String?, value: Boolean) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    fun getInt(key: String?, defValue: Int): Int {
        return mSharedPref!!.getInt(key, defValue)
    }

    fun setValue(key: String?, value: Int?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putInt(key, value!!).apply()
    }
}