package cube.ware.service.conference;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.mvp.eventbus.EventBusUtil;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.RingtoneUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.conference.ConferenceListener;
import cube.service.conference.Conference;
import cube.service.conference.ConferenceStream;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CallStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * 引擎会议服务处理
 *
 * @author LiuFeng
 * @date 2018-8-09
 */
public class ConferenceHandle implements ConferenceListener {

    private static ConferenceHandle instance = new ConferenceHandle();

    private Context mContext;

    String CONFERENCE_CALLSTATA   = "call_state";
    String CONFERENCE_CONFERENCE  = "conference";
    String CONFERENCE_INVITE_LIST = "invite_list";
    String CONFERENCE_INVITE_Id   = "invite_id";
    String CONFERENCE_GROUP_ID    = "group_id";

    private ConferenceHandle() {
    }

    public static ConferenceHandle getInstance() {
        return instance;
    }

    private List<ConferenceListener> mConferenceStateListeners = new ArrayList<>();

    public void addConferenceStateListener(ConferenceListener listener) {
        if (listener != null && !mConferenceStateListeners.contains(listener)) {
            mConferenceStateListeners.add(listener);
        }
    }

    public void removeConferenceStateListener(ConferenceListener listener) {
        if (listener != null) {
            mConferenceStateListeners.remove(listener);
        }
    }

    /**
     * 启动监听
     *
     * @param
     */
    public void start() {
        mContext = CubeCore.getContext();
        CubeEngine.getInstance().getConferenceService().addConferenceListener(this);
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().getConferenceService().removeConferenceListener(this);
    }

    /**
     * 当创建会议成功时回调
     *
     * @param conference 会议实体
     * @param from       会议创建者
     */
    @Override
    public void onConferenceCreated(Conference conference, User from) {
        LogUtil.d("===会议创建了==");
        List<String> mGroupIds = new ArrayList<>();
        if (!conference.bindGroupId.equals(conference.conferenceId)) { //不相等表示依赖群
            mGroupIds.add(conference.bindGroupId);
            //有人退出，需要更新聊天界面的tipview数值显示
            EventBusUtil.post(CubeConstants.Event.UpdateConferenceTipView, mGroupIds);
        }
        for (int i = 0; i < mConferenceStateListeners.size(); i++) {
            mConferenceStateListeners.get(i).onConferenceCreated(conference, from);
        }
    }

    /**
     * 当会议销毁时回调
     *
     * @param conference 会议实体
     * @param from       会议销毁者（默认为创建者）
     */

    @Override
    public void onConferenceDestroyed(Conference conference, User from) {
        RingtoneUtil.release();
        LogUtil.d("===会议销毁了了==");
        List<String> mGroupIds = new ArrayList<>();
        if (!conference.bindGroupId.equals(conference.conferenceId)) { //不相等表示依赖群
            mGroupIds.add(conference.bindGroupId);
            //有人退出，需要更新聊天界面的tipview数值显示
            EventBusUtil.post(CubeConstants.Event.UpdateConferenceTipView, mGroupIds);
        }
        for (int i = 0; i < mConferenceStateListeners.size(); i++) {
            mConferenceStateListeners.get(i).onConferenceDestroyed(conference, from);
        }
    }

    /**
     * 收到邀请回调(仅邀请者自己和被邀请者收到)
     *
     * @param conference 会议实体
     * @param from       邀请者
     * @param invites    被邀请列表
     */
    @Override
    public void onConferenceInvited(Conference conference, User from, List<User> invites) {
        LogUtil.i("ConferenceInvited", from.toString() + invites.toString());
        //是自己收到了邀请
        if (!from.cubeId.equals(CubeCore.getInstance().getCubeId()) || from.cubeId.equals("10000")) { //from.cubeId.equals("10000")表示自己创建的会议，收到服务器发起的邀请
            //不在会议中,不在单聊中
            if (!CubeCore.getInstance().isCalling()) { //没有正在会议
                if (conference.type.equals(GroupType.SHARE_SCREEN)) {
                    //表示为共享屏幕邀请
                    LogUtil.d("====收到邀请 --- " + conference.conferenceId);
                    for (Member invite : conference.invites) {
                        if (TextUtils.equals(invite.cubeId, CubeEngine.getInstance().getSession().user.cubeId)) {
                            //收到远程桌面邀请回调 并且自己在被邀请者集合里面 并且桌面activity没被启动过
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("shaerdesketop", conference);
                            bundle.putString("inviteId", from.cubeId);
                            bundle.putSerializable("statues", CallStatus.REMOTE_DESKTOP_INCOMING);
                            ARouter.getInstance().build(CubeConstants.Router.ShareScreenActivity).withBundle("desketop_data", bundle).navigation();
                            RingtoneUtil.play(R.raw.ringing, CubeCore.getContext());
                            break;
                        }
                    }
                }
                else { //多人语音或者多人视频邀请跳转到相应的会议界面
                    Bundle bundle = new Bundle();
                    if (conference.bindGroupId.equals(conference.conferenceId)) { //这俩参数相同，表示不依赖群组
                        bundle.putString(CONFERENCE_GROUP_ID, "");
                    }
                    else {
                        bundle.putString(CONFERENCE_GROUP_ID, conference.bindGroupId);
                    }
                    bundle.putString(CONFERENCE_INVITE_Id, from.cubeId);
                    bundle.putSerializable(CONFERENCE_CONFERENCE, conference);
                    bundle.putSerializable(CONFERENCE_CALLSTATA, CallStatus.GROUP_CALL_INCOMING); //邀请
                    RouterUtil.navigation(CubeConstants.Router.ConferenceActivity, bundle);
                    RingtoneUtil.play(R.raw.ringing, mContext);
                    for (int i = 0; i < mConferenceStateListeners.size(); i++) {
                        mConferenceStateListeners.get(i).onConferenceInvited(conference, from, invites);
                    }
                }
            }
            else {
                //再次邀请就拒绝
                CubeEngine.getInstance().getConferenceService().rejectInvite(conference.conferenceId, from.cubeId);
            }
            //发起者回调
        }
        else if (!conference.type.equals(GroupType.SHARE_SCREEN)) {
            //发送EventBus到会议列表
            EventBusUtil.post(CubeConstants.Event.InviteConferenceEvent, conference);
            for (int i = 0; i < mConferenceStateListeners.size(); i++) {
                mConferenceStateListeners.get(i).onConferenceInvited(conference, from, invites);
            }
        }
    }

