package cube.ware.data.room;

import cube.ware.data.room.dao.CubeConferenceDao;
import cube.ware.data.room.dao.CubeMessageDao;
import cube.ware.data.room.dao.CubeRecentSessionDao;
import cube.ware.data.room.dao.CubeUserDao;

/**
 * Created by dth
 * Des: 数据库工厂类
 * Date: 2018/8/30.
 */

public class AppDataBaseFactory {

    /**
     * 获取 CubeRecentSessionDao
     * @return
     */
    public static CubeRecentSessionDao getCubeRecentSessionDao() {
        return AppDataBase.getInstance().getCubeRecentSessionDao();
    }

    public static CubeMessageDao getCubeMessageDao() {
        return AppDataBase.getInstance().getCubeMessageDao();
    }

    public static CubeUserDao getCubeUserDao() {
        return AppDataBase.getInstance().getCubeUserDao();
    }

    public static CubeConferenceDao getCubeConferenceDao() {
        return AppDataBase.getInstance().getCubeConferenceDao();
    }

}
