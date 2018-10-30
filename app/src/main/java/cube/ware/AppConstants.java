package cube.ware;

import cube.ware.utils.SpUtil;

/**
 * 全局常量池
 *
 * @author LiuFeng
 * @date 2018-8-15
 */
public interface AppConstants {

    /**
     * 正式服环境
     */
    public interface Release {
        String APP_ID      = "5d5fe3a5637d4bfda644b0933336865c";            // 引擎id
        String APP_KEY     = "0fac82c9b98f4973afc244800122b48e ";           // 引擎key
        String BASE_URL    = "http://user.shixincube.com";                  // 服务器接口地址
        String LICENSE_URL = "http://license.getcube.cn/auth/license/get";  // 服务license地址
    }

    /**
     * 测试服环境
     */
    public interface Debug {
//        String APP_ID      = "09eab471bc1d4a1b855f6b8f87b01caf800";         // 引擎id
//        String APP_KEY     = "cc9793d0dd534cfc8c8f74e2f2ab1baf800";         // 引擎key
        String APP_ID      = "6365f0cafd8a47b984bdc08a64327881";         // 引擎id
        String APP_KEY     = "9074ad1395f24fbd83a92ddc80facb1f";         // 引擎key
        String BASE_URL    = "http://125.208.1.67:4000";                    // 服务器接口地址
        String LICENSE_URL = "http://dev.license.shixincube.cn/auth/license/get";   // 服务license地址
        
    }

    //超时时间常量
    long TIME_OUT = 3 * 60 * 1000;

    // keyboard常量
    int SHOW_LAYOUT_DELAY         = 200;
    int SHOW_EMOTICON_LAYOUT      = 1001;
    int SHOW_KEYBOARD             = 1002;
    int SHOW_VOICE_LAYOUT         = 1003;
    int SHOW_MORE_FUNCTION_LAYOUT = 1004;
    int HIDE_ALL_LAYOUT           = 1005;

    // 底部输入面板导航栏常量
    int NAVIGATION_TYPE_CAMERA = 1010;
    int NAVIGATION_TYPE_FILE   = 1011;
    int NAVIGATION_TYPE_IMAGE  = 1012;
    int NAVIGATION_TYPE_VOICE  = 1013;

    // 参数
    public static final String EXTRA_CHAT_ID            = "chat_id";
    public static final String EXTRA_CHAT_NAME          = "chat_name";
    public static final String EXTRA_CHAT_TYPE          = "chat_type";
    public static final String EXTRA_CHAT_CUSTOMIZATION = "chat_customization";
    public static final String EXTRA_CHAT_MESSAGE       = "chat_message";
    public static final String EXTRA_CHAT_RX_MANAGER    = "extra_chat_rx_manager";
    public static final long   EXTRA_CHAT_MANAGER_SN    = -1;

    //请求结果码
    public static final int REQUEST_CODE_CAMERA_IMAGE    = 1; // 拍照图片
    public static final int REQUEST_CODE_LOCAL_IMAGE     = 2;  // 本地图片
    public static final int REQUEST_CODE_LOCAL_FILE      = 3;  // 本地文件

    // 草稿消息
    String MESSAGE_DRAFT = "message_draft_";
    //测试环境用户头像固定地址拼接cubeid成完整地址
    String AVATAR_URL = "https://dev.download.shixincube.cn/file/avatar/";

    /**
     * SharedPreferences常量
     */
    public interface Sp {
        String RESOURCE_PATH       = "resource_path";
        String RESOURCE_LOG_PATH   = "resource_log_path";
        String RESOURCE_IMAGE_PATH = "resource_image_path";
        String RESOURCE_FILE_PATH  = "resource_file_path";
        String RESOURCE_THUMB_PATH = "resource_thumb_path";

        String PATH_APP   = "CubeWare";                // 根目录
        String PATH_LOG   = "log";                     // 日志目录
        String PATH_IMAGE = "image";                   // 图片目录
        String PATH_FILE  = "file";                    // 文件目录
        String PATH_THUMB = ".thumb";                  // 缩略图目录，隐藏目录

        String CUBE_TOKEN ="cubeToken";                 //cubeToken
        String USER_CUBEID ="userCubeId";              //userCubeId
        String CUBE_NAME ="cubeName";                 //cubeToken
        String USER_AVATOR ="userAvator";              //userAvator
        String USER_JSON ="userJson";              //userAvator


         String SP_CUBE               = SpUtil.getCubeId();
         String SP_CUBE_AT            = "sp_cube_at" + SP_CUBE;
         String SP_CUBE_AT_ALL        = "sp_cube_at_all" + SP_CUBE;    // @全体成员的数量
         String SP_CUBE_RECEIVE_ATALL = "sp_cube_receive_at_all" + SP_CUBE;//接收到的@All
    }

