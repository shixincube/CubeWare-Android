package cube.ware.api;

import android.content.Context;
import android.support.annotation.NonNull;
import cube.service.account.AccountState;
import cube.ware.impl.UIRoot;

/**
 * CubeWare组件接口
 *
 * @author LiuFeng
 * @data 2019/12/26 16:01
 */
public abstract class CubeUI {

    protected CubeUI() {}

    public static CubeUI getInstance() {
        return CubeUIHolder.INSTANCE;
    }

    private static class CubeUIHolder {
        private static final CubeUI INSTANCE = new UIRoot();
    }

    /**
     * CubeUI初始化
     *
     * @param context
     * @param appId
     * @param appKey
     * @param appResourcePath
     */
    public abstract void init(Context context, String appId, String appKey, String licensePath, String appResourcePath);

    /**
     * 启动引擎
     *
     * @param context
     *
     * @return
     */
    public abstract void startup(Context context);

    /**
     * 登录引擎
     *
     * @param cubeId
     * @param cubeToken
     * @param displayName
     */
    public abstract void login(@NonNull String cubeId, @NonNull String cubeToken, String displayName);

    /**
     * 注销引擎
     */
    public abstract void logout();

    /**
     * 获取ApplicationContext
     *
     * @return
     */
    public abstract Context getContext();

    /**
     * 引擎是否已启动
     *
     * @return
     */
    public abstract boolean isStarted();

    /**
     * 获取引擎登录状态
     *
     * @return
     */
    public abstract AccountState getAccountState();

    /**
     * 判断当前是否正在通话中或响铃中
     *
     * @return
     */
    public abstract boolean isCalling();

    /**
     * 判断当前是否正在多人音视频
     *
     * @return
     */
    public abstract boolean isConference();
}
