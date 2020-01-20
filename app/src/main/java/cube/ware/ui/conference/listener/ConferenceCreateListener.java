package cube.ware.ui.conference.listener;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.common.sdk.RouterUtil;

import java.util.ArrayList;
import java.util.List;

import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.conference.model.Conference;
import cube.service.user.model.User;
import cube.ware.AppConstants;
import cube.ware.core.data.model.CallStatus;
import cube.ware.service.conference.ConferenceStateListener;

/**
 * author: kun .
 * des:会议创建的监听
 * date:   On 2018/9/10
 */
public class ConferenceCreateListener implements ConferenceStateListener {

    private String mGroupId;
    private User mUser;
    private Context mContext;
    private ArrayList<String> mInviteList;
    private CreateCallback mCreateCallback;

    public void setCreateCallback(CreateCallback createCallback) {
        mCreateCallback = createCallback;
    }

    public ConferenceCreateListener(Context context, String mGroupId, List<String> mInviteList) {
        this.mGroupId=mGroupId;
        this.mContext=context;
        this.mInviteList= (ArrayList<String>) mInviteList;
        mUser=CubeEngine.getInstance().getSession().getUser();
    }

    @Override
    public void onConferenceCreated(Conference conference, User from) {
        //mGroupId 和conference.bindGroupId不相同，会出现串群，所以添加这一个判断  mGroupId为空，代表没有办规定群
        if(TextUtils.isEmpty(mGroupId) || mGroupId.equals(conference.bindGroupId)){
            CubeEngine.getInstance().getConferenceService().join(conference.conferenceId);
            mCreateCallback.onCreate(conference);
        }
    }

    @Override
    public void onConferenceDestroyed(Conference conference, User from) {

    }

    @Override
    public void onConferenceInvited(Conference conference, User from, List<User> invites) {

    }

    @Override
    public void onConferenceRejectInvited(Conference conference, User from, User rejectMember) {

    }

    @Override
    public void onConferenceAcceptInvited(Conference conference, User from, User joinedMember) {

    }

    @Override
    public void onConferenceJoined(Conference conference, User joinedMember) {
        if(TextUtils.isEmpty(mGroupId) ||mUser.cubeId.equals(joinedMember.cubeId)){
//            CubeEngine.getInstance().getConferenceService().addControlAudio(conference.conferenceId,mUser.cubeId);
            mCreateCallback.onJoined(conference);
        }
    }

    @Override
    public void onVideoEnabled(Conference conference, boolean videoEnabled) {
        if(TextUtils.isEmpty(mGroupId) || mGroupId.equals(conference.bindGroupId)){
            Bundle bundle=new Bundle();
            bundle.putString(AppConstants.Value.CONFERENCE_GROUP_ID,mGroupId);
            bundle.putSerializable(AppConstants.Value.CONFERENCE_CALLSTATA, CallStatus.GROUP_VIDEO_CALLING);//创建者
            bundle.putSerializable(AppConstants.Value.CONFERENCE_CONFERENCE,conference);
            bundle.putStringArrayList(AppConstants.Value.CONFERENCE_INVITE_LIST,mInviteList);//传递对象集合
            RouterUtil.navigation(mContext,bundle,AppConstants.Router.ConferenceActivity);
            mCreateCallback.onFinish(conference);
        }
    }

    @Override
    public void onAudioEnabled(Conference conference, boolean videoEnabled) {
        if(TextUtils.isEmpty(mGroupId) || mGroupId.equals(conference.bindGroupId)){
            Bundle bundle=new Bundle();
            bundle.putSerializable(AppConstants.Value.CONFERENCE_CALLSTATA, CallStatus.GROUP_AUDIO_CALLING);//创建者
            bundle.putSerializable(AppConstants.Value.CONFERENCE_CONFERENCE,conference);
            bundle.putString(AppConstants.Value.CONFERENCE_GROUP_ID,mGroupId);
            bundle.putStringArrayList(AppConstants.Value.CONFERENCE_INVITE_LIST,mInviteList);//传递对象集合
            RouterUtil.navigation(mContext,bundle,AppConstants.Router.ConferenceActivity);
            mCreateCallback.onFinish(conference);
        }
    }

    @Override
    public void onConferenceUpdated(Conference conference) {

    }

    @Override
    public void onConferenceQuited(Conference conference, User quitMember) {

    }

    @Override
    public void onConferenceFailed(Conference conference, CubeError error) {
//        if(conference!=null&&mGroupId.equals(conference.bindGroupId)){
            mCreateCallback.onError(conference,error);
//        }
    }
}
