package cube.ware.service.user;

import java.util.List;

import cube.service.common.model.CubeError;
import cube.service.common.model.CubeSession;
import cube.service.common.model.DeviceInfo;
import cube.service.user.model.User;

/**
 * 用户状态监听
 */
public interface UserStateListener {
    /**
     * 用户登录成功回调
     *
     * @param session
     * @param from
     */
    public void onLogin(CubeSession session, User from);

    /**
     * 用户登出成功回调
     *
     * @param session
     * @param from
     */
    public void onLogout(CubeSession session, User from);

    /**
     * 用户出错回调
     *
     * @param error
     * @param from
     */
    public void onUserFailed(CubeError error, User from);

    /**
     * 设备上线
     *
     * @param loginDevice
     * @param onlineDevices
     * @param from
     */
    public void onDeviceOnline(DeviceInfo loginDevice, List<DeviceInfo> onlineDevices, User from);
}
