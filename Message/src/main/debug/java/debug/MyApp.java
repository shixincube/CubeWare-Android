package debug;

import android.app.Application;

import com.billy.cc.core.component.CC;

/**
 * Application
 *
 * @author LiuFeng
 * @data 2019/1/22 11:32
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CC.enableVerboseLog(true);
        CC.enableDebug(true);
        CC.enableRemoteCC(true);
    }
}
