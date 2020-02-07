package cube.ware.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import cube.ware.data.room.model.CubeRecentSession;
import java.util.List;

/**
 * Created by dth
 * Des: 最近会话信息操作dao类
 * Date: 2018/8/30.
 */
@Dao
public abstract class CubeSessionDao {

    /**
     * 插入数据
     *
     * @param cubeRecentSessions
     */
    @Insert
    public abstract void save(CubeRecentSession... cubeRecentSessions);

    /**
     * 更新数据
     *
     * @param cubeRecentSessions
     */
    @Update
    public abstract void update(CubeRecentSession... cubeRecentSessions);

    /**
     * 插入或更新数据
     *
     * @param cubeRecentSessions
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveOrUpdate(CubeRecentSession... cubeRecentSessions);

    /**
     * 批量插入或更新数据
     *
     * @param cubeRecentSessions
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveOrUpdate(List<CubeRecentSession> cubeRecentSessions);

    /**
     * 删除数据
     *
     * @param cubeRecentSessions
     */
    @Delete
    public abstract void delete(CubeRecentSession... cubeRecentSessions);

    /**
     * 查询全部数据
     *
     * @return
     */
    @Query("SELECT * FROM CubeRecentSession order by timestamp desc")
    public abstract List<CubeRecentSession> queryAll();

    /**
     * 通过会话id 查询数据
     *
     * @param sessionId
     *
     * @return
     */
    @Query("SELECT * FROM CubeRecentSession WHERE sessionId = :sessionId")
    public abstract CubeRecentSession queryBySessionId(String sessionId);
}
