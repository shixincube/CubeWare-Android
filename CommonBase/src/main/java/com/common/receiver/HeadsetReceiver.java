package com.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import java.util.ArrayList;
import java.util.List;

/**
 * 耳机广播接收器
 *
 * @author PengZhenjin
 * @date 2016-12-6
 */
public class HeadsetReceiver extends BroadcastReceiver {
    private volatile static HeadsetReceiver sReceiver;
    private List<HeadsetListener> mHeadsetListeners = new ArrayList<>();
    private boolean               isRegister        = false;

    private HeadsetReceiver() {}

    public static HeadsetReceiver getInstance() {
        if (sReceiver == null) {
            synchronized (HeadsetReceiver.class) {
                if (sReceiver == null) {
                    sReceiver = new HeadsetReceiver();
                }
            }
        }
        return sReceiver;
    }

    /**
     * 注册耳机广播
     *
     * @param context
     */
    public synchronized void registerReceiver(Context context) {
        if (!isRegister) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_HEADSET_PLUG);
            filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            context.registerReceiver(sReceiver, filter);
            isRegister = true;
        }
    }

    /**
     * 解注册耳机广播
     *
     * @param context
     */
    public synchronized void unregisterReceiver(Context context) {
        if (isRegister) {
            context.unregisterReceiver(sReceiver);
            isRegister = false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            // 插入和拔出耳机会触发此广播
            case Intent.ACTION_HEADSET_PLUG:
                int state = intent.getIntExtra("state", 0);
                if (this.mHeadsetListeners != null && !this.mHeadsetListeners.isEmpty()) {
                    for (HeadsetListener listener : this.mHeadsetListeners) {
                        listener.onInOrOut(state);
                    }
                }
                break;
            // 拔出耳机会触发此广播,插入不会触发,且此广播比上一个早,故可在此暂停播放,收到上一个广播时在恢复播放
            case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                if (this.mHeadsetListeners != null && !this.mHeadsetListeners.isEmpty()) {
                    for (HeadsetListener listener : this.mHeadsetListeners) {
                        listener.onPullOut();
                    }
                }
                break;
        }
    }

    /**
     * 添加耳机插拔监听
     *
     * @param listener
     */
    public void addHeadsetListener(HeadsetListener listener) {
        this.mHeadsetListeners.add(listener);
    }

    /**
     * 移除耳机插拔监听
     *
     * @param listener
     */
    public void removeHeadsetListener(HeadsetListener listener) {
        this.mHeadsetListeners.remove(listener);
    }

    /**
     * 耳机插拔监听器
     *
     * @author Wangxx
     * @date 2017/3/27
     */

    public interface HeadsetListener {
        /**
         * 耳机插入
         *
         * @param state
         */
        void onInOrOut(int state);

        /**
         * 耳机拔出
         */
        void onPullOut();
    }
}
