package com.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.annotation.NonNull;

/**
 * 屏幕状态广播接收者
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {

    private static ScreenBroadcastReceiver mInstance = new ScreenBroadcastReceiver();

    private ScreenStateListener mScreenStateListener;

    /**
     * 单例
     *
     * @return
     */
    public static ScreenBroadcastReceiver getInstance() {
        return mInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (this.mScreenStateListener == null) {
            return;
        }

        // 开屏
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            this.mScreenStateListener.onScreenOn();
        }
        // 锁屏
        else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            this.mScreenStateListener.onScreenOff();
        }
        // 解锁
        else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            this.mScreenStateListener.onScreenUnlock();
        }
    }

    /**
     * 获取屏幕状态
     *
     * @param context
     */
    private void getScreenState(@NonNull Context context) {
        PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (manager == null || this.mScreenStateListener == null) {
            return;
        }

        if (manager.isScreenOn()) {
            this.mScreenStateListener.onScreenOn();
        }
        else {
            this.mScreenStateListener.onScreenOff();
        }
    }

    /**
     * 注册
     *
     * @param context
     */
    private void register(@NonNull Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(mInstance, filter);
        this.getScreenState(context);
    }

    /**
     * 取消注册
     *
     * @param context
     */
    public void unregister(@NonNull Context context) {
        context.unregisterReceiver(mInstance);
    }

    public void setScreenStateListener(ScreenStateListener listener) {
        this.mScreenStateListener = listener;
    }

    public void cancelScreenStateListener() {
        this.mScreenStateListener = null;
    }

    /**
     * 屏幕状态监听器
     */
    public interface ScreenStateListener {
        /**
         * 亮屏
         */
        void onScreenOn();

        /**
         * 锁屏
         */
        void onScreenOff();

        /**
         * 解锁
         */
        void onScreenUnlock();
    }
}
