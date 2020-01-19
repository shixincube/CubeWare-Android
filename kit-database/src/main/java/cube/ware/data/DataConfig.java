package cube.ware.data;

/**
 * @author: LiuFeng
 * @data: 2020/1/19
 */
public class DataConfig {

    /**
     * 是否为调试模式
     */
    private boolean isDebug;

    /**
     * 用户中心服务器地址
     */
    private String userCenterUrl;

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
}
