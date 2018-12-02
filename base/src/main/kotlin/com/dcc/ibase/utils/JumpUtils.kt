package com.dcc.ibase.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * 定禅天 净琉璃
 * 2018-11-26 12:49:34 Monday
 * 描述：系统跳转工具
 */
object JumpUtils {
    /**
     * 打开手机系统设置界面
     */
    fun toSettingPage() {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        localIntent.data = Uri.fromParts("package", AppUtils.app.packageName, null)
        AppUtils.app.startActivity(localIntent)
    }

    /**
     * 跳转到系统发送短信界面
     */
    fun toSmsPage(context: Context? = null, phoneNumber: String? = "", content: String? = "") {
        var context_ = context
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber"))
        intent.putExtra("sms_body", content)
        if (context_ is Activity) {
            context_.startActivity(intent)
            return
        }
        when (context_) {
            null ->  context_ = AppUtils.app
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context_?.startActivity(intent)
    }
}