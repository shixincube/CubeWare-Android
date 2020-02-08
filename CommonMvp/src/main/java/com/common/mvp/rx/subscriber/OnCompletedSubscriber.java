package com.common.mvp.rx.subscriber;

/**
 * onCompleted回调的订阅者
 *
 * @author LiuFeng
 * @data 2019/1/28 19:31
 */
public abstract class OnCompletedSubscriber<T> extends BaseSubscriber<T> {

    @Override
    public abstract void onCompleted();
}
