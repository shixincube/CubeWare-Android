package cube.ware.service.conference.conference;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.mvp.base.BaseActivity;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.RingtoneUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.glide.GlideUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.common.model.CubeErrorCode;
import cube.service.conference.ConferenceListener;
import cube.service.conference.model.Conference;
import cube.service.conference.model.ConferenceControl;
import cube.service.conference.model.ConferenceStream;
import cube.service.conference.model.ControlAction;
import cube.service.conference.model.MemberStatus;
import cube.service.group.GroupType;
import cube.service.group.model.Member;
import cube.service.media.MediaService;
import cube.service.user.model.User;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CallStatus;
import cube.ware.service.conference.ConferenceHandle;
import cube.ware.service.conference.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Route(path = CubeConstants.Router.ConferenceActivity)
public class ConferenceActivity extends BaseActivity<ConferencePresenter> implements ConferenceContract.View, ConferenceListener {

    private ViewStub     mVSConferenceReceiveLayout;  // 受邀
    private ViewStub     mVSConferenceLayout;  //  视频演示
    private ViewStub     mVSApplyJoinConferenceLayout;  // 申请加入
    private ViewStub     mVSAudioConferenceLayout;  // 音频演示
    private CallStatus   callState;
    private ImageView    mIvPeerHeader;
    private TextView     mTvPeerName;
    private Button       mBtCancel;
    private TextView     mTvTitle;
    private LinearLayout mLlVideoView;
    private Button       mBtHangUp;
    private RecyclerView mRvJoined;
    private RecyclerView mRvWaiteJoined;
    private Button       mBtAnswer;
    private Button       mBtRefuse;
    private LinearLayout mPeerVideoLayout;    // 对方的视频布局
    private LinearLayout mMyVideoLayout;    // 自己的视频布局
    private View         mMyVideoView;    // 自己的视频view
    private View         mPeerVideoView;    // 对方的视频view

    private Conference        mConference;
    private ArrayList<String> mInviteList;
    private String            mInviteId;
    private String            TAG = "ConferenceActivity";
    private String            mGroupId;

    private LinearLayoutManager mLayoutManagerJoined;
    private LinearLayoutManager mLayoutManagerWaite;
    private List<String>        joinedList      = new ArrayList<>();
    private List<String>        waiteJoinedList = new ArrayList<>();
    private JoinedMemberAdapter mRvWaiteJoinedMemAdapter;
    private JoinedMemberAdapter mRvJoinedMemAdapter;
    private User                mUserSelf;
    private Button              mSwitchCameraBtn;
    private Button              mCallSwitchMuteBtn;
    private Button              mCallSswitchSpeakerBtn;
    private Button              mCallSwitchAudioBtn;
    private Chronometer         mCallTimeTip;
    private ImageButton         mBtAddMem;
    private RecyclerView        mRvNeedInvite;
    private TextView            mTvCallType;
    private JoinedMemberAdapter mRvNeedInviteAdapter;
    private Button              mBtJoin;
    private TextView            mTvJoinTitle;
    private ProgressDialog      mProgressDialog;
    private LinearLayout        mLlHeaderLayout;
    private LinearLayout        mLlControlLayout;
    private ImageView           imag_back;

