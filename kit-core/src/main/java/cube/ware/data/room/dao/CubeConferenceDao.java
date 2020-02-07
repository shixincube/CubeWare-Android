package cube.ware.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import cube.ware.data.room.model.CubeConference;
import java.util.List;

/**
 * Created by dth
 * Des: 用户操作dao类
 * Date: 2018/9/3.
 */
@Dao
public abstract class CubeConferenceDao {

    /**
     * 插入数据
     *
     * @param cubeConference
     */
    @Insert
    public abstract void save(CubeConference... cubeConference);

    /**
     * 更新数据
     *
     * @param
     */
    @Update
    public abstract void update(CubeConference... cubeConference);

    /**
     * 插入或更新数据
     *
     * @param
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveOrUpdate(CubeConference... cubeConference);

    /**
     * 批量插入或更新数据
     *
     * @param
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveOrUpdate(List<CubeConference> cubeConferences);

    /**
     * 删除数据
     *
     * @param
     */
    @Delete
    public abstract void delete(CubeConference... cubeConference);

    /**
     * 查询全部数据
     *
     * @return
     */
    @Query("SELECT * FROM CubeConference")
    public abstract List<CubeConference> queryAll();
}
