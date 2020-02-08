package com.common.mvp.rx.subscriber;

/**
 * 两种回调的订阅者
 *
 * @author LiuFeng
 * @data 2019/1/28 19:31
 */
public abstract class OnTwiceSubscriber<T> extends BaseSubscriber<T> {

    @Override
    public abstract void onNext(T t);

    @Override
    public abstract void onError(Throwable e);
}
