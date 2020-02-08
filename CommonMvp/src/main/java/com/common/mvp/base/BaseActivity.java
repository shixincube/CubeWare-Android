package com.common.mvp.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.common.mvp.eventbus.Event;
import com.common.mvp.eventbus.EventBusUtil;
import com.common.utils.manager.ActivityManager;
import com.common.utils.receiver.NetworkStateReceiver;
import com.common.utils.utils.ClickUtil;
import com.common.utils.utils.NetworkUtil;
import com.common.utils.utils.UIHandler;
import com.common.utils.utils.log.LogUtil;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 基础的activity
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements View.OnClickListener, BaseView, NetworkStateReceiver.NetworkStateChangedListener {

    protected P       mPresenter;
    protected Bundle  mSavedInstanceState;
    private   Handler mHandler;
    private   boolean mDestroyed = false;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().addActivity(this);
        setContentView(this.getContentViewId());
        this.mContext = this;
        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
        this.mPresenter = this.createPresenter();
        this.initToolBar();
        this.initView();
        this.initListener();
        this.mSavedInstanceState = savedInstanceState;
        this.initData();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (layoutResID != 0) {
            super.setContentView(layoutResID);
        }
    }

    /**
     * 获取布局文件id
     *
     * @return Id
     */
    protected abstract int getContentViewId();

    /**
     * 创建presenter
     *
     * @return Presenter
     */
    protected abstract P createPresenter();

    ;

    /**
     * 初始化数据
     */
    protected void initData() {}

    /**
     * 初始化toolbar
     */
    protected void initToolBar() {}

    /**
     * 初始化组件
     */
    protected void initView() {}

    /**
     * 初始化监听器
     */
    protected void initListener() {
        NetworkStateReceiver.getInstance().addNetworkStateChangedListener(this);
    }

    /**
     * 网络状态变化回调
     *
     * @param isNetAvailable 网络是否可用
     */
    @Override
    public void onNetworkStateChanged(boolean isNetAvailable) {
        LogUtil.i("网络是否可用：" + isNetAvailable);
    }

    /**
     * 判断网络是否可用
     *
     * @return
     */
    protected boolean isNetAvailable() {
        return NetworkUtil.isNetAvailable(mContext);
    }

    /**
     * 点击事件
     *
     * @param v 点击事件view
     */
    @Override
    public void onClick(View v) {
        // 去抖动点击处理
        if (ClickUtil.isNormalClick(v)) {
            onNormalClick(v);
        }
    }

    /**
     * 去抖动后的正常点击事件
     *
     * @param v 点击事件view
     */
    public void onNormalClick(View v) {}

    @Override
    public void showLoading() {}

    @Override
    public void hideLoading() {}

    @Override
    public void showMessage(String message) {}

    @Override
    public void onError(int code, String message) {
        LogUtil.e("code:" + code + " message:" + message);
    }

    /**
     * 获取主Handler
     *
     * @return 主线程Handler
     */
    protected final Handler getHandler() {
        if (this.mHandler == null) {
            this.mHandler = UIHandler.getInstance();
        }
        return this.mHandler;
    }

    /**
     * 是否需要弹出键盘
     *
     * @param isShow true为弹出键盘
     */
    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
            else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        }
        else {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 延时弹出键盘
     *
     * @param focus 键盘的焦点view
     */
    protected void showKeyboardDelayed(View focus) {
        final View viewToFocus = focus;
        if (focus != null) {
            focus.requestFocus();
        }
        this.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (viewToFocus == null || viewToFocus.isFocused()) {
                    showKeyboard(true);
                }
            }
        }, 200);
    }

    /**
     * 添加一个fragment
     *
     * @param fragment
     *
     * @return
     */
    public BaseFragment addFragment(BaseFragment fragment) {
        List<BaseFragment> fragments = new ArrayList<>(1);
        fragments.add(fragment);

        List<BaseFragment> fragmentList = this.addFragmentList(fragments);
        return fragmentList.get(0);
    }

    /**
     * 添加fragment列表
     *
     * @param fragments
     *
     * @return
     */
    public List<BaseFragment> addFragmentList(List<BaseFragment> fragments) {
        List<BaseFragment> fragmentList = new ArrayList<>(fragments.size());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        boolean commit = false;

        for (BaseFragment fragment : fragments) {
            int containerId = fragment.getContainerId();
            BaseFragment fragment2 = (BaseFragment) fm.findFragmentById(containerId);
            if (fragment2 == null) {
                fragment2 = fragment;
                transaction.add(containerId, fragment);
                commit = true;
            }
            fragmentList.add(fragment2);
        }

        if (commit) {
            try {
                transaction.commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return fragmentList;
    }

    /**
     * 切换fragment
     *
     * @param fragment
     *
     * @return
     */
    public <T extends BaseFragment> T switchContent(T fragment) {
        return switchContent(fragment, false, null);
    }

    /**
     * 切换fragment
     *
     * @param fragment
     * @param needAddToBackStack 是否需要添加到返回栈
     * @param tag
     *
     * @return
     */
    protected <T extends BaseFragment> T switchContent(T fragment, boolean needAddToBackStack, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (tag != null) {
            fragmentTransaction.replace(fragment.getContainerId(), fragment, tag);
        }
        else {
            fragmentTransaction.replace(fragment.getContainerId(), fragment);
        }

        if (needAddToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        try {
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragment;
    }

    /**
     * 切换fragment
     *
     * @param fragment
     */
    protected void switchFragmentContent(BaseFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(fragment.getContainerId(), fragment);
        try {
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT >= 17) {
            return super.isDestroyed();
        }
        else {
            return this.mDestroyed || super.isFinishing();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        ActivityManager.getInstance().finishActivity(this);
        super.onDestroy();
        ClickUtil.clear();
        NetworkStateReceiver.getInstance().removeNetworkStateChangedListener(this);
        this.mDestroyed = true;
        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
        if (this.mPresenter != null) {
            this.mPresenter.onDestroy();
        }
    }

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，false不绑定
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusCome(Event event) {
        if (event != null) {
            onReceiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(Event event) {
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
