package cube.ware.service.core;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import com.common.utils.log.LogUtil;

/**
 * cube核心service
 *
 * @author PengZhenjin
 * @date 2017-1-10
 */
public class CoreService extends Service {
    private static final int FORE_SERVICE_ID = 1;

    private static CoreService mInstance;

    /**
     * 单例
     *
     * @return
     */
    public static CoreService getInstance() {
        return mInstance;
    }

    /**
     * 启动service
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, CoreService.class);
        ContextCompat.startForegroundService(context, intent);
    }

    /**
     * 检查服务是否运行，并启动服务
     *
     * @param context
     */
    public static void checkServiceIsHealthy(Context context) {
        if (!isServiceRunning(context)) {
            start(context);
        }
    }

    /**
     * 判断服务是否正在运行
     *
     * @param context
     *
     * @return
     */
    private static boolean isServiceRunning(Context context) {
        boolean ret = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceInfo.service.getPackageName().equals(context.getPackageName()) && CoreService.class.getName().equals(serviceInfo.service.getClassName())) {
                ret = true;
                break;
            }
        }
        LogUtil.i("CoreService is running : " + ret);
        return ret;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtil.i("CoreService ===》 created");
        super.onCreate();
        mInstance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i("CoreService ===》 onStartCommand");

        //8.0  startForeground 报错问题
        String channelOneId = "com.primedu.cn";
        String channelOneName = "Channel One";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelOneId, channelOneName, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }

        Notification notification = new Notification();
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, channelOneId).setTicker("Nature").setContentIntent(pendingIntent).build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            this.startForeground(FORE_SERVICE_ID, notification);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                this.startService(new Intent(getApplication(), InnerService.class));
            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForeground(FORE_SERVICE_ID, notification);
        }

        return START_STICKY;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onDestroy() {
        LogUtil.i("CoreService ===》 destroyed");
        super.onDestroy();
    }

    /**
     * 内部service，作用：保活CoreService
     *
     * @author PengZhenjin
     * @date 2016-12-13
     */
    public static class InnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            this.startForeground(FORE_SERVICE_ID, new Notification());
            this.stopForeground(true);
            this.stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
    }
}
