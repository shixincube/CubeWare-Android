package cube.ware.core;

/**
 * 常量池
 *
 * @author LiuFeng
 * @data 2020/1/19 18:18
 */
public interface CubeConstants {

    /**
     * 路由地址
     */
    interface Router {

        //选择联系人
        String SelectContactActivity = "/app/SelectContactActivity";

        //选择人员
        String SelectMemberActivity = "/app/SelectMemberActivity";

        //添加好友
        String AddFriendActivity = "/app/AddFriendActivity";

        //用户详情
        String FriendDetailsActivity = "/app/FriendDetailsActivity";

        //修改昵称
        String ModifyNameActivity = "/app/ModifyNameActivity";

        //一对一语音通话
        String P2PCallActivity = "/call/P2PCallActivity";

        //白板
        String WhiteBoardActivity = "/whiteboard/WhiteBoardActivity";

        // 分享屏幕界面
        String ShareScreenActivity = "/sharedesktop/ShareScreenActivity";

        //会议页面
        String ConferenceActivity = "/conference/ConferenceActivity";

        //会议创建
        String CreateConferenceActivity = "/conference/CreateConferenceActivity";

        //群组详情
        String GroupDetailsActivity = "/group/GroupDetailsActivity";

        //p2p聊天
        String P2PChatActivity = "/message/P2PChatActivity";

        //group聊天
        String GroupChatActivity = "/message/GroupChatActivity";
    }

    /**
     * 事件通知Key
     */
    interface Event {
        String UpdateWhiteBoardTipView = "UpdateWhiteBoardTipView";

        String UpdateConferenceTipView = "UpdateConferenceTipView";

        String InviteConferenceEvent = "InviteConferenceEvent";

        String CreateConferenceEvent = "CreateConferenceEvent";

        String SelectMemberEvent = "SelectMemberEvent";
    }
}
