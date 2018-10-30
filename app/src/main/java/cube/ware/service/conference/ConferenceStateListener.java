package cube.ware.service.conference;

import java.util.List;

import cube.service.common.model.CubeError;
import cube.service.conference.model.Conference;
import cube.service.user.model.User;

/**
 * author: kun .
 * date:   On 2018/9/5
 */
public interface ConferenceStateListener {
    /**
     * 当创建会议成功时回调
     *
     * @param conference 会议实体
     * @param from       会议创建者
     */
    public void onConferenceCreated(Conference conference, User from);

    /**
     * 当会议销毁时回调
     *
     * @param conference 会议实体
     * @param from       会议销毁者（默认为创建者）
     */
    public void onConferenceDestroyed(Conference conference, User from);

    /**
     * 收到邀请回调(仅邀请者自己和被邀请者收到)
     *
     * @param conference 会议实体
     * @param from       邀请者
     * @param invites    被邀请列表Conf
     */
    public void onConferenceInvited(Conference conference, User from, List<User> invites);

    /**
     * 拒绝要邀请回调（仅拒绝者和邀请者收到）
     *
     * @param conference   会议实体
     * @param from         邀请者
     * @param rejectMember 拒绝加入者
     */
    public void onConferenceRejectInvited(Conference conference, User from, User rejectMember);

    /**
     * 同意邀请时回调
     *
     * @param conference   会议实体
     * @param from         邀请者
     * @param joinedMember 同意加入者
     */
    public void onConferenceAcceptInvited(Conference conference, User from, User joinedMember);

    /**
     * 通账号除加入设备之外的其他设备回调
     *
     * @param conference   会议实体
     * @param joinedMember 入会者
     */
    public void onConferenceJoined(Conference conference, User joinedMember);

    /**
     * 开启视频回调
     *
     * @param conference   会议实体
     * @param videoEnabled 是否开启视频
     */
    public void onVideoEnabled(Conference conference, boolean videoEnabled);

    /**
     * 开启音频回调
     *
     * @param conference   会议实体
     * @param videoEnabled 是否开启音频
     */
    public void onAudioEnabled(Conference conference, boolean videoEnabled);
    /**
     * 会议成员状态改变/有会控时回调
     *
     * @param conference 会议实体
     */
    public void onConferenceUpdated(Conference conference);

    /**
     * 退出时回调
     *
     * @param conference 会议实体
     * @param quitMember 退出者
     */
    public void onConferenceQuited(Conference conference, User quitMember);

    /**
     * 错误回调
     *
     * @param conference 会议实体
     * @param error      错误信息
     */
    public void onConferenceFailed(Conference conference, CubeError error);
}
