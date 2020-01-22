package cube.ware.service.user;

import android.text.TextUtils;

import com.common.utils.utils.log.LogUtil;

import cube.ware.core.CubeCore;
import java.util.ArrayList;
import java.util.List;

import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.common.model.CubeSession;
import cube.service.common.model.DeviceInfo;
import cube.service.user.UserListener;
import cube.service.user.model.User;
import cube.ware.data.repository.CubeUserRepository;
import cube.ware.data.room.model.CubeUser;
import rx.functions.Action1;

/**
 * 用户服务处理
 *
 * @author LiuFeng
 * @date 2018-8-09
 */
public class UserHandle implements UserListener {

    public List<UserStateListener> userStateListeners = new ArrayList<>();

    private static UserHandle instance = new UserHandle();

    private UserHandle() {}

    /**
     * 单例
     *
     * @return
     */
    public static UserHandle getInstance() {
        return instance;
    }

    /**
     * 启动监听
     */
    public void start() {
        CubeEngine.getInstance().getUserService().addUserListener(this);
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().getUserService().removeUserListener(this);
    }

    /**
     * 添加用户状态监听器
     *
     * @param listener
     */
    public void addUserStateListener(UserStateListener listener) {
        if (listener != null && !userStateListeners.contains(listener)) {
            userStateListeners.add(listener);
        }
    }

    /**
     * 删除用户状态监听器
     *
     * @param listener
     */
    public void removeUserStateListener(UserStateListener listener) {
        if (listener != null) {
            userStateListeners.remove(listener);
        }
    }

    /**
     * 用户登录成功回调
     *
     * @param session
     * @param from
     */
    @Override
    public void onLogin(CubeSession session, User from) {
        for (UserStateListener listener : userStateListeners) {
            listener.onLogin(session, from);
        }
    }

    /**
     * 用户登出成功回调
     *
     * @param session
     * @param from
     */
    @Override
    public void onLogout(CubeSession session, User from) {
        for (UserStateListener listener : userStateListeners) {
            listener.onLogout(session, from);
        }
    }

    @Override
    public void onUserUpdated(User user) {
        //更新SPUtil中保存的用户信息
        LogUtil.d("===更新用户信息==: " + user);
        //SpUtil.setCubeId(user.cubeId);
        //SpUtil.setUserAvator(user.avatar);
        //SpUtil.setUserName(user.displayName);
        CubeCore.getInstance().setCubeId(user.cubeId);

        CubeUser cubeUser = new CubeUser(user.cubeId, user.displayName, user.avatar);
        CubeUserRepository.getInstance().saveUser(cubeUser).subscribe(new Action1<CubeUser>() {
            @Override
            public void call(CubeUser cubeUser) {
                //RxBus.getInstance().post(CubeEvent.EVENT_REFRESH_CUBE_USER,cubeUser);
            }
        });
    }

    /**
     * 用户出错回调
     *
     * @param error
     * @param from
     */
    @Override
    public void onUserFailed(CubeError error, User from) {
        for (UserStateListener listener : userStateListeners) {
            listener.onUserFailed(error, from);
        }
    }

    /**
     * 设备上线
     *
     * @param loginDevice
     * @param onlineDevices
     * @param from
     */
    @Override
    public void onDeviceOnline(DeviceInfo loginDevice, List<DeviceInfo> onlineDevices, User from) {
        DeviceInfo deviceInfo = CubeEngine.getInstance().getDeviceInfo();
        String platform = loginDevice.getPlatform();
        if (deviceInfo.equals(loginDevice)) {
            return;
        }

        LogUtil.i("platform -------> : " +platform);
        //不是移动端允许多终端在线
        if (!TextUtils.equals(loginDevice.getPlatform(), "Android") && !TextUtils.equals(platform, "ios")) {
            return;
        }
        for (UserStateListener listener : userStateListeners) {
            listener.onDeviceOnline(loginDevice, onlineDevices,from);
        }
    }

    /**
     * 设备下线
     *
     * @param logoutDevice
     * @param onlineDevices
     * @param from
     */
    @Override
    public void onDeviceOffline(DeviceInfo logoutDevice, List<DeviceInfo> onlineDevices, User from) {
    }
}