    /**
     * 拒绝要邀请回调（仅拒绝者和邀请者收到）
     *
     * @param conference   会议实体
     * @param from         邀请者
     * @param rejectMember 拒绝加入者
     */
    @Override
    public void onConferenceRejectInvited(Conference conference, User from, User rejectMember) {
        RingtoneUtil.release();
        //发送EventBus到会议列表
        EventBusUtil.post(CubeConstants.Event.InviteConferenceEvent, conference);
        for (int i = 0; i < mConferenceStateListeners.size(); i++) {
            mConferenceStateListeners.get(i).onConferenceRejectInvited(conference, from, rejectMember);
        }
    }

    /**
     * 同意邀请时回调
     *
     * @param conference   会议实体
     * @param from         邀请者
     * @param joinedMember 同意加入者
     */
    @Override
    public void onConferenceAcceptInvited(Conference conference, User from, User joinedMember) {
        RingtoneUtil.release();
        LogUtil.d("====同意邀请==" + conference.type);
        for (int i = 0; i < mConferenceStateListeners.size(); i++) {
            mConferenceStateListeners.get(i).onConferenceAcceptInvited(conference, from, joinedMember);
        }
    }

    /**
     * 通账号除加入设备之外的其他设备回调
     *
     * @param conference   会议实体
     * @param joinedMember 入会者
     */
    @Override
    public void onConferenceJoined(Conference conference, User joinedMember) {
        //发送EventBus到会议列表
        EventBusUtil.post(CubeConstants.Event.InviteConferenceEvent, conference);
        LogUtil.d("===加入会议成功准备去调用会控方法==" + conference.bindGroupId);
        List<String> mGroupIds = new ArrayList<>();
        if (!conference.bindGroupId.equals(conference.conferenceId)) { //不相等表示依赖群
            mGroupIds.add(conference.bindGroupId);
            //有人退出，需要更新聊天界面的tipview数值显示
            EventBusUtil.post(CubeConstants.Event.UpdateConferenceTipView, mGroupIds);
        }
        for (int i = 0; i < mConferenceStateListeners.size(); i++) {
            mConferenceStateListeners.get(i).onConferenceJoined(conference, joinedMember);
        }
    }

    /**
     * 开启视频回调
     *
     * @param conference   会议实体
     * @param videoEnabled 是否开启视频
     */
    @Override
    public void onVideoEnabled(Conference conference, boolean videoEnabled) {
        for (int i = 0; i < mConferenceStateListeners.size(); i++) {
            mConferenceStateListeners.get(i).onVideoEnabled(conference, videoEnabled);
        }
    }

    /**
     * 开启音频回调
     *
     * @param conference   会议实体
     * @param audioEnabled 是否开启音频
     */
    @Override
    public void onAudioEnabled(Conference conference, boolean audioEnabled) {
        for (int i = 0; i < mConferenceStateListeners.size(); i++) {
            mConferenceStateListeners.get(i).onAudioEnabled(conference, audioEnabled);
        }
    }

    /**
     * 会议成员状态改变/有会控时回调
     *
     * @param conference 会议实体
     */
    @Override
    public void onConferenceUpdated(Conference conference) {
        for (int i = 0; i < mConferenceStateListeners.size(); i++) {
            mConferenceStateListeners.get(i).onConferenceUpdated(conference);
        }
    }

    /**
     * 退出时回调
     *
     * @param conference 会议实体
     * @param quitMember 退出者
     */
    @Override
    public void onConferenceQuited(Conference conference, User quitMember) {
        RingtoneUtil.release();
        //发送EventBus到会议列表
        EventBusUtil.post(CubeConstants.Event.InviteConferenceEvent, conference);
        List<String> mGroupIds = new ArrayList<>();
        if (!conference.bindGroupId.equals(conference.conferenceId)) { //不相等表示依赖群
            mGroupIds.add(conference.bindGroupId);
            //有人退出，需要更新聊天界面的tipview数值显示
            EventBusUtil.post(CubeConstants.Event.UpdateConferenceTipView, mGroupIds);
        }
        for (int i = 0; i < mConferenceStateListeners.size(); i++) {
            mConferenceStateListeners.get(i).onConferenceQuited(conference, quitMember);
        }
    }

    @Override
    public void onConferenceAddStream(ConferenceStream conferenceStream) {

    }

    @Override
    public void onConferenceRemoveStream(ConferenceStream conferenceStream) {

    }

    /**
     * 错误回调
     *
     * @param conference 会议实体
     * @param error      错误信息
     */
    @Override
    public void onConferenceFailed(Conference conference, CubeError error) {
        for (int i = 0; i < mConferenceStateListeners.size(); i++) {
            mConferenceStateListeners.get(i).onConferenceFailed(conference, error);
        }
    }
}