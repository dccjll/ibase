package com.dcc.ibase.utils

import java.io.*
import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * 定禅天 净琉璃
 * 2018-11-21 13:33:52 Wednesday
 * 描述：文件压缩工具
 */
object ZipUtils {
    interface ZipListener {
        fun zipProgress(zipProgress: Int)
    }

    private const val BUFF_SIZE = 1024 * 1024 // 1M Byte

    /**
     * 批量压缩文件（夹）
     * @param resFileList 要压缩的文件（夹）列表
     * @param zipFile     生成的压缩文件
     * @param zipListener zipListener
     */
    fun startZip(resFileList: Collection<File>, zipFile: File, zipListener: ZipListener) {
        val zipout: ZipOutputStream
        try {
            zipout = ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile), BUFF_SIZE))
            for (resFile in resFileList) {
                zip(resFile, zipout, "", zipListener)
            }
            zipout.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 压缩文件
     * @param resFile  需要压缩的文件（夹）
     * @param zipOutputStream   压缩的目的文件
     * @param rootPath 压缩的文件路径
     */
    private fun zip(resFile: File, zipOutputStream: ZipOutputStream, rootPath: String, zipListener: ZipListener) {
        var tempRootPath = rootPath
        try {
            tempRootPath = tempRootPath + (if (tempRootPath.trim { it <= ' ' }.isEmpty()) "" else File.separator) + resFile.name
            tempRootPath = String(tempRootPath.toByteArray(charset("8859_1")), Charset.forName("GB2312"))
            if (resFile.isDirectory) {
                val fileList = resFile.listFiles()
                val length = fileList.size
                zipListener.zipProgress((1 / (length + 1).toFloat() * 100).toInt())
                for (i in 0 until length) {
                    val file = fileList[i]
                    zip(file, zipOutputStream, tempRootPath, zipListener)
                    zipListener.zipProgress(((i + 2) / (length + 1).toFloat() * 100).toInt())
                }
            } else {
                val buffer = ByteArray(BUFF_SIZE)
                val bis = BufferedInputStream(FileInputStream(resFile), BUFF_SIZE)
                zipOutputStream.putNextEntry(ZipEntry(tempRootPath))
                var realLength: Int = bis.read(buffer)
                while (realLength != -1) {
                    zipOutputStream.write(buffer, 0, realLength)
                    realLength = bis.read(buffer)
                }
                bis.close()
                zipOutputStream.flush()
                zipOutputStream.closeEntry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}