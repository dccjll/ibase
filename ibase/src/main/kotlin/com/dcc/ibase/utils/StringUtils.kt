package com.dcc.ibase.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import java.util.*

/**
 * 定禅天 净琉璃
 * 2018-11-21 13:33:52 Wednesday
 * 描述：字符串工具
 */
object StringUtils {

    /**
     * 将一个用分隔符连接的字符串以分隔符为分隔反转
     * @param string    原字符串
     * @param toUpperCase   强制转换为大写
     * @return  转换结果
     */
    fun reserveString(string: String, separator: String, toUpperCase: Boolean): String {
        val starr = string.split(separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val sBuffer = StringBuilder()
        for (i in starr.indices.reversed()) {
            sBuffer.append(starr[i]).append(separator)
        }
        var reserveString = sBuffer.toString().substring(0, sBuffer.toString().lastIndexOf(separator))
        if (toUpperCase) {
            reserveString = reserveString.toUpperCase(Locale.getDefault())
        }
        return reserveString
    }

    /**
     * 新建一个可以添加文字大小属性的文本对象
     * @param hintText  文本
     * @param hintTextSize  文本字体大小
     * @return  文本对象
     */
    fun buildSizeSpannableString(hintText: String, hintTextSize: Int): SpannableString {
        // 新建一个可以添加属性的文本对象
        val ss = SpannableString(hintText)
        // 新建一个属性对象,设置文字的大小
        val ass = AbsoluteSizeSpan(hintTextSize, true)
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ss
    }

    /**
     * 从给定的信息构建一个映射
     */
    fun buildStringMap(keys: Array<String>?, values: Array<String>?): Map<String, String>? {
        val data = HashMap<String, String>()
        if (keys == null || values == null || keys.size != values.size) {
            return null
        }
        for (index in keys.indices) {
            data[keys[index]] = values[index]
        }
        return data
    }
}
