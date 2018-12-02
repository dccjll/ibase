package com.dcc.ibase.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.text.TextUtils
import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * 定禅天 净琉璃
 * 2018-11-26 08:50:54 Monday
 * 描述：bitmap工具
 */
object BitmapUtils {

    /**
     * 将bitmap转换成base64字符串
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos)
        val bytes = bos.toByteArray()
        return Base64Utils.encodeToString(bytes)
    }

    /**
     * 保存bitmap图片
     * @param bitmap
     * @param outFile
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun save(bitmap: Bitmap?, outFile: String): Boolean {
        if (TextUtils.isEmpty(outFile) || bitmap == null)
            return false
        val data = bitmap2byte(bitmap)
        return save(data, outFile)
    }

    /**
     * 将Bitmap转化为字节数组
     * @param bitmap
     * @return byte[]
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun bitmap2byte(bitmap: Bitmap): ByteArray {
        var baos: ByteArrayOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val array = baos.toByteArray()
            baos.flush()
            return array
        } finally {
            close(baos)
        }
    }

    /**
     * 保存图片字节
     * @param bitmapBytes
     * @param outFile
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun save(bitmapBytes: ByteArray, outFile: String): Boolean {
        var output: FileOutputStream? = null
        var channel: FileChannel? = null
        try {
            val tmpFile = File(outFile)
            tmpFile.delete()
            FileUtils.createFile(outFile)
            output = FileOutputStream(outFile)
            channel = output.channel
            val buffer = ByteBuffer.wrap(bitmapBytes)
            channel!!.write(buffer)
            return true
        } finally {
            close(channel)
            close(output)
        }
    }

    /**
     * 关闭流
     * @param closeable
     */
    fun close(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     * 其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    fun getVideoThumbnail(videoPath: String, width: Int, height: Int, kind: Int): Bitmap? {
        var bitmap: Bitmap?
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind)
        if (bitmap != null) {
            println("w" + bitmap.width)
            println("h" + bitmap.height)
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
        }
        return bitmap
    }

    /**
     * 放大缩小图片
     *
     * @param bitmap    要放大的图片
     * @param dstWidth  目标宽
     * @param dstHeight 目标高
     * @return
     */
    fun zoomBitmap(bitmap: Bitmap?, dstWidth: Int, dstHeight: Int): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        val scaleWidht = dstWidth.toFloat() / width
        val scaleHeight = dstHeight.toFloat() / height
        matrix.postScale(scaleWidht, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true)
    }
}