    /**
     * 事件通知
     */
    public interface Event {

    }

    /**
     * 跳转传值字段
     */
    public interface Value{
        //白板字段
        String BUNDLE="bundle";
        String CALLSTATA_WHITE_BOARD="callState_white_board";
        String WHITEBOARD="whiteboard";
        String INVITE_ID="invite_id";
        String  GROUP_ID="group_id";
        String  CHAT_TYPE="chat_type";
        String INVITE_LIST="invite_list";
        int CALLSTATE_INVITE=1;
        int CALLSTATE_CREATE=2;
        int CALLSTATE_JOIN=3;
        //会议字段
        String CONFERENCE_CALLSTATA="call_state";
        String CONFERENCE_CONFERENCE="conference";
        String CONFERENCE_INVITE_LIST="invite_list";
        String CONFERENCE_INVITE_Id="invite_id";
        String CONFERENCE_GROUP_ID="group_id";

    }

    /**
     * 内部路由地址
     */
    public interface Router {
        // 登陆界面
        String LoginActivity = "/app/LoginActivity";

        // 主界面
        String MainActivity = "/app/MainActivity";

        // cubeIdList界面
        String CubIdListActivity = "/app/CubeIdListActivity";

        // 分享屏幕界面
        String ShareScreenActivity = "/app/ShareScreenActivity";

        //修改头像
        String ChangeAvatorActivity = "/app/ChangeAvatorActivity";

        //修改昵称
        String ModifyNameActivity = "/app/ModifyNameActivity";

        //添加好友
        String AddFriendActivity = "/app/AddFriendActivity";

        //一对一语音通话
        String P2PCallActivity = "/app/P2PCallActivity";

        //测试
        String P2PtestActivity = "/app/P2PtestActivity";

        //选择联系人
        String SelectContactActivity = "/app/SelectContactActivity";

        //群组详情
        String GroupDetailsActivity = "/app/GroupDetailsActivity";

        //白板
        String WhiteBoardActivity = "/app/WhiteBoardActivity";

        //用户详情
        String FriendDetailsActivity = "/app/FriendDetailsActivity";

        //p2p聊天
        String P2PChatActivity = "/app/P2PChatActivity";

        //group聊天
        String GroupChatActivity = "/app/GroupChatActivity";

        //会议创建
        String CreateConferenceActivity="/app/CreateConferenceActivity";

        //选择会议人员
        String SelectMemberActivity="/app/SelectMemberActivity";

        //会议页面
        String ConferenceActivity="/app/ConferenceActivity";

        //设置页面
        String SettingActivity="/app/SettingActivity";
    }

    public interface REGEX{
        String REGEX_AT_MEMBER = "@\\{cube:[^,]*,name:[^\\}]*\\}"; // @成员
        String REGEX_AT_ALL    = "@\\{group:[^,]*,name:[^\\}]*\\}"; // @全体成员
    }

    /**
     * 消息类型
     */
    public interface MessageType {
        int UNKNOWN             = -1;    // 未知类型
        int CHAT_TXT            = 1;    // 文本消息
        int CHAT_FILE           = 2;    // 文件消息
        int CHAT_IMAGE          = 3;    // 图片消息
        int CHAT_AUDIO          = 4;    // 语音消息
        int CHAT_VIDEO          = 5;    // 视频消息
        int CHAT_WHITEBOARD     = 6;    // 白板消息
        int CUSTOM_TIPS         = 7;    // 自定义消息-提示
        int CUSTOM_CALL_VIDEO   = 8;    // 自定义消息-视频通话
        int CUSTOM_CALL_AUDIO   = 9;    // 自定义消息-语音通话
        int CUSTOM_SHARE        = 10;   // 自定义消息-分享
        int CUSTOM_SHAKE        = 11;   // 自定义消息-抖动
        int CHAT_CARD           = 12;   // 卡片消息
        int CHAT_RICH_TEXT      = 13;   // 富文本消息
        int CHAT_EMOJI          = 15;   // Emoji贴图消息
        int RECALL_MESSAGE_TIPS = 16;   // 撤回的提示消息
        int REPLY_MESSAGE       = 17;   // 回复消息
        int SERVICE_NUMBER      = 18;   // 服务号消息
        int GroupShareCard      = 19;   // 推荐群
        int UserShareCard       = 20;   // 推荐联系人
        int GroupTaskNew        = 21;   // 推荐联系人
        int GroupTaskComplete   = 22;   // 推荐联系人
    }
}
