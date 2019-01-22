package cube.ware.data.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.text.TextUtils;
import android.util.Log;

import com.common.utils.utils.log.LogUtil;

import cube.ware.App;
import cube.ware.data.room.dao.CubeConferenceDao;
import cube.ware.data.room.dao.CubeMessageDao;
import cube.ware.data.room.dao.CubeRecentSessionDao;
import cube.ware.data.room.dao.CubeUserDao;
import cube.ware.data.room.model.CubeConference;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.data.room.model.CubeRecentSession;
import cube.ware.data.room.model.CubeUser;
import cube.ware.utils.SpUtil;

/**
 * Created by dth
 * Des: 数据库操作类
 * Date: 2018/8/30.
 */
@Database(entities = {CubeRecentSession.class, CubeMessage.class, CubeUser.class, CubeConference.class}, version = AppDataBase.DB_VERSION, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    private static final String TAG = "AppDataBase";

    private static volatile AppDataBase INSTANCE;

    private static final Object sLock = new Object();

    private static final String DB_NAME = "cube_ware_v";//数据库名

    public static final int DB_VERSION = 1;//数据库版本



    public static AppDataBase getInstance() {
        if (INSTANCE == null) {
            synchronized (sLock) {
                if (INSTANCE == null) {
                    initInstance();
                }
            }
        }
        return INSTANCE;
    }

    private static void initInstance() {
        String cubeId = SpUtil.getCubeId();
        if (TextUtils.isEmpty(cubeId)) {
            throw new RuntimeException("数据库初始化失败，请先进行用户信息初始化！");
        }

        LogUtil.i("数据库name -----> :  " + getDbName(cubeId));

        INSTANCE = Room.databaseBuilder(App.getContext(), AppDataBase.class, getDbName(cubeId))
                //                            .addMigrations(MIGRATION_1_2)//添加数据库迁移
                .fallbackToDestructiveMigration()//如果更新版本，没有加入迁移，则会删除所有数据
                .build();
    }

    public static String getDbName(String cubeId) {
        return  DB_NAME + DB_VERSION + "_"+cubeId+ ".db";
    }

    public void release() {
        close();
        INSTANCE = null;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Log.i(TAG, "migrate: MIGRATION_1_2");
            //此处执行对于数据库中的所有更新的代码
//            database.execSQL("...");
        }
    };


    /**
     * 获取最近会话dao类
     * @return
     */
    public abstract CubeRecentSessionDao getCubeRecentSessionDao();

    /**
     * 获取消息dao类
     * @return
     */
    public abstract CubeMessageDao getCubeMessageDao();

    /**
     * 获取用户dao类
     * @return
     */
    public abstract CubeUserDao getCubeUserDao();

    /**
     * 获取会议dao类
     * @return
     */
    public abstract CubeConferenceDao getCubeConferenceDao();

}
