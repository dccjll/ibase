package com.dcc.ibase.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * 定禅天 净琉璃
 * 2018-11-21 10:45:50 星期三
 * 描述：剪贴板工具
 */
object ClipboardUtils {

    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    fun copyText(text: CharSequence) {
        val clipboard = AppUtils.app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newPlainText("text", text)
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    val text: CharSequence?
        get() {
            val clipboard = AppUtils.app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip
            return if (clip != null && clip.itemCount > 0) {
                clip.getItemAt(0).coerceToText(AppUtils.app)
            } else null
        }
}
