package com.common.sdk;

import android.app.Application;
import android.content.Context;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.sdk.BuildConfig;

/**
 * CommonSdk初始化类
 *
 * @author liufeng
 * @date 2018-7-13
 */
public class CommonSdk {

    private static Context mContext;

    private CommonSdk() {
    }

    public static void init(Application application) {
        mContext = application.getApplicationContext();

        if (BuildConfig.DEBUG) {
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init((Application) mContext);
    }

    public static Context getContext() {
        return mContext;
    }
}
