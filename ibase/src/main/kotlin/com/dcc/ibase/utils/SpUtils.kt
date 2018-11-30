package com.dcc.ibase.utils

import android.content.Context
import java.util.*

/**
 * 定禅天 净琉璃
 * 2018-11-21 13:33:52 Wednesday
 * 描述：SharePreference工具
 */
class SpUtils(private val spName: String) {

    fun putString(context: Context?, key: String, value: String?): Boolean {
        val settings = context?.getSharedPreferences(
                spName, Context.MODE_PRIVATE)
        val editor = settings?.edit()
        editor?.putString(key, value)
        return editor?.commit() ?: false
    }

    @JvmOverloads
    fun getString(context: Context?, key: String, defaultValue: String? = ""): String {
        val settings = context?.getSharedPreferences(
                spName, Context.MODE_PRIVATE)
        return settings?.getString(key, defaultValue) ?: ""
    }

    fun putInt(context: Context?, key: String, value: Int): Boolean {
        val settings = context?.getSharedPreferences(
                spName, Context.MODE_PRIVATE)
        val editor = settings?.edit()
        editor?.putInt(key, value)
        return editor?.commit() ?: false
    }

    @JvmOverloads
    fun getInt(context: Context?, key: String, defaultValue: Int = -1): Int {
        val settings = context?.getSharedPreferences(
                spName, Context.MODE_PRIVATE)
        return settings?.getInt(key, defaultValue) ?: -1
    }

    fun putLong(context: Context?, key: String, value: Long): Boolean {
        val settings = context?.getSharedPreferences(
                spName, Context.MODE_PRIVATE)
        val editor = settings?.edit()
        editor?.putLong(key, value)
        return editor?.commit() ?: false
    }

    fun getLong(context: Context?, key: String, defaultValue: Long? = -1L): Long {
        val settings = context?.getSharedPreferences(
                spName, Context.MODE_PRIVATE)
        return settings?.getLong(key, defaultValue ?: -1L) ?: -1L
    }

    fun putFloat(context: Context?, key: String, value: Float): Boolean {
        val settings = context?.getSharedPreferences(
                spName, Context.MODE_PRIVATE)
        val editor = settings?.edit()
        editor?.putFloat(key, value)
        return editor?.commit() ?: false
    }

    fun getFloat(context: Context?, key: String, defaultValue: Float? = 1.0f): Float {
        val settings = context?.getSharedPreferences(
                spName, Context.MODE_PRIVATE)
        return settings?.getFloat(key, defaultValue ?: 1.0f) ?: 1.0f
    }

    fun putBoolean(context: Context?, key: String, value: Boolean): Boolean {
        val settings = context?.getSharedPreferences(
                spName, Context.MODE_PRIVATE)
        val editor = settings?.edit()
        editor?.putBoolean(key, value)
        return editor?.commit() ?: false
    }

    @JvmOverloads
    fun getBoolean(context: Context?, key: String, defaultValue: Boolean? = false): Boolean {
        val settings = context?.getSharedPreferences(
                spName, Context.MODE_PRIVATE)
        return settings?.getBoolean(key, defaultValue ?: false) ?: false
    }

    fun remove(context: Context?, key: String) {
        val settings = context?.getSharedPreferences(spName, Context.MODE_PRIVATE)
        if (settings?.contains(key) == true) {
            val editor = settings.edit()
            editor.remove(key)
            editor.apply()
        }
    }

    fun clear(context: Context?) {
        val settings = context?.getSharedPreferences(spName, Context.MODE_PRIVATE)
        val editor = settings?.edit()
        editor?.clear()
        editor?.apply()
    }

    fun clearExcept(context: Context?, names: Array<String>) {
        val valueList = ArrayList<String>()
        for (name in names) {
            val value = getString(context, name, "")
            if (value != null) {
                valueList.add(value)
            }
        }
        clear(context)
        for (i in names.indices) {
            putString(context, names[i], valueList[i])
        }
    }
}
