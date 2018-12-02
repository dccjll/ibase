package com.dcc.ibase.utils

import android.text.TextUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import java.util.regex.Pattern
import kotlin.experimental.and
import kotlin.experimental.xor

/**
 * 定禅天 净琉璃
 * 2018-11-21 13:33:52 Wednesday
 * 描述：字节工具
 */
object ByteUtils {

    /**
     * 截取字节的一部分
     * @param bytes bytes 需要截取的字节数组
     * @param start 截取的开始索引
     * @param len 需要截取的长度
     * @return 截取的字节数组
     */
    fun subBytes(bytes: ByteArray?, start: Int, len: Int): ByteArray? {
        var startIndex = start
        if (bytes == null || bytes.size < len || len == 0) {
            return null
        } else if (bytes.size == len) {
            return bytes
        }
        val bs = ByteArray(len)
        for (i in 0 until len) {
            bs[i] = bytes[startIndex++]
        }
        return bs
    }

    /**
     * 将一个字节数组转换成字节数组列表<br>
     * 比如：<br>
     * 原数组srcBytes：01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24<br>
     * 单个数组的最大长度maxLength：20<br>
     * 字节数组列表为：[01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19,
     *               20 21 22 23 24]
     * @param srcBytes  源字节数组
     * @param maxLength 单个数组的最大长度
     * @return  字节数组列表
     */
    fun parseBytesToBytesListByLength(srcBytes: ByteArray?, maxLength: Int?): List<ByteArray>? {
        if (srcBytes == null || srcBytes.isEmpty() || maxLength == null || maxLength == 0) {
            return null
        }
        val byteList = ArrayList<ByteArray>()
        if (maxLength >= srcBytes.size) {
            byteList.add(srcBytes)
            return byteList
        }
        val num = srcBytes.size / maxLength
        val level = srcBytes.size % maxLength
        var subData: ByteArray?
        for (index in 0 until num) {
            subData = subBytes(srcBytes, index * maxLength, maxLength)
            if (subData != null) {
                byteList.add(subData)
            }
        }
        subData = subBytes(srcBytes, maxLength * num, level)
        if (subData != null) {
            byteList.add(subData)
        }
        return byteList
    }

    /**
     * 将字节数组转换为16进制字符串
     * @param src 源数组
     * @param upperCase 转换结果中的16进制字符是否大写，默认大写
     * @param gap 每两位字符之间的间隔字符，默认为空格
     * @return 转换好的字符串
     */
    fun parseBytesToHexString(src: ByteArray?, upperCase: Boolean? = true, gap: String? = " "): String? {
        val stringBuilder = StringBuilder("")
        if (src == null || src.isEmpty()) {
            return null
        }
        var _gap = gap
        if (TextUtils.isEmpty(_gap)) {
            _gap = " "
        }
        for (i in src.indices) {
            val v = src[i].toInt() and 0xFF
            var hv = Integer.toHexString(v)
            if (hv.length < 2) {
                stringBuilder.append(0)
            } else if (hv.length > 2) {
                hv = hv.substring(hv.length - 2)
            }
            if (i == src.size - 1) {
                stringBuilder.append(when (upperCase) {
                    true, null -> hv.toUpperCase(Locale.getDefault())
                    else -> hv
                })
            } else {
                stringBuilder.append(when (upperCase) {
                    true, null -> hv.toUpperCase(Locale.getDefault())
                    else -> hv
                }).append(_gap)
            }
        }
        return stringBuilder.toString()
    }

    /**
     * 将字节数组转换为16进制字符串
     * @param src 源数组
     * 转换结果中的16进制字符是否大写，默认大写<br>
     * 每两位字符之间的间隔字符，默认为空格
     * @return 转换好的字符串
     */
    fun parseBytesToHexStringDefault(src: ByteArray?): String? {
        return parseBytesToHexString(src)
    }

    /**
     * 将16进制字符串转换成字节数组
     * @param hexString 输入的16进制字符串
     * @return 字节数组
     */
    fun parseHexStringToBytes(hexString: String?): ByteArray? {
        var src = hexString
        if (src == null || src.isEmpty()) {
            return null
        }
        src = src.replace(" ", "").replace("-", "").replace("_", "")
        val byteArray = ByteArray(when (src.length % 2 == 0) {
            true -> src.length / 2
            false -> src.length /2 + 1
        })
        var i = 0
        while (i < src.length) {
            val segString: String = if (i == src.length - 1) {
                "0${src.substring(i, i + 1)}"
            } else {
                src.substring(i, i + 2)
            }
            if (!Pattern.compile("^[0-9a-fA-F]{2}$").matcher(segString).matches()) {
                return null
            }
            byteArray[i/2] = Integer.parseInt(segString, 16).toByte()
            i += 2
        }
        return byteArray
    }

    /**
     * 比较两个字节数组是否相等
     * @param buffer1 第一个字节
     * @param buffer2 第二个字节
     * @return 是否相等
     */
    fun compareBytesEquals(buffer1: ByteArray?, buffer2: ByteArray?): Boolean {
        if (buffer1 == null || buffer2 == null || buffer1.size != buffer2.size) {
            return false
        }
        for (i in buffer1.indices) {
            if (buffer1[i] != buffer2[i]) {
                return false
            }
        }
        return true
    }

