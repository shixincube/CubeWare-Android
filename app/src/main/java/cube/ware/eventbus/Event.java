package cube.ware.eventbus;

/**
 * Cube事件
 *
 * @author PengZhenjin
 * @date 2016/6/2
 */
public interface Event {
    /**
     * 未读消息的总数量
     */
    String EVENT_UNREAD_MESSAGE_SUM = "event_unread_message_sum";


    /**
     * 刷新最近会话列表
     */
    String EVENT_REFRESH_RECENT_SESSION_LIST = "event_refresh_recent_session_list";


    /**
     * 刷新一条最近会话信息
     */
    String EVENT_REFRESH_RECENT_SESSION_SINGLE = "event_refresh_recent_session_single";

    /**
     * 删除一条最近会话信息
     */
    String EVENT_REMOVE_RECENT_SESSION_SINGLE = "event_remove_recent_session_single";


    /**
     * 批量更新会话页面
     */
    String EVENT_SYNCING_MESSAGE = "event_syncing_message";


    /**
     * 更新群组
     */
    String EVENT_UPDATE_GROUP = "event_update_group";


    /**
     * 刷新验证消息
     */
    String EVENT_REFRESH_SYSTEM_MESSAGE = "event_refresh_system_message";

    /**
     * 更新以为CubeUser信息
     */
    String EVENT_REFRESH_CUBE_USER = "event_refresh_cube_user";


    /**
     * 更换头像缓存签名，刷新列表头像
     */
    String EVENT_REFRESH_CUBE_AVATAR = "event_refresh_cube_avatar";
}
