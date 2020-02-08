package cube.ware.impl;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeConfig;
import cube.service.CubeEngine;
import cube.service.account.AccountState;
import cube.ware.api.CubeUI;
import cube.ware.core.CubeConstants;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.user.UserHandle;

/**
 * CubeWare组件接口实现类
 *
 * @author LiuFeng
 * @data 2020/1/23 11:23
 */
public final class UIRoot extends CubeUI {
    private Context mContext;

    @Override
    public void init(Context context, String appId, String appKey, String licensePath, String appResourcePath) {
        this.mContext = context.getApplicationContext();

        // 初始化引擎配置信息
        this.initCubeConfig(appId, appKey, licensePath, appResourcePath);
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

    @Override
    public void startup(Context context) {
        if (!isStarted() && CubeEngine.getInstance().startup(context)) {
            // 注册启动监听
            //startListener();
        }
    }

    /**
     * 启动引擎各服务的监听
     */
    /*private void startListener() {
        //CubeEngineHandle.getInstance().start();
        UserHandle.getInstance().start();
        MessageHandle.getInstance().start();
        CallHandle.getInstance().start();
        //ConferenceHandle.getInstance().start();
        //FileHandle.getInstance().start();
        GroupHandle.getInstance().start();
        //ShareDesktopHandle.getInstance().start();
        //WhiteBoardHandle.getInstance().start();
        //SettingHandle.getInstance().start();
    }*/

    /**
     * 登录引擎
     *
     * @param cubeId
     * @param cubeToken
     * @param displayName
     */
    @Override
    public void login(@NonNull String cubeId, @NonNull String cubeToken, String displayName) {
        CubeEngine.getInstance().getAccountService().login(cubeId, "123456", cubeToken, displayName);
    }

    /**
     * 注销引擎
     */
    @Override
    public void logout() {
        CubeEngine.getInstance().getAccountService().logout();
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    /**
     * 引擎是否已启动
     *
     * @return
     */
    @Override
    public boolean isStarted() {
        return CubeEngine.getInstance().isStarted();
    }

    @Override
    public AccountState getAccountState() {
        return CubeEngine.getInstance().getSession().getAccountState();
    }

    /**
     * 判断当前是否正在通话中
     *
     * @return
     */
    @Override
    public boolean isCalling() {
        return CubeEngine.getInstance().getSession().isCalling();
    }

    /**
     * 判断当前是否正在多人音视频
     *
     * @return
     */
    @Override
    public boolean isConference() {
        return CubeEngine.getInstance().getSession().isConference();
    }

    @Override
    public void startP2PChat(Context context, String chatId, String chatName) {
        startChat(context, chatId, chatName, CubeSessionType.P2P);
    }

    @Override
    public void startGroupChat(Context context, String chatId, String chatName) {
        startChat(context, chatId, chatName, CubeSessionType.Group);
    }

    /**
     * 启动聊天界面
     *
     * @param context
     * @param chatId
     * @param chatName
     * @param sessionType
     */
    private void startChat(Context context, String chatId, String chatName, CubeSessionType sessionType) {
        String CHAT_ID = "chat_id";
        String CHAT_NAME = "chat_name";
        String CHAT_TYPE = "chat_type";
        Bundle bundle = new Bundle();
        bundle.putString(CHAT_ID, chatId);
        bundle.putString(CHAT_NAME, chatName);
        bundle.putString(CHAT_TYPE, sessionType.name());
        if (sessionType == CubeSessionType.P2P) {
            RouterUtil.navigation(context, bundle, CubeConstants.Router.P2PChatActivity);
        }
        else {
            RouterUtil.navigation(context, bundle, CubeConstants.Router.GroupChatActivity);
        }
    }
}
