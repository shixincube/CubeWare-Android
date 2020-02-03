package cube.ware.service.user;

import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.Session;
import cube.service.account.AccountListener;

/**
 * 用户服务处理
 *
 * @author LiuFeng
 * @date 2018-8-09
 */
public class UserHandle implements AccountListener {

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
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().getAccountService().removeAccountListener(this);
    }

    @Override
    public void onLogin(Session session) {

    }

    @Override
    public void onLogout(Session session) {

    }

    @Override
    public void onAccountFailed(CubeError cubeError) {

    }
}
