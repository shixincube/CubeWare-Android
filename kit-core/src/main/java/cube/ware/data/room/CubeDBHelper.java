package cube.ware.data.room;

import cube.ware.utils.CoreSpUtil;

/**
 * 数据库帮助类
 *
 * @author LiuFeng
 * @date 2018-5-15
 */
public class CubeDBHelper {

    /**
     * 判断数据库是否改变
     *
     * @param cubeId
     *
     * @return
     */
    public static boolean isDBChanged(String cubeId) {
        return !CubeDBConfig.getDBName(cubeId).equals(CoreSpUtil.getDBName());
    }

    /**
     * 检查更新DB
     * 备注：账号改变时，则关闭数据库，调用时再重新实例化
     *
     * @param cubeId
     */
    public static void checkUpdateDB(String cubeId) {
        if (isDBChanged(cubeId)) {
            CubeDataBase.release();
        }
    }

    /**
     * 关闭DB
     */
    public static void closeDB() {
        CubeDataBase.release();
    }
}
