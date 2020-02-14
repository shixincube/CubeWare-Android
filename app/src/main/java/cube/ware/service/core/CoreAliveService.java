package cube.ware.service.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.common.alive.AbsWorkService;
import com.common.alive.IntentWrapper;

/**
 * Created by dth
 * Des: keep-alive service 需要引导用户打开白名单 {@link IntentWrapper#whiteListMatters(Activity, String)}}
 * 否则app被杀时service不能拉起来。不同机型效果不敢保证。
 * 如果只需要保证引擎的生命周期和application生命周期一样，可以考虑将各个Handle注册到CubeUI中，不采用service方式注册
 * 通知栏消息接第三方推送，保证消息到达
 * Date: 2018/10/19.
 */

public class CoreAliveService extends AbsWorkService {

    private static boolean isStarted = false;

    public static void start(Context context) {
        Intent intent = new Intent(context, CoreAliveService.class);
        context.startService(intent);
    }

    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return false;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {

    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {

    }

    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        return isStarted;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent, Void alwaysNull) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {

    }
}
