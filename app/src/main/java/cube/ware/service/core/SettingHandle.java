package cube.ware.service.core;

import com.common.utils.utils.log.LogUtil;

import java.util.Map;

import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.setting.Setting;
import cube.service.setting.SettingListener;

/**
 * Created by dth
 * Des: 引擎设置服务处理
 * Date: 2018/9/17.
 */

public class SettingHandle implements SettingListener{

    /**
     * 单例
     * @return
     */
    public static SettingHandle getInstance() {
        return SettingHandleHolder.INSTANCE;
    }

    private static class SettingHandleHolder{
        public static final SettingHandle INSTANCE = new SettingHandle();
    }

    private SettingHandle() {}

    public void start() {
        CubeEngine.getInstance().getSettingService().addSettingListener(this);
    }

    public void stop() {
        CubeEngine.getInstance().getSettingService().removeSettingListener(this);
    }



    @Override
    public void onNotifyConfigUpdated(Setting.NotifyConfig config) {
        LogUtil.i("onNotifyConfigUpdated: 更新通知设置-----");
    }

    @Override
    public void onDisturbConfigUpdated(Setting.DisturbConfig config) {
        LogUtil.i("onDisturbConfigUpdated: 更新免打扰设置-----");
    }

    @Override
    public void onDeviceTokenUpdated(String deviceToken) {
        LogUtil.i("onDeviceTokenUpdated: 更新DiviceToken-----");
    }

    @Override
    public void onTopSessionAdded(Map<String, Long> topList, String sessionId) {
        LogUtil.i("onTopSessionAdded: 添加置顶列表-----");
    }

    @Override
    public void onTopSessionRemoved(Map<String, Long> topList, String sessionId) {
        LogUtil.i("onTopSessionRemoved: 移除置顶列表-----");
    }

    @Override
    public void onMuteSessionAdded(Map<String, Long> muteList, String sessionId) {
        LogUtil.i("onMuteSessionAdded: 添加静音列表-----");
    }

    @Override
    public void onMuteSessionRemoved(Map<String, Long> muteList, String sessionId) {
        LogUtil.i("onMuteSessionRemoved: 移除静音列表-----");
    }

    @Override
    public void onSettingSynced(Setting setting, long updateTime) {
        LogUtil.i("onSettingSynced: 同步设置列表-----");
    }

    @Override
    public void onSettingFailed(CubeError cubeError) {
        LogUtil.e("onSettingFailed: 更新设置失败-----" + cubeError.toString());
    }
}
