package com.dcc.ibase.lifecircle;

import android.app.Application;

/**
 * 定禅天 净琉璃
 * 2018-11-22 13:08:39 Thursday
 * 描述：基类Application
 */
public abstract class BaseApplication extends Application implements IBaseApplication {

    private static final String TAG = "BaseApplication";

    private static BaseApplication sInstance;

    public static BaseApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        registerActivityLifecycleCallbacks(getActivityLifecycleCallbacks());
    }
}
