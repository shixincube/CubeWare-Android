package com.common.mvp.rx.subscriber;

import com.common.utils.utils.log.LogUtil;
import rx.Subscriber;

/**
 * 基础通用订阅者
 *
 * @author LiuFeng
 * @data 2019/1/28 19:31
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {
    private static final String TAG = "BaseSubscriber";

    @Override
    public void onNext(T t) {}

    @Override
    public void onError(Throwable e) {
        LogUtil.e(TAG, e.getMessage(), e);
    }

    @Override
    public void onCompleted() {}
}
