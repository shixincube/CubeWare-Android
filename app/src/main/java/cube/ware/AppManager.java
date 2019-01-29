package cube.ware;

import android.content.Context;
import com.common.utils.utils.AppUtil;
import com.common.utils.utils.log.LogUtil;
import cube.ware.utils.SpUtil;

/**
 * 应用程序管理者
 *
 * @author LiuFeng
 * @date 2018-8-14
 */
public class AppManager {

    private static Boolean mIsDebug = true; // 是否是debug模式

    static {
        LogUtil.setLoggable(true);          // 是否打印日志
    }

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        initLogger(context);
        syncIsDebug(context);
    }

    /**
     * 是否是debug模式
     *
     * @return
     */
    public static boolean isDebug() {
        return mIsDebug != null && mIsDebug;
    }

    /**
     * 同步app的debug模式
     * 注意：应该在Application中初始化
     *
     * @param context
     */
    public static void syncIsDebug(Context context) {
        if (mIsDebug == null) {
            mIsDebug = AppUtil.isApkInDebug(context);
        }
    }

    /**
     * 获取AppId
     *
     * @return
     */
    public static String getAppId() {
//        if (isDebug()) {
//            return AppConstants.Debug.APP_ID;
//        }
//        else {
//            return AppConstants.Release.APP_ID;
//        }
        return AppConstants.Release.APP_ID;
    }

    /**
     * 获取AppKey
     *
     * @return
     */
    public static String getAppKey() {
//        if (isDebug()) {
//            return AppConstants.Debug.APP_KEY;
//        }
//        else {
//            return AppConstants.Release.APP_KEY;
//        }
        return AppConstants.Release.APP_KEY;
    }

    /**
     * 获取LicenceUrl
     *
     * @return
     */
    public static String getLicenceUrl() {
        if (isDebug()) {
            return AppConstants.Debug.LICENSE_URL;
        }
        else {
            return AppConstants.Release.LICENSE_URL;
        }
    }

    /**
     * 初始化日志工具
     */
    private static void initLogger(Context context) {
        String logPath = SpUtil.getLogPath();
        LogUtil.addCommonLogHandle();                  // 普通日志
        LogUtil.addDiskLogHandle(context, logPath);    // 文件日志
        LogUtil.setLogTag("CubeWare");                 // TAG
    }
}
