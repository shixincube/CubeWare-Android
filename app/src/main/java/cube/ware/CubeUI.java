package cube.ware;

import android.content.Context;
import android.support.annotation.NonNull;
import com.common.utils.alive.DaemonEnv;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.model.CubeConfig;
import cube.service.user.UserState;
import cube.ware.service.core.CoreAliveService;

/**
 * CubeWare全局管理类
 *
 * @author Wangxx
 * @date 2016/12/29
 */
public class CubeUI {

    private static CubeUI mInstance = new CubeUI();

    private Context mContext;

    /**
     * 私有化构造方法
     */
    private CubeUI() {}

    /**
     * 获取 CubeUI 单例对象
     *
     * @return
     */
    public static CubeUI getInstance() {
        return mInstance;
    }

    /**
     * CubeUI初始化
     *
     * @param context
     * @param appId
     * @param appKey
     * @param appResourcePath
     */
    public void init(Context context, String appId, String appKey, String licensePath, String appResourcePath) {
        this.mContext = context.getApplicationContext();

        // 初始化引擎配置信息
        this.initCubeConfig(appId, appKey, licensePath, appResourcePath);
    }

    /**
     * 获取ApplicationContext
     *
     * @return
     */
    public Context getContext() {
        return this.mContext;
    }

    /**
     * 初始化引擎配置信息
     *
     * @param appId
     * @param appKey
     * @param appResourcePath
     */
    public void initCubeConfig(String appId, String appKey, String licensePath, String appResourcePath) {
        // 配置引擎相关参数
        CubeConfig config = new CubeConfig();
        config.setVideoCodec("VP8");                                // 设置视频编解码格式
        config.setVideoWidth(640);
        config.setVideoHeight(480);
        config.setResourceDir(appResourcePath);                     // 设置资源存放目录
        config.setAppId(appId);
        config.setAppKey(appKey);
        config.setLicenseServer(licensePath);
        config.setDebug(LogUtil.isLoggable());                     //是否打开引擎的日志记录系统
        CubeEngine.getInstance().setCubeConfig(config);
    }

    /**
     * 启动引擎
     *
     * @param context
     *
     * @return
     */
    public boolean startupCube(Context context) {
        if (CubeEngine.getInstance().startup(context)) {
            // 启动CubeService
            //            CoreService.start(context);

            //启动尝试保活的service
            DaemonEnv.initialize(context,  //Application Context.
                                 CoreAliveService.class, //Service 对应的 Class 对象.
                                 3 * 1000);  //定时唤醒的时间间隔(ms), 默认 6 分钟.
            CoreAliveService.start(context);
            return true;
        }
        return false;
    }

    /**
     * 引擎是否已启动
     *
     * @return
     */
    public boolean isStarted() {
        return CubeEngine.getInstance().isStarted();
    }

    /**
     * 获取引擎登录状态
     *
     * @return
     */
    public UserState getAccountState() {
        return CubeEngine.getInstance().getSession().userState;
    }

    /**
     * 是否登录引擎
     */
    public boolean isLoginSucceed() {
        return isStarted() && getAccountState() == UserState.LoginSucceed;
    }

    /**
     * 登录引擎
     *
     * @param cubeId
     * @param cubeToken
     * @param displayName
     */
    public void login(@NonNull String cubeId, @NonNull String cubeToken, String displayName) {
        CubeEngine.getInstance().getUserService().login(cubeId, cubeToken, displayName);
    }

    /**
     * 注销引擎
     */
    public void logout() {
        CubeEngine.getInstance().getUserService().logout();
    }

    /**
     * 判断当前是否正在通话中
     *
     * @return
     */
    public boolean isCalling() {
        return CubeEngine.getInstance().getSession().isCalling();
    }

    /**
     * 判断当前是否正在多人音视频
     *
     * @return
     */
    public boolean isConference() {
        return CubeEngine.getInstance().getSession().isConference();
    }
}
