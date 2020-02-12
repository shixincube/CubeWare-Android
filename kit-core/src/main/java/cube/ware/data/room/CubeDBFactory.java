package cube.ware.data.room;

import cube.ware.data.room.dao.CubeConferenceDao;
import cube.ware.data.room.dao.CubeMessageDao;
import cube.ware.data.room.dao.CubeSessionDao;
import cube.ware.data.room.dao.CubeUserDao;

/**
 * 数据库工厂类
 *
 * @author LiuFeng
 * @data 2020/2/7 16:12
 */
public class CubeDBFactory {

    /**
     * 获取最近会话Dao
     *
     * @return
     */
    public static CubeSessionDao getCubeRecentSessionDao() {
        return CubeDataBase.getInstance().getCubeRecentSessionDao();
    }

    /**
     * 获取消息Dao
     *
     * @return
     */
    public static CubeMessageDao getCubeMessageDao() {
        return CubeDataBase.getInstance().getCubeMessageDao();
    }

    /**
     * 获取用户Dao
     *
     * @return
     */
    public static CubeUserDao getCubeUserDao() {
        return CubeDataBase.getInstance().getCubeUserDao();
    }

    /**
     * 获取会议Dao
     *
     * @return
     */
    public static CubeConferenceDao getCubeConferenceDao() {
        return CubeDataBase.getInstance().getCubeConferenceDao();
    }
}
