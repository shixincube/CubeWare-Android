package com.common.mvp.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import com.common.utils.utils.log.LogUtil;

/**
 * BaseApplication
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public abstract class BaseApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
    public static Context mContext;

    private int mActivityCount; // activity计数器

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        // 注册App所有Activity的生命周期回调监听器
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mContext = null;
        unregisterActivityLifecycleCallbacks(this);
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(final Activity activity) {
        this.mActivityCount++;
        if (0 == this.mActivityCount - 1) {
            LogUtil.i("App 从后台回到前台了" + activity.getLocalClassName());
            onBackToFront(activity);
        }
    }

    /**
     * App 从后台回到前台
     *
     * @param activity
     */
    protected void onBackToFront(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {
        this.mActivityCount--;
        if (0 == this.mActivityCount) {
            LogUtil.i("App 从前台切换到后台了" + activity.getLocalClassName());
            onFrontToBack(activity);
        }
    }

    /**
     * App 从前台切换到后台
     *
     * @param activity
     */
    protected void onFrontToBack(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}
}
