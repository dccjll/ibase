package com.dcc.ibase.utils

import android.content.pm.PackageManager

/**
 * 定禅天 净琉璃
 * 2018-11-21 10:45:50 星期三
 * 描述：闪光灯工具
 */
object FlashlightUtils {

    /**
     * 检测手机是否支持闪光灯
     * true 支持 false 不支持
     */
    fun supportFlashlight(): Boolean {
        return AppUtils.app.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) ?: false
    }
}
