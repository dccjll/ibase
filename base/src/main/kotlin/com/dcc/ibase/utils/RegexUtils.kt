package com.dcc.ibase.utils

import android.text.TextUtils
import java.util.regex.Pattern

/**
 * 定禅天 净琉璃
 * 2018-11-21 10:45:50 星期三
 * 描述：正则验证工具
 */
object RegexUtils {
    /**
     * 正则通用验证
     */
    private fun checkRegex(res: String?, regex: String): Boolean {
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(res)
        return matcher.matches()
    }

    /**
     * 验证设备mac地址
     */
    fun checkAddress(address: String?): Boolean {
        if (address == null || TextUtils.isEmpty(address)) {
            return false
        }
        if (address.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size != 6) {
            return false
        }
        val macChars = address.replace(":", "").toCharArray()
        val regexChars = "0123456789ABCDEFabcdef"
        for (c in macChars) {
            if (!regexChars.contains(c + "")) {
                return false
            }
        }
        return true
    }

    /**
     * 验证手机号码
     */
    fun checkMobile(res: String?): Boolean {
        return checkRegex(res, "^[0-9]{11}$")
    }

    /**
     * 是否是普通数字密码，同时匹配连号、顺增顺降
     */
    fun checkSimpleNumber(source: String): Boolean {
        return checkContinueString(source, source.length) || checkAscDesc(source)
    }

    /**
     * 验证连号
     */
    fun checkContinueString(source: String, continueSize: Int): Boolean {
        if (TextUtils.isEmpty(source) || continueSize <= 1) {
            return false
        }
        val pattern = Pattern.compile("([\\d])\\1{" + (continueSize - 1) + "}")
        val matcher = pattern.matcher(source)
        if (matcher.matches()) {
            return true
        }
        return false
    }

    /**
     * 验证顺增或顺降
     */
    fun checkAscDesc(source: String): Boolean {
        if (TextUtils.isEmpty(source)) {
            return false
        }
        val pattern = Pattern.compile("(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){5}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){5})\\d")
        val matcher = pattern.matcher(source)
        if (matcher.matches()) {
            return true
        }
        return false
    }
}
