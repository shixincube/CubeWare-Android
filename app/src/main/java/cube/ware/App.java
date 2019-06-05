package cube.ware;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import com.common.mvp.base.BaseApplication;
import com.common.mvp.crash.AppCrashHandler;
import com.common.sdk.CommonSdk;
import com.common.utils.receiver.NetworkStateReceiver;
import com.common.utils.utils.CommonUtils;
import com.common.utils.utils.log.LogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.commonsdk.UMConfigure;
import cube.ware.utils.SpUtil;

/**
 * 应用Application
 *
 * @author LiuFeng
 * @date 2018-8-14
 */
public class App extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // 非包名进程不执行下面的初始化
        if (!getPackageName().equals(getCurProcessName(this))) {
            return;
        }

        // 通用模块
        CommonUtils.init(this);
        CommonSdk.init(this);

        // App管理者
        AppManager.init(this);

        //UM统计
        if (AppManager.isDebug()) {
            UMConfigure.setLogEnabled(true);
        }
        UMConfigure.init(this, BuildConfig.UMENG_APP_KEY, "shixin", UMConfigure.DEVICE_TYPE_PHONE, null);

        // app崩溃处理
        AppCrashHandler.getInstance().init(this, SpUtil.getLogPath());

        // 注册网络状态变化广播接收器
        NetworkStateReceiver.getInstance().register(this);

        // 初始化CubeUI
        CubeUI.getInstance().init(this, AppManager.getAppId(), AppManager.getAppKey(), AppManager.getLicenceUrl(), SpUtil.getResourcePath());

        // 启动cube引擎
        CubeUI.getInstance().startupCube(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 取消注册网络变化广播
        NetworkStateReceiver.getInstance().unregister(this);
        LogUtil.i("App已关闭");
    }

    /**
     * 获取当前进程名称
     *
     * @param context
     *
     * @return
     */
    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }
}
