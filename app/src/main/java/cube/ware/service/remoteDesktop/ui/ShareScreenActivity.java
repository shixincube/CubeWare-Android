package cube.ware.service.remoteDesktop.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.mvp.base.BaseActivity;
import com.common.mvp.base.BasePresenter;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.RingtoneUtil;
import com.common.utils.utils.ScreenSwitchUtils;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.conference.model.Conference;
import cube.service.media.MediaService;
import cube.service.user.model.User;
import cube.ware.AppConstants;
import cube.ware.CubeUI;
import cube.ware.R;
import cube.ware.core.data.model.CallStatus;
import cube.ware.data.repository.CubeUserRepository;
import cube.ware.data.room.model.CubeUser;
import cube.ware.service.conference.ConferenceHandle;
import cube.ware.service.conference.ConferenceStateListener;
import cube.ware.utils.SpUtil;
import rx.functions.Action1;

/**
 * 共享屏幕主页面，展示屏幕
 * Created by zzy on 2018/8/27.
 */
@Route(path = AppConstants.Router.ShareScreenActivity)
public class ShareScreenActivity extends BaseActivity implements ScreenSwitchUtils.ScreenChangedListener, ConferenceStateListener {

    private ViewStub mShareIncomingVs;                 //共享屏幕邀请界面
    private ViewStub mshareCOnnectVs;                  //共享屏幕接通界面
    private ViewStub mshareJoinVs;                     //共享屏幕主动加入界面


    private FrameLayout connect_view;                  //连接成功FrameLayout

    private RecyclerView mRecyclerView_joined;         //已加入成员列表
    private RecyclerView mRecyclerView_toJoin;         //待加入成员列表

    private TextView tv_joined;                        //已加入
    private TextView tv_toJoin;                        //待加入

    private Button mShareCancelBtn;                    // 取消按钮
    private Button mShareHangUpBtn;                    // 挂断
    private Button mShareJoinBtn;                      // 加入按钮

    private ImageView mPeerHeadIv;                      // 对方的头像
    private TextView mPeerNameTv;                      // 对方的名字
    private TextView tv_hint;                          //提示文字
    private TextView tv_members;                        //参与成员提示文本

    private LinearLayout members_layout;                //邀请界面成员layout
    private RecyclerView members_recycleview;           //邀请的成员成员列表
    private RecyclerView member_head_recycleview;       //已加入的成员头像列表
    private RecyclerView invite_head_recycleview;       //待加入成员头像列表

    private View desktopView;                           //承载画面的view

    private CallStatus mStatus;                         //当前共享屏幕状态
    private String inviteID;                           //邀请者ID
    private ScreenSwitchUtils mScreenSwitchUtils;       //屏幕工具管理器 ，主要是横竖屏切换

    private MemberAdapter mAdapter;                  //邀请界面参与人员适配器
    private MemberAdapter joinedAdapter;             //已加入成员适配器
    private MemberAdapter tojoinAdapter;             //待加入成员列表
    private ImageView add_member;                       //添加成员

    private Conference mConference;                     //当前的共享屏幕流程

    private Button switch_speaker_btn;                  //免提开关

