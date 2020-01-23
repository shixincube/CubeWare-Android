package cube.ware.core;

import android.content.Context;
import android.support.annotation.NonNull;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.model.CubeConfig;

/**
 * 数据辅助操作类
 *
 * @author LiuFeng
 * @data 2020/1/19 16:05
 */
public class CubeCore {

    private static CubeCore instance = new CubeCore();

    private static Context    context;
    private        CoreConfig config;
    private        String     cubeId;
    private        String     userName;

    private CubeCore() {}

    public static CubeCore getInstance() {
        return instance;
    }

    public static void setContext(@NonNull Context context) {
        CubeCore.context = context.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public String getCubeId() {
        return cubeId;
    }

    public void setCubeId(String cubeId) {
        this.cubeId = cubeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDataConfig(@NonNull CoreConfig config) {
        this.config = config;
    }

    public String getUserCenterUrl() {
        return config != null ? config.getUserCenterUrl() : null;
    }

    public String getAvatarUrl() {
        return config != null ? config.getAvatarUrl() : null;
    }

    public boolean isDebug() {
        return config != null && config.isDebug();
    }

    /**
     * 判断当前是否正在通话中
     *
     * @return
     */
    public boolean isCalling() {
        return CubeEngine.getInstance().getSession().isCalling();
    }

    /**
     * 判断当前是否正在多人音视频
     *
     * @return
     */
    public boolean isConference() {
        return CubeEngine.getInstance().getSession().isConference();
    }
}
