package com.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.common.eventbus.Event;
import com.common.eventbus.EventBusUtil;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 延迟加载的fragment（即：懒加载）
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public abstract class BaseLazyFragment extends Fragment {

    private boolean isFirstResume    = true;
    private boolean isFirstVisible   = true;
    private boolean isFirstInvisible = true;
    private boolean isPrepared;

    @Override

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            }
            else {
                onUserVisible();
            }
        }
        else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            }
            else {
                onUserInvisible();
            }
        }
    }

    private synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        }
        else {
            isPrepared = true;
        }
    }

    /**
     * 第一次fragment可见（进行初始化工作）
     */
    protected abstract void onFirstUserVisible();

    /**
     * fragment可见（切换回来或者onResume）
     */
    protected abstract void onUserVisible();

    /**
     * 第一次fragment不可见（不建议在此处理事件）
     */
    private void onFirstUserInvisible() {
        // here we do not recommend do something
    }

    /**
     * fragment不可见（切换掉或者onPause）
     */
    protected abstract void onUserInvisible();

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，false不绑定
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public <T> void onEventBusCome(Event<T> event) {
        if (event != null) {
            onReceiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public <T> void onStickyEventBusCome(Event<T> event) {
        if (event != null) {
            onReceiveStickyEvent(event);
        }
    }

    /**
     * 接收到分发到事件
     *
     * @param event 事件
     */
    public <T> void onReceiveEvent(Event<T> event) {

    }

    /**
     * 接受到分发的粘性事件
     *
     * @param event 粘性事件
     */
    public <T> void onReceiveStickyEvent(Event<T> event) {

    }
}
