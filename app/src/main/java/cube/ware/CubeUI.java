package cube.ware;

import android.content.Context;
import android.support.annotation.NonNull;
import com.common.utils.utils.log.LogUtil;
import com.umeng.analytics.MobclickAgent;
import cube.service.CubeEngine;
import cube.service.common.model.CubeConfig;
import cube.service.common.model.CubeError;
import cube.service.common.model.Version;
import cube.service.message.model.MessageEntity;
import cube.service.user.UserState;
import cube.ware.service.call.CallHandle;
import cube.ware.service.conference.ConferenceHandle;
import cube.ware.service.core.SettingHandle;
import cube.ware.service.engine.CubeEngineHandle;
import cube.ware.service.engine.CubeEngineWorkerListener;
import cube.ware.service.file.FileHandle;
import cube.ware.service.group.GroupHandle;
import cube.ware.service.message.MessageHandle;
import cube.ware.service.remoteDesktop.RemoteDesktopHandle;
import cube.ware.service.user.UserHandle;
import cube.ware.service.whiteboard.WhiteBoardHandle;
import cube.ware.ui.chat.ChatEventListener;
import cube.ware.ui.recent.listener.UnreadMessageCountListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * CubeWare全局管理类
 *
 * @author Wangxx
 * @date 2016/12/29
 */
public class CubeUI {

    private static CubeUI mInstance = new CubeUI();

    private Context mContext;

    private final StringBuilder buffer = new StringBuilder();

    private List<CubeEngineWorkerListener> mCubeEngineWorkerListenerList = new ArrayList<>(); // CubeEngine工作状态监听
    private List<ChatEventListener>        sChatEventListeners           = new ArrayList<>(); // 会话窗口消息列表一些点击事件的响应处理回调

    private List<WeakReference<UnreadMessageCountListener>> mUnreadMessageCountListeners = new ArrayList<>();//未读消息总数监听器

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
    public void startupCube(Context context) {
        if (!isStarted() && CubeEngine.getInstance().startup(context)) {
            /*//启动尝试保活的service
            DaemonEnv.initialize(
                    context,  //Application Context.
                    CoreAliveService.class, //Service 对应的 Class 对象.
                    3 * 1000);  //定时唤醒的时间间隔(ms), 默认 6 分钟.
            CoreAliveService.start(context);*/

            // 注册启动监听
            startListener();
        }
    }

