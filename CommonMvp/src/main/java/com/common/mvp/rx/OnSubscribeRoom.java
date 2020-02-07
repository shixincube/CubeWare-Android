package com.common.mvp.rx;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/30.
 */

public abstract class OnSubscribeRoom<T> implements Observable.OnSubscribe<T>{

    @Override
    public void call(Subscriber<? super T> subscriber) {
        try {
            T t = get();
            subscriber.onNext(t);
        } catch (Exception e) {
            subscriber.onError(e);
            e.printStackTrace();
        } finally {
            subscriber.onCompleted();
        }
    }


    /**
     * 得到数据库返回值
     *
     * @param
     */
    protected abstract T get();
}
