package com.common.rx;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * rx线程调度工具类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class RxSchedulers {

    private static RxSchedulers sInstance = new RxSchedulers();

    public static RxSchedulers getInstance() {
        return sInstance;
    }

    private RxSchedulers() {}

    /**
     * 从io线程切换到android主线程
     *
     * @param <T>
     *
     * @return
     */
    public static <T> Observable.Transformer<T, T> io_main() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
