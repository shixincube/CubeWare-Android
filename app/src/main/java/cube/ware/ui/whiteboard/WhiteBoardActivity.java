package cube.ware.ui.whiteboard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.mvp.base.BaseActivity;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.RingtoneUtil;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.glide.GlideUtil;
import com.common.utils.utils.log.LogUtil;
import java.util.ArrayList;
import java.util.List;
import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.common.model.CubeErrorCode;
import cube.service.group.model.Member;
import cube.service.user.model.User;
import cube.service.whiteboard.model.Whiteboard;
import cube.service.whiteboard.model.WhiteboardSlide;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.whiteboard.WhiteBoardHandle;
import cube.ware.service.whiteboard.WhiteBoardStateListener;
import cube.ware.service.whiteboard.manager.WBCallManager;
import cube.ware.ui.whiteboard.adapter.RVJoinedMemAdapter;
import cube.ware.utils.SpUtil;

@Route(path = AppConstants.Router.WhiteBoardActivity)
public class WhiteBoardActivity extends BaseActivity<WhitePresenter> implements WhiteContract.View,View.OnClickListener, WhiteBoardStateListener {

    private int callState;
    private ViewStub mVSWhiteReceiveallLayout;  // 受邀
    private ViewStub mVSWhiteBoardLayout;       // 演示
    private ViewStub mVSWhiteBoardJoinLayout;   // 直接加入

    private ImageView mIvCallAvator;
    private TextView mTvCallName;
    private ImageView mImageViewvReceiveAvator;
    private TextView mTvReceiveCallName;
    private Button mIvReceiveRefuse;
    private Button mIvReceiveAnswer;
    private LinearLayout pencialLayout;
    private LinearLayout eraserLayout;
    private LinearLayout revokeLayout;
    private LinearLayout recoverLayout;
    private LinearLayout wipeLayout;
    private LinearLayout mLlPainContain;
    private ImageView mIvPainThin;
    private ImageView mIvPainMiddle;
    private ImageView mIvPainLarge;
    private ImageView mIvColorBase_1;
    private ImageView mIvColorBase_2;
    private ImageView mIvColorBase_7;
    private ImageView mIvColorBase_6;

    private Whiteboard mWhiteboard;
    private String inviteId;
    private List<String> inviteList;
    private TextView mTvTitle;
    private ImageButton mBtAddMem;
    private LinearLayout mLlWhiteBoardView;
    private Button mBtHangUp;
    private LinearLayout mLlpencialToolContain;
    private RecyclerView mRvJoined;
    private RecyclerView mRvWaiteJoined;
    private LinearLayoutManager mLayoutManagerJoined;
    private LinearLayoutManager mLayoutManagerWaite;
    private String groupId;
    private String TAG="WhiteBoardActivity";