    /**
     * 将字节数组转换成长整型
     * @param src 原始字节数组
     * @return 整形值
     */
    fun parseBytesToLong(src: ByteArray?): Long? {
        if (src == null || src.isEmpty() || src.size > 4) {
            return null
        }

        var target = 0L
        for (i in src.indices) {
            target += (src[i].toInt() shl((src.size - i - 1) * 8)).toLong()
        }
        return target
    }

    /**
     * 将字节数组转换成整形
     * @param src 原始字节数组
     * @return 整型值
     */
    fun parseBytesToInt(src: ByteArray?): Int {
        return parseBytesToLong(src)?.toInt() ?: 0
    }

    /**
     * 将一个两位的十进制字面量数据转换成对应字面量的字节显示<br>
     * 即十进制的16转换为0x16
     * @param str 输入的数字字符串
     * @return 一个字节
     */
    fun parseStringToByte(str: String?): Byte? = when {
        str == null || str.length != 2 || !TextUtils.isDigitsOnly(str) -> null
        else -> ((Integer.parseInt(str.substring(0, 1)) shl 4) or Integer.parseInt(str.substring(1, 2))).toByte()
    }

    /**
     * 两个字节数组按位异或
     * @param srcBuffer    源字节数组
     * @param xorBuffer    进行异或操作的字节数组
     * @return 异或后的字节数组
     */
    fun xorBytes(srcBuffer: ByteArray?, xorBuffer: ByteArray?): ByteArray? {
        if (srcBuffer == null || xorBuffer == null) {
            return null
        }
        val data = ByteArray(srcBuffer.size)
        val xorTempBytes = ByteArray(srcBuffer.size)
        if (srcBuffer.size < xorBuffer.size) {
            System.arraycopy(xorBuffer, 0, xorTempBytes, 0, xorTempBytes.size)
        } else {
            val num = srcBuffer.size / xorBuffer.size
            val levelNum = srcBuffer.size % xorBuffer.size
            val subBytes = subBytes(xorBuffer, 0, levelNum)
            for (i in 0 until num) {
                System.arraycopy(xorBuffer, 0, xorTempBytes, i * xorBuffer.size, xorBuffer.size)
            }
            if (levelNum > 0 && subBytes != null && subBytes.isNotEmpty()) {
                System.arraycopy(subBytes, 0, xorTempBytes, num * xorBuffer.size, levelNum)
            }
        }
        for (i in srcBuffer.indices) {
            data[i] = (srcBuffer[i] xor xorTempBytes[i])
        }
        return data
    }

    /**
     * 将short整型数值转换为字节数组
     * @param data 输入的短整形数值
     * @return 字节数组
     */
    fun parseShortToBytes(data: Short): ByteArray {
        val bytes = ByteArray(2)
        bytes[0] = ((data and 0xff00.toShort()).toInt() shr 8).toByte()
        bytes[1] = (data and 0xff).toByte()
        return bytes
    }

    /**
     * 将整型数值转换为字节数组
     * @param data 输入的整形数值
     * @return 字节数组
     */
    fun parseIntToBytes(data: Int): ByteArray {
        val bytes = ByteArray(4)
        bytes[0] = (data and -0x1000000 shr 24).toByte()
        bytes[1] = (data and 0xff0000 shr 16).toByte()
        bytes[2] = (data and 0xff00 shr 8).toByte()
        bytes[3] = (data and 0xff).toByte()
        return bytes
    }

    /**
     * byte[] 转为 对象
     *
     * @param bytes
     * @return
     */
    @Throws(Exception::class)
    fun parseBytesToObject(bytes: ByteArray): Any {
        var ois: ObjectInputStream? = null
        try {
            ois = ObjectInputStream(ByteArrayInputStream(bytes))
            return ois.readObject()
        } finally {
            ois?.close()
        }
    }

    /**
     * 对象 转为 byte[]
     *
     * @param obj
     * @return
     */
    @Throws(Exception::class)
    fun parseObjectToBytes(obj: Any): ByteArray {
        var oos: ObjectOutputStream? = null
        try {
            val bos = ByteArrayOutputStream()
            oos = ObjectOutputStream(bos)
            oos.writeObject(obj)
            return bos.toByteArray()
        } finally {
            oos?.close()
        }
    }

    fun parseBytesToBit(bytes: ByteArray, sb: StringBuilder) {
        for (i in 0 until java.lang.Byte.SIZE * bytes.size)
            sb.append(if (bytes[i / java.lang.Byte.SIZE].toInt() shl i % java.lang.Byte.SIZE and 0x80 == 0) '0' else '1')
    }

    fun parseBytesToBit(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in 0 until java.lang.Byte.SIZE * bytes.size)
            sb.append(if (bytes[i / java.lang.Byte.SIZE].toInt() shl i % java.lang.Byte.SIZE and 0x80 == 0) '0' else '1')
        return sb.toString()
    }
}