package com.common.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * UI数据刷新操作
 */
public class UIHandler extends Handler {

    private UIHandler() {
        super(Looper.getMainLooper());
    }

    /**
     * Handler单实例
     *
     * @return
     */
    public static UIHandler getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final UIHandler INSTANCE = new UIHandler();
    }

    /**
     * 运行在主线程
     *
     * @param runnable
     */
    public static void run(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
            return;
        }
        getInstance().post(runnable);
    }

    /**
     * 清空当前Handler队列所有消息
     */
    public static void dispose() {
        getInstance().removeCallbacksAndMessages(null);
    }
}