    private List<String> joinedList =new ArrayList<>();
    private List<String> waiteJoinedList=new ArrayList<>();
    private RVJoinedMemAdapter mRvWaiteJoinedMemAdapter;
    private RVJoinedMemAdapter mRvJoinedMemAdapter;
    private TextView mTvCallType;
    private CubeSessionType mChatType;
    private User mUser;
    private RecyclerView mRvNeedInvite;
    private RVJoinedMemAdapter mRvNeedInviteAdapter;
    private Button mBtJoin;
    private TextView mTvJoinTitle;
    private ImageView mIvPeerHeader;
    private boolean hasJoined;
    private ProgressDialog mProgressDialog;
    private ImageView imag_back;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_call;
    }

    @Override
    protected WhitePresenter createPresenter() {
        return new WhitePresenter(this,this);
    }

    @Override
    protected void initView() {
        WhiteBoardHandle.getInstance().addWhiteBoardStateListeners(this);
        getArgment();
        switchViewStub();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("加入中。。。");
    }

    private void getArgment() {
        Bundle bundle = getIntent().getBundleExtra(AppConstants.Value.BUNDLE);
        callState= (int) bundle.get(AppConstants.Value.CALLSTATA_WHITE_BOARD);
        mWhiteboard= (Whiteboard) bundle.getSerializable(AppConstants.Value.WHITEBOARD);
        inviteId=bundle.getString(AppConstants.Value.INVITE_ID);
        groupId=bundle.getString(AppConstants.Value.GROUP_ID);
        mChatType= (CubeSessionType) bundle.getSerializable(AppConstants.Value.CHAT_TYPE);
        inviteList=bundle.getStringArrayList(AppConstants.Value.INVITE_LIST);
        LogUtil.d("===getArgment--中获取到的邀请者id== "+inviteId);
        LogUtil.d("===getArgment--中获取到的groupId== "+groupId);

        if(callState==AppConstants.Value.CALLSTATE_CREATE){
            initAction();
//            RingtoneUtil.play(R.raw.outgoing,this);
        }
        mUser=CubeEngine.getInstance().getSession().getUser();
    }

    private void initAction() {
        if(inviteList!=null&&inviteList.size()>0){
            LogUtil.i("Whiteboard---->>",inviteList.toString());
            CubeEngine.getInstance().getWhiteboardService().inviteMembers(mWhiteboard.whiteboardId,inviteList);
        }
    }

    private void switchViewStub() {
        hideWhiteReceiveallLayoutViewStub();
        hideWhiteBoardLayoutViewStub();
        hideWhiteBoardJoinLayoutViewStub();
        if (callState == AppConstants.Value.CALLSTATE_INVITE) {    // 受邀
            this.showWhiteReceiveallLayout();
        }if (callState == AppConstants.Value.CALLSTATE_CREATE) {    // 演示
            this.showWhiteBoardLayout();
        }if (callState == AppConstants.Value.CALLSTATE_JOIN) {    // 主动加入
            this.showWhiteBoardJoinLayout();
        }
    }

    private void hideWhiteBoardJoinLayoutViewStub() {
        if(this.mVSWhiteBoardJoinLayout!=null){
           this.mVSWhiteBoardJoinLayout.setVisibility(View.GONE);
        }
    }

    private void hideWhiteBoardLayoutViewStub() {
        if (this.mVSWhiteBoardLayout != null) {
            this.mVSWhiteBoardLayout.setVisibility(View.GONE);
        }
    }

    private void hideWhiteReceiveallLayoutViewStub() {
        if (this.mVSWhiteReceiveallLayout != null) {
            this.mVSWhiteReceiveallLayout.setVisibility(View.GONE);
        }
    }
    //接受者
    private void showWhiteReceiveallLayout() {
        if(mVSWhiteReceiveallLayout==null){
            mVSWhiteReceiveallLayout=this.findViewById(R.id.call_audio_incoming_vs);
            View inflateView = this.mVSWhiteReceiveallLayout.inflate();
            mImageViewvReceiveAvator = inflateView.findViewById(R.id.peer_head_iv);
            mTvReceiveCallName = inflateView.findViewById(R.id.peer_name_tv);
            mIvReceiveRefuse = inflateView.findViewById(R.id.call_refuse_btn);
            mIvReceiveAnswer = inflateView.findViewById(R.id.call_answer_btn);
            mTvCallType = inflateView.findViewById(R.id.call_hint_tv);
            mRvNeedInvite = inflateView.findViewById(R.id.need_join_members_recycleview);
            if(mChatType==CubeSessionType.P2P){
                inflateView.findViewById(R.id.members_layout).setVisibility(View.GONE);
            }else {
                inflateView.findViewById(R.id.members_layout).setVisibility(View.VISIBLE);
            }
        }

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3);
        mRvNeedInvite.setLayoutManager(gridLayoutManager);
        List<Member> memberList=new ArrayList<>();
        if (mWhiteboard != null && mWhiteboard.members != null) {
            //设置数据
            memberList.addAll(mWhiteboard.members);
        }
        if (mWhiteboard != null && mWhiteboard.invites != null) {
            //设置数据
            memberList.addAll(mWhiteboard.invites);
        }
        initInviteRV(memberList);
        //加载头像
        GlideUtil.loadCircleImage(AppConstants.AVATAR_URL+inviteId,this,mImageViewvReceiveAvator, DiskCacheStrategy.NONE,false,R.drawable.default_head_user);
        //获取邀请者数据
        LogUtil.d("===邀请者=="+inviteId);
        mPresenter.getUserData(inviteId);
        mTvCallType.setText(this.getResources().getString(R.string.someone_wanted_to_talk_to_you_wb_calls));
    }

    //演示
    private void showWhiteBoardLayout() {
        if(mVSWhiteBoardLayout == null){
            mVSWhiteBoardLayout = this.findViewById(R.id.call_audio_vs);
            View inflateView = this.mVSWhiteBoardLayout.inflate();
            mTvTitle = inflateView.findViewById(R.id.tv_title);
            mTvTitle.setText(this.getResources().getString(R.string.wb_calling_title));
            mLlWhiteBoardView = inflateView.findViewById(R.id.ll_romote_layout_contain);
            mBtHangUp = inflateView.findViewById(R.id.call_hang_up_btn);
            mBtAddMem = inflateView.findViewById(R.id.call_group_add_btn);
            mLlpencialToolContain = inflateView.findViewById(R.id.wb_tab_window_ll);
            mRvJoined = inflateView.findViewById(R.id.joined_members_recycleview);
            mRvWaiteJoined = inflateView.findViewById(R.id.to_be_joined_members_recycleview);
            //白板工具的layout
            inflateView.findViewById(R.id.rl_wb_tool_layout).setVisibility(View.VISIBLE);
            inflateView.findViewById(R.id.call_switch_speaker_btn).setVisibility(View.INVISIBLE);
            inflateView.findViewById(R.id.call_switch_mute_btn).setVisibility(View.INVISIBLE);
            inflateView.findViewById(R.id.call_chronometer).setVisibility(View.GONE);
            pencialLayout = inflateView.findViewById(R.id.pencil_layout);//铅笔
            revokeLayout = inflateView.findViewById(R.id.revoke_layout);//圆形
            recoverLayout = inflateView.findViewById(R.id.recover_layout);//矩形
            wipeLayout = inflateView.findViewById(R.id.wipe_layout);//箭头
            eraserLayout = inflateView.findViewById(R.id.wb_eraser_ll);//擦除
            mLlPainContain = inflateView.findViewById(R.id.paint_attr_ll);
//            mIvPainThin = inflateView.findViewById(R.id.paint_weight_thin);
//            mIvPainMiddle = inflateView.findViewById(R.id.paint_weight_middle);
//            mIvPainLarge = inflateView.findViewById(R.id.paint_weight_large);
//            mIvColorBase_1 = inflateView.findViewById(R.id.wb_getcolor_base_1);
//            mIvColorBase_2 = inflateView.findViewById(R.id.wb_getcolor_base_2);
//            mIvColorBase_6 = inflateView.findViewById(R.id.wb_getcolor_base_6);
//            mIvColorBase_7 = inflateView.findViewById(R.id.wb_getcolor_base_7);
            //单聊白板,隐藏部分view
            if(mChatType==CubeSessionType.P2P){
                mBtAddMem.setVisibility(View.GONE);
            }else {
                mBtAddMem.setVisibility(View.VISIBLE);
            }
            inflateView.findViewById(R.id.to_be_joined_layout).setVisibility(View.VISIBLE);
        }

        //添加白板view,必须修改LayoutParams，否则没有宽高
        View view = CubeEngine.getInstance().getWhiteboardService().getView();
        int displayHeight = ScreenUtil.getDisplayHeight();
        mLlWhiteBoardView.addView(view,new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                displayHeight*2/5));
        initRecyclerView();
        initListener();
        initRVData();
