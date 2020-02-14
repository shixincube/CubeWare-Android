package com.common.rx.subscriber;

/**
 * onNext回调的订阅者
 *
 * @author LiuFeng
 * @data 2019/1/28 19:31
 */
public abstract class OnNextSubscriber<T> extends BaseSubscriber<T> {

    @Override
    public abstract void onNext(T t);
}
