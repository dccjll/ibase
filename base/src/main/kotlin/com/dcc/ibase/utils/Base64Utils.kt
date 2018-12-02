package com.dcc.ibase.utils

import android.util.Base64

/**
 * 定禅天 净琉璃
 * 2018-11-29 12:52:08 Thursday
 * 描述：base64工具
 */
object Base64Utils {
    fun encode(plain: ByteArray): ByteArray {
        return Base64.encode(plain, Base64.DEFAULT)
    }

    fun encodeToString(plain: ByteArray): String {
        return Base64.encodeToString(plain, Base64.DEFAULT)
    }

    fun decode(text: String): ByteArray {
        return Base64.decode(text, Base64.DEFAULT)
    }

    fun decode(text: ByteArray): ByteArray {
        return Base64.decode(text, Base64.DEFAULT)
    }
}