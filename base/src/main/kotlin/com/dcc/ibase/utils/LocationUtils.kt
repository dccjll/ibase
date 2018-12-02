package com.dcc.ibase.utils

import android.content.Context
import android.location.LocationManager

/**
 * 定禅天 净琉璃
 * 2018-11-21 10:45:50 星期三
 * 描述：定位相关工具
 */
object LocationUtils {

    /**
     * 判断定位功能是否已打开
     */
    val locationEnable: Boolean
        get() {
            val lm = AppUtils.app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
}
