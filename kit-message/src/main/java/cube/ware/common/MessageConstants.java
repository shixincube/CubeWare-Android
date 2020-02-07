package cube.ware.common;

/**
 * 消息相关常量池
 *
 * @author LiuFeng
 * @data 2020/1/21 17:53
 */
public interface MessageConstants {
    interface REGEX {
        String REGEX_AT_MEMBER = "@\\{cube:[^,]*,name:[^\\}]*\\}"; // @成员
        String REGEX_AT_ALL    = "@\\{group:[^,]*,name:[^\\}]*\\}"; // @全体成员
    }

    /**
     * SharedPreferences常量
     */
    interface Sp {
        String PATH_APP   = "CubeWare";                // 根目录
        String PATH_LOG   = "log";                     // 日志目录
        String PATH_IMAGE = "image";                   // 图片目录
        String PATH_FILE  = "file";                    // 文件目录
        String PATH_THUMB = ".thumb";                  // 缩略图目录，隐藏目录

        String CUBE_TOKEN  = "cubeToken";                 //cubeToken
        String USER_CUBEID = "userCubeId";              //userCubeId
        String CUBE_NAME   = "cubeName";                 //cubeToken
        String USER_AVATOR = "userAvator";              //userAvator
        String USER_JSON   = "userJson";              //userAvator

        String SP_CUBE_AT            = "sp_cube_at";
        String SP_CUBE_AT_ALL        = "sp_cube_at_all";    // @全体成员的数量
        String SP_CUBE_RECEIVE_ATALL = "sp_cube_receive_at_all";//接收到的@All

        // 草稿消息
        String MESSAGE_DRAFT = "message_draft_";
    }

    /**
     * 事件通知Key
     */
    interface Event {
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
}
