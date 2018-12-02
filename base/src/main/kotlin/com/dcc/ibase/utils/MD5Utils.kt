package com.dcc.ibase.utils

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import kotlin.experimental.and

/**
 * 定禅天 净琉璃
 * 2018-11-29 11:31:26 Thursday
 * 描述：md5工具
 */
object MD5Utils {
    /**
     * 十六进制
     *
     * @param buffer
     * @return
     */
    fun getMessageDigest(buffer: ByteArray): String? {
        val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
        try {
            val mdTemp = MessageDigest.getInstance("MD5")
            mdTemp.update(buffer)
            val md = mdTemp.digest()
            val j = md.size
            val str = CharArray(j * 2)
            var k = 0
            for (i in 0 until j) {
                val byte0 = md[i]
                str[k++] = hexDigits[byte0.toInt() ushr(4) and 0xf]
                str[k++] = hexDigits[(byte0 and 0xf).toInt()]
            }
            return String(str)
        } catch (e: Exception) {
            return null
        }

    }

    /**
     * @param buffer
     * @return
     */
    fun getRawDigest(buffer: ByteArray): ByteArray? {
        try {
            val mdTemp = MessageDigest.getInstance("MD5")
            mdTemp.update(buffer)
            return mdTemp.digest()

        } catch (e: Exception) {
            return null
        }

    }


    private fun getMD5(`is`: InputStream?, bufLen: Int): String? {
        if (`is` == null || bufLen <= 0) {
            return null
        }
        try {
            val md = MessageDigest.getInstance("MD5")
            val md5Str = StringBuilder(32)

            val buf = ByteArray(bufLen)
            var readCount = `is`.read(buf)
            while (readCount != -1) {
                md.update(buf, 0, readCount)
                readCount = `is`.read(buf)
            }

            val hashValue = md.digest()

            for (i in hashValue.indices) {
                md5Str.append(Integer.toString((hashValue[i] and 0xff.toByte()) + 0x100, 16).substring(1))
            }
            return md5Str.toString()
        } catch (e: Exception) {
            return null
        }

    }

    /**
     * 对文件进行md5
     *
     * @param filePath 文件路径
     * @return
     */
    fun getMD5(filePath: String?): String? {
        if (filePath == null) {
            return null
        }

        val f = File(filePath)
        return if (f.exists()) {
            getMD5(f, 1024 * 100)
        } else null
    }

    /**
     * 文件md5
     *
     * @param file
     * @return
     */
    fun getMD5(file: File): String? {
        return getMD5(file, 1024 * 100)
    }


    private fun getMD5(file: File?, bufLen: Int): String? {
        if (file == null || bufLen <= 0 || !file.exists()) {
            return null
        }

        var fin: FileInputStream? = null
        try {
            fin = FileInputStream(file)
            val md5 = getMD5(fin, when {
                bufLen <= file.length() -> bufLen
                else -> file.length().toInt()
            })
            fin.close()
            return md5

        } catch (e: Exception) {
            return null

        } finally {
            try {
                fin?.close()
            } catch (e: IOException) {

            }

        }
    }
}