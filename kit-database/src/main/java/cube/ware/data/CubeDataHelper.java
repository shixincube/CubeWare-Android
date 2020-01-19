package cube.ware.data;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @author: LiuFeng
 * @data: 2020/1/19
 */
public class CubeDataHelper {

    private static CubeDataHelper instance = new CubeDataHelper();

    private static Context    context;
    private DataConfig config;
    private String cubeId;

    private CubeDataHelper() {}

    public static CubeDataHelper getInstance() {
        return instance;
    }

    public static void setContext(@NonNull Context context) {
        CubeDataHelper.context = context.getApplicationContext();
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

    public void setDataConfig(@NonNull DataConfig config) {
        this.config = config;
    }

    public String getUserCenterUrl() {
        return config != null ? config.getUserCenterUrl() : null;
    }

    public boolean isDebug() {
        return config != null && config.isDebug();
    }
}
