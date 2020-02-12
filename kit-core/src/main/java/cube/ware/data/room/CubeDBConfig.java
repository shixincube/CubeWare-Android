package cube.ware.data.room;

/**
 * 数据库配置信息
 *
 * @author LiuFeng
 * @date 2018-6-1
 */
public class CubeDBConfig {

    /**
     * 数据库版本号
     * 注意：数据库表结构变化后，需要手动修改数据库版本号
     */
    public static final int DB_VERSION = 1;

    /**
     * 获取数据库名称
     * 命名方式：cubeId + 后缀
     *
     * @return
     */
    public static String getDBName(String cubeId) {
        return cubeId + "_cube_ware.db";
    }
}