    private Button switch_mute_btn;                     //麦克风开关
    private RecyclerView group_member_face;             //成员头像列表(主动加入界面)
    private Button call_join_btn;                       //立即加入按钮
    private TextView hint_tv;
    private TextView hint_groupName_tv;                 //群组名称
    private ProgressDialog mProgressDialog;
    private ImageView imag_back;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_call;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        this.getAgruments();
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mScreenSwitchUtils = ScreenSwitchUtils.init(CubeUI.getInstance().getContext());
        mScreenSwitchUtils.setChangedListener(this);
        if (null != imag_back){
            imag_back.setOnClickListener(this);
        }
        if (mShareCancelBtn != null) {
            //拒接
            mShareCancelBtn.setOnClickListener(this);
        }
        if (mShareJoinBtn != null) {
            //接受邀请
            mShareJoinBtn.setOnClickListener(this);
        }
        if (mShareHangUpBtn != null) {
            //挂断
            mShareHangUpBtn.setOnClickListener(this);
        }
        if (add_member != null) {
            add_member.setOnClickListener(this);
        }
        if (switch_speaker_btn != null) {
            switch_speaker_btn.setOnClickListener(this);
        }
        if (switch_mute_btn != null) {
            switch_mute_btn.setOnClickListener(this);
        }
        if (call_join_btn != null){
            call_join_btn.setOnClickListener(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ConferenceHandle.getInstance().addConferenceStateListener(this);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);     // for screen locked
        if (mStatus == CallStatus.REMOTE_DESKTOP_INCOMING) {
            //显示邀请界面
            showInvitedViewStub(mConference);
        } else if (mStatus == CallStatus.REMOTE_DESKTOP_JOIN) {
            showJoinViewStub(mConference);
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("加载中。。。");

    }

    /**
     * 获取参数
     */
    private void getAgruments() {
        Bundle bundle = getIntent().getBundleExtra("desketop_data");
        this.mConference = ((Conference) bundle.getSerializable("shaerdesketop"));
        this.mStatus = ((CallStatus) bundle.getSerializable("statues"));
        this.inviteID = bundle.getString("inviteId");
    }

    @Override
    public void hideLoading() {
        super.hideLoading();

    }

    @Override
    public void onConfigurationChanged(float rotation) {
//        if (connect_view != null) {
//            connect_view.removeAllViews();
//            if (desktopView != null) {
//                desktopView.setRotation(rotation);
//                connect_view.addView(desktopView);
//            }
//        }
    }

    @Override
    public void onNetworkStateChanged(boolean isNetAvailable) {
        super.onNetworkStateChanged(isNetAvailable);
        ToastUtil.showToast(this,R.string.network_not_available_please_try_again_later);
        CubeEngine.getInstance().getConferenceService().quit(mConference.conferenceId);
        release();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mScreenSwitchUtils.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScreenSwitchUtils.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScreenSwitchUtils != null) {
            mScreenSwitchUtils.stop();
            mScreenSwitchUtils.removeChangedListener(this);
            mScreenSwitchUtils = null;
        }
        ConferenceHandle.getInstance().removeConferenceStateListener(this);
    }
 private void getUserNickName(String cubeId) {
    CubeUserRepository.getInstance().queryUser(cubeId).subscribe(new Action1<CubeUser>() {
        @Override
        public void call(CubeUser cubeUser) {
            if (!cubeUser.getDisplayName().equals("") && null != cubeUser.getDisplayName()) {
                mPeerNameTv.setText(cubeUser.getDisplayName());
            } else {
                mPeerNameTv.setText(cubeId);
            }
        }
    });
}
    /**
     * 一对一,群组共享屏幕邀请ViewStub
     */
    private void showInvitedViewStub(Conference shareDesktop) {
        if (mShareIncomingVs == null) {
            mShareIncomingVs = this.findViewById(R.id.call_audio_incoming_vs);
            assert this.mShareIncomingVs != null;
            View inflateView = this.mShareIncomingVs.inflate();
            mPeerHeadIv = ((ImageView) inflateView.findViewById(R.id.peer_head_iv));
            mPeerNameTv = ((TextView) inflateView.findViewById(R.id.peer_name_tv));
            tv_hint = ((TextView) inflateView.findViewById(R.id.call_hint_tv));
            mShareCancelBtn = ((Button) inflateView.findViewById(R.id.call_refuse_btn));
            mShareJoinBtn = ((Button) inflateView.findViewById(R.id.call_answer_btn));
            tv_hint.setText(R.string.someone_wanted_to_talk_to_you_share_desktop);
            hint_groupName_tv = ((TextView) inflateView.findViewById(R.id.call_hint_group_name_tv));
            //群组名称
            hint_groupName_tv.setText(shareDesktop.displayName);
            getUserNickName(shareDesktop.founder);
            // 群组屏幕邀请
            if (null != shareDesktop.bindGroupId || TextUtils.isEmpty(shareDesktop.bindGroupId)) {
                members_layout = ((LinearLayout) inflateView.findViewById(R.id.members_layout));
                tv_members = ((TextView) inflateView.findViewById(R.id.tv_need_join_members));
                members_recycleview = ((RecyclerView) inflateView.findViewById(R.id.need_join_members_recycleview));
                members_layout.setVisibility(View.VISIBLE);
                //若是群组，则取出shareDesktop中邀请成员
                List<String> inviteMembers = new ArrayList<>();
                for (int i = 0; i < shareDesktop.invites.size(); i++) {
                    //暂时是id
                    inviteMembers.add(AppConstants.AVATAR_URL+shareDesktop.invites.get(i).cubeId);
                }
                mAdapter = new MemberAdapter(inviteMembers,this);
                members_recycleview.setLayoutManager(new GridLayoutManager(this, 4));
                members_recycleview.setAdapter(mAdapter);
            } else {
                //一对一屏幕邀请
                hint_groupName_tv.setVisibility(View.GONE);
                members_layout.setVisibility(View.GONE);
            }

            this.initListener();

        }


    }

