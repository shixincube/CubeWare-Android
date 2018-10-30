package com.common.mvp.rx;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * 用于管理RxBus的事件和Rxjava相关代码的生命周期处理
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class RxManager implements Serializable {

    /**
     * 管理被观察者
     */
    private Map<String, Observable<Object>> mObservableMap = new HashMap<>();

    /**
     * 管理订阅
     */
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    /**
     * 发送事件
     *
     * @param tag
     * @param content
     */
    public void post(Object tag, Object content) {
        RxBus.getInstance().post(tag, content);
    }

    /**
     * 接收事件
     *
     * @param eventName
     * @param action1
     */
    public void on(String eventName, final Action1<Object> action1) {
        final Observable<Object> observable = RxBus.getInstance().register(eventName);
        this.mObservableMap.put(eventName, observable);
        final Subscription subscription = observable.flatMap(new Func1<Object, Observable<Object>>() {
            @Override
            public Observable<Object> call(Object o) {
                try {
                    return Observable.just(o);
                } catch (Throwable t) {
                    return Observable.error(t);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(action1, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                // 事件失败后再次订阅才能再次接收到事件
                observable.observeOn(AndroidSchedulers.mainThread()).subscribe(action1);
            }
        });
        this.add(subscription);
    }

    /**
     * 判断事件是否已注册
     *
     * @param eventName
     *
     * @return
     */
    public boolean registered(String eventName) {
        return RxBus.getInstance().registered(eventName);
    }

    /**
     * 添加订阅
     *
     * @param subscription
     */
    public void add(Subscription subscription) {
        this.mCompositeSubscription.add(subscription);
    }

    /**
     * 取消订阅，并清除所有的事件监听
     */
    public void clear() {
        this.mCompositeSubscription.unsubscribe();  // 取消订阅
        for (Map.Entry<String, Observable<Object>> entry : this.mObservableMap.entrySet()) {
            RxBus.getInstance().unregister(entry.getKey(), entry.getValue());   // 移除观察
        }
    }
}
