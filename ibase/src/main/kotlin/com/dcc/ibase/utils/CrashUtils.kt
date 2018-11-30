package com.dcc.ibase.utils

import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import com.dcc.ibase.log.LogManager
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * 定禅天 净琉璃
 * 2018-11-21 10:45:50 星期三
 * 描述：崩溃工具
 */
object CrashUtils {
    /**
     * 崩溃监听器
     */
    interface OnCrashListener {
        fun onCrash(errorInfo: String?)
    }

    private const val TAG = "CrashUtils"

    private var versionName: String? = null
    private var versionCode: Int = 0

    private val CRASH_HEAD: String

    private val DEFAULT_UNCAUGHT_EXCEPTION_HANDLER: Thread.UncaughtExceptionHandler?
    private val UNCAUGHT_EXCEPTION_HANDLER: Thread.UncaughtExceptionHandler

    private var sOnCrashListener: OnCrashListener? = null

    //用来存储设备信息和异常信息
    private val infos = HashMap<String, String>()
    /**
     * 获取崩溃日志保存的文件夹路径
     */
    var errorLogPath = AppUtils.app.filesDir.toString() + "/error/"//错误日志在应用内的相对路径
    private set

    init {
        try {
            val pi = AppUtils.app
                    .packageManager
                    .getPackageInfo(AppUtils.app.packageName, 0)
            if (pi != null) {
                versionName = pi.versionName
                versionCode = pi.versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        CRASH_HEAD = "************* Crash Log Head ****************" +
                "\nDevice BleDeviceType: " + Build.MANUFACTURER +
                "\nDevice BRAND: " + Build.BRAND +
                "\nDevice Model       : " + Build.MODEL +
                "\nAndroid Version    : " + Build.VERSION.RELEASE +
                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                "\nApp VersionName    : " + versionName +
                "\nApp VersionCode    : " + versionCode +
                "\n************* Crash Log Head ****************\n\n"

        DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = Thread.getDefaultUncaughtExceptionHandler()

        UNCAUGHT_EXCEPTION_HANDLER = Thread.UncaughtExceptionHandler { t, e ->
            if (e == null) {
                if (DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null) {
                    DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(t, null)
                } else {
                    try {
                        Thread.sleep(2000)
                    } catch (e1: InterruptedException) {
                        LogManager.e(TAG, "error : $e1")
                    }

                    Process.killProcess(Process.myPid())
                    System.exit(1)
                }
                return@UncaughtExceptionHandler
            }
            val errorInfo = getThrowableErrorInfo(e)
            LogManager.e(TAG, "creash=======\n" + errorInfo!!)
            infos["appsoftversion"] = versionName ?: ""
            infos["phonename"] = Build.BRAND
            infos["phonetype"] = (Build.MODEL + ","
                    + Build.VERSION.SDK_INT)
            infos["time"] = SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault()).format(Date())
            saveCatchInfo2File(e)
            if (sOnCrashListener != null) {
                sOnCrashListener!!.onCrash(errorInfo)
            }
            DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(t, e)
        }
    }

    /**
     * 收集错误信息
     */
    private fun getThrowableErrorInfo(ex: Throwable?): String? {
        try {
            val sb = StringBuilder()
            val writer = StringWriter()
            val printWriter = PrintWriter(writer)
            ex!!.printStackTrace(printWriter)
            var cause: Throwable? = ex.cause
            while (cause != null) {
                cause.printStackTrace(printWriter)
                cause = cause.cause
            }
            printWriter.close()
            val result = writer.toString()
            sb.append(result)
            return CRASH_HEAD + "\n" + sb.toString()
        } catch (e: Exception) {
            LogManager.e(TAG, "an error occured while writing file...$e")
        }

        return null
    }

    /**
     * 保存错误信息到文件中，返回文件名称,便于将文件传送到服务器
     */
    private fun saveCatchInfo2File(ex: Throwable?) {
        try {
            val sb = StringBuilder()
            for ((key, value) in infos) {
                sb.append(key).append("=").append(value).append("\n")
            }

            val writer = StringWriter()
            val printWriter = PrintWriter(writer)
            ex!!.printStackTrace(printWriter)
            var cause: Throwable? = ex.cause
            while (cause != null) {
                cause.printStackTrace(printWriter)
                cause = cause.cause
            }
            printWriter.close()
            val result = writer.toString()
            sb.append(result)
            infos["bugcontent"] = sb.toString()
            val jsonObject = JSONObject(infos)
            val fileName = "errorlog_" + System.currentTimeMillis() + ".txt"
            FileUtils.writeFile(errorLogPath + fileName, jsonObject.toString(), false)
        } catch (e: Exception) {
            LogManager.e(TAG, "an error occured while writing file...$e")
        }

    }

    /**
     * 初始化，带监听器
     */
    @JvmOverloads
    fun init(onCrashListener: OnCrashListener? = null) {
        sOnCrashListener = onCrashListener
        Thread.setDefaultUncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER)
    }
}
