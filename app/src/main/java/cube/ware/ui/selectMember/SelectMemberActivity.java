package cube.ware.ui.selectMember;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.base.BaseActivity;
import com.common.eventbus.EventBusUtil;
import com.common.utils.ToastUtil;
import cube.service.CubeEngine;
import cube.service.conference.Conference;
import cube.ware.R;
import cube.ware.core.CubeConstants;
import cube.ware.data.room.model.CubeUser;
import cube.ware.ui.contact.adapter.SelectContactsAdapter;
import cube.ware.utils.SpUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Route(path = CubeConstants.Router.SelectMemberActivity)
public class SelectMemberActivity extends BaseActivity<SelectMemberPresenter> implements SelectMemberContract.View, SwipeRefreshLayout.OnRefreshListener, SelectContactsAdapter.OnItemSelectedListener, SelectMemberAdapter.OnItemSelectedShowToast {
    private TextView                        mBack;
    private TextView                        mTitle;
    private TextView                        mComplete;
    private SwipeRefreshLayout              mRefreshLayout;
    private RecyclerView                    mFriendRv;
    public  LinkedHashMap<String, CubeUser> mSelectedCubeMap = new LinkedHashMap<>();
    private List<String>                    mNotChecked      = new ArrayList<>();
    private SelectMemberAdapter             mRvSelectAdapter;
    private List<CubeUser>                  mCubeUsers       = new ArrayList<>();
    private LinearLayoutManager             mLinearLayoutManager;
    private int                             selectType; //1 不依赖群组视频首次邀请 2 音频依赖群组首次邀请 3 白板首次邀请  4 不依赖群组视频频首次邀请 5 音频视频会议二次邀请
    // 6 白板群二次邀请演示 7 分享桌面 8 视频依赖群组首次邀请 9 不依赖群组音频 10 不依赖群组白板
    private String                          mConferenceId;
    private String                          mWhiteBoardId;
    private String                          mShareDeskId;
    private String                          mGroupId;
    private Conference                      mConference;
    private ProgressDialog                  mProgressDialog;

