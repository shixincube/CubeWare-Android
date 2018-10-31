package cube.ware.service.call.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.mvp.base.BaseActivity;
import com.common.utils.utils.NetworkUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.glide.GlideUtil;
import com.common.utils.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cube.service.CubeEngine;
import cube.service.call.CallAction;
import cube.service.call.CallDirection;
import cube.service.call.model.CallSession;
import cube.service.common.model.CubeErrorCode;
import cube.service.media.MediaService;
import cube.service.user.model.User;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.data.model.dataModel.enmu.CallStatus;
import cube.ware.service.call.CallHandle;
import cube.ware.service.call.adapter.P2PCallContract;
import cube.ware.service.call.adapter.P2PCallPresenter;
import cube.ware.service.call.adapter.P2PmemberAdapter;
import cube.ware.service.listener.CallStateListener;
import cube.ware.utils.SpUtil;


/**
 * 一对一音视频通话页面
 * Created by zzy on 2018/8/28.
 */
@Route(path = AppConstants.Router.P2PCallActivity)
public class P2PCallActivity extends BaseActivity <P2PCallPresenter> implements CallStateListener,P2PCallContract.View {
    private String         mCallId;
    private CallStatus     mCallState = CallStatus.NO_CALL; //通话形式，据此判断显示view（视频或者语音)
    private long           mCallTime  = 0; //通话时间
    private ViewStub       mCallAudioOutgoingVs;    // 语音呼叫的UI
    private ViewStub       mCallAudioIncomingVs;    // 语音来电的UI
    private ViewStub       mCallAudioCallVs;    // 语音通话的UI
    private ViewStub       mCallVideoOutgoingVs;    // 视频呼叫的UI
    private ViewStub       mCallVideoIncomingVs;    // 视频来电的UI
    private ViewStub       mCallVideoCallVs;    // 视频通话的UI

    private ImageView      mPeerHeadIv;    // 对方的头像
    private TextView       mPeerNameTv;    // 对方的名字
    private TextView       mCallHintTv;    // 通话提示文本
    private TextView       tv_tojoin;       //待加入成员提示文本
    private Button         mCallCancelBtn;  // 取消按钮
    private Button         mCallRefuseBtn;  // 拒绝按钮
    private Button         mCallAnswerBtn;  // 接听按钮
    private Button         mCallHangUpBtn;  // 挂断
    private RelativeLayout mCallAudioRootLayout;    // 语音通话根部局
    private FrameLayout    mCallVideoRootLayout;    // 视频通话页面跟布局
    private LinearLayout   mCallControlLayout;    // 通话控制面板
    private Button         mCallSwitchSpeakerBtn;   // 切换到免提
    private Button         mCallSwitchMuteBtn;    // 切换到静音
    private Button         mCallSwitchCameraBtn;    // 切换相机
    private Button         mCallSwitchAudioBtn; // 切换到语音通过
    private Chronometer    mCallChronometer;    // 通话计时器
    private LinearLayout   mPeerVideoLayout;    // 对方的视频布局
    private LinearLayout   mMyVideoLayout;    // 自己的视频布局

    private View           mMyVideoView;    // 自己的视频view
    private View           mPeerVideoView;    // 对方的视频view

    private PowerManager.WakeLock mWakeLock;
    private final Object   mWakeLockSync = new Object();

