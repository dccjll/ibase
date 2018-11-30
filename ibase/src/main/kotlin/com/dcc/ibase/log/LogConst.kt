package com.dcc.ibase.log

/**
 * 定禅天 净琉璃
 * 2018-11-23 09:39:10 Friday
 * 描述：日志常量
 */
object LogConst {
    /**
     * 日志文件名的默认日期格式
     */
    const val logFileNameDateFormatDefault = "yyyyMMdd"

    /**
     * 每一行输出日志的默认时间格式
     */
    const val logConsoleDateFormatDefault = "yyyy-MM-dd HH:mm:ss SSS"

    /**
     * 日志文件名的默认标签
     */
    const val logFileNameTagDefault = "_log_"

    /**
     * 日志文件名的默认扩展名
     */
    const val logFileNameExtendNameDefault = ".txt"

    /**
     * 日志文件名的默认扩展名前缀字符
     */
    const val logFileNameExtendNamePreSuffixCharDefault = "."

    /**
     * 日志保存的默认天数
     */
    const val logKeepDaysNumDefault = 7

    /**
     * 日志保存的最小天数
     */
    const val logKeepDaysNumMin = 1

    /**
     * 日志保存的最大天数
     */
    const val logKeepDaysNumMax = 15

    /**
     * 日志文件路径的默认标签
     */
    const val logPathTagDefault = "log"

    /**
     * 待上传的日志文件的名称格式
     */
    const val logFileNameFormatForUpload = "yyyyMMdd_HHmmss_SSS"

    /**
     * 待上传的日志文件的名称标签
     */
    const val logFileNameTagForUpload = "_upload_"

    /**
     * 待上传的日志文件的扩展名
     */
    const val logFileNameExtendNameForUpload = ".zip"
}