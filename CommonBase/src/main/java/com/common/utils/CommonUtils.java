package com.common.utils;

import android.app.Application;
import android.content.Context;
import com.common.router.RouterUtil;

/**
 * CommonUtils初始化类
 *
 * @author liufeng
 * @date 2017-11-13
 */
public class CommonUtils {

    private static Context mContext;

    /**
     * 传入上下文，初始化数据
     *
     * @param application
     */
    public static void init(Application application) {
        mContext = application.getApplicationContext();
        RouterUtil.init(application);
    }

    public static Context getContext() {
        return mContext;
    }
}
