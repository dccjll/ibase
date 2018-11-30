package com.dcc.ibase.lifecircle;

import android.app.Application;

/**
 * 定禅天 净琉璃
 * 2018-11-22 13:08:39 Thursday
 * 描述：基类Application接口
 */
interface IBaseApplication {
    /**
     * 获取ActivityLifecycleCallbacks
     */
    Application.ActivityLifecycleCallbacks getActivityLifecycleCallbacks();
}
