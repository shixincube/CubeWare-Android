package com.common.rx.subscriber;

/**
 * onError回调的订阅者
 *
 * @author LiuFeng
 * @data 2019/1/28 19:31
 */
public abstract class OnErrorSubscriber<T> extends BaseSubscriber<T> {

    @Override
    public abstract void onError(Throwable e);
}