    private RecyclerView   members_recycleview;
    private P2PmemberAdapter joinedMemberAdapter ;//已加入成员适配器
    private List<String>   memberList ;             //加入成员集合



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CallHandle.getInstance().addCallStateListener(this);
        this.showViewStub();
    }
    @Override
    protected int getContentViewId() {
        return R.layout.activity_call;
    }

    @Override
    protected P2PCallPresenter createPresenter() {
         return  new P2PCallPresenter(this,this);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();
        this.getArguments();
        if (this.mCallState == CallStatus.AUDIO_OUTGOING || this.mCallState == CallStatus.VIDEO_OUTGOING) {
            if (!NetworkUtil.isNetAvailable(this)) {
                ToastUtil.showToast(this, 0, getString(R.string.network_not_available_please_try_again_later));
                this.release();
            }
            else if (!CubeEngine.getInstance().getCallService().makeCall(this.mCallId, this.mCallState == CallStatus.VIDEO_OUTGOING)) {
                ToastUtil.showToast(this, 0, getString(R.string.call_failure_please_try_again_later));
                this.release();
            }
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        if (this.mCallCancelBtn != null) {
            this.mCallCancelBtn.setOnClickListener(this);
        }
        if (this.mCallHangUpBtn != null) {
            this.mCallHangUpBtn.setOnClickListener(this);
        }
        if (this.mCallRefuseBtn != null) {
            this.mCallRefuseBtn.setOnClickListener(this);
        }
        if (this.mCallAnswerBtn != null) {
            this.mCallAnswerBtn.setOnClickListener(this);
        }
        if (this.mCallSwitchAudioBtn != null) {
            this.mCallSwitchAudioBtn.setOnClickListener(this);
        }
        if (this.mCallSwitchSpeakerBtn != null) {
            this.mCallSwitchSpeakerBtn.setOnClickListener(this);
        }
        if (this.mCallSwitchMuteBtn != null) {
            this.mCallSwitchMuteBtn.setOnClickListener(this);
        }
        if (this.mCallSwitchCameraBtn != null) {
            this.mCallSwitchCameraBtn.setOnClickListener(this);
        }
        if (this.mCallAudioRootLayout != null) {
            this.mCallAudioRootLayout.setOnClickListener(this);
        }
        if (this.mCallVideoRootLayout != null) {
            this.mCallVideoRootLayout.setOnClickListener(this);
        }
    }

    @Override
    public void onNetworkStateChanged(boolean isNetAvailable) {
        super.onNetworkStateChanged(isNetAvailable);
        //当在通话界面中
//        ToastUtil.showToast(this,R.string.network_not_available_please_try_again_later);
//        CubeEngine.getInstance().getCallService().terminateCall(getPeerCubeId());
//        release();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.call_cancel_btn) {
            CubeEngine.getInstance().getCallService().terminateCall(getPeerCubeId());
            release();
        }
        else if (i == R.id.call_hang_up_btn) {
            CubeEngine.getInstance().getCallService().terminateCall(getPeerCubeId());
            release();
        }
        else if (i == R.id.call_refuse_btn) {
            LogUtil.d("===拒接了哦===");
            CubeEngine.getInstance().getCallService().terminateCall(getPeerCubeId());
            release();
        }
        else if (i == R.id.call_answer_btn) {
            String cubeId = CubeEngine.getInstance().getSession().call.caller.cubeId;
            CubeEngine.getInstance().getCallService().answerCall(cubeId);
        }
        else if (i == R.id.call_switch_audio_btn) {
            MediaService ms = CubeEngine.getInstance().getMediaService();
            if (ms != null) {
                if (ms.isVideoEnabled()) {
                    ms.setVideoEnabled(false);
                    mCallSwitchAudioBtn.setText(R.string.switch_to_video);
                }else{
                    ms.setVideoEnabled(true);
                    mCallSwitchAudioBtn.setText(R.string.switch_to_voice);
                }
            }
//            //切换到语音通话视图
//            hideVideoCallViewStub();
//            showAudioCallViewStub();
        }
        else if (i == R.id.call_switch_speaker_btn) {
            MediaService ms = CubeEngine.getInstance().getMediaService();
            LogUtil.d("===走到切换免提了");
            if (ms.isSpeakerEnabled()) {
                // 免提（不是听筒）
                ms.setSpeakerEnabled(false);
                this.mCallSwitchSpeakerBtn.setSelected(false);
            }
            else {
                ms.setSpeakerEnabled(true);
                this.mCallSwitchSpeakerBtn.setSelected(true);
            }
        }
        else if (i == R.id.call_switch_mute_btn) {
            MediaService ms = CubeEngine.getInstance().getMediaService();
            if (ms.isAudioEnabled()) {
                ms.setAudioEnabled(false);
                this.mCallSwitchMuteBtn.setSelected(false);
            }
            else {
                ms.setAudioEnabled(true);
                this.mCallSwitchMuteBtn.setSelected(true);
            }
        }
        else if (i == R.id.call_switch_camera_btn) {
            MediaService ms = CubeEngine.getInstance().getMediaService();
            if (ms != null) {
                ms.switchCamera();
            }
        }
        else if (i == R.id.call_audio_root_layout) {
            if (this.mCallState == CallStatus.AUDIO_CALLING) {    // 语音通话中
                if (this.mCallControlLayout.getVisibility() == View.VISIBLE) {
                    this.mCallControlLayout.setVisibility(View.GONE);
                }
                else {
                    this.mCallControlLayout.setVisibility(View.VISIBLE);
                }
            }
        }
        else if (i == R.id.call_video_root_layout) {
            if (this.mCallState == CallStatus.VIDEO_CALLING) {    // 视频通话中
                if (this.mCallControlLayout.getVisibility() == View.VISIBLE) {
                    this.mCallControlLayout.setVisibility(View.GONE);
                }
                else {
                    this.mCallControlLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    private static String getPeerCubeId() {
        CallSession call = CubeEngine.getInstance().getSession().call;
        String cubeId;
        if (call.callDirection == CallDirection.Incoming) {
            cubeId = call.caller.cubeId;
        }
        else {
            cubeId = call.callee.cubeId;
        }
        LogUtil.d("===获取cueID==="+cubeId);
        return cubeId;
    }

    /**
     * 获取参数
     */
    private void getArguments() {
        Bundle data = getIntent().getBundleExtra("call_data");
        this.mCallId = data.getString("call_id");
        this.mCallState = (CallStatus) data.getSerializable("call_state");
        this.mCallTime = data.getLong("call_time");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (this.mCallState == CallStatus.AUDIO_OUTGOING || mCallState == CallStatus.AUDIO_INCOMING || mCallState == CallStatus.VIDEO_OUTGOING || mCallState == CallStatus.VIDEO_INCOMING) {
//            this.release();
//        }
        CubeEngine.getInstance().getCallService().terminateCall(getPeerCubeId());
        release();
    }
    /**
     * 显示语音呼叫视图
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void showAudioOutgoingViewStub() {
        if (this.mCallAudioOutgoingVs == null) {
            this.mCallAudioOutgoingVs = (ViewStub) this.findViewById(R.id.call_audio_outgoing_vs);
            assert this.mCallAudioOutgoingVs != null;
            View inflateView = this.mCallAudioOutgoingVs.inflate();
            this.mPeerHeadIv = (ImageView) inflateView.findViewById(R.id.peer_head_iv);
            this.mPeerNameTv = (TextView) inflateView.findViewById(R.id.peer_name_tv);
            this.mCallHintTv = (TextView) inflateView.findViewById(R.id.call_hint_tv);
            this.mCallCancelBtn = (Button) inflateView.findViewById(R.id.call_cancel_btn);
            this.mCallSwitchSpeakerBtn = (Button) inflateView.findViewById(R.id.call_switch_speaker_btn);
            this.mCallSwitchMuteBtn = (Button) inflateView.findViewById(R.id.call_switch_mute_btn);
        } else {
            this.mCallAudioOutgoingVs.setVisibility(View.VISIBLE);
        }
        if (this.mCallSwitchMuteBtn != null) {
            this.mCallSwitchMuteBtn.setSelected(CubeEngine.getInstance().getMediaService().isAudioEnabled());
        }
        this.mPeerNameTv.setText(this.mCallId);
        GlideUtil.loadCircleImage(AppConstants.AVATAR_URL+ this.mCallId,this,mPeerHeadIv, DiskCacheStrategy.NONE,true,R.drawable.default_head_user);
        this.initListener();
    }
    /**
     * 隐藏语音呼叫视图
     */
    private void hideAudioOutgoingViewStub() {
        if (this.mCallAudioOutgoingVs != null) {
            this.mCallAudioOutgoingVs.setVisibility(View.GONE);
        }
    }
    /**
     * 显示语音来电视图
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void showAudioIncomingViewStub() {
        if (this.mCallAudioIncomingVs == null) {
            this.mCallAudioIncomingVs = (ViewStub) this.findViewById(R.id.call_audio_incoming_vs);
            assert this.mCallAudioIncomingVs != null;
            View inflateView = this.mCallAudioIncomingVs.inflate();
            this.mPeerHeadIv = (ImageView) inflateView.findViewById(R.id.peer_head_iv);
            this.mPeerNameTv = (TextView) inflateView.findViewById(R.id.peer_name_tv);
            this.mCallHintTv = (TextView) inflateView.findViewById(R.id.call_hint_tv);
            this.mCallRefuseBtn = (Button) inflateView.findViewById(R.id.call_refuse_btn);
            this.mCallAnswerBtn = (Button) inflateView.findViewById(R.id.call_answer_btn);
            this.mCallHintTv.setText(R.string.someone_wanted_to_talk_to_you_voice_calls);
        }
        else {
            this.mCallAudioIncomingVs.setVisibility(View.VISIBLE);
        }
        this.mPeerNameTv.setText(this.mCallId);
        GlideUtil.loadCircleImage(AppConstants.AVATAR_URL+this.mCallId,this,mPeerHeadIv, DiskCacheStrategy.NONE,true,R.drawable.default_head_user);
        this.initListener();
    }

    /**
     * 隐藏语音来电视图
     */
    private void hideAudioIncomingViewStub() {
        if (this.mCallAudioIncomingVs != null) {
            this.mCallAudioIncomingVs.setVisibility(View.GONE);
        }
    }
    /**
     * 显示语音通话视图
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void showAudioCallViewStub() {
        if (this.mCallAudioCallVs == null) {
            this.mCallAudioCallVs = (ViewStub) this.findViewById(R.id.call_audio_vs);
            assert this.mCallAudioCallVs != null;
            View inflateView = this.mCallAudioCallVs.inflate();
            this.mCallAudioRootLayout = (RelativeLayout) inflateView.findViewById(R.id.call_audio_root_layout);
            this.mPeerHeadIv = (ImageView) inflateView.findViewById(R.id.peer_head_iv);
            this.mPeerNameTv = (TextView) inflateView.findViewById(R.id.peer_name_tv);
            this.mCallHintTv = (TextView) inflateView.findViewById(R.id.call_hint_tv);
            this.mCallControlLayout = (LinearLayout) inflateView.findViewById(R.id.call_control_layout);
            this.mCallHangUpBtn = (Button) inflateView.findViewById(R.id.call_hang_up_btn);
            this.mCallSwitchSpeakerBtn = (Button) inflateView.findViewById(R.id.call_switch_speaker_btn);
            this.mCallSwitchMuteBtn = (Button) inflateView.findViewById(R.id.call_switch_mute_btn);
            this.mCallChronometer = (Chronometer) inflateView.findViewById(R.id.call_chronometer);
            this.tv_tojoin = ((TextView) inflateView.findViewById(R.id.tv_to_join));
            this.members_recycleview = ((RecyclerView) inflateView.findViewById(R.id.joined_members_recycleview));
            this.mCallHintTv.setVisibility(View.VISIBLE);
        }
        else {
            this.mCallAudioCallVs.setVisibility(View.VISIBLE);
        }
        // 默认打开免提
        if (this.mCallSwitchSpeakerBtn != null) {
            this.mCallSwitchSpeakerBtn.setSelected(CubeEngine.getInstance().getMediaService().isSpeakerEnabled());
        }
        if (this.mCallSwitchMuteBtn != null) {
            this.mCallSwitchMuteBtn.setSelected(CubeEngine.getInstance().getMediaService().isAudioEnabled());
        }

        // 查询用户信息.....
        //主要是头像的获取
        this.initListener();

        //已加入成员列表
        //对端成员通过id获取头像，目前没有接口
        List<String> avatarList = new ArrayList<>();
        avatarList.add(AppConstants.AVATAR_URL+SpUtil.getCubeId());
        avatarList.add(AppConstants.AVATAR_URL+this.mCallId);
        P2PmemberAdapter adapter = new P2PmemberAdapter(avatarList,this);
        LinearLayoutManager ms= new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        members_recycleview.setLayoutManager(ms);
        members_recycleview.setAdapter(adapter);

    }
    /**
     * 隐藏语音通话视图
     */
    private void hideAudioCallViewStub() {
        if (this.mCallAudioCallVs != null) {
            this.mCallAudioCallVs.setVisibility(View.GONE);
        }
    }
    /**
     * 显示视频呼叫视图
     */
    private void showVideoOutgoingViewStub() {
        if (this.mCallVideoOutgoingVs == null) {
            this.mCallVideoOutgoingVs = (ViewStub) this.findViewById(R.id.call_audio_outgoing_vs);
            assert this.mCallVideoOutgoingVs != null;
            View inflateView = this.mCallVideoOutgoingVs.inflate();
            this.mPeerHeadIv = (ImageView) inflateView.findViewById(R.id.peer_head_iv);
            this.mPeerNameTv = (TextView) inflateView.findViewById(R.id.peer_name_tv);
            this.mCallHintTv = (TextView) inflateView.findViewById(R.id.call_hint_tv);
            this.mCallCancelBtn = (Button) inflateView.findViewById(R.id.call_cancel_btn);
            this.mCallSwitchSpeakerBtn = (Button) inflateView.findViewById(R.id.call_switch_speaker_btn);
            this.mCallSwitchMuteBtn = (Button) inflateView.findViewById(R.id.call_switch_mute_btn);
        }
        else {
            this.mCallVideoOutgoingVs.setVisibility(View.VISIBLE);
        }

        if (this.mCallSwitchMuteBtn != null) {
            this.mCallSwitchMuteBtn.setSelected(CubeEngine.getInstance().getMediaService().isAudioEnabled());
        }
        //初始化用户信息
        this.mPeerNameTv.setText(this.mCallId);
        GlideUtil.loadCircleImage(AppConstants.AVATAR_URL+ SpUtil.getCubeId(),this,mPeerHeadIv, DiskCacheStrategy.NONE,true,R.drawable.default_head_user);

        // 初始化事件
        this.initListener();
    }

    /**
     * 隐藏视频呼叫视图
     */
    private void hideVideoOutgoingViewStub() {
        if (this.mCallVideoOutgoingVs != null) {
            this.mCallVideoOutgoingVs.setVisibility(View.GONE);
        }
    }

    /**
     * 显示视频来电视图
     */
    private void showVideoIncomingViewStub() {
        if (this.mCallVideoIncomingVs == null) {
            this.mCallVideoIncomingVs = (ViewStub) this.findViewById(R.id.call_audio_incoming_vs);
            assert this.mCallVideoIncomingVs != null;
            View inflateView = this.mCallVideoIncomingVs.inflate();
            this.mPeerHeadIv = (ImageView) inflateView.findViewById(R.id.peer_head_iv);
            this.mPeerNameTv = (TextView) inflateView.findViewById(R.id.peer_name_tv);
            this.mCallHintTv = (TextView) inflateView.findViewById(R.id.call_hint_tv);
            this.mCallRefuseBtn = (Button) inflateView.findViewById(R.id.call_refuse_btn);
            this.mCallAnswerBtn = (Button) inflateView.findViewById(R.id.call_answer_btn);
            this.mCallHintTv .setText(R.string.someone_wanted_to_talk_to_you_video_calls);
        }
        else {
            this.mCallVideoIncomingVs.setVisibility(View.VISIBLE);
        }
        //初始化用户信息
        this.mPeerNameTv.setText(this.mCallId);
        GlideUtil.loadCircleImage(AppConstants.AVATAR_URL+this.mCallId,this,mPeerHeadIv, DiskCacheStrategy.NONE,true,R.drawable.default_head_user);

        // 初始化事件
        this.initListener();
    }

    /**
     * 隐藏视频来电视图
     */
    private void hideVideoIncomingViewStub() {
        if (this.mCallVideoIncomingVs != null) {
            this.mCallVideoIncomingVs.setVisibility(View.GONE);
        }
    }

    /**
     * 显示视频通话视图
     */
    private void showVideoCallViewStub() {
        if (this.mCallVideoCallVs == null) {
            this.mCallVideoCallVs = (ViewStub) this.findViewById(R.id.call_video_vs);
            assert this.mCallVideoCallVs != null;
            View inflateView = this.mCallVideoCallVs.inflate();
            this.mCallVideoRootLayout = (FrameLayout) inflateView.findViewById(R.id.call_video_root_layout);
            this.mMyVideoLayout = (LinearLayout) inflateView.findViewById(R.id.my_video_layout);
            this.mPeerVideoLayout = (LinearLayout) inflateView.findViewById(R.id.peer_video_layout);
            this.mCallControlLayout = (LinearLayout) inflateView.findViewById(R.id.call_control_layout);
            this.mCallHangUpBtn = (Button) inflateView.findViewById(R.id.call_hang_up_btn);
            this.mCallSwitchSpeakerBtn = (Button) inflateView.findViewById(R.id.call_switch_speaker_btn);
            this.mCallSwitchMuteBtn = (Button) inflateView.findViewById(R.id.call_switch_mute_btn);
            this.mCallSwitchAudioBtn = (Button) inflateView.findViewById(R.id.call_switch_audio_btn);
            this.mCallSwitchCameraBtn = (Button) inflateView.findViewById(R.id.call_switch_camera_btn);
            this.mCallChronometer = (Chronometer) inflateView.findViewById(R.id.call_chronometer);
            this.members_recycleview = ((RecyclerView) inflateView.findViewById(R.id.joined_members_recycleview));
            this.tv_tojoin = ((TextView) inflateView.findViewById(R.id.tv_to_join));
        }
        else {
            this.mCallVideoCallVs.setVisibility(View.VISIBLE);
        }
        //已加入成员头像显示
        List<String> avatarList = new ArrayList<>();
        avatarList.add(AppConstants.AVATAR_URL+SpUtil.getCubeId());
        avatarList.add(AppConstants.AVATAR_URL+this.mCallId);
        P2PmemberAdapter adapter = new P2PmemberAdapter(avatarList,this);
        members_recycleview.setAdapter(adapter);
        if (this.mCallSwitchMuteBtn != null) {
            this.mCallSwitchMuteBtn.setSelected(CubeEngine.getInstance().getMediaService().isAudioEnabled());
        }

        // 初始化事件
        this.initListener();
        this.mMyVideoView = CubeEngine.getInstance().getCallService().getLocalView();
        this.mPeerVideoView = CubeEngine.getInstance().getCallService().getRemoteView();

        if (this.mMyVideoLayout.getChildAt(0) == null) {
            this.mMyVideoLayout.addView(this.mMyVideoView, 0);
        }
        if (this.mPeerVideoLayout.getChildAt(0) == null) {
            this.mPeerVideoLayout.addView(this.mPeerVideoView, 0);
        }

        //已加入成员列表
        memberList = new ArrayList<>();
        List<String> memberList = new ArrayList<>();
        memberList.add(AppConstants.AVATAR_URL+SpUtil.getCubeId());
        memberList.add(AppConstants.AVATAR_URL+this.mCallId);
        joinedMemberAdapter = new P2PmemberAdapter(memberList,this);
        LinearLayoutManager ms= new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        members_recycleview.setLayoutManager(ms);
        members_recycleview.setAdapter(joinedMemberAdapter);
    }

    /**
     * 隐藏视频通话视图
     */
    private void hideVideoCallViewStub() {
        if (this.mCallVideoCallVs != null) {
            this.mCallVideoCallVs.setVisibility(View.GONE);
        }
    }

    /**
     * 释放
     */
    private void release() {
        this.reset();
        this.releaseScreenOn();
        this.finish();
    }

    /**
     * 保持屏幕常亮
     */
    private void keepScreenOn() {
        synchronized (this.mWakeLockSync) {
            if (this.mWakeLock == null) {
                PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
                this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, P2PCallActivity.this.getClass().toString());
            }
            this.mWakeLock.acquire();
        }
    }


    private void releaseScreenOn() {
        synchronized (this.mWakeLockSync) {
            if (this.mWakeLock != null && this.mWakeLock.isHeld()) {
                this.mWakeLock.release();
            }
        }
    }

    private void reset() {
        this.mCallState = CallStatus.NO_CALL;
        this.mCallState = CallStatus.NO_CALL;
        if (this.mCallChronometer != null) {
            this.mCallChronometer.stop();
            this.mCallChronometer.setVisibility(View.INVISIBLE);
        }
        if (this.mMyVideoLayout != null) {
            this.mMyVideoLayout.removeView(this.mMyVideoView);
        }
        if (this.mPeerVideoLayout != null) {
            this.mPeerVideoLayout.removeView(this.mPeerVideoView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallHandle.getInstance().removeCallStateListener(this);
    }

    /**
     * 显示视图布局文件
     */
    private void showViewStub() {
        if (mCallState == CallStatus.AUDIO_OUTGOING) {    // 语音呼叫
            this.showAudioOutgoingViewStub();
        }
        else if (mCallState == CallStatus.AUDIO_INCOMING) {    // 语音来电
            this.showAudioIncomingViewStub();
        }
        else if (mCallState == CallStatus.AUDIO_CALLING) {    // 语音通话中
            this.showAudioCallViewStub();
        }
        else if (mCallState == CallStatus.VIDEO_OUTGOING) {    // 视频呼叫
            this.showVideoOutgoingViewStub();
        }
        else if (mCallState == CallStatus.VIDEO_INCOMING) {    // 视频来电
            this.showVideoIncomingViewStub();
        }
        else if (mCallState == CallStatus.VIDEO_CALLING) {    // 视频通话中
            this.showVideoCallViewStub();
        }
    }
    /**
     * 切换ViewStub
     */
    private void switchViewStub(int viewStubId) {
        this.hideAudioOutgoingViewStub();
        this.hideAudioIncomingViewStub();
        this.hideAudioCallViewStub();
        this.hideVideoOutgoingViewStub();
        this.hideVideoIncomingViewStub();
        this.hideVideoCallViewStub();
        if (viewStubId == R.id.call_audio_outgoing_vs) {
            this.showAudioOutgoingViewStub();
        }
        else if (viewStubId == R.id.call_audio_incoming_vs) {
            this.showAudioIncomingViewStub();
        }
        else if (viewStubId == R.id.call_audio_vs) {
            this.showAudioCallViewStub();
        }
        else if (viewStubId == R.id.call_video_vs) {
            this.showVideoCallViewStub();
        }
    }

    /**
     *  //通话状态监听器
     * @param session
     */
    @Override
    public void onInProgress(CallSession session) {

    }

    @Override
    public void onCallRinging(CallSession session) {

    }

    @Override
    public void onCallConnected(CallSession session) {
        boolean isVideoCall = session.videoEnabled;
        LogUtil.i("===回调了这里了===> onCallConnected"+isVideoCall);
        if (isVideoCall) {
            // 切换到视频通话页面
            this.mCallState = CallStatus.VIDEO_CALLING;
            this.switchViewStub(R.id.call_video_vs);
        }
        else {
            // 切换到语音通话页面
            this.mCallState = CallStatus.AUDIO_CALLING;
            this.switchViewStub(R.id.call_audio_vs);
        }

        CubeEngine.getInstance().getMediaService().setSpeakerEnabled(isVideoCall);
         //默认打开免提
        if (this.mCallSwitchSpeakerBtn != null) {
            this.mCallSwitchSpeakerBtn.setSelected(CubeEngine.getInstance().getMediaService().isSpeakerEnabled());
        }

        // 开始计时
        this.mCallChronometer.setBase(SystemClock.elapsedRealtime());

        // 屏幕常亮
        this.keepScreenOn();

    }

    @Override
    public void onCallEnded(CallSession session, CallAction action) {
        release();

    }

    @Override
    public void onCallFailed(CallSession session, CubeErrorCode errorCode) {
        LogUtil.i("P2PCallActivity ===> onCallFailed" + "，errorCode：" + errorCode.code);
        if (errorCode == CubeErrorCode.ConnectionFailed) {
            ToastUtil.showToast(this, 0, getString(R.string.connection_failure_please_try_again_later));
        }
        else if (errorCode == CubeErrorCode.ICEConnectionFailed) {
            ToastUtil.showToast(this, 0, getString(R.string.connection_failure_please_try_again_later));
        }
        else if (errorCode == CubeErrorCode.NetworkNotReachable) {
          ToastUtil.showToast(this,0,getString(R.string.connection_failure));
        }
        else if (errorCode == CubeErrorCode.BusyHere) {
            ToastUtil.showToast(this, 0, getString(R.string.call_user_busy));
        }
        else if (errorCode == CubeErrorCode.DoNotDisturb) {
            ToastUtil.showToast(this, 0, getString(R.string.call_user_busy));
        }
        else if (errorCode == CubeErrorCode.RequestTerminated) {
            ToastUtil.showToast(this, 0, getString(R.string.peer_offline_please_try_again_later));
        }
        else if (errorCode == CubeErrorCode.AlreadyInCalling) {
            ToastUtil.showToast(this, "您已经在通话中！");
        }else{
            ToastUtil.showToast(this, "对方正在通话中,请稍后再拨");
        }
        this.release();

    }

//获取通话用户成功
    @Override
    public void getCallUserSuccess(User callUser ) {
        if (null != callUser) {
            GlideUtil.loadCircleImage(callUser.avatar, P2PCallActivity.this, mPeerHeadIv, R.drawable.default_head_user);
            mPeerNameTv.setText(callUser.displayName);
            if (mCallState == CallStatus.AUDIO_INCOMING) {    // 语音来电
                mCallHintTv.setText(getResources().getString(R.string.someone_wanted_to_talk_to_you_voice_calls));
            }
            else if (mCallState == CallStatus.VIDEO_INCOMING) {    // 视频来电
                mCallHintTv.setText(getResources().getString(R.string.someone_wanted_to_talk_to_you_video_calls));
            }
        }

    }
}



