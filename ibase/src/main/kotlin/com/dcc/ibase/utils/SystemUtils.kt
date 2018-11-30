package com.dcc.ibase.utils

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import java.io.File
import java.io.FileInputStream
import java.util.*

/**
 * 定禅天 净琉璃
 * 2018-11-21 13:33:52 Wednesday
 * 描述：系统工具
 */
object SystemUtils {

    const val SYS_EMUI = "sys_emui"
    const val SYS_MIUI = "sys_miui"
    const val SYS_FLYME = "sys_flyme"
    private const val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
    private const val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private const val KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage"
    private const val KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level"
    private const val KEY_EMUI_VERSION = "ro.build.version.emui"
    private const val KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion"

    /**
     * 获取系统标记，目前支持小米、华为、魅族
     */
    //小米
    //华为
    //魅族
    val system: String
        get() {
            var sys = ""
            try {
                val prop = Properties()
                prop.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
                if (prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                        || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                        || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null) {
                    sys = SYS_MIUI
                } else if (prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
                        || prop.getProperty(KEY_EMUI_VERSION, null) != null
                        || prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null) {
                    sys = SYS_EMUI
                } else if (getSystemProperty("ro.build.display.id", "").toLowerCase().contains("flyme")) {
                    sys = SYS_FLYME
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                return sys
            }

            return sys
        }

    /**
     * 显示虚拟键盘
     */
    fun showKeyboard(view: View?) {
        view?.postDelayed({
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }, 50)
    }

    /**
     * 隐藏虚拟键盘
     */
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }

    /**
     * 改变背景透明度
     */
    fun changeAlpha(context: Context, alpha: Float) {
        val params = (context as Activity).window.attributes
        params.alpha = alpha
        context.window.attributes = params
    }

    /**
     * 获取系统属性
     */
    private fun getSystemProperty(key: String, defaultValue: String): String {
        try {
            val clz = Class.forName("android.os.SystemProperties")
            val get = clz.getMethod("get", String::class.java, String::class.java)
            return get.invoke(clz, key, defaultValue) as String
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return defaultValue
    }

    /**
     * 设置全屏
     */
    fun setFullScreen(activity: Activity) {
        activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * 取消全屏
     */
    fun cancelFullScreen(activity: Activity) {
        activity.window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

}
