package com.dcc.ibase.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * 定禅天 净琉璃
 * 2018-11-29 10:52:17 Thursday
 * 描述：网络工具
 */
object NetworkUtils {

    /**
     * 获取ConnectivityManager
     */
    private fun getConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    /**
     * 判断网络连接是否有效（此时可传输数据）。
     *
     * @return boolean 不管wifi，还是mobile net，只有当前在连接状态（可有效传输数据）才返回true,反之false。
     */
    fun isConnected(context: Context): Boolean {
        val net = getConnectivityManager(context).activeNetworkInfo
        return net?.isConnected == true
    }
}