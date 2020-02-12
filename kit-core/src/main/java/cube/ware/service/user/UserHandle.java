package cube.ware.service.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import com.common.utils.manager.ActivityManager;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.DeviceInfo;
import cube.service.Session;
import cube.service.account.AccountListener;
import cube.service.account.DeviceListener;
import cube.ware.data.room.CubeDBHelper;
import java.util.List;

/**
 * 用户服务处理
 *
 * @author LiuFeng
 * @date 2018-8-09
 */
public class UserHandle implements AccountListener, DeviceListener {

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
        CubeEngine.getInstance().getAccountService().addAccountListener(this);
        CubeEngine.getInstance().getAccountService().addDeviceListener(this);
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().getAccountService().removeAccountListener(this);
        CubeEngine.getInstance().getAccountService().removeDeviceListener(this);
    }

    @Override
    public void onLogin(Session session) {
        CubeDBHelper.checkUpdateDB(session.getCubeId());
    }

    @Override
    public void onLogout(Session session) {
        CubeDBHelper.closeDB();
    }

    @Override
    public void onAccountFailed(CubeError cubeError) {

    }

    @Override
    public void onDeviceOnline(DeviceInfo loginDevice, List<DeviceInfo> onlineDevices) {
        if (onlineDevices != null && onlineDevices.size() > 1) {
            for (DeviceInfo deviceInfo : onlineDevices) {
                if (TextUtils.equals(loginDevice.getPlatform(), deviceInfo.getPlatform()) && !TextUtils.equals(loginDevice.getDeviceId(), deviceInfo.getDeviceId())) {
                    LogUtil.i("有人登录你的账号，强制下线");
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityManager.getInstance().currentActivity());
                    builder.setTitle("重复登录");
                    builder.setCancelable(false);
                    builder.setMessage("有人登录你的账号，强制下线");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CubeEngine.getInstance().getAccountService().logout();
                        }
                    }).show();
                    break;
                }
            }
        }
    }

    @Override
    public void onDeviceOffline(DeviceInfo logoutDevice, List<DeviceInfo> onlineDevices) {

    }
}
