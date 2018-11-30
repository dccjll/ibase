package com.dcc.ibase.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Uri
import android.telephony.TelephonyManager
import android.util.Log
import java.lang.reflect.InvocationTargetException

/**
 * 定禅天 净琉璃
 * 2018-11-21 10:45:50 星期三
 * 描述：App全局工具
 */
class AppUtils private constructor() {

    class ContentProvider4SubUtil : ContentProvider() {

        override fun onCreate(): Boolean {
            Log.i("AppUtils", "ContentProvider4SubUtil onCreate")
            AppUtils.init(context)
            return true
        }

        override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
            return null
        }

        override fun getType(uri: Uri): String? {
            return null
        }

        override fun insert(uri: Uri, values: ContentValues?): Uri? {
            return null
        }

        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
            return 0
        }

        override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
            return 0
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var sApplication: Application? = null
        private const val SEMICOLON = ";"
        private const val SourceType = "Android"

        private fun init(context: Context?) {
            if (context == null) {
                init(applicationByReflect)
                return
            }
            init(context.applicationContext as Application)
        }

        private fun init(app: Application?) {
            if (sApplication == null) {
                if (app == null) {
                    AppUtils.sApplication = applicationByReflect
                } else {
                    AppUtils.sApplication = app
                }
            }
        }

        /**
         * 获取App全局上下文
         * @return App全局上下文
         */
        val app: Application
            get() {
                return sApplication ?: applicationByReflect ?: throw Exception("app init fail")
            }

        private val applicationByReflect: Application?
            get() {
                try {
                    @SuppressLint("PrivateApi")
                    val activityThread = Class.forName("android.app.ActivityThread")
                    val at = activityThread.getMethod("currentActivityThread").invoke(null)
                    val app = activityThread.getMethod("getApplication").invoke(at)
                            ?: throw NullPointerException("u should init first")
                    init(app as Application)
                    return sApplication
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }

                throw NullPointerException("u should init first")
            }

        /**
         * 调用系统分享
         */
        fun shareToOtherApp(context: Context, title: String, content: String, dialogTitle: String) {
            val intentItem = Intent(Intent.ACTION_SEND)
            intentItem.type = "text/plain"
            intentItem.putExtra(Intent.EXTRA_SUBJECT, title)
            intentItem.putExtra(Intent.EXTRA_TEXT, content)
            intentItem.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(Intent.createChooser(intentItem, dialogTitle))
        }

        /**
         * need < uses-permission android:name =“android.permission.GET_TASKS” />
         * 判断是否前台运行
         */
        fun isRunningForeground(context: Context): Boolean {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val taskList = am.getRunningTasks(1)
            if (taskList != null && !taskList.isEmpty()) {
                val componentName = taskList[0].topActivity
                if (componentName != null && componentName.packageName == context.packageName) {
                    return true
                }
            }
            return false
        }

        /**
         * 获取app包名
         */
        fun getAppPackageName(context: Context): String {
            return app.packageName
        }

        /**
         * 获取App包 信息版本号
         *
         * @param context
         * @return
         */
        fun getPackageInfo(context: Context): PackageInfo? {
            val packageManager = context.packageManager
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                
            }

            return packageInfo
        }

        /**
         * 判断APK包是否已经安装
         *
         * @param context     上下文，一般为Activity
         * @param packageName 包名
         * @return 包存在则返回true，否则返回false
         */
        fun isPackageExists(context: Context, packageName: String?): Boolean {
            if (null == packageName || "" == packageName) {
                throw IllegalArgumentException("Package name cannot be null or empty !")
            }
            try {
                val info = context.packageManager
                        .getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES)
                return null != info
            } catch (e: PackageManager.NameNotFoundException) {
                return false
            }

        }

        /**
         * @return 当前程序的版本名称
         */
        fun getVersionName(context: Context): String {
            var version: String
            try {
                val pm = context.packageManager
                val packageInfo = pm.getPackageInfo(context.packageName, 0)
                version = packageInfo.versionName
            } catch (e: Exception) {
                e.printStackTrace()
                
                version = ""
            }

            return version
        }

        /**
         * 方法: getVersionCode
         * 描述: 获取客户端版本号
         *
         * @return int    版本号
         */
        fun getVersionCode(context: Context): Int {
            var versionCode: Int
            try {
                val pm = context.packageManager
                val packageInfo = pm.getPackageInfo(context.packageName, 0)
                versionCode = packageInfo.versionCode
            } catch (e: Exception) {
                e.printStackTrace()
                
                versionCode = 999
            }

            return versionCode
        }

        /**
         * 获取进程名字
         *
         * @param cxt
         * @param pid
         * @return
         */
        fun getProcessName(cxt: Context, pid: Int): String? {
            val am = cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = am.runningAppProcesses ?: return null
            for (procInfo in runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName
                }
            }
            return null
        }

        /**
         * 获取userAgent
         *
         * @param context 上下文信息
         * @return 系统相关的信息
         */
        fun getUserAgent(context: Context, appId: String): String {
            val userAgent = StringBuffer()
            // ==============================================================
            // User-Agent
            // 格式：
            // 应用名称;应用版本;平台;OS版本;OS版本名称;厂商;机型;分辨率(宽*高);安装渠道;网络;
            // 示例：
            // HET;2.2.0;Android;4.2.2;N7100XXUEMI6BYTuifei;samsung;GT-I9300;480*800;360;WIFI;
            userAgent.append(appId)// 应用名称
            userAgent.append(SEMICOLON)
            userAgent.append(AppUtils.getVersionName(context)) // App版本
            userAgent.append(SEMICOLON)
            userAgent.append(SourceType)// 平台
            userAgent.append(SEMICOLON)
            userAgent.append(AppUtils.getOSVersionName()) // OS版本
            userAgent.append(SEMICOLON)
            userAgent.append(AppUtils.getOSVersionDisplayName()) // OS显示版本
            userAgent.append(SEMICOLON)
            userAgent.append(AppUtils.getBrandName()) // 品牌厂商
            userAgent.append(SEMICOLON)
            userAgent.append(AppUtils.getModelName()) // 设备
            userAgent.append(SEMICOLON)
            userAgent.append("${ScreenUtils.screenWidth}*${ScreenUtils.screenHeight}") // 分辨率
            userAgent.append(SEMICOLON)
            userAgent.append(AppUtils.getImei(context)) // IMEI
            userAgent.append(SEMICOLON)
            userAgent.append(AppUtils.getNetType(context)) // 网络类型
            userAgent.append(SEMICOLON)
            return userAgent.toString()
        }

        @SuppressLint("MissingPermission", "HardwareIds")
        private fun getImei(context: Context): String {
            return (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
                    .deviceId
        }

        /**
         * 获取渠道，用于打包
         *
         * @param context
         * @param metaName
         * @return
         */
        fun getAppSource(context: Context, metaName: String): String? {
            var result: String? = null
            try {
                val appInfo = context.packageManager
                        .getApplicationInfo(context.packageName,
                                PackageManager.GET_META_DATA)
                if (appInfo.metaData != null) {
                    result = appInfo.metaData.getString(metaName)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                
            }

            return result
        }

        /**
         * 获取网络类型
         *
         * @param context
         * @return
         */
        fun getNetType(context: Context): String {
            val connectionManager = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectionManager.activeNetworkInfo
            // networkInfo.getDetailedState();//获取详细状态。
            // networkInfo.getExtraInfo();//获取附加信息。
            // networkInfo.getReason();//获取连接失败的原因。
            // networkInfo.getType();//获取网络类型(一般为移动或Wi-Fi)。
            // networkInfo.getTypeName();//获取网络类型名称(一般取值“WIFI”或“MOBILE”)。
            // networkInfo.isAvailable();//判断该网络是否可用。
            // networkInfo.isConnected();//判断是否已经连接。
            // networkInfo.isConnectedOrConnecting();//：判断是否已经连接或正在连接。
            // networkInfo.isFailover();//：判断是否连接失败。
            // networkInfo.isRoaming();//：判断是否漫游
            return networkInfo.typeName
        }

        /**
         * 获取设备制造商名称.
         *
         * @return 设备制造商名称
         */
        fun getManufacturerName(): String {
            return android.os.Build.MANUFACTURER
        }

        /**
         * 获取设备名称.
         *
         * @return 设备名称
         */
        fun getModelName(): String {
            return android.os.Build.MODEL
        }

        /**
         * 获取产品名称.
         *
         * @return 产品名称
         */
        fun getProductName(): String {
            return android.os.Build.PRODUCT
        }

        /**
         * 获取品牌名称.
         *
         * @return 品牌名称
         */
        fun getBrandName(): String {
            return android.os.Build.BRAND
        }

        /**
         * 获取操作系统版本号.
         *
         * @return 操作系统版本号
         */
        fun getOSVersionCode(): Int {
            return android.os.Build.VERSION.SDK_INT
        }

        /**
         * 获取操作系统版本名.
         *
         * @return 操作系统版本名
         */
        fun getOSVersionName(): String {
            return android.os.Build.VERSION.RELEASE
        }

        /**
         * 获取操作系统版本显示名.
         *
         * @return 操作系统版本显示名
         */
        fun getOSVersionDisplayName(): String {
            return android.os.Build.DISPLAY
        }

        /**
         * 获取主机地址.
         *
         * @return 主机地址
         */
        fun getHost(): String {
            return android.os.Build.HOST
        }
    }
}