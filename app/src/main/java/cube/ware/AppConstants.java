package cube.ware;

/**
 * 全局常量池
 *
 * @author LiuFeng
 * @date 2018-8-15
 */
public interface AppConstants {

    /**
     * 正式服环境
     */
    interface Official {
        String APP_ID      = "b6830d7360d54c72aaffbd3785a1a884";                     // 引擎id
        String APP_KEY     = "ad9c414718814830863171b88f1798c0";                     // 引擎key
        String BASE_URL    = "https://aws-user.shixincube.com";                      // 服务器接口地址
        String LICENSE_URL = "https://aws-license.shixincube.com/auth/license/get";  // 服务license地址
    }

    /**
     * 测试服环境
     */
    interface Beta {
        String APP_ID      = "6365f0cafd8a47b984bdc08a64327881";                     // 引擎id
        String APP_KEY     = "9074ad1395f24fbd83a92ddc80facb1f";                     // 引擎key
        String BASE_URL    = "https://test-user.shixincube.cn";                      // 服务器接口地址
        String LICENSE_URL = "https://test-license.shixincube.cn/auth/license/get";  // 服务license地址
    }

    /**
     * 开发服环境
     */
    interface Develop {
        String APP_ID      = "944f19ab363940118af79ba2ece767c6";                     // 引擎id
        String APP_KEY     = "4f7ee32b492241f6928d45da5bb79081546";                  // 引擎key
        String BASE_URL    = "http://103.6.222.234:4000";                            // 基础接口地址
        String LICENSE_URL = "http://103.6.222.234:9000/auth/license/get";           // license地址

        //String APP_ID      = "9c2ed36ae5d34131b3768ea432da6cea005";            // 引擎id
        //String APP_KEY     = "5df6d5495fb74b35ad157c94977527ff005";         // 引擎key
        //String BASE_URL    = "https://dev.user.shixincube.cn";                      // 服务器接口地址
        //String LICENSE_URL = "https://dev.license.shixincube.cn/auth/license/get";  // 服务license地址
    }

    /**
     * 内部路由地址
     */
    interface Router {
        // 登陆界面
        String LoginActivity = "/app/LoginActivity";

        // 主界面
        String MainActivity = "/app/MainActivity";

        // cubeIdList界面
        String CubIdListActivity = "/app/CubeIdListActivity";

        //修改头像
        String ChangeAvatorActivity = "/app/ChangeAvatorActivity";

        //修改昵称
        String ModifyNameActivity = "/app/ModifyNameActivity";

        //添加好友
        String AddFriendActivity = "/app/AddFriendActivity";

        //选择联系人
        String SelectContactActivity = "/app/SelectContactActivity";

        //用户详情
        String FriendDetailsActivity = "/app/FriendDetailsActivity";

        //选择会议人员
        String SelectMemberActivity = "/app/SelectMemberActivity";

        //设置页面
        String SettingActivity = "/app/SettingActivity";
    }
}
