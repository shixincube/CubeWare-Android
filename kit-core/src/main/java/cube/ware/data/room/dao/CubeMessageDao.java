package cube.ware.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import cube.ware.data.room.model.CubeMessage;
import java.util.List;

/**
 * Created by dth
 * Des: 消息操作dao类
 * Date: 2018/8/30.
 */
@Dao
public abstract class CubeMessageDao {

    /**
     * 插入数据
     *
     * @param CubeMessages
     */
    @Insert
    public abstract void save(CubeMessage... CubeMessages);

    /**
     * 更新数据
     *
     * @param CubeMessages
     */
    @Update
    public abstract void update(CubeMessage... CubeMessages);

    /**
     * 插入或更新数据
     *
     * @param CubeMessages
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveOrUpdate(CubeMessage... CubeMessages);

    /**
     * 批量插入或更新数据
     *
     * @param CubeMessages
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveOrUpdate(List<CubeMessage> CubeMessages);

    /**
     * 删除数据
     *
     * @param CubeMessages
     */
    @Delete
    public abstract void delete(CubeMessage... CubeMessages);

    /**
     * 通过sn删除消息
     *
     * @param sn
     */
    @Query("DELETE FROM CubeMessage WHERE messageSN = :sn")
    public abstract void deleteMessageBySn(long sn);

    /**
     * 查询全部数据
     *
     * @return
     */
    @Query("SELECT * FROM CubeMessage")
    public abstract List<CubeMessage> queryAll();

    /**
     * 根据sn查消息
     *
     * @return
     */
    @Query("SELECT * FROM CubeMessage where messageSN = :sn")
    public abstract CubeMessage queryMessageBySn(long sn);

    /**
     * 查询小于time的chatid会话 所有未回执消息
     *
     * @param chatId
     * @param time
     * @param isReceipted
     *
     * @return
     */
    @Query("SELECT * FROM CubeMessage where ((groupId is null and (senderId = :chatId or receiverId = :chatId)) or (groupId is not null and groupId = :chatId)) and isReceipt == :isReceipted and timestamp <= :time order by timestamp desc")
    public abstract List<CubeMessage> queryMessages(String chatId, long time, boolean isReceipted);

    /**
     * 分页查询消息
     *
     * @param chatId 会话id
     * @param time   开始查询时间
     * @param limit  每页条数
     *
     * @returnde
     */
    @Query("SELECT * FROM CubeMessage where ((groupId is null and (senderId = :chatId or receiverId = :chatId)) or (groupId is not null and groupId = :chatId)) and timestamp < :time order by timestamp desc limit :limit")
    public abstract List<CubeMessage> queryMessages(String chatId, long time, int limit);

    /**
     * 查询当前会话所有未读消息
     *
     * @param chatId      会话id
     * @param isReceipted 是否回执
     *
     * @returnde
     */
    @Query("SELECT * FROM CubeMessage where ((groupId is null and senderId = :chatId) or (groupId is not null and groupId = :chatId)) and isReceipt == :isReceipted")
    public abstract List<CubeMessage> queryMessages(String chatId, boolean isReceipted);

    /**
     * 根据消息类型查询消息
     *
     * @param chatId      会话id
     * @param messageType 消息类型
     *
     * @returnde
     */
    @Query("SELECT * FROM CubeMessage where ((groupId is null and (senderId = :chatId or receiverId = :chatId)) or (groupId is not null and groupId = :chatId)) and messageType == :messageType order by timestamp asc")
    public abstract List<CubeMessage> queryMessageListByType(String chatId, String messageType);

    /**
     * 查询当前会话所有未读消息数量
     *
     * @param chatId      会话id
     * @param isReceipted 是否回执
     *
     * @returnde
     */
    @Query("SELECT count(*) FROM CubeMessage where ((groupId is null and senderId = :chatId) or (groupId is not null and groupId = :chatId and senderId != :sender)) and isReceipt == :isReceipted")
    public abstract int queryUnReadMessagesCount(String chatId, String sender, boolean isReceipted);

    /**
     * 查询所有未读消息数量
     *
     * @param chatIds
     * @param sender      针对group查询 如果是自己发送的消息不计入
     * @param isReceipted
     *
     * @return
     */
    @Query("SELECT count(*) FROM CubeMessage where ((groupId is null and senderId in (:chatIds)) or (groupId is not null and groupId in (:chatIds) and senderId != :sender)) and isReceipt == :isReceipted")
    public abstract int queryAllUnReadMessagesCount(List<String> chatIds, String sender, boolean isReceipted);
}
