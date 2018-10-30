package com.common.utils.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;

import com.common.utils.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络状态广播接收者
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    private static NetworkStateReceiver mInstance = new NetworkStateReceiver();

    /**
     * 网络状态变化监听器
     */
    private List<NetworkStateChangedListener> mListeners = new ArrayList<>();

    /**
     * 私有化构造方法
     */
    private NetworkStateReceiver() {}

    /**
     * 单例
     *
     * @return
     */
    public static NetworkStateReceiver getInstance() {
        return mInstance;
    }

    /**
     * 广播接收者回调方法
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean isNetAvailable = NetworkUtil.isNetAvailable(context);
            if (this.mListeners != null && !this.mListeners.isEmpty()) {
                for (NetworkStateChangedListener listener : this.mListeners) {
                    listener.onNetworkStateChanged(isNetAvailable);
                }
            }
        }
    }

    /**
     * 添加网络状态变化监听器
     *
     * @param listener
     */
    public void addNetworkStateChangedListener(@NonNull NetworkStateChangedListener listener) {
        if (!mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    /**
     * 移除网络状态变化监听器
     *
     * @param listener
     */
    public void removeNetworkStateChangedListener(@NonNull NetworkStateChangedListener listener) {
        this.mListeners.remove(listener);
    }

    /**
     * 网络状态变化监听器
     */
    public interface NetworkStateChangedListener {
        /**
         * 网络状态有变化
         *
         * @param isNetAvailable 网络是否可用
         */
        void onNetworkStateChanged(boolean isNetAvailable);
    }

    /**
     * 注册
     *
     * @param context
     */
    public void register(@NonNull Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
    }

    /**
     * 取消注册
     *
     * @param context
     */
    public void unregister(@NonNull Context context) {
        context.unregisterReceiver(this);
    }
}
