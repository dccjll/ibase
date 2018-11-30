package com.dcc.ibase.utils

import android.graphics.Bitmap
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader

/**
 * 定禅天 净琉璃
 * 2018-09-19 12:39:19 星期三
 * 描述：图形码工具
 */
object GraphicCodeUtils {
    /**
     * 解析二维码图片
     */
    fun zxingScanQrcodePicture(bitmap: Bitmap): String {
        val width = bitmap.width
        val height = bitmap.height
        val data = IntArray(width * height)
        bitmap.getPixels(data, 0, width, 0, 0, width, height)
        val source = RGBLuminanceSource(width, height, data)
        val bitmap_ = BinaryBitmap(HybridBinarizer(source))
        val reader = QRCodeReader()
        var re: Result? = null
        try {
            re = reader.decode(bitmap_)
        } catch (e: NotFoundException) {
            e.printStackTrace()
        } catch (e: ChecksumException) {
            e.printStackTrace()
        } catch (e: FormatException) {
            e.printStackTrace()
        }

        return if (re == null) {
            ""
        } else re.text
    }
}
