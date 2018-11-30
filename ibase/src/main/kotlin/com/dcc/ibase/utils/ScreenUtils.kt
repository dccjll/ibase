package com.dcc.ibase.utils

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

/**
 * 定禅天 净琉璃
 * 2018-11-21 13:33:52 Wednesday
 * 描述：屏幕工具
 */
object ScreenUtils {

    /**
     * 获取屏幕的宽度（单位：px）
     */
    val screenWidth: Int
        get() {
            val wm = AppUtils.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            wm.defaultDisplay.getRealSize(point)
            return point.x
        }

    /**
     * 获取屏幕的高度（单位：px）
     */
    val screenHeight: Int
        get() {
            val wm = AppUtils.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            wm.defaultDisplay.getRealSize(point)
            return point.y
        }
}