    private void hideInComingView() {
        if (mShareIncomingVs != null) {
            mShareIncomingVs.setVisibility(View.GONE);
        }
    }

    /**
     * 主动加入共享屏幕
     * @param conference
     */
    private void showJoinViewStub(Conference conference) {
        if (mshareJoinVs == null){
            mshareJoinVs = this.findViewById(R.id.group_join_vs);
            assert  this.mshareJoinVs!= null;
            View inflateView = this.mshareJoinVs.inflate();
            group_member_face = ((RecyclerView)inflateView.findViewById(R.id.group_member_face));
            call_join_btn = ((Button) inflateView.findViewById(R.id.call_group_join_btn));
            hint_tv = ((TextView) inflateView.findViewById(R.id.call_group_hint_tv));
            imag_back = ((ImageView) inflateView.findViewById(R.id.imag_back));
            hint_tv.setText(getString(R.string.share_Screen_now_num,conference.getMembers().size()));
            List<String> membersList = new ArrayList<>();
            for (int i = 0; i <conference.getMembers().size(); i++) {
                membersList.add(AppConstants.AVATAR_URL+conference.getMembers().get(i).cubeId);
            }
            MemberAdapter adapter = new MemberAdapter(membersList,this);
            RecyclerView.LayoutManager manager = new GridLayoutManager(this,4);
            group_member_face.setLayoutManager(manager);
            group_member_face.setAdapter(adapter);
            this.initListener();
        }
    }
    private void hideJoinView(){
        if (mshareJoinVs != null){
            mshareJoinVs.setVisibility(View.GONE);
        }
    }

