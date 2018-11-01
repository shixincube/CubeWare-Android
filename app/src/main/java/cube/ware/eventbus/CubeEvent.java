package cube.ware.eventbus;

/**
 * Cube事件
 *
 * @author PengZhenjin
 * @date 2016/6/2
 */
public class CubeEvent {
    /**
     * 未读消息的总数量
     */
    public static final String EVENT_UNREAD_MESSAGE_SUM = "event_unread_message_sum";

    /**
     * 添加或更新聊天消息
     */
    public static final String EVENT_ADD_OR_UPDATE_CHAT_MESSAGE = "event_add_or_update_chat_message";

    /**
     * 更新聊天消息名称
     */
    public static final String EVENT_REFRESH_CHAT_TITLE = "event_refresh_chat_title";

    /**
     * 刷新最近会话列表
     */
    public static final String EVENT_REFRESH_RECENT_SESSION_LIST = "event_refresh_recent_session_list";

    /**
     * 刷新最近密聊会话列表
     */
    public static final String EVENT_REFRESH_RECENT_SECRET_SESSION_LIST = "event_refresh_recent_secret_session_list";

    /**
     * 刷新一条最近会话信息
     */
    public static final String EVENT_REFRESH_RECENT_SESSION_SINGLE = "event_refresh_recent_session_single";

    /**
     * 删除一条最近会话信息
     */
    public static final String EVENT_REMOVE_RECENT_SESSION_SINGLE = "event_remove_recent_session_single";

    /**
     * 刷新一条最近密聊会话信息
     */
    public static final String EVENT_REFRESH_RECENT_SECRET_SESSION_SINGLE = "event_refresh_recent_secret_session_single";

    /**
     * 增删好友事件
     */
    public static final String EVENT_ADD_OR_DELETE_FRIEND = "event_add_or_delete_friend";

    /**
     * 删除一条消息更新聊天页面
     */
    public static final String EVENT_DELETE_MESSAGE = "event_delete_message";

    /**
     * 回执一条匿名消息更新
     */
    public static final String EVENT_RECEIPT_SECRET_MESSAGE = "event_receipt_secret_message";

    /**
     * 更新多人音视频会话状态
     */
    public static final String EVENT_GROUP_CALL = "event_unread_group_call";

    /**
     * 更新多人音视频会话中成员状态
     */
    public static final String EVENT_GROUP_CALL_MEMBER = "event_group_call_member";

    /**
     * 批量更新会话页面
     */
    public static final String EVENT_SYNCING_MESSAGE = "event_syncing_message";

    /**
     * 查询无会议
     */
    public static final String EVENT_NO_GROUPCALL = "event_no_conference";

    /**
     * 被群组移除
     */
    public static final String EVENT_REMOVE_BY_GROUP = "event_remove_by_group";

    /**
     * 解散群组
     */
    public static final String EVENT_DISSOLVE_GROUP = "event_dissolve_group";

    /**
     * 更新群组
     */
    public static final String EVENT_UPDATE_GROUP = "event_update_group";

    /**
     * 未读消息数据变更
     */
    public static final String EVENT_UNREAD_MESSAGE_CHANGED = "event_unread_message_changed";

    /**
     * 获取发送文件总数量
     */
    public static final String EVENT_FILE_COUNT = "event_file_count";

    /**
     * 通知文件选中到上限了
     */
    public static final String EVENT_FILE_PROHIBIT = "event_file_prohibit";

    /**
     * 通知发送文件
     */
    public static final String EVENT_SEND_FILE = "event_send_file";

    /**
     * 刷新验证消息
     */
    public static final String EVENT_REFRESH_SYSTEM_MESSAGE = "event_refresh_system_message";

    /**
     * 更新以为CubeUser信息
     */
    public static final String EVENT_REFRESH_CUBE_USER = "event_refresh_cube_user";

    /**
     * 通知消息同步完成
     */
    public static final String EVENT_MESSAGE_SYNC_END = "event_message_sync_end";

    /**
     * 更换头像缓存签名，刷新列表头像
     */
    public static final String EVENT_REFRESH_CUBE_AVATAR = "event_refresh_cube_avatar";
}
