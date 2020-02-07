package cube.ware.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import cube.ware.data.room.model.CubeUser;
import java.util.List;

/**
 * Created by dth
 * Des: 用户操作dao类
 * Date: 2018/9/3.
 */
@Dao
public abstract class CubeUserDao {

    /**
     * 插入数据
     *
     * @param cubeUsers
     */
    @Insert
    public abstract void save(CubeUser... cubeUsers);

    /**
     * 更新数据
     *
     * @param cubeUsers
     */
    @Update
    public abstract void update(CubeUser... cubeUsers);

    /**
     * 插入或更新数据
     *
     * @param cubeUsers
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveOrUpdate(CubeUser... cubeUsers);

    /**
     * 批量插入或更新数据
     *
     * @param cubeUsers
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveOrUpdate(List<CubeUser> cubeUsers);

    /**
     * 删除数据
     *
     * @param cubeUsers
     */
    @Delete
    public abstract void delete(CubeUser... cubeUsers);

    /**
     * 查询全部数据
     *
     * @return
     */
    @Query("SELECT * FROM CubeUser")
    public abstract List<CubeUser> queryAll();

    /**
     * 查询指定用户
     *
     * @param cubeId
     *
     * @return
     */
    @Query("SELECT * FROM CubeUser WHERE cubeId = :cubeId")
    public abstract CubeUser queryUser(String cubeId);
}
