package cube.ware.service.conference.conferenceList;

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
import com.common.base.BaseFragment;
import com.common.eventbus.Event;
import com.common.router.RouterUtil;
import com.common.utils.ToastUtil;
import com.common.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.conference.model.Conference;
import cube.service.group.GroupType;
import cube.utils.SpUtil;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.service.conference.R;
import cube.ware.service.conference.create.CreateData;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public class ConferenceListFragment extends BaseFragment<ConferenceListContract.Presenter> implements ConferenceListContract.View, AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener, ConferenceListAdapter.OnItemClickListener {

    private TextView              mTvTitle;
    private RecyclerView          mRvConference;
    private FloatingActionButton  mFbtAdd;
    private AppBarLayout          mAppBarLayout;
    private SwipeRefreshLayout    mSrlRefresh;
    private ConferenceListAdapter mRVConferenceListAdapter;
    private LinearLayoutManager   mLinearLayoutManager;
    private List<CreateData>      mCreateData = new ArrayList<>();
    private List<GroupType>       mGroupTypes = new ArrayList<>();
    private TextView              mTvCreateConference;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_conference;
    }

    @Override
    protected boolean openEventBus() {
        return true;
    }

    @Override
    protected ConferenceListContract.Presenter createPresenter() {
        return new ConferenceListPresenter(getActivity(), this);
    }

    @Override
    protected void initView() {
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
        mRVConferenceListAdapter = new ConferenceListAdapter(getContext(), mCreateData);
        mRvConference.setAdapter(mRVConferenceListAdapter);
        mRVConferenceListAdapter.setOnItemClickListener(this);
        mGroupTypes.add(GroupType.VIDEO_CONFERENCE);
        //查询会议
        mPresenter.getConferenceList(CubeCore.getInstance().getCubeId(), mGroupTypes);
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
        if (v.getId() == R.id.tv_create_conference) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", 1);// 1 邀请，其他为创建
            RouterUtil.navigation(getContext(), bundle, CubeConstants.Router.CreateConferenceActivity);
        }
    }

    @Override
    public void onRefresh() {
        mCreateData.clear();
        mRVConferenceListAdapter.setData(mCreateData);
        //刷新 查询会议
        mPresenter.getConferenceList(CubeEngine.getInstance().getSession().user.cubeId, mGroupTypes);
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

    @Override
    public void onReceiveEvent(Event event) {
        switch (event.eventName) {
            case CubeConstants.Event.InviteConferenceEvent:
                //再查一次就可以了
                mCreateData.clear();
                mRVConferenceListAdapter.setData(mCreateData);
                mPresenter.getConferenceList(CubeEngine.getInstance().getSession().user.cubeId, mGroupTypes);
                break;

            case CubeConstants.Event.CreateConferenceEvent:
                mPresenter.getConferenceList(CubeEngine.getInstance().getSession().user.cubeId, mGroupTypes);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClickListener(CreateData createData) {
        //查询出来的
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);// 1 邀请，其他为创建
        bundle.putSerializable("conference", createData.getConference());
        RouterUtil.navigation(getContext(), bundle, CubeConstants.Router.CreateConferenceActivity);
    }

    @Override
    public void onLongClickListener(CreateData createData, int position) {
        showDialog(createData.getConference(), position);
    }

    private void showDialog(final Conference conference, final int position) {
        // todo 会议创建者为后台 10000 ，调用destory会出现 {"code":1511,"desc":"CloseConference not the conference master"}
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("删除会议").setMessage("是否删除会议").setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (conference.founder.equals(SpUtil.getCubeId())) {
                    CubeEngine.getInstance().getConferenceService().destroy(conference.conferenceId);
                    mCreateData.remove(position);
                    mRVConferenceListAdapter.setData(mCreateData);
                }
                else {
                    showMessage("对不起，您没有删除权限");
                }
            }
        }).setNegativeButton("取消", null).show();
    }

    @Override
    public void showMessage(String message) {
        ToastUtil.showToast( message);
    }

    @Override
    public void getConference(List<Conference> conferenceList) {
        if (conferenceList != null && conferenceList.size() > 0) {
            for (int i = 0; i < conferenceList.size(); i++) {
                CreateData createData = new CreateData();
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
}
