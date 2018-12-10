package com.dcc.ibase.log

import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.dcc.ibase.utils.AppUtils
import com.dcc.ibase.utils.FileUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * 定禅天 净琉璃
 * 2018-11-22 13:08:39 Thursday
 * 描述：日志配置
 */
class LogConfig(var consoleSwitch: Boolean? = true,//是否开启控制台日志打印 true 开启 false 关闭
                var fileSwitch: Boolean? = true,//是否开启日志写入文件 true 开启 false 关闭
                val storageType: StorageType = StorageType.APP,//日志存储类型
                var logFileRelativePath: String? = "",//日志文件存储的相对路径
                var logFileNamePreSuffix: String? = "",//日志文件名的前缀
                var logFileNameEndSuffix: String? = "",//日志文件名的后缀
                var logFileNameExtendName: String? = "",//日志文件名的扩展名
                var consoleLogTimeFormat: String? = ""//日志时间格式，即输出到控制台的每一行日志的开始时间格式
) {
    val sNoLogContent = "no log content"
    private val tagName = LogConfig::class.java.simpleName
    private var companyName: String = AppUtils.app.packageName.substring(AppUtils.app.packageName.lastIndexOf(".") + 1)

    lateinit var logName: String
        private set

    lateinit var logPath: String
        private set

    var logFileNameForUpload: String? = ""//待上传的日志文件的名称，一般是一个压缩包
        set(value) {
            if (TextUtils.isEmpty(value)) {
                val time = SimpleDateFormat(LogConst.logFileNameFormatForUpload, Locale.getDefault()).format(Date())
                field = "${getPreSuffix()}$companyName${LogConst.logFileNameTagForUpload}${Build.MANUFACTURER}_${Build.MODEL}_$time${getEndSuffix()}${LogConst.logFileNameExtendNameForUpload}"
            }
        }

    var keepDaysNum: Int? = LogConst.logKeepDaysNumDefault//日志保存的天数
        set(value) {
            when {
                value != null && value >= LogConst.logKeepDaysNumMin && value <= LogConst.logKeepDaysNumMax -> field = value
            }
        }

    init {
        updateLogParam()
        deleteExpiredFile()
    }

    /**
     * 日志存储类型<br>
     */
    enum class StorageType {
        /**
         * 应用内部
         */
        APP,

        /**
         * sd卡
         */
        SDCARD
    }

    /**
     * 更新日志参数
     */
    fun updateLogParam() {
        updateLogName(SimpleDateFormat(LogConst.logFileNameDateFormatDefault, Locale.getDefault()).format(Date()))
        updateLogPath()
        logFileNameForUpload = ""
        consoleLogTimeFormat = when {
            !TextUtils.isEmpty(consoleLogTimeFormat) -> consoleLogTimeFormat
            else -> SimpleDateFormat(LogConst.logConsoleDateFormatDefault, Locale.getDefault()).format(Date())
        }
    }

    /**
     * 更新日志名称
     */
    private fun updateLogName(dateTime: String?) {
        val preSuffix = getPreSuffix()
        val endSuffix = getEndSuffix()
        val tempSuffix = when {
            !TextUtils.isEmpty(logFileNameExtendName) -> when {
                logFileNameExtendName?.startsWith(LogConst.logFileNameExtendNamePreSuffixCharDefault) == true -> logFileNameExtendName
                else -> "${LogConst.logFileNameExtendNamePreSuffixCharDefault}$logFileNameExtendName"
            }
            else -> LogConst.logFileNameExtendNameDefault
        }
        logName = "$preSuffix$companyName${LogConst.logFileNameTagDefault}${Build.MANUFACTURER}_${Build.MODEL}_$dateTime$endSuffix$tempSuffix"
    }

    /**
     * 更新日志路径
     */
    private fun updateLogPath() {
        if (logFileRelativePath.isNullOrEmpty() || logFileRelativePath?.startsWith(File.separatorChar) == false) {
            logFileRelativePath = File.separator + AppUtils.app.packageName.substring(AppUtils.app.packageName.lastIndexOf(".") + 1) + File.separator + LogConst.logPathTagDefault
        }
        logPath = when (storageType) {
            StorageType.APP -> {
                AppUtils.app.filesDir.path + logFileRelativePath
            }
            StorageType.SDCARD -> Environment.getExternalStorageDirectory().path + logFileRelativePath
        }
    }

    /**
     * 获取前缀
     */
    private fun getPreSuffix() = if (!TextUtils.isEmpty(logFileNamePreSuffix)) "$logFileNamePreSuffix" else ""

    /**
     * 获取后缀
     */
    private fun getEndSuffix() = if (!TextUtils.isEmpty(logFileNameEndSuffix)) "$logFileNameEndSuffix" else ""

    /**
     * 删除过期的日志文件
     */
    private fun deleteExpiredFile() {
        val list = FileUtils.getFileNameList(logPath)
        val simpleDateFormat = SimpleDateFormat(LogConst.logFileNameDateFormatDefault, Locale.getDefault())
        for (fileName in list) {
            try {
                val lastWeekDateString = getExpiredDateString(keepDaysNum)
                val lastWeekDate = simpleDateFormat.parse(lastWeekDateString)
                val matcher = Pattern.compile("\\d{8}").matcher(fileName)
                if (!matcher.find()) {
                    continue
                }
                val fileDateString = matcher.group(0)
                val fileDate = simpleDateFormat.parse(fileDateString)
                if (fileDate.before(lastWeekDate)) {
                    val file = logPath + File.separator + fileName
                    Log.i(tagName, "已删除过期的日志文件:$file")
                    FileUtils.deleteFile(file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 获得过期的日期信息，返回20180920这样的数据
     * @param daysNum 回溯天数，不超过当月的天数
     */
    private fun getExpiredDateString(daysNum: Int? = 0): String {
        val date = Date()
        var year = Integer.parseInt(SimpleDateFormat("yyyy", Locale.getDefault()).format(date))
        var month = Integer.parseInt(SimpleDateFormat("MM", Locale.getDefault()).format(date))
        var day = Integer.parseInt(SimpleDateFormat("dd", Locale.getDefault()).format(date)) - (daysNum ?: 7 - 1)

        if (day < 1) {
            month -= 1
            if (month == 0) {
                year -= 1
                month = 12
            }
            if (month == 4 || month == 6 || month == 9 || month == 11) {
                day += 30
            } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                day += 31
            } else if (month == 2) {
                day = when {
                    year % 400 == 0 || year % 4 == 0 && year % 100 != 0 -> 29 + day
                    else -> 28 + day
                }
            }
        }
        val y = year.toString() + ""
        val m: String
        val d: String
        m = when {
            month < 10 -> "0$month"
            else -> month.toString() + ""
        }
        d = when {
            day < 10 -> "0$day"
            else -> day.toString() + ""
        }
        return y + m + d
    }

}