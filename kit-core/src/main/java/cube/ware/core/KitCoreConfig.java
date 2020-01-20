package cube.ware.core;

/**
 * 数据配置
 *
 * @author LiuFeng
 * @data 2020/1/19 16:06
 */
public class KitCoreConfig {

    /**
     * 是否为调试模式
     */
    private boolean isDebug;

    /**
     * 用户中心服务器地址
     */
    private String userCenterUrl;

    /**
     * 用户头像地址
     */
    private String avatarUrl;

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public String getUserCenterUrl() {
        return userCenterUrl;
    }

    public void setUserCenterUrl(String userCenterUrl) {
        this.userCenterUrl = userCenterUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
