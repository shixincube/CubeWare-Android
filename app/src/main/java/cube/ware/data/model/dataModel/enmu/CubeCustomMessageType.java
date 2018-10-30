package cube.ware.data.model.dataModel.enmu;

/**
 * 自定义消息的类型
 *
 * @author PengZhenjin
 * @date 2016/6/16
 */
public enum CubeCustomMessageType {
    Login("login"),                           // 登录
    Logout("logout"),                         // 登出
    WriteEvent("writing"),                // 输入事件
    NewGroup("new_group"),                    // 创建群组
    UpdateGroupNotice("update_group_notice"), // 修改群公告
    UpdateGroupFace("group_face_update"),  // 更新群头像
    UpdateGroupName("update_group_name"),    // 变更群名称
    DelGroupMember("del_group_member"),      // 删除群成员
    GroupMemberQuit("group_member_quit"),      // 退出群
    AddGroupMember("add_group_member"),      // 添加群成员
    VerificationUpdate("verification_update"), // 验证消息
    ApplyFriend("apply_friend"),             // 申请添加好友
    AddFriend("add_friend"),                 // 添加好友成功
    AddGroupManager("add_group_manager"),    // 添加管理员
    TransferGroupMaster("transfer_group_master"), // 转移群组
    DelGroupManager("del_group_manager"),    // 删除管理员
    DismissGroup("dismiss_group"),           // 解散群
    DelFriend("del_friend"),                 // 删除好友
    RefuseFriend("refuse_friend"),           // 拒绝成为好友
    VideoCall("video_call"),                 // 视频通话
    AudioCall("audio_call"),                 // 语音通话
    CallAbnormal("call_abnormal"),           // 通话异常
    GroupShareQr("group_share_qr"),          // 分享群二维码
    UserShareQr("user_share_qr"),            // 分享个人二维码
    UpdateUserInfo("update_user_info"),      // 更新用户个人资料
    UpdateGroupMemberRemark("update_group_member_remark"),  // 更新群成员备注
    ApplyOrAgreeToGroup("apply_or_agree_to_group"),  // 被邀请者同意入群或者申请入群
    InviteToGroup("invite_to_group"),  // 邀请入群
    RefuseInviteToGroup("refuse_invite_to_group"),  // 被邀请者拒绝入群
    ApplyConference("apply_conference"),  // 创建多人音视频
    CloseConference("close_conference"),  // 关闭多人音视频
    SharerRD("sharerRD"),  // 收到远程桌面分享
    RevokeRD("revokeRD"),  // 关闭远程桌面
    SecretTip("secret_tip"),  // 密聊提示
    ShakeEvent("shake"),//抖动事件
    UpdateUserPwd("update_user_pwd"), // 在其他端修改密码
    DownLoad_Complete("download"), //接收方文件下载完成
    GroupFileAdd("group_file_add"), // 新增群文件
    GroupFileDelete("group_file_delete"), // 删除群文件
    validationemali("update_user_binding"),//验证邮箱
    AddorFeruseFriend("verification_update"),// 验证消息变更【群申请、好友申请、好友拒绝、群申请拒绝、邀请者同意入群】这几个暂时使用这一个
    Servicenumber("official_push"),//服务号
    GroupShareCard("group_share_card"),//推荐群
    UserShareCard("user_share_card"),//推荐联系人
    GroupTaskNew("group_task_new"),// 新建群任务
    GroupTaskComplete("group_task_complete"),// 标记群任务
    DOWNLOAD_COMPLETE("download"), //接收方文件下载完成
    ConferenceVideoCall("video-call"),// 会议视频通话
    ConferenceVoiceCall("voice-call"),// 会议语音通话
    SHAREDESKCREATE("share-desk-create"),
    SHAREDESKDESTORY("share-desk-destory"),
    WhiteBoardApply("create_wb"),// 群主白板创建
    WhiteBoardClose("destory_wb"),// 群主白板结束
    P2PWhiteBoardApply("p2p_create_wb"),// 白板p2p创建
    P2PWhiteBoardClose("p2p_destory_wb"),// 白板p2p销毁
    No("");

    public String type;

    CubeCustomMessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
