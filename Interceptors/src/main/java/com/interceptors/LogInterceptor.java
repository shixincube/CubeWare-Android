package com.interceptors;

import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.Chain;
import com.billy.cc.core.component.IGlobalCCInterceptor;
import com.common.utils.utils.log.LogUtil;

/**
 * 全局拦截器：日志打印
 *
 * @author LiuFeng
 * @data 2019/1/22 10:39
 */
public class LogInterceptor implements IGlobalCCInterceptor {
    private static final String TAG = "LogInterceptor";

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public CCResult intercept(Chain chain) {
        CCResult result = chain.proceed();
        LogUtil.i(TAG, "LogInterceptor--> \nCCResult:" + LogUtil.getFormatJson(result.toString()) + "\nCC:" + LogUtil.getFormatJson(chain.getCC().toString()));
        return result;
    }
}
