package cube.ware.data.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.text.TextUtils;
import cube.ware.core.CubeCore;
import cube.ware.data.room.dao.CubeConferenceDao;
import cube.ware.data.room.dao.CubeMessageDao;
import cube.ware.data.room.dao.CubeSessionDao;
import cube.ware.data.room.dao.CubeUserDao;
import cube.ware.data.room.model.CubeConference;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.data.room.model.CubeRecentSession;
import cube.ware.data.room.model.CubeUser;
import cube.ware.utils.CoreSpUtil;

/**
 * CubeWare数据库
 *
 * @author LiuFeng
 * @data 2020/2/12 16:29
 */
@Database(entities = { CubeRecentSession.class, CubeMessage.class, CubeUser.class, CubeConference.class }, version = CubeDBConfig.DB_VERSION, exportSchema = false)
public abstract class CubeDataBase extends RoomDatabase {
    private static volatile CubeDataBase INSTANCE;

    /**
     * 数据库单例
     *
     * @return
     */
    public static CubeDataBase getInstance() {
        if (INSTANCE == null) {
            synchronized (CubeDataBase.class) {
                if (INSTANCE == null) {
                    initInstance();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化实例
     */
    private static void initInstance() {
        String cubeId = CubeCore.getInstance().getCubeId();
        if (TextUtils.isEmpty(cubeId)) {
            throw new RuntimeException("数据库初始化失败，请先进行用户信息初始化！");
        }

        Context context = CubeCore.getContext();
        String dbName = CubeDBConfig.getDBName(cubeId);
        INSTANCE = Room.databaseBuilder(context, CubeDataBase.class, dbName)
                       //添加数据库迁移
                       .addMigrations(CubeDBMigration.getAllMigrations())
                       // 构建实例
                       .build();
        CoreSpUtil.setDBName(dbName);
    }

    /**
     * 关闭释放数据库
     */
    public synchronized static void release() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }

    /**
     * 获取最近会话dao类
     *
     * @return
     */
    public abstract CubeSessionDao getCubeRecentSessionDao();

    /**
     * 获取消息dao类
     *
     * @return
     */
    public abstract CubeMessageDao getCubeMessageDao();

    /**
     * 获取用户dao类
     *
     * @return
     */
    public abstract CubeUserDao getCubeUserDao();

    /**
     * 获取会议dao类
     *
     * @return
     */
    public abstract CubeConferenceDao getCubeConferenceDao();
}
