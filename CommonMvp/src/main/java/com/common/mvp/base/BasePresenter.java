package com.common.mvp.base;

import android.content.Context;
import com.common.utils.utils.NetworkUtil;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * presenter基类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public abstract class BasePresenter<V extends BaseView> {

    /**
     * 上下文
     */
    protected Context mContext;

    /**
     * 绑定的view
     */
    protected V mView;

    /**
     * 综合订阅管理
     */
    private CompositeSubscription mCompositeSubscription;

    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public BasePresenter(Context context, V view) {
        this.mContext = context;
        this.mView = view;
    }

    /**
     * view是否已绑定
     *
     * @return
     */
    protected boolean isAttachView() {
        return this.mView != null;
    }

    /**
     * 网络是否可用
     *
     * @return
     */
    protected boolean isNetAvailable() {
        return NetworkUtil.isNetAvailable(mContext);
    }

    /**
     * 添加订阅
     *
     * @param subscription
     */
    protected void addSubscribe(Subscription subscription) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(subscription);
    }

    /**
     * 取消一个订阅
     *
     * @param subscription
     */
    protected void unSubscribe(Subscription subscription) {
        if (this.mCompositeSubscription != null && this.mCompositeSubscription.hasSubscriptions()) {
            this.mCompositeSubscription.remove(subscription);
        }
    }

    /**
     * 取消所有订阅
     */
    protected void unSubscribe() {
        if (this.mCompositeSubscription != null && this.mCompositeSubscription.hasSubscriptions()) {
            this.mCompositeSubscription.clear();
        }
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        unSubscribe();
        this.mContext = null;
        this.mView = null;
    }
}