//        //设置画布的放大缩小
        CubeEngine.getInstance().getWhiteboardService().zoom(1.2f);
        CubeEngine.getInstance().getWhiteboardService().zoom(1.0f);
    }

    //主动加入
    private void showWhiteBoardJoinLayout(){
        if(mVSWhiteBoardJoinLayout==null){
            mVSWhiteBoardJoinLayout = this.findViewById(R.id.group_join_vs);
            View inflateView = this.mVSWhiteBoardJoinLayout.inflate();
            mBtJoin = inflateView.findViewById(R.id.call_group_join_btn);
            mTvJoinTitle = inflateView.findViewById(R.id.call_group_hint_tv);
            mTvCallType = inflateView.findViewById(R.id.call_hint_tv);
            mRvNeedInvite = inflateView.findViewById(R.id.group_member_face);
            imag_back = ((ImageView) inflateView.findViewById(R.id.imag_back));
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRvNeedInvite.setLayoutManager(gridLayoutManager);
        List<Member> memberList=new ArrayList<>();
        if (mWhiteboard != null && mWhiteboard.members != null) {
            //设置数据
            memberList.addAll(mWhiteboard.members);
        }
        if (mWhiteboard != null && mWhiteboard.invites != null) {
            //设置数据
            memberList.addAll(mWhiteboard.invites);
        }
        initInviteRV(memberList);

        mTvJoinTitle.setText(getString(R.string.start_whiteboard,mWhiteboard.members.size()+""));
        mPresenter.getUserData(mWhiteboard.founder);
        this.initListener();
    }

    /**
     * 数据库调用获取user时会回调回来
     * @param user
     */
    @Override
    public void getUserData(User user) {
        if(mVSWhiteReceiveallLayout!=null){
            mTvReceiveCallName.setText(user.displayName+"");
        }
    }

    private void initInviteRV(List<Member> cubeIdList) {
        if(cubeIdList!=null) {
            //直接刷新头像列表
            mRvNeedInviteAdapter = new RVJoinedMemAdapter(WhiteBoardActivity.this, MemberToCubeIds(cubeIdList));
            mRvNeedInvite.setAdapter(mRvNeedInviteAdapter);
        }
    }

    //recyclerview
    private void initRecyclerView() {
        mLayoutManagerJoined = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mLayoutManagerWaite = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRvJoinedMemAdapter = new RVJoinedMemAdapter(this,joinedList);
        mRvWaiteJoinedMemAdapter = new RVJoinedMemAdapter(this,waiteJoinedList);
        mRvJoined.setLayoutManager(mLayoutManagerJoined);
        mRvWaiteJoined.setLayoutManager(mLayoutManagerWaite);
        mRvJoined.setAdapter(mRvJoinedMemAdapter);
        mRvWaiteJoined.setAdapter(mRvWaiteJoinedMemAdapter);
    }

    private void initRVData() {
        //加入自己
        updateJoinedAdapter(SpUtil.getCubeId(),true);
        //发起者
        if(inviteId!=null){
            updateJoinedAdapter(inviteId,true);
        }
        //受邀者加入
        List<Member> members = mWhiteboard.members;
        if(members!=null){
            mRvJoinedMemAdapter.addListDate(MemberToCubeIds(members));
        }

        List<Member> invites = mWhiteboard.invites;
        //去重后的集合
        List<Member> membersList = mPresenter.deleteRepeat(members, invites);
        if(membersList!=null){
            mRvWaiteJoinedMemAdapter.addListDate(MemberToCubeIds(membersList));
        }
    }

    @Override
    protected void initListener() {
        if (null != imag_back){
            imag_back.setOnClickListener(this);
        }
        //接受邀请
        if(callState==AppConstants.Value.CALLSTATE_INVITE){
            mIvReceiveAnswer.setOnClickListener(this);
            mIvReceiveRefuse.setOnClickListener(this);
        }
        //发起者
        if(callState==AppConstants.Value.CALLSTATE_CREATE){
            mBtAddMem.setOnClickListener(this);
            mBtHangUp.setOnClickListener(this);
//            mTvPencilPain.setOnClickListener(this);
            pencialLayout.setOnClickListener(this);
            eraserLayout.setOnClickListener(this);
            revokeLayout.setOnClickListener(this);
            recoverLayout.setOnClickListener(this);
            wipeLayout.setOnClickListener(this);
            //画笔工具
//            mIvPainThin.setOnClickListener(this);
//            mIvPainMiddle.setOnClickListener(this);
//            mIvPainLarge.setOnClickListener(this);
//            mIvColorBase_1.setOnClickListener(this);
//            mIvColorBase_2.setOnClickListener(this);
//            mIvColorBase_7.setOnClickListener(this);
//            mIvColorBase_6.setOnClickListener(this);
        }
        if(callState==AppConstants.Value.CALLSTATE_JOIN){
            mBtJoin.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.imag_back:
                this.finish();
                break;
            case R.id.call_group_join_btn: //主动加入
                CubeEngine.getInstance().getWhiteboardService().join(mWhiteboard.whiteboardId);
                if(mProgressDialog!=null&&!mProgressDialog.isShowing()){
                    mProgressDialog.show();
                }
                break;
            case R.id.call_group_add_btn:
                jumpToAdd(); //邀请
                break;
            case R.id.call_refuse_btn:  //拒绝
                CubeEngine.getInstance().getWhiteboardService().rejectInvite(mWhiteboard.whiteboardId,inviteId);
                finish();
                break;
            case R.id.call_answer_btn:  //接受加入
                CubeEngine.getInstance().getWhiteboardService().acceptInvite(mWhiteboard.whiteboardId,inviteId);
                if(mProgressDialog!=null&&!mProgressDialog.isShowing()){
                    mProgressDialog.show();
                }
                break;
                //演示的view
            case R.id.call_hang_up_btn: //挂断
                CubeEngine.getInstance().getWhiteboardService().quit(mWhiteboard.whiteboardId);
                finish();
                break;
            case R.id.pencil_layout: //铅笔
                CubeEngine.getInstance().getWhiteboardService().selectPencil();
                break;
            case R.id.revoke_layout: //圆形
                CubeEngine.getInstance().getWhiteboardService().selectEllipse();
                break;
            case R.id.recover_layout: //矩形(拖曳方法)
                //调用聚焦。保证在屏幕中间
                CubeEngine.getInstance().getWhiteboardService().zoom(1.2f);
                CubeEngine.getInstance().getWhiteboardService().zoom(1.0f);
                CubeEngine.getInstance().getWhiteboardService().unSelect();
                break;
            case R.id.wipe_layout: //箭头
                CubeEngine.getInstance().getWhiteboardService().selectArrow();
                break;
            case R.id.wb_eraser_ll: //擦除
                CubeEngine.getInstance().getWhiteboardService().cleanup();
                break;
                //画笔粗细
//            case R.id.paint_weight_thin:
//                setPencilWeight(2);
//                break;
//            case R.id.paint_weight_middle:
//                setPencilWeight(10);
//                break;
//             case R.id.paint_weight_large:
//                 setPencilWeight(20);
//                break;
//                //画笔颜色
//            case R.id.wb_getcolor_base_1:
//                setPencilColor("#000000",R.drawable.wb_paint_color_black_selector);
//                break;
//            case R.id.wb_getcolor_base_2:
//                setPencilColor("#f04f57",R.drawable.wb_paint_color_red_selecor);
//                break;
//            case R.id.wb_getcolor_base_6:
//                setPencilColor("#00a0e9",R.drawable.wb_paint_color_blue_selector);
//                break;
//            case R.id.wb_getcolor_base_7:
//                setPencilColor("#009944",R.drawable.wb_paint_color_green_selector);
//                break;
        }
    }

    //二次邀请成员
    private void jumpToAdd() {
        ArrayList<String> notCheckList = getNotCheckList();
        notCheckList.addAll(MemberToCubeIds(mWhiteboard.invites));
        Bundle bundle=new Bundle();
        bundle.putInt("select_type",6);//二次邀请
        bundle.putString("group_id",groupId);
        bundle.putString("white_board_id",mWhiteboard.whiteboardId);//首次创建
        bundle.putStringArrayList("not_check_list",notCheckList);
        RouterUtil.navigation(AppConstants.Router.SelectMemberActivity,bundle);
    }

    private ArrayList<String> getNotCheckList() {
        List<Member> members = mWhiteboard.members;
        ArrayList<String> list=new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            list.add(members.get(i).cubeId);
        }
        return list;
    }

    private void showLeftPencialTool() {
        if(mLlPainContain.getVisibility()==View.GONE) {
            mLlPainContain.setVisibility(View.VISIBLE);
        }
    }
    private void dismissLeftPencialTool() {
        if(mLlPainContain.getVisibility()==View.VISIBLE){
            mLlPainContain.setVisibility(View.GONE);
        }
    }

    //画笔颜色
    private void setPencilColor(String color,int colorDrawable) {
        CubeEngine.getInstance().getWhiteboardService().setLineColor(color);
    }

    //画笔粗细
    private void setPencilWeight(int wide) {
        CubeEngine.getInstance().getWhiteboardService().setLineWeight(wide);
    }


    @Override
    public void onWhiteboardCreated(Whiteboard var1, User var2) {

    }

    @Override
    public void onWhiteboardDestroyed(Whiteboard whiteboard, User user) {
        if(TextUtils.isEmpty(groupId) || mPresenter.isCurrentGroup(groupId,whiteboard.bindGroupId)){
            if(mWhiteboard.whiteboardId.equals(whiteboard.whiteboardId)){
                finish();
            }
        }
    }

    @Override
    public void onWhiteboardInvited(Whiteboard whiteboard, User from, List<User> userList) {
        LogUtil.i(TAG,"Invited:"+from.cubeId+" "+groupId+" "+whiteboard.groupId);
//        LogUtil.i(TAG,"Invited:"+whiteboard.toString());
        //是当前白板
        if(TextUtils.isEmpty(groupId) || mPresenter.isCurrentGroup(groupId,whiteboard.bindGroupId)){
            mWhiteboard=whiteboard;
            //获取到邀请的人
            List<Member> members = mWhiteboard.members;
            if(members!=null){
                if(mRvJoinedMemAdapter!=null){
                    mRvJoinedMemAdapter.addListDate(MemberToCubeIds(members));
                }
            }
            List<Member> invites = mWhiteboard.invites;//去重后的集合
            List<Member> membersList = mPresenter.deleteRepeat(members, invites);

            if(membersList!=null){//添加数据
                if(mRvWaiteJoinedMemAdapter!=null){
                    mRvWaiteJoinedMemAdapter.addListDate(MemberToCubeIds(membersList));
                }
            }
        }
    }

    @Override
    public void onWhiteboardRejectInvited(Whiteboard whiteboard, User from, User rejectUser) {
        if(TextUtils.isEmpty(groupId) || mPresenter.isCurrentGroup(groupId,whiteboard.bindGroupId) || !from.cubeId.equals(mUser.cubeId) ){
            if(whiteboard.whiteboardId.equals(mWhiteboard.whiteboardId)){
                mWhiteboard=whiteboard;
                showMessage(rejectUser.cubeId+"拒绝邀请");
                updateJoinedAdapter(rejectUser.cubeId,false);
                //对方决绝，是否需要提出
                if(mChatType==CubeSessionType.P2P&&!rejectUser.cubeId.equals(SpUtil.getCubeId())){
                    CubeEngine.getInstance().getWhiteboardService().quit(mWhiteboard.whiteboardId);
                    finish();
                }
            }
        }
    }

    @Override
    public void onWhiteboardAcceptInvited(Whiteboard whiteboard, User from, User joinedMember) {
        if(TextUtils.isEmpty(groupId) || mPresenter.isCurrentGroup(groupId,whiteboard.bindGroupId)){
            if(whiteboard.whiteboardId.equals(mWhiteboard.whiteboardId)){
                this.mWhiteboard=whiteboard;
                if(mPresenter.isSelf(joinedMember.cubeId)){
                    CubeEngine.getInstance().getWhiteboardService().join(whiteboard.whiteboardId);
                }else {
                    //创建者收到有人加入的回调,刷新两个adapter
                    updateJoinedAdapter(joinedMember.cubeId,true);
                }
            }
        }
    }

    /**
     * 加入
     * @param whiteboard
     * @param user
     */
    @Override
    public void onWhiteboardJoined(Whiteboard whiteboard, User user) {
        LogUtil.i(TAG,"join:"+user.cubeId+"  "+whiteboard.toString());
        if(TextUtils.isEmpty(groupId) || mPresenter.isCurrentGroup(groupId,whiteboard.bindGroupId)){
            if(whiteboard.whiteboardId.equals(mWhiteboard.whiteboardId)){
                this.mWhiteboard=whiteboard;
                hasJoined=true;//表示有人加入过，对方不是呼叫状态
                //邀请人员加入
                if(mPresenter.isSelf(user.cubeId)){
                    //跳转到白板演示页
                    if(mProgressDialog!=null && mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                    callState=AppConstants.Value.CALLSTATE_CREATE;
                    switchViewStub();
                }else {
                    //创建者收到有人加入的回调,刷新两个adapter
                    updateJoinedAdapter(user.cubeId,true);
                }
            }
        }
    }

    //刷新两个adapter
    private void updateJoinedAdapter(String cubeId,boolean isJoin) {
        if(isJoin){ //加入
            if(mRvJoinedMemAdapter!=null){
                mRvJoinedMemAdapter.addDate(cubeId);
            }
            if(mRvWaiteJoinedMemAdapter!=null){
                mRvWaiteJoinedMemAdapter.removeDate(cubeId);
            }
        }else { //退出
            if(mRvJoinedMemAdapter!=null){
                mRvJoinedMemAdapter.removeDate(cubeId);
            }
            if(mRvWaiteJoinedMemAdapter!=null){
                mRvWaiteJoinedMemAdapter.removeDate(cubeId);
            }
        }
    }

    @Override
    public void onWhiteboardQuited(Whiteboard whiteboard, User user) {
        if(TextUtils.isEmpty(groupId) || mPresenter.isCurrentGroup(groupId,whiteboard.bindGroupId)){
            if(mWhiteboard.whiteboardId.equals(whiteboard.whiteboardId)){
                mWhiteboard=whiteboard;
                ToastUtil.showToast(this,user.cubeId+"退出");
                updateJoinedAdapter(user.cubeId,false);
            }
        }
    }

    @Override
    public void onSlideUploading(Whiteboard var1, WhiteboardSlide var2, long var3, long var5) {

    }

    @Override
    public void onSlideUploadCompleted(Whiteboard var1, WhiteboardSlide var2) {

    }

    @Override
    public void onSlideUpdated(Whiteboard var1, WhiteboardSlide var2) {

    }

    @Override
    public void onWhiteboardFailed(Whiteboard var1, CubeError cubeError) {
        if(TextUtils.isEmpty(groupId) || mPresenter.isCurrentGroup(groupId,mWhiteboard.bindGroupId)){
            LogUtil.i("error:",cubeError.toString());
            //导出白板错误
            if(cubeError.code==CubeErrorCode.ExportWhiteboardFailed.code){
                showMessage(CubeErrorCode.ExportWhiteboardFailed.message);
            }
            //导入白板错误
            if(cubeError.code==CubeErrorCode.ImportWhiteboardFailed.code){
                showMessage(CubeErrorCode.ImportWhiteboardFailed.message);
            //参数错误
            }/*if(cubeError.code==704){
                showMessage("参数错误");
            }*/
        }
    }

    @Override
    public void onBackPressed() {
        if(mWhiteboard!=null){
            if(mWhiteboard.members.size()==1){
                CubeEngine.getInstance().getWhiteboardService().destroy(mWhiteboard.whiteboardId);
            }else {
                CubeEngine.getInstance().getWhiteboardService().quit(mWhiteboard.whiteboardId);
            }

        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //自己是创建者
        WhiteBoardHandle.getInstance().removeWhiteBoardStateListeners(this);
        //退出，isCall==false
        WBCallManager.getInstance().restCalling();
    }

    @Override
    public void showMessage(String msg){
        ToastUtil.showToast(this,msg);
    }

    /**
     * 数据转换
     * @param members
     * @return
     */
    private List<String> MemberToCubeIds(List<Member> members){
        List<String> cubeIds=new ArrayList<>();
        if (null != members && members.size()>0){
            for (int i = 0; i < members.size(); i++) {
                cubeIds.add(members.get(i).cubeId);
            }
        }
        return cubeIds;
    }
}
