package com.dcc.ibase.third.igexin;

import com.dcc.ibase.utils.AppUtils;
import com.igexin.sdk.PushManager;

public class GTPushUtils {

    // 初始化推送服务
    public static void initPushService() {
        PushManager.getInstance().initialize(AppUtils.Companion.getApp(), GTPushService.class);
        PushManager.getInstance().registerPushIntentService(AppUtils.Companion.getApp(), IGTIntentService.class);
    }

    // 重启推送服务
    public static void restartPushService() {
        // 如果推送服务未开启，则重新开启
        if (!PushManager.getInstance().isPushTurnedOn(AppUtils.Companion.getApp())) {
            initPushService();
        }
    }

    // 停止推送服务
    public static void stopPushService() {
        PushManager.getInstance().stopService(AppUtils.Companion.getApp());
    }

}
