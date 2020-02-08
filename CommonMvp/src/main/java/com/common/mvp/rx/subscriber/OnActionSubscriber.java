package com.common.mvp.rx.subscriber;

/**
 * onNext回调的订阅者--用于替换Action1
 *
 * @author LiuFeng
 * @data 2019/1/28 19:31
 */
public abstract class OnActionSubscriber<T> extends BaseSubscriber<T> {

    @Override
    public void onNext(T t) {
        call(t);
    }

    public abstract void call(T t);
}
