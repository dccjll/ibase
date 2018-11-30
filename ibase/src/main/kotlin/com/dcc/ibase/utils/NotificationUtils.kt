package com.dcc.ibase.utils

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context

/**
 * 定禅天 净琉璃
 * 2018-11-26 08:50:54 Monday
 * 描述：通知工具
 */
object NotificationUtils {

    private const val CHECK_OP_NO_THROW = "checkOpNoThrow"
    private const val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"

    /**
     * 检测当前app通知是否打开
     */
    @SuppressLint("NewApi")
    fun isNotificationEnabled(context: Context): Boolean {
        val mAppOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val appInfo = context.applicationInfo
        val pkg = context.applicationContext.packageName
        val uid = appInfo.uid

        val appOpsClass: Class<*>
        try {
            appOpsClass = Class.forName(AppOpsManager::class.java.name)
            val checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String::class.java)
            val opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)

            val value = opPostNotificationValue.get(Int::class.java) as Int
            return checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) as Int == AppOpsManager.MODE_ALLOWED

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}