    String BUNDLE                 = "bundle";
    String CONFERENCE_CALLSTATA   = "call_state";
    String CONFERENCE_CONFERENCE  = "conference";
    String CONFERENCE_INVITE_LIST = "invite_list";
    String CONFERENCE_INVITE_Id   = "invite_id";
    String CONFERENCE_GROUP_ID    = "group_id";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_call;
    }

    @Override
    protected ConferencePresenter createPresenter() {
        return new ConferencePresenter(this, this);
    }

    @Override
    protected void initView() {
        ConferenceHandle.getInstance().addConferenceStateListener(this);
        mUserSelf = CubeEngine.getInstance().getSession().getUser();
        getArgment();
        switchViewStub();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("加载中。。。");
    }

    private void getArgment() {
        Bundle bundle = getIntent().getBundleExtra(BUNDLE);
        callState = (CallStatus) bundle.getSerializable(CONFERENCE_CALLSTATA);
        mConference = (Conference) bundle.getSerializable(CONFERENCE_CONFERENCE);
        mInviteList = bundle.getStringArrayList(CONFERENCE_INVITE_LIST); //发起者才会有邀请集合
        mInviteId = bundle.getString(CONFERENCE_INVITE_Id);
        mGroupId = bundle.getString(CONFERENCE_GROUP_ID, "");
        if (callState.equals(CallStatus.GROUP_VIDEO_CALLING)) {
            if (mConference != null && (mConference.type == GroupType.VIDEO_CALL || mConference.type == GroupType.VIDEO_CONFERENCE)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    private void switchViewStub() {
        hideConferenceReceiveLayoutViewStub();
        hideConferenceLayoutViewStub();
        hideApplyJoinConferenceLayoutViewStub();
        hideConferenceAudioLayoutViewStub();

        if (callState == CallStatus.GROUP_CALL_INCOMING) { // 受邀
            this.showConferenceReceiveLayout();
        }
        if (callState == CallStatus.GROUP_VIDEO_CALLING) {   // 视频演示
            this.showConferenceLayout();
        }
        if (callState == CallStatus.GROUP_AUDIO_CALLING) {   // 音频演示
            this.showConferenceAudioLayout();
        }
        if (callState == CallStatus.GROUP_CALL_JOIN) {    //  主动加入
            this.showApplyJoinConferenceLayout();
        }
    }

    //接受邀请界面
    private void showConferenceReceiveLayout() {
        if (mVSConferenceReceiveLayout == null) {
            this.mVSConferenceReceiveLayout = findViewById(R.id.call_audio_incoming_vs);
            View inflateView = this.mVSConferenceReceiveLayout.inflate();
            mIvPeerHeader = inflateView.findViewById(R.id.peer_head_iv);
            mTvPeerName = inflateView.findViewById(R.id.peer_name_tv);
            mBtRefuse = inflateView.findViewById(R.id.call_refuse_btn);
            mBtAnswer = inflateView.findViewById(R.id.call_answer_btn);
            mTvCallType = inflateView.findViewById(R.id.call_hint_tv);
            mRvNeedInvite = inflateView.findViewById(R.id.need_join_members_recycleview);
            inflateView.findViewById(R.id.members_layout).setVisibility(View.VISIBLE);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRvNeedInvite.setLayoutManager(gridLayoutManager);
        if (mConference != null && mConference.invites != null) {
            //设置数据
            initInviteRV(mConference.invites);
        }

        //加载加入邀请的头像
        GlideUtil.loadCircleImage(CubeCore.getInstance().getAvatarUrl() + mConference.founder, this, mIvPeerHeader, DiskCacheStrategy.NONE, false, R.drawable.default_head_user);
        //获取邀请者数据
        mPresenter.getUserData(mConference.founder);
        if (mConference.type.equals(GroupType.VOICE_CONFERENCE) || mConference.type.equals(GroupType.VOICE_CALL)) {
            mTvCallType.setText(this.getResources().getString(R.string.someone_wanted_to_talk_to_you_voice_calls));
        }
        else {
            mTvCallType.setText(this.getResources().getString(R.string.someone_wanted_to_talk_to_you_video_calls));
        }
        this.initListener();
    }

    private void initInviteRV(List<Member> invites) {
        //直接刷新头像列表
        if (invites != null) {
            mRvNeedInviteAdapter = new JoinedMemberAdapter(ConferenceActivity.this, MemberToCubeIds(invites));
            mRvNeedInvite.setAdapter(mRvNeedInviteAdapter);
        }
    }

    /**
     * 查询数据库返回的数据
     *
     * @param user
     */
    @Override
    public void getUserData(User user) {
        //只是查询名字显示
        if (mTvPeerName != null) {
            mTvPeerName.setText(user.displayName);
        }
    }

    //音频会议中演示
    private void showConferenceAudioLayout() {
        if (mVSAudioConferenceLayout == null) {
            this.mVSAudioConferenceLayout = findViewById(R.id.call_audio_vs);
            View inflateView = this.mVSAudioConferenceLayout.inflate();
            mTvTitle = inflateView.findViewById(R.id.tv_title);
            mBtHangUp = inflateView.findViewById(R.id.call_hang_up_btn);
            mBtAddMem = inflateView.findViewById(R.id.call_group_add_btn);
            mBtAddMem.setVisibility(View.VISIBLE);
            inflateView.findViewById(R.id.call_hint_tv).setVisibility(View.VISIBLE);
            mRvJoined = inflateView.findViewById(R.id.joined_members_recycleview);
            mRvWaiteJoined = inflateView.findViewById(R.id.to_be_joined_members_recycleview);
            inflateView.findViewById(R.id.to_be_joined_layout).setVisibility(View.VISIBLE);
            mCallSswitchSpeakerBtn = inflateView.findViewById(R.id.call_switch_speaker_btn);//免提或者听筒
            mCallSwitchMuteBtn = inflateView.findViewById(R.id.call_switch_mute_btn);//切换麦或者静音
            mCallTimeTip = inflateView.findViewById(R.id.call_chronometer);//计时器
        }
        mCallSswitchSpeakerBtn.setSelected(true);
        mCallSwitchMuteBtn.setSelected(true);
        //计时器
        initRecyclerView();
        initAction();
        this.initListener();
    }

    //视频会议中的界面
    private void showConferenceLayout() {
        if (mVSConferenceLayout == null) {
            this.mVSConferenceLayout = findViewById(R.id.call_video_vs);
            View inflateView = this.mVSConferenceLayout.inflate();
            mTvTitle = inflateView.findViewById(R.id.tv_title);
            mBtHangUp = inflateView.findViewById(R.id.call_hang_up_btn);
            mBtAddMem = inflateView.findViewById(R.id.call_group_add_btn);
            mLlHeaderLayout = inflateView.findViewById(R.id.ll_header_layout);
            mLlControlLayout = inflateView.findViewById(R.id.call_control_layout);
            mBtAddMem.setVisibility(View.VISIBLE);
            mPeerVideoLayout = inflateView.findViewById(R.id.peer_video_layout);
            mMyVideoLayout = inflateView.findViewById(R.id.my_video_layout);
            mRvJoined = inflateView.findViewById(R.id.joined_members_recycleview);
            mRvWaiteJoined = inflateView.findViewById(R.id.to_be_joined_members_recycleview);
            inflateView.findViewById(R.id.to_be_joined_layout).setVisibility(View.VISIBLE);
            mCallSswitchSpeakerBtn = inflateView.findViewById(R.id.call_switch_speaker_btn);//免提或者听筒
            mCallSwitchAudioBtn = inflateView.findViewById(R.id.call_switch_audio_btn);//切换语音
            mSwitchCameraBtn = inflateView.findViewById(R.id.call_switch_camera_btn);//切换摄像头
            mCallSwitchMuteBtn = inflateView.findViewById(R.id.call_switch_mute_btn);//切换麦或者静音
            mCallTimeTip = inflateView.findViewById(R.id.call_chronometer);
            mCallTimeTip.setVisibility(View.GONE);
        }
        //默认开启
        mCallSswitchSpeakerBtn.setSelected(true);
        mCallSwitchMuteBtn.setSelected(true);
        mCallSwitchAudioBtn.setSelected(false);
        this.mMyVideoView = CubeEngine.getInstance().getConferenceService().getLocalView();
        this.mPeerVideoView = CubeEngine.getInstance().getConferenceService().getRemoteView();
        //视频
        if (this.mMyVideoLayout.getChildAt(0) == null && this.mMyVideoView != null) {
            this.mMyVideoLayout.setVisibility(View.GONE);
        }
        if (this.mPeerVideoLayout.getChildAt(0) == null && this.mPeerVideoView != null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            this.mPeerVideoLayout.addView(this.mPeerVideoView, 0, layoutParams);
        }
        //计时器
        //        this.mCallTimeTip.setBase(SystemClock.elapsedRealtime());
        //        mCallTimeTip.start();
        initRecyclerView();
        initAction();
        this.initListener();
    }

    //recyclerview
    private void initRecyclerView() {
        mLayoutManagerJoined = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mLayoutManagerWaite = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvJoinedMemAdapter = new JoinedMemberAdapter(this, joinedList);
        mRvWaiteJoinedMemAdapter = new JoinedMemberAdapter(this, waiteJoinedList);
        mRvJoined.setLayoutManager(mLayoutManagerJoined);
        mRvWaiteJoined.setLayoutManager(mLayoutManagerWaite);
        mRvJoined.setAdapter(mRvJoinedMemAdapter);
        mRvWaiteJoined.setAdapter(mRvWaiteJoinedMemAdapter);
    }

    private void initAction() {
        if (mInviteList != null && mInviteList.size() > 0) {
            //不绑定群组，且是视频会议，不需要邀请，在创建的时候由后台根据时间自动邀请
            if (TextUtils.isEmpty(mGroupId) && mConference.type.equals(GroupType.VIDEO_CONFERENCE)) {
                return;
            }
            else {
                LogUtil.i("ConferenceInvited", mInviteList.toString());
                CubeEngine.getInstance().getConferenceService().inviteMembers(mConference.conferenceId, mInviteList);
            }
        }
        //加入自己，数据库查询
        mPresenter.getUserData(mUserSelf.cubeId);
        //添加邀请人员
        List<Member> invites = mConference.invites;
        List<Member> members = mConference.getMembers();
        //加入者
        if (members != null) {
            LogUtil.i(TAG, "members:" + members.toString());
            if (members != null) {
                //更新adapter
                mRvJoinedMemAdapter.addListDate(MemberToCubeIds(members));
            }
        }
        //去重复
        List<Member> memberList = mPresenter.deleteRepeat(members, invites);
        //待加入
        mRvWaiteJoinedMemAdapter.addListDate(MemberToCubeIds(memberList));
    }

    //主动加入
    private void showApplyJoinConferenceLayout() {
        if (mVSApplyJoinConferenceLayout == null) {
            this.mVSApplyJoinConferenceLayout = findViewById(R.id.group_join_vs);
            View inflateView = this.mVSApplyJoinConferenceLayout.inflate();
            mBtJoin = inflateView.findViewById(R.id.call_group_join_btn);
            mTvJoinTitle = inflateView.findViewById(R.id.call_group_hint_tv);
            mTvCallType = inflateView.findViewById(R.id.call_hint_tv);
            mRvNeedInvite = inflateView.findViewById(R.id.group_member_face);
            imag_back = inflateView.findViewById(R.id.imag_back);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRvNeedInvite.setLayoutManager(gridLayoutManager);
        if (mConference != null && mConference.invites != null) {
            //设置数据
            initInviteRV(mConference.members);
        }
        //显示文字
        if (mConference.type.equals(GroupType.VOICE_CALL) || mConference.type.equals(GroupType.VOICE_CONFERENCE)) {
            mTvJoinTitle.setText(getString(R.string.start_join_screen, mConference.members.size() + ""));
        }
        else {
            mTvJoinTitle.setText(getString(R.string.start_join_video, mConference.members.size() + ""));
        }
        mPresenter.getUserData(mConference.founder);
        this.initListener();
    }

    //隐藏界面
    private void hideConferenceReceiveLayoutViewStub() {
        if (mVSConferenceReceiveLayout != null) {
            mVSConferenceReceiveLayout.setVisibility(View.GONE);
        }
    }

    private void hideConferenceLayoutViewStub() {
        if (mVSConferenceLayout != null) {
            mVSConferenceLayout.setVisibility(View.GONE);
        }
    }

    private void hideConferenceAudioLayoutViewStub() {
        if (mVSAudioConferenceLayout != null) {
            mVSAudioConferenceLayout.setVisibility(View.GONE);
        }
    }

    private void hideApplyJoinConferenceLayoutViewStub() {
        if (mVSApplyJoinConferenceLayout != null) {
            mVSApplyJoinConferenceLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListener() {
        //邀请
        if (callState.equals(CallStatus.GROUP_CALL_INCOMING)) {
            mBtAnswer.setOnClickListener(this);
            mBtRefuse.setOnClickListener(this);
        }
        //视频会议中
        if (callState.equals(CallStatus.GROUP_VIDEO_CALLING)) {
            mBtHangUp.setOnClickListener(this);
            mBtAddMem.setOnClickListener(this);
            mCallSswitchSpeakerBtn.setOnClickListener(this);
            mCallSwitchAudioBtn.setOnClickListener(this);
            mCallSwitchMuteBtn.setOnClickListener(this);
            mSwitchCameraBtn.setOnClickListener(this);
            mPeerVideoLayout.setOnClickListener(this);
        }

        //音频会议中
        if (callState.equals(CallStatus.GROUP_AUDIO_CALLING)) {
            mBtHangUp.setOnClickListener(this);
            mBtAddMem.setOnClickListener(this);
            mCallSswitchSpeakerBtn.setOnClickListener(this);
            mCallSwitchMuteBtn.setOnClickListener(this);
        }
        //主动加入
        if (callState.equals(CallStatus.GROUP_CALL_JOIN)) {
            mBtJoin.setOnClickListener(this);
        }
        if (null != imag_back) {
            imag_back.setOnClickListener(this);
        }

        //测试媒体通话质量
        //        CubeEngine.getInstance().getMediaService().getMediaQuality(new CubeCallback<CubeMediaQuality>() {
        //            @Override
        //            public void onSucceed(CubeMediaQuality cubeMediaQuality) {
        //                LogUtil.w("cubeMediaQuality: " +cubeMediaQuality);
        //            }
        //
        //            @Override
        //            public void onFailed(CubeError error) {
        //
        //            }
        //        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imag_back) {
            this.finish();
        }
        else if (id == R.id.call_group_join_btn) {//接收
            CubeEngine.getInstance().getConferenceService().join(mConference.conferenceId);
            mProgressDialog.setMessage(getString(R.string.joining));
            mProgressDialog.show();
        }
        else if (id == R.id.call_answer_btn) {//接收
            CubeEngine.getInstance().getConferenceService().acceptInvite(mConference.conferenceId, mInviteId);
            if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                //不加这句，鲁比亚要崩
                mProgressDialog.dismiss();
                mProgressDialog.setMessage(getString(R.string.joining));
                mProgressDialog.show();
            }
        }
        else if (id == R.id.call_group_add_btn) { //二次邀请
            jumpAddMember();
        }
        else if (id == R.id.call_refuse_btn) {//拒绝
            CubeEngine.getInstance().getConferenceService().rejectInvite(mConference.conferenceId, mInviteId);
            finish();
        }
        else if (id == R.id.call_hang_up_btn) {//退出
            CubeEngine.getInstance().getConferenceService().quit(mConference.conferenceId);
            finish();
        }
        else if (id == R.id.call_switch_audio_btn) { //切换音频视频
            MediaService ms = CubeEngine.getInstance().getMediaService();
            ms.setVideoEnabled(!ms.isVideoEnabled());
            if (mCallSwitchAudioBtn.isSelected()) {
                //关闭
                mCallSwitchAudioBtn.setSelected(false);
                this.mCallSwitchAudioBtn.setText(getString(R.string.switch_to_voice));
            }
            else {  //开启
                mCallSwitchAudioBtn.setSelected(true);
                this.mCallSwitchAudioBtn.setText(getString(R.string.switch_to_video));
            }
        }
        else if (id == R.id.call_switch_camera_btn) { //切换摄像头
            MediaService msCamera = CubeEngine.getInstance().getMediaService();
            //切换摄像头
            msCamera.switchCamera();
            //更新button状态
            this.mSwitchCameraBtn.setSelected(mSwitchCameraBtn.isSelected());
        }
        else if (id == R.id.call_switch_speaker_btn) {//免提与否
            MediaService mSpeaker = CubeEngine.getInstance().getMediaService();
            if (mCallSswitchSpeakerBtn.isSelected()) {
                // 免提（不是听筒）
                mSpeaker.setSpeakerEnabled(false);
                this.mCallSswitchSpeakerBtn.setSelected(false);
            }
            else {
                mSpeaker.setSpeakerEnabled(true);
                this.mCallSswitchSpeakerBtn.setSelected(true);
            }
        }
        else if (id == R.id.call_switch_mute_btn) {//免提与否
            MediaService mMute = CubeEngine.getInstance().getMediaService();
            if (mCallSwitchMuteBtn.isSelected()) {
                mMute.setAudioEnabled(false);
                this.mCallSwitchMuteBtn.setSelected(false);
            }
            else {
                mMute.setAudioEnabled(true);
                this.mCallSwitchMuteBtn.setSelected(true);
            }
        }
        else if (id == R.id.peer_video_layout) { //视频会议点击隐藏界面
            if (mLlHeaderLayout.getVisibility() == View.GONE) {
                mLlHeaderLayout.setVisibility(View.VISIBLE);
                mLlControlLayout.setVisibility(View.VISIBLE);
            }
            else {
                mLlHeaderLayout.setVisibility(View.GONE);
                mLlControlLayout.setVisibility(View.GONE);
            }
        }
    }

    //跳转到邀请页面
    private void jumpAddMember() {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(mGroupId)) {
            bundle.putInt("select_type", 4);//音频,视频
        }
        else {
            bundle.putInt("select_type", 5);//音频,视频
        }
        bundle.putString("group_id", mGroupId);
        bundle.putString("conference_id", mConference.conferenceId);//首次创建
        HashMap<String, MemberStatus> status = mConference.status;
        ArrayList<String> list = new ArrayList<>();
        list.addAll(MemberToCubeIds(mConference.invites));
        //不能二次邀请的人员
        bundle.putStringArrayList("not_check_list", list);
        LogUtil.i(TAG, "status:" + list.toString());
        RouterUtil.navigation(CubeConstants.Router.SelectMemberActivity, bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RingtoneUtil.release();
        if (mConference != null && callState.equals(CallStatus.GROUP_CALL_JOIN)) {
            //没有进入，直接关闭
            CubeEngine.getInstance().getConferenceService().quit(mConference.conferenceId);
        }
        ConferenceHandle.getInstance().removeConferenceStateListener(this);
    }

    //会议的回调
    @Override
    public void onConferenceCreated(Conference conference, User from) {

    }

    @Override
    public void onConferenceInvited(Conference conference, User from, List<User> invites) {
        LogUtil.i(TAG, "onConferenceInvited " + from.toString() + invites.toString());
        if (mGroupId.equals("") || mPresenter.isCurrentGroup(mGroupId, conference.bindGroupId)) {
            mConference = conference;
            //已加入
            List<Member> members = conference.getMembers();
            if (members != null) {
                mRvJoinedMemAdapter.addListDate(MemberToCubeIds(members));
            }
            //待加入
            List<Member> invitesMem = conference.invites;
            //去重复后的集合
            List<Member> memberList = mPresenter.deleteRepeat(members, invitesMem);
            //查询设值·
            mRvWaiteJoinedMemAdapter.addListDate(MemberToCubeIds(memberList));
        }
    }

    @Override
    public void onConferenceRejectInvited(Conference conference, User from, User rejectMember) {
        //mGroupId为空 没有群概念
        if (TextUtils.isEmpty(mGroupId) || mPresenter.isCurrentGroup(mGroupId, conference.bindGroupId)) {
            //拒绝邀请
            updateJoinedAdapter(rejectMember.cubeId, false);
        }
    }

    @Override
    public void onConferenceAcceptInvited(Conference conference, User from, User acceptMember) {
        LogUtil.i(TAG, "onConferenceAcceptInvited " + acceptMember.toString());
        //mGroupId为空 没有群概念
        if (TextUtils.isEmpty(mGroupId) || mPresenter.isCurrentGroup(mGroupId, conference.bindGroupId)) {
            //自己接受，加就加入
            if (mPresenter.isSelf(acceptMember.cubeId)) {
                CubeEngine.getInstance().getConferenceService().join(conference.conferenceId);
            }
            else {

            }
        }
    }

    @Override
    public void onConferenceJoined(Conference conference, User joinedMember) {
        LogUtil.i(TAG, "onConferenceJoined " + joinedMember.toString());
        if (mGroupId.equals("") || mPresenter.isCurrentGroup(mGroupId, conference.bindGroupId)) {
            if (mConference.conferenceId.equals(conference.conferenceId)) {
                mConference = conference;
                //自己接受，加就加入
                if (mPresenter.isSelf(joinedMember.cubeId)) {
                    //                    if (conference.type.equals(GroupType.VOICE_CALL)) {
                    //                        CubeEngine.getInstance().getConferenceService().addControlAudio(conference.conferenceId, joinedMember.cubeId);
                    //                    } else {
                    //                        CubeEngine.getInstance().getConferenceService().addControlVideo(conference.conferenceId, joinedMember.cubeId);
                    //                    }
                    mProgressDialog.setMessage(getString(R.string.join_in_conference));
                }
                else {
                    //有人加入，要刷新adapter
                    updateJoinedAdapter(joinedMember.cubeId, true);
                }
            }
        }
    }

    @Override
    public void onConferenceQuited(Conference conference, User quitMember) {
        LogUtil.i("quit：" + mGroupId + "-" + conference.bindGroupId);
        if (mGroupId.equals("") || mPresenter.isCurrentGroup(mGroupId, conference.bindGroupId)) {
            if (mConference.conferenceId.equals(conference.conferenceId)) {
                mConference = conference;
                //不是自己
                if (!mPresenter.isSelf(quitMember.cubeId)) {
                    updateJoinedAdapter(quitMember.cubeId, false);
                }
            }
        }
    }

    @Override
    public void onConferenceAddStream(ConferenceStream conferenceStream) {

    }

    @Override
    public void onConferenceRemoveStream(ConferenceStream conferenceStream) {

    }

    @Override
    public void onConferenceDestroyed(Conference conference, User from) {
        if (mGroupId.equals("") || mPresenter.isCurrentGroup(mGroupId, conference.bindGroupId)) {
            if (mConference.conferenceId.equals(conference.conferenceId)) {
                finish();
            }
        }
        LogUtil.i(TAG, "onConferenceDestroyed " + from.toString());
    }

    @Override
    public void onVideoEnabled(Conference conference, boolean videoEnabled) {
        LogUtil.i(TAG, "onVideoEnabled");
        if ((videoEnabled && mGroupId.equals("")) || (videoEnabled && mPresenter.isCurrentGroup(mGroupId, conference.bindGroupId))) {
            //切换视频界面
            callState = CallStatus.GROUP_VIDEO_CALLING;
            switchViewStub();
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (mConference != null && mConference.type == GroupType.VIDEO_CALL || mConference.type == GroupType.VIDEO_CONFERENCE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    @Override
    public void onAudioEnabled(Conference conference, boolean videoEnabled) {
        LogUtil.i(TAG, "onAudioEnabled");
        if ((videoEnabled && mGroupId.equals("")) || (videoEnabled && mPresenter.isCurrentGroup(mGroupId, conference.bindGroupId))) {
            //切换音频界面
            callState = CallStatus.GROUP_AUDIO_CALLING;
            switchViewStub();
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void onConferenceUpdated(Conference conference) {
        if (mGroupId.equals("") || mPresenter.isCurrentGroup(mGroupId, conference.bindGroupId)) {
            mConference = conference;
            List<ConferenceControl> actions = conference.actions;
            for (int i = 0; i < actions.size(); i++) {
                ConferenceControl conferenceControl = actions.get(i);
                if (conferenceControl.action != null && conferenceControl.action.getAction().equals(ControlAction.KICK.getAction())) {
                    updateJoinedAdapter(conferenceControl.controlled.cubeId, false);
                    //未接，自己被踢，结束页面
                    if (conferenceControl.controlled.cubeId.equals(CubeCore.getInstance().getCubeId())) {
                        finish();
                    }
                }
                if (conferenceControl.action != null && conferenceControl.action.getAction().equals(ControlAction.VMUTE.getAction())) {

                }
                if (conferenceControl.action != null && conferenceControl.action.getAction().equals(ControlAction.SPEAKER.getAction())) {

                }
                if (conferenceControl.action != null && conferenceControl.action.getAction().equals(ControlAction.HEAR.getAction())) {

                }
            }
        }
    }

    @Override
    public void onConferenceFailed(Conference conference, CubeError cubeError) {
        LogUtil.i(TAG, "onConferenceFailed " + cubeError.toString());
        if (cubeError.code == CubeErrorCode.ApplyConferenceFailed.code) {
            showMessage(CubeErrorCode.ApplyConferenceFailed.message);
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (conference == null) {
            //            showMessage("登录sip失败");
            if (cubeError.code == CubeErrorCode.JoinConferenceEarly.code) {
                showMessage(CubeErrorCode.JoinConferenceEarly.message);
            }
            LogUtil.i(TAG, "onConferenceFailed: " + "登录sip失败");
            finish();
            return;
        }
        if (conference.conferenceId.equals(mGroupId) || TextUtils.isEmpty(mGroupId)) {
            if (cubeError.code == CubeErrorCode.ConferenceRejectByOther.code) {
                showMessage(CubeErrorCode.ConferenceRejectByOther.message);
            }
            if (cubeError.code == CubeErrorCode.ConferenceJoinFromOther.code) {
                //加入会议会收到此通知 别人加入会议也会通知
                showMessage(CubeErrorCode.ConferenceJoinFromOther.message);
            }
            if (cubeError.code == CubeErrorCode.OverMaxNumber.code) {
                //加入时或申请加入时会出这个错误 关闭界面
                showMessage(CubeErrorCode.OverMaxNumber.message);
                if (callState == CallStatus.GROUP_CALL_JOIN) {
                    CubeEngine.getInstance().getConferenceService().quit(conference.conferenceId);
                    finish();
                }
                return;
            }
            if (cubeError.code == CubeErrorCode.AlreadyInCalling.code) {
                showMessage(CubeErrorCode.AlreadyInCalling.message);
                finish();
            }
            if (cubeError.code == CubeErrorCode.ConferenceExist.code) {
                //申请会议时报错 关闭申请界面
                showMessage(CubeErrorCode.ConferenceExist.message);
            }
            if (cubeError.code == CubeErrorCode.ConferenceClosed.code) {
                // 会议已经销毁
                showMessage(CubeErrorCode.ConferenceClosed.message);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mConference != null) {
            CubeEngine.getInstance().getConferenceService().quit(mConference.conferenceId);
        }
        super.onBackPressed();
    }

    @Override
    public void showMessage(String message) {
        ToastUtil.showToast(this, message);
    }

    //刷新两个adapter
    private void updateJoinedAdapter(String cubeId, boolean isJoin) {
        if (isJoin) { //加入
            if (mRvJoinedMemAdapter != null) {
                mRvJoinedMemAdapter.addDate(cubeId);
            }
            if (mRvWaiteJoinedMemAdapter != null) {
                mRvWaiteJoinedMemAdapter.removeDate(cubeId);
            }
        }
        else { //退出
            if (mRvJoinedMemAdapter != null) {
                mRvJoinedMemAdapter.removeDate(cubeId);
            }
            if (mRvWaiteJoinedMemAdapter != null) {
                mRvWaiteJoinedMemAdapter.removeDate(cubeId);
            }
        }
    }

    /**
     * 数据转换
     *
     * @param members
     *
     * @return
     */
    private List<String> MemberToCubeIds(List<Member> members) {
        List<String> cubeIds = new ArrayList<>();
        if (members != null) {
            for (int i = 0; i < members.size(); i++) {
                cubeIds.add(members.get(i).cubeId);
            }
        }
        return cubeIds;
    }
}