    /**
     * 启动引擎各服务的监听
     */
    private void startListener() {
        CubeEngineHandle.getInstance().start();
        UserHandle.getInstance().start();
        MessageHandle.getInstance().start();
        CallHandle.getInstance().start();
        ConferenceHandle.getInstance().start();
        FileHandle.getInstance().start();
        GroupHandle.getInstance().start();
        RemoteDesktopHandle.getInstance().start();
        WhiteBoardHandle.getInstance().start();
        SettingHandle.getInstance().start();
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
    public boolean isLoginSucceedOrLoginProgress() {
        return isStarted() && (getAccountState() == UserState.LoginSucceed || getAccountState() == UserState.LoginProgress);
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
        LogUtil.i("是否正在通话中：" + CubeEngine.getInstance().getSession().isCalling());
        return CubeEngine.getInstance().getSession().isCalling();
    }

    /**
     * 判断当前是否正在多人音视频
     *
     * @return
     */
    public boolean isConference() {
        LogUtil.i("是否正在多人音视频：" + CubeEngine.getInstance().getSession().isConference());
        return CubeEngine.getInstance().getSession().isConference();
    }

    /**
     * 上传错误到友盟
     *
     * @param messageEntity
     * @param cubeError
     */
    public void reportError(MessageEntity messageEntity, CubeError cubeError) {
        String desc = (messageEntity != null ? ("sn:" + messageEntity.getSerialNumber() + " type:" + messageEntity.getType() + " sendTime:" + messageEntity.getSendTimestamp() + " time:" + messageEntity.getTimestamp()) : null);
        reportError(desc, cubeError);
    }

    /**
     * 上传错误到友盟
     *
     * @param desc
     */
    public void reportError(String desc) {
        reportError(desc, null);
    }

    /**
     * 上传错误到友盟
     *
     * @param cubeError
     */
    public void reportError(CubeError cubeError) {
        reportError("", cubeError);
    }

    /**
     * 上传错误到友盟
     *
     * @param desc
     * @param cubeError
     */
    public void reportError(String desc, CubeError cubeError) {
        buffer.append("onFailed: Version:");
        buffer.append(Version.getDescription());
        buffer.append("WB:");
        buffer.append(Version.WB_V);
        buffer.append(" CC:");
        buffer.append(genie.api.Version.getNumbers());
        buffer.append("\n");
        buffer.append(cubeError != null ? cubeError.toString() : "");
        buffer.append("\n");
        buffer.append(desc);

        MobclickAgent.reportError(CubeUI.getInstance().getContext(), buffer.toString());

        buffer.delete(0, buffer.length());
    }

    /**
     * 添加CubeEngine监听器
     *
     * @param cubeEngineWorkerListener
     */
    public void addCubeEngineWorkerListener(CubeEngineWorkerListener cubeEngineWorkerListener) {
        if (cubeEngineWorkerListener != null) {
            mCubeEngineWorkerListenerList.add(cubeEngineWorkerListener);
        }
    }

    /**
     * 移除CubeEngine监听器
     *
     * @param cubeEngineWorkerListener
     */
    public void removeCubeEngineWorkerListener(CubeEngineWorkerListener cubeEngineWorkerListener) {
        if (cubeEngineWorkerListener != null && mCubeEngineWorkerListenerList.contains(cubeEngineWorkerListener)) {
            mCubeEngineWorkerListenerList.remove(cubeEngineWorkerListener);
        }
    }

    public List<CubeEngineWorkerListener> getCubeEngineWorkerListener() {
        return mCubeEngineWorkerListenerList;
    }

    /**
     * 获取聊天界面事件监听器
     *
     * @return
     */
    public List<ChatEventListener> getChatEventListeners() {
        return sChatEventListeners;
    }

    /**
     * 设置聊天界面的事件监听器
     *
     * @param chatEventListener
     */
    public void addChatEventListener(ChatEventListener chatEventListener) {
        if (chatEventListener != null && !sChatEventListeners.contains(chatEventListener)) {
            sChatEventListeners.add(chatEventListener);
        }
    }

    /**
     * 删除聊天界面的事件监听器
     *
     * @param chatEventListener
     */
    public void removeChatEventListener(ChatEventListener chatEventListener) {
        if (chatEventListener != null && sChatEventListeners.contains(chatEventListener)) {
            sChatEventListeners.remove(chatEventListener);
        }
    }

    /**
     * 设置未读消息总数监听器
     *
     * @param listener
     */
    public void addUnreadMessageCountListener(UnreadMessageCountListener listener) {
        for (WeakReference<UnreadMessageCountListener> unreadMessageCountListener : mUnreadMessageCountListeners) {
            if (unreadMessageCountListener != null && unreadMessageCountListener.get() == listener) {
                return;
            }
        }
        this.mUnreadMessageCountListeners.add(new WeakReference<UnreadMessageCountListener>(listener));
    }

    /**
     * 设置未读消息总数监听器
     *
     * @param listener
     */
    public void removeUnreadMessageCountListener(UnreadMessageCountListener listener) {
        for (WeakReference<UnreadMessageCountListener> unreadMessageCountListener : mUnreadMessageCountListeners) {
            if (unreadMessageCountListener != null && unreadMessageCountListener.get() == listener) {
                this.mUnreadMessageCountListeners.remove(unreadMessageCountListener);
                return;
            }
        }
    }

    /**
     * 获取未读消息总数监听器
     *
     * @return
     */
    public List<WeakReference<UnreadMessageCountListener>> getUnreadMessageCountListener() {
        return this.mUnreadMessageCountListeners;
    }
}
