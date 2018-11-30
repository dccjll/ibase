package com.dcc.ibase.utils

import android.app.Activity
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.*
import android.widget.LinearLayout
import com.dcc.ibase.utils.SystemUtils.SYS_FLYME
import com.dcc.ibase.utils.SystemUtils.SYS_MIUI

/**
 * 定禅天 净琉璃
 * 2018-11-26 15:11:09 Monday
 * 描述：状态栏工具
 */
object StatusBarUtils {

    /**
     * 获得状态栏的高度
     */
    fun getStatusBarHeight(): Int {
        var statusHeight = 0
        try {
            val clazz = Class.forName("com.android.internal.R\$dimen")
            val `object` = clazz.newInstance()
            val height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(`object`).toString())
            statusHeight = AppUtils.app.resources.getDimensionPixelSize(height)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (statusHeight != 0) {
            return statusHeight
        }
        val resourceId = AppUtils.app.resources
                .getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusHeight = AppUtils.app.resources.getDimensionPixelSize(resourceId)
        }
        return statusHeight
    }

    /**
     * 设置状态栏的背景颜色1
     */
    fun setStatusBarColor(activity: Activity, color: Int) {
        setStatusBarColor(activity.window, color)
    }

    /**
     * 设置状态栏的背景颜色2
     */
    fun setStatusBarColor(window: Window, color: Int) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = color //设置状态栏颜色

                // 设置系统状态栏处于可见状态
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                // 让view不根据系统窗口来调整自己的布局
                val mContentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
                val mChildView = mContentView.getChildAt(0)
                if (mChildView != null) {
                    ViewCompat.setFitsSystemWindows(mChildView, false)
                    ViewCompat.requestApplyInsets(mChildView)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                val decorViewGroup = window.decorView as ViewGroup
                val statusBarView = View(window.context)
                val statusBarHeight = StatusBarUtils.getStatusBarHeight()
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statusBarHeight)
                params.gravity = Gravity.TOP
                statusBarView.layoutParams = params
                statusBarView.setBackgroundColor(color)
                decorViewGroup.addView(statusBarView)
            }
        }
    }

    /**
     * 设置状态栏的字体颜色1，dark表示是否为深色
     */
    fun setStatusBarFontColor(activity: Activity, color: Int, dark: Boolean) {
        setStatusBarFontColor(activity.window, color, dark)
    }

    /**
     * 设置状态栏的字体颜色2，dark表示是否为深色
     */
    fun setStatusBarFontColor(window: Window, color: Int, dark: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (dark) {
                window.statusBarColor = ContextCompat.getColor(AppUtils.app, android.R.color.white)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.statusBarColor = ContextCompat.getColor(AppUtils.app, color)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        } else if (SystemUtils.system == SYS_MIUI) {
            setStatusBarLightModeMinu(window, dark)
        } else if (SystemUtils.system == SYS_FLYME) {
            setStatusBarLightModeFlyme(window, dark)
        }
    }

    /**
     * 魅族设置状态栏亮模式
     */
    private fun setStatusBarLightModeFlyme(window: Window?, dark: Boolean): Boolean {
        var result = false
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                        .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (dark) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                window.attributes = lp
                result = true
            } catch (e: Exception) {

            }

        }
        return result
    }

    /**
     * 小米设置状态栏量模式
     */
    private fun setStatusBarLightModeMinu(window: Window?, dark: Boolean): Boolean {
        var result = false
        if (window != null) {
            val clazz = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag)//清除黑色字体
                }
                result = true
            } catch (e: Exception) {

            }

        }
        return result
    }
}