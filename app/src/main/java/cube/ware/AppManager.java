package cube.ware;

import android.content.Context;
import com.common.utils.utils.AppUtil;
import com.common.utils.utils.log.LogUtil;
import cube.ware.data.CubeDataHelper;
import cube.ware.data.DataConfig;
import cube.ware.utils.SpUtil;

/**
 * 应用程序管理者
 *
 * @author LiuFeng
 * @date 2018-8-14
 */
public class AppManager {

    /** 是否是debug模式 **/
    private static boolean mIsDebug;

    /** 服务器环境配置 **/
    private static ServerEnum serverConfig = ServerEnum.BETA;

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
        initDatabase(context);
    }

    /**
     * 是否是debug模式
     *
     * @return
     */
    public static boolean isDebug() {
        return mIsDebug;
    }

    /**
     * 同步app的debug模式
     * 注意：应该在Application中初始化
     *
     * @param context
     */
    public static void syncIsDebug(Context context) {
        // 无默认配置时，则以Apk的debug模式为准
        if (serverConfig == null) {
            mIsDebug = AppUtil.isApkInDebug(context);
            return;
        }

        // 正式服为false，否则为true
        if (serverConfig == ServerEnum.OFFICIAL) {
            mIsDebug = false;
        }
        else {
            mIsDebug = true;
        }
    }

    /**
     * 获取AppId
     *
     * @return
     */
    public static String getAppId() {
        if (serverConfig == ServerEnum.OFFICIAL) {
            return AppConstants.Official.APP_ID;
        }
        else if (serverConfig == ServerEnum.BETA) {
            return AppConstants.Beta.APP_ID;
        }
        else {
            return AppConstants.Develop.APP_ID;
        }
    }

    /**
     * 获取AppKey
     *
     * @return
     */
    public static String getAppKey() {
        if (serverConfig == ServerEnum.OFFICIAL) {
            return AppConstants.Official.APP_KEY;
        }
        else if (serverConfig == ServerEnum.BETA) {
            return AppConstants.Beta.APP_KEY;
        }
        else {
            return AppConstants.Develop.APP_KEY;
        }
    }

    /**
     * 获取base地址
     *
     * @return
     */
    public static String getBaseUrl() {
        if (serverConfig == ServerEnum.OFFICIAL) {
            return AppConstants.Official.BASE_URL;
        }
        else if (serverConfig == ServerEnum.BETA) {
            return AppConstants.Beta.BASE_URL;
        }
        else {
            return AppConstants.Develop.BASE_URL;
        }
    }

    /**
     * 获取LicenceUrl
     *
     * @return
     */
    public static String getLicenceUrl() {
        if (serverConfig == ServerEnum.OFFICIAL) {
            return AppConstants.Official.LICENSE_URL;
        }
        else if (serverConfig == ServerEnum.BETA) {
            return AppConstants.Beta.LICENSE_URL;
        }
        else {
            return AppConstants.Develop.LICENSE_URL;
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

    /**
     * 初始化数据库配置数据
     *
     * @param context
     */
    private static void initDatabase(Context context) {
        CubeDataHelper.setContext(context);
        DataConfig config = new DataConfig();
        config.setDebug(isDebug());
        config.setUserCenterUrl(getBaseUrl());
        CubeDataHelper.getInstance().setDataConfig(config);
    }
}
