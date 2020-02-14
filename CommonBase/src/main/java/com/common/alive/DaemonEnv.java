package com.common.alive;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dth
 * Des:
 * Date: 2018/10/19.
 */

public final class DaemonEnv {

    private DaemonEnv() {}

    public static final long DEFAULT_WAKE_UP_INTERVAL = 6 * 60 * 1000;
    private static final long MINIMAL_WAKE_UP_INTERVAL = 3 * 60 * 1000;

    static Context                         sApp;
    static Class<? extends AbsWorkService> sServiceClass;
    private static long sWakeUpInterval = DEFAULT_WAKE_UP_INTERVAL;
    static boolean sInitialized;

    static final Map<Class<? extends Service>, ServiceConnection> BIND_STATE_MAP = new HashMap<>();

    /**
     * @param app Application Context.
     * @param wakeUpInterval 定时唤醒的时间间隔(ms).
     */
    public static void initialize(@NonNull Context app, @NonNull Class<? extends AbsWorkService> serviceClass, long wakeUpInterval) {
        sApp = app;
        sServiceClass = serviceClass;
        sWakeUpInterval = wakeUpInterval;
        sInitialized = true;
    }

    public static void startServiceMayBind(@NonNull final Class<? extends Service> serviceClass) {
        if (!sInitialized) return;
        final Intent i = new Intent(sApp, serviceClass);
        startServiceSafely(i);
        ServiceConnection bound = BIND_STATE_MAP.get(serviceClass);
        if (bound == null) sApp.bindService(i, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BIND_STATE_MAP.put(serviceClass, this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                BIND_STATE_MAP.remove(serviceClass);
                startServiceSafely(i);
                if (!sInitialized) return;
                sApp.bindService(i, this, Context.BIND_AUTO_CREATE);
            }

            @Override
            public void onBindingDied(ComponentName name) {
                onServiceDisconnected(name);
            }
        }, Context.BIND_AUTO_CREATE);
    }

    static void startServiceSafely(Intent i) {
        if (!sInitialized) return;
        try { sApp.startService(i); } catch (Exception ignored) {}
    }

    static long getWakeUpInterval() {
        return Math.max(sWakeUpInterval, MINIMAL_WAKE_UP_INTERVAL);
    }
}
