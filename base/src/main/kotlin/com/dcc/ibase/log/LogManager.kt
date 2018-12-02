package com.dcc.ibase.log

import android.text.TextUtils
import android.util.Log
import com.dcc.ibase.utils.FileUtils
import com.dcc.ibase.utils.ZipUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 定禅天 净琉璃
 * 2018-11-22 12:43:29 Thursday
 * 描述：日志管理
 */
class LogManager {

    companion object {
        private const val tagName = "LogManager"

        var logConfig = LogConfig()

        /**
         * 获取待上传的日志文件
         */
        val logFileForUpload: File
            get() {
                val file = File(logConfig.logPath + File.separator + logConfig.logFileNameForUpload)
                val files = ArrayList<File>()
                val fileNameList = FileUtils.getFileNameList(logConfig.logPath)
                for (name in fileNameList) {
                    files.add(File(logConfig.logPath + File.separator + name))
                }
                ZipUtils.startZip(files, file, object : ZipUtils.ZipListener {
                    override fun zipProgress(zipProgress: Int) {
                        Log.i(tagName, "zipProgress:$zipProgress")
                    }
                })
                return file
            }

        /**
         * 根据tag, msg和等级，输出日志
         */
        private fun log(tag: String, msg: String, level: Char) {
            logConfig.updateLogParam()
            if (logConfig.consoleSwitch == true && !TextUtils.isEmpty(msg)) {
                var i = 0
                while (i < msg.length) {
                    val str = msg.substring(i, if (i + 2000 > msg.length) msg.length else i + 2000)
                    when (level) {
                        'e' -> Log.e(tag, str)
                        'w' -> Log.w(tag, str)
                        'd' -> Log.d(tag, str)
                        'i' -> Log.i(tag, str)
                        else -> Log.v(tag, str)
                    }
                    i += 2000
                }
            }
            if (logConfig.fileSwitch == true) {
                writeLogtoFile(level.toString(), tag, msg)
            }
        }

        /**
         * 日志文件写入日志
         */
        private fun writeLogtoFile(mylogtype: String, tag: String, text: String?) {
            val nowTime = Date()
            val msg = SimpleDateFormat(logConfig.consoleLogTimeFormat, Locale.getDefault()).format(nowTime) + "    " + mylogtype + "    " + tag + "    " + text + "\n"
            try {
                FileUtils.writeFile(logConfig.logPath + File.separator + logConfig.logName, msg, true)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        fun e(tag: String, text: Any?) {
            log(tag, text?.toString() ?: logConfig.sNoLogContent, 'e')
        }

        fun w(tag: String, text: Any?) {
            log(tag, text?.toString() ?: logConfig.sNoLogContent, 'w')
        }

        fun d(tag: String, text: Any?) {
            log(tag, text?.toString() ?: logConfig.sNoLogContent, 'd')
        }

        fun i(tag: String, text: Any?) {
            log(tag, text?.toString() ?: logConfig.sNoLogContent, 'i')
        }

        fun v(tag: String, text: Any?) {
            log(tag, text?.toString() ?: logConfig.sNoLogContent, 'v')
        }

        fun e(text: Any?) {
            e(tagName, text ?: "")
        }

        fun w(text: Any?) {
            w(tagName, text ?: "")
        }

        fun d(text: Any?) {
            d(tagName, text ?: "")
        }

        fun i(text: Any?) {
            i(tagName, text ?: "")
        }

        fun v(text: Any?) {
            v(tagName, text ?: "")
        }
    }
}  