    String BUNDLE = "bundle";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_select_member;
    }

    @Override
    protected SelectMemberPresenter createPresenter() {
        return new SelectMemberPresenter(this, this);
    }

    @Override
    protected void initData() {
        //视频会议数据源，没有群概念的数据
        if (selectType == 1 || selectType == 9 || selectType == 10) {
            mPresenter.getMemberList();
        }
        mProgressDialog.show();
        //数据源,来自群
        if (selectType == 2 || selectType == 3 || selectType == 4 || selectType == 5 || selectType == 6 || selectType == 7 || selectType == 8) {
            if (TextUtils.isEmpty(mGroupId)) {
                mPresenter.getMemberList();
            }
            else {
                mPresenter.getMemberFromGroup(mGroupId);
            }
        }
    }

    @Override
    protected void initView() {
        getArgment();
        mBack = (TextView) findViewById(R.id.title_back);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mComplete = (TextView) findViewById(R.id.title_complete);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setColorSchemeResources(R.color.primary);
        mFriendRv = (RecyclerView) findViewById(R.id.contacts_rv);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mFriendRv.setLayoutManager(mLinearLayoutManager);
        mRvSelectAdapter = new SelectMemberAdapter(this, mSelectedCubeMap, mCubeUsers, -1);
        mFriendRv.setAdapter(mRvSelectAdapter);
        if (!mNotChecked.contains(SpUtil.getCubeId())) {
            mNotChecked.add(SpUtil.getCubeId());
        }
        mRvSelectAdapter.setNotChecked(mNotChecked);
        mRvSelectAdapter.setOnItemSelectedListener(this);
        mRvSelectAdapter.setOnItemSelectedShowToast(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("加载中。。。");
    }

    @Override
    public void getArgment() {
        Bundle bundleExtra = getIntent().getBundleExtra(BUNDLE);
        selectType = bundleExtra.getInt("select_type");
        ArrayList<String> not_check_list = bundleExtra.getStringArrayList("not_check_list");
        if (not_check_list != null) {
            mNotChecked = not_check_list;
        }
        mGroupId = bundleExtra.getString("group_id");
        mConferenceId = bundleExtra.getString("conference_id");
        mWhiteBoardId = bundleExtra.getString("white_board_id");
        mShareDeskId = bundleExtra.getString("share_desk_id");
    }

    @Override
    public void getCubeIdListSuccess(LinkedHashMap<String, CubeUser> mSelectedCubeMap, List<CubeUser> mSelectedCubeList) {
        //获取到值
        this.mCubeUsers.addAll(mSelectedCubeList);
        mRvSelectAdapter.notifyDataSetChanged();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setMessage("创建中。。。");
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void initListener() {
        mBack.setOnClickListener(this);
        mComplete.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onNormalClick(View view) {

        switch (view.getId()) {
            case R.id.title_back:
                if (mConference != null) {
                    CubeEngine.getInstance().getConferenceService().closeConference(mConference.getConferenceId());
                }
                finish();
                break;
            case R.id.title_complete:
                if (mSelectedCubeMap.size() < 1) {
                    showMessage(getString(R.string.please_choose_member));
                }
                else {
                    List<CubeUser> cubeUsers = new ArrayList<>();
                    List<String> iniviteList = new ArrayList<>();
                    for (Map.Entry<String, CubeUser> entry : mSelectedCubeMap.entrySet()) {
                        cubeUsers.add(entry.getValue());
                        iniviteList.add(entry.getKey());
                    }
                    if (selectType == 1) { //会议视频首次邀请人员
                        //传值到上一个页面
                        EventBusUtil.post(CubeConstants.Event.SelectMemberEvent, cubeUsers);
                    }
                    if (selectType == 2) { //音频首次邀请人员（没有群组）
                        //mCreateListener = new ConferenceCreateListener(this, mGroupId, iniviteList);
                        //mCreateListener.setCreateCallback(this);
                        //ConferenceHandle.getInstance().addConferenceStateListener(mCreateListener);
                        ////音频会议
                        //ConferenceConfig audioConfig = mPresenter.initConferenceConfig(GroupType.VOICE_CALL, mGroupId);
                        //CubeEngine.getInstance().getConferenceService().create(audioConfig);
                        //mProgressDialog.show();
                        return;
                    }
                    if (selectType == 8) { //视频绑定群组首次邀请人员
                        //mCreateListener = new ConferenceCreateListener(this, mGroupId, iniviteList);
                        //mCreateListener.setCreateCallback(this);
                        //ConferenceHandle.getInstance().addConferenceStateListener(mCreateListener);
                        //ConferenceConfig videoConfig = mPresenter.initConferenceConfig(GroupType.VIDEO_CALL, mGroupId);
                        ////                        videoConfig.invites=iniviteList;
                        //CubeEngine.getInstance().getConferenceService().create(videoConfig);
                        //mProgressDialog.show();
                        return;
                    }
                    if (selectType == 3) { //白板首次邀请人员
                        //mWBListener = new WBListener(this, CubeSessionType.Group, iniviteList, mGroupId);
                        //mWBListener.setCreateCallback(this);
                        //WhiteBoardHandle.getInstance().addWhiteBoardStateListeners(mWBListener);
                        //WhiteboardConfig whiteboardConfig = mPresenter.initWhiteboardConfig(GroupType.SHARE_WB, mGroupId);
                        //CubeEngine.getInstance().getWhiteboardService().create(whiteboardConfig);
                        //mProgressDialog.show();
                        return;
                    }
                    if (selectType == 9) {
                        //创建不依赖群的音频
                        //mCreateListener = new ConferenceCreateListener(this, mGroupId, iniviteList);
                        //mCreateListener.setCreateCallback(this);
                        //ConferenceHandle.getInstance().addConferenceStateListener(mCreateListener);
                        //ConferenceConfig audioConfig = mPresenter.initConferenceConfig(GroupType.VOICE_CALL, mGroupId);
                        //CubeEngine.getInstance().getConferenceService().create(audioConfig);
                        //mProgressDialog.show();
                        return;
                    }
                    if (selectType == 10) {
                        //创建不依赖群的白板
                        //mWBListener = new WBListener(this, CubeSessionType.Group, iniviteList, "");
                        //mWBListener.setCreateCallback(this);
                        //WhiteBoardHandle.getInstance().addWhiteBoardStateListeners(mWBListener);
                        //WhiteboardConfig whiteboardConfig = mPresenter.initWhiteboardConfig(GroupType.SHARE_WB, "");
                        //CubeEngine.getInstance().getWhiteboardService().create(whiteboardConfig);
                        //mProgressDialog.show();
                        return;
                    }
                    /*if (selectType == 4 || selectType == 5) { //音频视频二次邀请人员,方法是一样的
                        //会议邀请
                        CubeEngine.getInstance().getConferenceService().inviteMembers(mConferenceId, iniviteList);
                    }
                    if (selectType == 6) { //白板邀请人员
                        //白板邀请人员
                        CubeEngine.getInstance().getWhiteboardService().inviteMembers(mWhiteBoardId, iniviteList);
                    }
                    if (selectType == 7) {
                        //分享邀请
                        CubeEngine.getInstance().getConferenceService().inviteMembers(mConferenceId, iniviteList);
                    }*/
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String selectedCube) {

    }

    @Override
    public void onItemUnselected(String selectedCube) {

    }

    @Override
    public void onSelectedList(LinkedHashMap<String, CubeUser> list) {
        mSelectedCubeMap = list;
        if (list.size() == 0) {
            mComplete.setTextColor(getResources().getColor(R.color.assist_text));
            mComplete.setText("确定");
        }
        else {
            mComplete.setText("确定(" + list.size() + ")");
            mComplete.setTextColor(getResources().getColor(R.color.C8));
        }
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String message) {
        ToastUtil.showToast( message);
    }

    /*//会议的创建回调
    @Override
    public void onFinish(Conference conference) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        finish();
    }

    @Override
    public void onJoined(Conference conference) {
        mConference = conference;
        mProgressDialog.setMessage("加入成功，进入会议中。。。");
    }

    @Override
    public void onCreate(Conference conference) {
        mConference = conference;
        mProgressDialog.setMessage("创建成功，加入中。。。");
    }

    //错误处理
    @Override
    public void onError(Conference conference, CubeError error) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        //会议创建成功，但是sip失败，销毁会议
        if (mConference != null) {
            CubeEngine.getInstance().getConferenceService().destroy(mConference.conferenceId);
        }
        ConferenceHandle.getInstance().removeConferenceStateListener(mCreateListener);
        //        showMessage(error.desc);
    }*/

    @Override
    public void onItemSelectedToast() {
        Toast.makeText(mContext, "超过最大人数限制了", Toast.LENGTH_SHORT).show();
    }
}
