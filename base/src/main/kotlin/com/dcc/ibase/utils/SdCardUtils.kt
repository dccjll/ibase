package com.dcc.ibase.utils

import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.text.format.Formatter
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * SD卡相关的辅助类
 */

object SdCardUtils {

    /**
     * 获得SD卡总容量，格式化输出
     */
    val sdTotalSize: String
        get() {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val totalBlocks = stat.blockCount.toLong()
            return Formatter.formatFileSize(AppUtils.app, blockSize * totalBlocks)
        }

    /**
     * 获得sd卡剩余容量，格式化输出
     */
    val sdAvailableSize: String
        get() {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.availableBlocks.toLong()
            return Formatter.formatFileSize(AppUtils.app, blockSize * availableBlocks)
        }

    /**
     * 获得sd卡剩余容量，单位为字节
     */
    val sdAvailableSizeByte: Long
        get() {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.availableBlocks.toLong()
            return blockSize * availableBlocks
        }

    /**
     * 获取 SD 卡路径
     *
     * @return SD 卡路径
     */
    private val sdCardPaths: List<String>
        get() {
            val storageManager = AppUtils.app
                    .getSystemService(Context.STORAGE_SERVICE) as StorageManager
            var paths: List<String> = ArrayList()
            try {
                val getVolumePathsMethod = StorageManager::class.java.getMethod("getVolumePaths")
                getVolumePathsMethod.isAccessible = true
                val invoke = getVolumePathsMethod.invoke(storageManager)
                paths = Arrays.asList(*invoke as Array<String>)
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

            return paths
        }

    /**
     * 判断SDCard是否可用
     */
    fun checkEnable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED && !sdCardPaths.isEmpty()
    }
}
