package com.dcc.ibase.utils

import android.content.Context
import android.net.wifi.*
import java.util.*

class WifiUtils(context: Context) {

    /**
     * 提供Wifi管理的各种主要API，主要包含wifi的扫描、建立连接、配置信息等
     */
    private val localWifiManager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    /**
     * WIFIConfiguration描述WIFI的链接信息，包括SSID、SSID隐藏、password等的设置
     */
    private var wifiConfigList: List<WifiConfiguration>? = null

    /**
     * 已经建立好网络链接的信息
     */
    private var wifiConnectedInfo: WifiInfo? = null

    /**
     * 得到扫描结果
     */
    //得到扫描结果
    val scanResults: List<ScanResult>
        get() = localWifiManager.scanResults

    /**
     * 得到连接的wifi名称
     */
    val connectedSSID: String?
        get() {
            getConnectedInfo()
            if (wifiConnectedInfo == null || wifiConnectedInfo!!.supplicantState != SupplicantState.COMPLETED) {
                return null
            }
            val ssid = wifiConnectedInfo!!.ssid
            return ssid.replace("\"", "")
        }

    /**
     * 检查WIFI是否已开启
     */
    fun checkState(): Int {
        return localWifiManager.wifiState
    }

    /**
     * 开启WIFI
     */
    fun openWifi() {
        if (!localWifiManager.isWifiEnabled) {
            localWifiManager.isWifiEnabled = true
        }
    }

    /**
     * 关闭WIFI
     */
    fun closeWifi() {
        if (localWifiManager.isWifiEnabled) {
            localWifiManager.isWifiEnabled = false
        }
    }

    /**
     * 扫描wifi
     */
    fun startWifi() {
        localWifiManager.startScan()
    }

    /**
     * 扫描结果转换为字符串
     */
    fun parseScanResultListToString(list: List<ScanResult>): List<String> {
        val strReturnList = ArrayList<String>()
        for (i in list.indices) {
            val strScan = list[i]
            val str = strScan.toString()
            strReturnList.add(str)
        }
        return strReturnList
    }

    /**
     * 得到Wifi配置好的信息
     */
    private fun getConfiguration() {
        wifiConfigList = localWifiManager.configuredNetworks
    }

    /**
     * 判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId
     */
    fun isConfiguration(SSID: String): Int {
        getConfiguration()
        for (i in wifiConfigList!!.indices) {
            if (wifiConfigList!![i].SSID == SSID) {//地址相同
                return wifiConfigList!![i].networkId
            }
        }
        return -1
    }

    /**
     * 添加指定WIFI的配置信息d
     */
    fun addWifiConfig(ssid: String, pwd: String): Int {
        val wifiCong = WifiConfiguration()
        wifiCong.SSID = "\"" + ssid + "\""//\"转义字符，代表"
        wifiCong.preSharedKey = "\"" + pwd + "\""//WPA-PSK密码
        wifiCong.hiddenSSID = false
        wifiCong.status = WifiConfiguration.Status.ENABLED
        return localWifiManager.addNetwork(wifiCong)
    }

    /**
     * 移除wifi
     */
    fun removeWifiConfig(ssid: String): Boolean {
        getConfiguration()
        for (i in wifiConfigList!!.indices) {
            val wifi = wifiConfigList!![i]
            if (wifi.SSID == "\"" + ssid + "\"") {
                return localWifiManager.removeNetwork(wifi.networkId)
            }
        }
        return false
    }

    /**
     * 连接指定Id的WIFI
     */
    fun connectWifi(wifiId: Int): Boolean {
        getConfiguration()
        for (i in wifiConfigList!!.indices) {
            val wifi = wifiConfigList!![i]
            if (wifi.networkId == wifiId) {
                return localWifiManager.enableNetwork(wifiId, true)
            }
        }
        return false
    }

    /**
     * 得到建立连接的wifi信息
     */
    private fun getConnectedInfo() {
        wifiConnectedInfo = localWifiManager.connectionInfo
    }

    companion object {

        /**
         * 获取wfii安全类型
         */
        fun getEncrypPasswordType(capabilities: String): Int {
            return if (capabilities.contains("WPA2") && capabilities.contains("CCMP")) {
                // sEncrypType = "AES";
                // sAuth = "WPA2";
                1
            } else if (capabilities.contains("WPA2") && capabilities.contains("TKIP")) {
                // sEncrypType = "TKIP";
                // sAuth = "WPA2";
                2
            } else if (capabilities.contains("WPA") && capabilities.contains("TKIP")) {
                // EncrypType = "TKIP";
                // sAuth = "WPA";
                2
            } else if (capabilities.contains("WPA") && capabilities.contains("CCMP")) {
                // sEncrypType = "AES";
                // sAuth = "WPA";
                1
            } else if (capabilities.contains("WEP")) {
                3
            } else {
                // sEncrypType = "NONE";
                // sAuth = "OPEN";
                0
            }
        }
    }
}
