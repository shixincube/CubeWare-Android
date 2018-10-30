package cube.ware.ui.conference;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.common.mvp.base.BaseFragment;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.conference.model.Conference;
import cube.service.group.GroupType;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.ui.conference.adapter.RVConferenceListAdapter;
import cube.ware.ui.conference.eventbus.CreateConferenceEvent;
import cube.ware.ui.conference.eventbus.CreateData;
import cube.ware.ui.conference.eventbus.InviteConferenceEvent;
import cube.ware.utils.SpUtil;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public class ConferenceFragment extends BaseFragment<ConferenceContract.Presenter> implements ConferenceContract.View, AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener, RVConferenceListAdapter.OnItemClickListener {

    private TextView mTvTitle;
    private RecyclerView mRvConference;
    private FloatingActionButton mFbtAdd;
    private AppBarLayout mAppBarLayout;
    private SwipeRefreshLayout mSrlRefresh;
    private RVConferenceListAdapter mRVConferenceListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private List<CreateData> mCreateData=new ArrayList<>();
    private List<GroupType> mGroupTypes=new ArrayList<>();
    private TextView mTvCreateConference;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_conference;
    }

    @Override
    protected ConferenceContract.Presenter createPresenter() {
        return new ConferencePresenter(getActivity(),this);
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mTvTitle = mRootView.findViewById(R.id.toolbar_title);
        mRvConference = mRootView.findViewById(R.id.rv_conference);
        mFbtAdd = mRootView.findViewById(R.id.bt_add_conference);
        mAppBarLayout = mRootView.findViewById(R.id.contact_appbar);
        mSrlRefresh = mRootView.findViewById(R.id.srl_refresh);
        mTvCreateConference = mRootView.findViewById(R.id.tv_create_conference);
    }

    @Override
    protected void initData() {
        mTvTitle.setText(getContext().getResources().getString(R.string.conference));
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRvConference.setLayoutManager(mLinearLayoutManager);
        mRVConferenceListAdapter=new RVConferenceListAdapter(getContext(),mCreateData);
        mRvConference.setAdapter(mRVConferenceListAdapter);
        mRVConferenceListAdapter.setOnItemClickListener(this);
        mGroupTypes.add(GroupType.VIDEO_CONFERENCE);
        //查询会议
        mPresenter.getConferenceList(SpUtil.getCubeId(),mGroupTypes);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

    @Override
    protected void initListener() {
        mAppBarLayout.addOnOffsetChangedListener(this);
        mFbtAdd.setOnClickListener(this);
        mSrlRefresh.setOnRefreshListener(this);
        mTvCreateConference.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_create_conference:
                Bundle bundle=new Bundle();
                bundle.putInt("type",1);// 1 邀请，其他为创建
                RouterUtil.navigation(getContext(),bundle, AppConstants.Router.CreateConferenceActivity);
                break;
        }
    }

    @Override
    public void onRefresh() {
        mCreateData.clear();
        mRVConferenceListAdapter.setData(mCreateData);
        //刷新 查询会议
        mPresenter.getConferenceList(CubeEngine.getInstance().getSession().user.cubeId,mGroupTypes);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset > -100) {
            mTvTitle.setVisibility(View.GONE);
        }
        else if (verticalOffset < -100) {
            mTvTitle.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getConferenceData(InviteConferenceEvent inviteConferenceEvent){
        if(inviteConferenceEvent!=null){
            //再查一次就可以了
            mCreateData.clear();
            mRVConferenceListAdapter.setData(mCreateData);
            mPresenter.getConferenceList(CubeEngine.getInstance().getSession().user.cubeId,mGroupTypes);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getConferenceCreateData(CreateConferenceEvent createConferenceEvent){
        if(createConferenceEvent!=null){
            mPresenter.getConferenceList(CubeEngine.getInstance().getSession().user.cubeId,mGroupTypes);
        }
    }

    @Override
    public void onClickListener(CreateData createData) {
        //查询出来的
        Bundle bundle=new Bundle();
        bundle.putInt("type",0);// 1 邀请，其他为创建
        bundle.putSerializable("conference",createData.getConference());
        RouterUtil.navigation(getContext(),bundle, AppConstants.Router.CreateConferenceActivity);
    }

    @Override
    public void onLongClickListener(CreateData createData, int position) {
        showDialog(createData.getConference(),position);
    }

    private void showDialog(Conference conference, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("删除会议").setMessage("是否删除会议").setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(conference.founder.equals(SpUtil.getCubeId())){
                    CubeEngine.getInstance().getConferenceService().destroy(conference.conferenceId);
                    mCreateData.remove(position);
                    mRVConferenceListAdapter.setData(mCreateData);
                }else {
                    showMessage("对不起，您没有删除权限");
                }
            }
        }).setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void showMessage(String message) {
        ToastUtil.showToast(getContext(),message);
    }

    @Override
    public void getConference(List<Conference> conferenceList) {
        if(conferenceList!=null&&conferenceList.size()>0){
            for (int i = 0; i < conferenceList.size(); i++) {
                CreateData createData=new CreateData();
                createData.setConference(conferenceList.get(i));
                mCreateData.add(createData);
            }
            mRVConferenceListAdapter.setData(mCreateData);
        }
        mSrlRefresh.setRefreshing(false);
    }

    @Override
    public void getConferenceFail(CubeError cubeError) {
        LogUtil.i(cubeError.toString());
        mSrlRefresh.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