    /**
     * 一对一，群组共享屏幕接通view
     *
     * @param conference
     */
    private void showConnetViewStub(Conference conference) {
        if (mshareCOnnectVs == null) {
            mshareCOnnectVs = this.findViewById(R.id.share_screen_connect_vs);
            assert this.mshareCOnnectVs != null;
            View inflateView = this.mshareCOnnectVs.inflate();
            connect_view = ((FrameLayout) inflateView.findViewById(R.id.screen_fl));
            member_head_recycleview = ((RecyclerView) inflateView.findViewById(R.id.members_head_icon_rv));
            tv_joined = ((TextView) inflateView.findViewById(R.id.tv_joined));
            tv_toJoin = ((TextView) inflateView.findViewById(R.id.tv_to_join));
            invite_head_recycleview = ((RecyclerView) inflateView.findViewById(R.id.to_be_joined_members_recycleview));
            mShareHangUpBtn = ((Button) inflateView.findViewById(R.id.call_group_hang_up_btn));
            invite_head_recycleview = ((RecyclerView) inflateView.findViewById(R.id.invite_head_icon_rv));
            add_member = ((ImageView) inflateView.findViewById(R.id.call_group_add_btn));
            switch_mute_btn = ((Button) inflateView.findViewById(R.id.call_group_switch_mute_btn));
            switch_speaker_btn = ((Button) inflateView.findViewById(R.id.call_group_switch_speaker_btn));

        }
        tv_joined.setVisibility(View.VISIBLE);
        tv_toJoin.setVisibility(View.VISIBLE);

        // 默认打开免提
        if (this.switch_speaker_btn != null) {
            this.switch_speaker_btn.setSelected(CubeEngine.getInstance().getMediaService().isSpeakerEnabled());
        }
        if (this.switch_mute_btn != null) {
            this.switch_mute_btn.setSelected(CubeEngine.getInstance().getMediaService().isAudioEnabled());
        }
        //表示为P2P共享屏幕
        if (conference.maxNumber == 2){
            invite_head_recycleview.setVisibility(View.GONE);
            add_member.setVisibility(View.GONE);

        }else{
            //群组共享屏幕
            invite_head_recycleview.setVisibility(View.VISIBLE);
            tv_toJoin.setVisibility(View.VISIBLE);
            add_member.setVisibility(View.VISIBLE);
            //取出shareDesktop中的成员和邀请列表


            LinearLayoutManager manager = new LinearLayoutManager(ShareScreenActivity.this);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            LinearLayoutManager manager1 = new LinearLayoutManager(ShareScreenActivity.this);
            manager1.setOrientation(LinearLayoutManager.HORIZONTAL);


            invite_head_recycleview.setLayoutManager(manager);
            member_head_recycleview.setLayoutManager(manager1);
            initDataList(conference);
            member_head_recycleview.setAdapter(joinedAdapter);
            invite_head_recycleview.setAdapter(tojoinAdapter);
        }
        //显示屏幕的view
        desktopView = CubeEngine.getInstance().getConferenceService().getRemoteView();
        ViewGroup.LayoutParams params = desktopView.getLayoutParams();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.getDisplayWidth()*9/16);
        layoutParams.gravity = Gravity.CENTER;
        connect_view.addView(desktopView, layoutParams);
        this.initListener();
    }

    /**
     *
     */
    private void initDataList(Conference conference) {
        List<String> membersJoined = new ArrayList<>();
        List<String> memberToJoin = new ArrayList<>();
        for (int i = 0; i < conference.members.size(); i++) {
            membersJoined.add(AppConstants.AVATAR_URL+conference.members.get(i).cubeId);
        }
        for (int i = 0; i < conference.invites.size(); i++) {
            memberToJoin.add(AppConstants.AVATAR_URL+conference.invites.get(i).cubeId);
        }
        //因邀请成员列表中包含成员列表，因此移除所得待加入成员列表
        for (int i = 0; i <membersJoined.size() ; i++) {
            if (memberToJoin.contains(membersJoined.get(i))){
                memberToJoin.remove(membersJoined.get(i));
                continue;
            }
        }
        joinedAdapter = new MemberAdapter(membersJoined,this);
        tojoinAdapter = new MemberAdapter(memberToJoin,this);

    }

    /**
     * 刷新界面布局，已加入成员列表待加入成员列表
     *
     * @param conference
     */
    private void updateAdapter(Conference conference) {
        List<String> membersJoined = new ArrayList<>();
        List<String> memberToJoin = new ArrayList<>();
        if (joinedAdapter != null) {
            for (int i = 0; i < conference.members.size(); i++) {
                membersJoined.add(AppConstants.AVATAR_URL+conference.members.get(i).cubeId);
            }
            joinedAdapter.refreshDataList(membersJoined);
        }
        if (tojoinAdapter != null) {
            if (joinedAdapter != null) {
                for (int i = 0; i < conference.invites.size(); i++) {
                    memberToJoin.add(AppConstants.AVATAR_URL+conference.invites.get(i).cubeId);
                }
                for (int i = 0; i <membersJoined.size() ; i++) {
                    if (memberToJoin.contains(membersJoined.get(i))){
                        memberToJoin.remove(membersJoined.get(i));
                        continue;
                    }
                }
                tojoinAdapter.refreshDataList(memberToJoin);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.imag_back:
                this.finish();
                break;
            case R.id.call_group_join_btn:
                CubeEngine.getInstance().getConferenceService().join(mConference.conferenceId);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                break;
            case R.id.call_group_hang_up_btn:
                //挂断
                CubeEngine.getInstance().getConferenceService().quit(mConference.conferenceId);
                release();
                break;
            case R.id.call_refuse_btn:
                //拒绝邀请
                CubeEngine.getInstance().getConferenceService().rejectInvite(mConference.conferenceId, inviteID);
                release();
                break;
            case R.id.call_answer_btn:
                //接受邀请
                CubeEngine.getInstance().getConferenceService().acceptInvite(mConference.conferenceId, inviteID);
                break;
            case R.id.call_group_add_btn:
                //邀请成员
                inviteMember();
                break;
            case R.id.call_group_switch_speaker_btn://免提与否
                MediaService mSpeaker = CubeEngine.getInstance().getMediaService();
                if (mSpeaker.isSpeakerEnabled()) {
                    // 免提（不是听筒）
                    mSpeaker.setSpeakerEnabled(false);
                    this.switch_speaker_btn.setSelected(false);
                } else {
                    mSpeaker.setSpeakerEnabled(true);
                    this.switch_speaker_btn.setSelected(true);
                }
                break;
            case R.id.call_group_switch_mute_btn://麦克风与否
                MediaService mMute = CubeEngine.getInstance().getMediaService();
                showMessage(mMute.isAudioEnabled() + "");
                if (mMute.isAudioEnabled()) {
                    mMute.setAudioEnabled(false);
                    this.switch_mute_btn.setSelected(false);
                } else {
                    mMute.setAudioEnabled(true);
                    this.switch_mute_btn.setSelected(true);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 要成其他群成员
     */
    private void inviteMember(){
        Bundle bundle = new Bundle();
        bundle.putInt("select_type",7);
        bundle.putString("group_id",mConference.bindGroupId);
        bundle.putString("conference_id",mConference.conferenceId);
        ArrayList<String> notCheckList = new ArrayList<>();
        for (int i = 0; i <mConference.invites.size() ; i++) {
            notCheckList.add(mConference.invites.get(i).cubeId);
        }
        bundle.putStringArrayList("not_check_list",notCheckList);
        RouterUtil.navigation(AppConstants.Router.SelectMemberActivity,bundle);
    }
    private void release() {
        hideLoading();
        this.reset();
        this.finish();
    }

    /**
     * 重置
     */
    private void reset() {
        // 释放铃声
        RingtoneUtil.release();
        mConference = null;
        this.mStatus = CallStatus.NO_CALL;
        if (desktopView != null) {
            desktopView = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CubeEngine.getInstance().getConferenceService().quit(mConference.conferenceId);
        release();
    }

    @Override
    public void onConferenceCreated(Conference conference, User from) {
        //保存当前共享屏幕
        mConference = conference;
        if (null != mConference.bindGroupId && !TextUtils.isEmpty(mConference.bindGroupId)) {
            //这里去刷新列表
//            EventBus.getDefault().post(MessageEvent<UpdateRecentListAboutGroup>);
        }
    }

    @Override
    public void onConferenceDestroyed(Conference conference, User from) {
        ToastUtil.showToast(this,"会议销毁了");
        ConferenceHandle.getInstance().removeConferenceStateListener(this);
        release();
    }

    @Override


    public void onConferenceInvited(Conference conference, User from, List<User> invites) {
    }

    @Override
    public void onConferenceRejectInvited(Conference conference, User from, User rejectMember) {
        if (rejectMember.cubeId.equals(SpUtil.getCubeId())) {
            release();
        } else {
            updateAdapter(conference);
        }

    }

    /**
     * 接受邀请加入到共享屏幕
     *
     * @param conference   会议实体
     * @param from         邀请者
     * @param joinedMember 同意加入者
     */
    @Override
    public void onConferenceAcceptInvited(Conference conference, User from, User joinedMember) {
        mConference = conference;
        if (TextUtils.equals(joinedMember.cubeId, SpUtil.getCubeId())) {
            //调用会议加入方法
            CubeEngine.getInstance().getConferenceService().join(mConference.conferenceId);
        }

    }

    @Override
    public void onConferenceJoined(Conference conference, User joinedMember) {
        //加入共享屏幕成功，需要调用会控方法，成为会议角色
        if (joinedMember.cubeId.equals(SpUtil.getCubeId())) {
//            CubeEngine.getInstance().getConferenceService().addControlVideo(mConference.conferenceId, joinedMember.cubeId);
        }
        mProgressDialog.setMessage(getString(R.string.join_in_conference_share));

    }

    @Override
    public void onVideoEnabled(Conference conference, boolean videoEnabled) {
        LogUtil.d("===videoEnabled"+videoEnabled);
        if (videoEnabled) {
            //自己加入收到回调
            mProgressDialog.dismiss();
            mConference = conference;
            mStatus = CallStatus.REMOTE_DESKTOP_CALLING;
            hideInComingView();
            hideJoinView();
            hideLoading();
            showConnetViewStub(mConference);
            RingtoneUtil.release();
        } else {
            release();
        }

    }

    @Override
    public void onAudioEnabled(Conference conference, boolean videoEnabled) {

    }

    @Override
    public void onConferenceUpdated(Conference conference) {
        //刷新适配器
//        updateAdapter(conference);

    }

    @Override
    public void onConferenceQuited(Conference conference, User quitMember) {
        if (quitMember.cubeId.equals(SpUtil.getCubeId())) {
            release();
        }
        //别的成员退出，刷新适配器便好
        else {
            updateAdapter(conference);
        }
    }
    @Override
    public void onConferenceFailed(Conference conference, CubeError error) {
        LogUtil.d("===会议发生错误===" + error.code + "==描述==" + error.desc);
        ToastUtil.showToast(this, "会议发生错误" + error.desc);
        release();
    }
}
