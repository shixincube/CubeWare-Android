package com.common.utils.utils;

import android.app.Application;
import android.content.Context;

/**
 * CommonUtils初始化类
 *
 * @author liufeng
 * @date 2017-11-13
 */
public class CommonUtils {

    private static Context mContext;

    private CommonUtils() {
    }

    public static void init(Application application) {
        mContext = application.getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
