package cube.ware.ui.contact.group;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.mvp.base.BaseFragment;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.Iterator;
import java.util.List;

import cube.service.CubeEngine;
import cube.service.common.CubeCallback;
import cube.service.common.model.CubeError;
import cube.service.common.model.PageInfo;
import cube.service.group.model.Group;
import cube.service.group.model.QueryGroup;
import cube.service.user.model.User;
import cube.ware.App;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.service.group.GroupHandle;
import cube.ware.ui.contact.adapter.GroupListAdapter;
import cube.ware.ui.group.adapter.GroupListenerAdapter;
import cube.ware.utils.SpUtil;

/**
 * Created by dth
 * Des: 群组列表
 * Date: 2018/8/27.
 */

public class GroupListFragment extends BaseFragment<GroupListContract.Presenter> implements GroupListContract.View, CubeCallback<QueryGroup>{

    private RecyclerView mGroupListView;
    private GroupListAdapter mGroupListAdapter;
    private SmartRefreshLayout mRefreshLayout;
    private int mOffset = 0;
    private int mLimit  = 10;
    private long mTotal;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_group_list;
    }

    @Override
    protected GroupListContract.Presenter createPresenter() {
        return new GroupListPresenter(getActivity(),this);
    }

    @Override
    protected void initView() {

        mGroupListView = (RecyclerView) mRootView.findViewById(R.id.group_list_view);
        mRefreshLayout = (SmartRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        mGroupListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mGroupListView.setItemAnimator(new DefaultItemAnimator());
        mGroupListAdapter = new GroupListAdapter(R.layout.item_friend_list);
        mGroupListView.setAdapter(mGroupListAdapter);
    }

    @Override
    protected void initData() {

        CubeEngine.getInstance().getGroupService().queryGroups(mOffset, mLimit,this);
    }

    @Override
    protected void initListener() {
        GroupHandle.getInstance().addGroupListener(groupListenerAdapter);
        mGroupListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Group group = mGroupListAdapter.getData().get(position);

                ARouter.getInstance().build(AppConstants.Router.GroupDetailsActivity)
                        .withString("groupId",group.groupId)
                        .withObject("group",group)
                        .navigation();
            }
        });

        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                mOffset = 0;
                mGroupListAdapter.getData().clear();
                initData();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mOffset += mLimit;
                if (mOffset >= mTotal) {
                    refreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                initData();
            }
        });
    }

    @Override
    public void onDestroy() {
        GroupHandle.getInstance().removeGroupListener(groupListenerAdapter);
        super.onDestroy();
    }

    @Override
    public void onSucceed(QueryGroup queryGroup) {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
        QueryGroup.Data data = queryGroup.data;
        PageInfo page = data.page;
        mTotal = page.total;
        LogUtil.d("mTotal: "+mTotal + " ----offset: "+page.offset);
        List<Group> groups = data.groups;
        if (groups != null) {
            mGroupListAdapter.addData(groups);
        }
    }

    @Override
    public void onFailed(CubeError cubeError) {
        LogUtil.e(cubeError.toString());
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

    private boolean isMaster(Group group) {
        String cubeId = SpUtil.getCubeId();
        return TextUtils.equals(group.owner, cubeId) || group.masters.contains(cubeId);
    }

    GroupListenerAdapter groupListenerAdapter = new GroupListenerAdapter() {
        @Override
        public void onGroupCreated(Group group, User user) {
            mGroupListAdapter.addData(group);
        }

        @Override
        public void onGroupDestroyed(Group group, User user) {

            ToastUtil.showToast(App.getContext(),"删除群组成功");
            int position = mGroupListAdapter.findPosition(group.groupId);
            if (position != -1) {
                mGroupListAdapter.getData().remove(position);
                mGroupListAdapter.notifyDataSetChanged();
            } else {
                mOffset = 0;
                mGroupListAdapter.getData().clear();
                initData();
            }
        }

        @Override
        public void onGroupQuited(Group group, User from) {
            if (TextUtils.equals(from.cubeId, SpUtil.getCubeId())) {
                ToastUtil.showToast(App.getContext(),"退出群组成功");
                List<Group> data = mGroupListAdapter.getData();
                Iterator<Group> iterator = data.iterator();
                while (iterator.hasNext()) {
                    Group next = iterator.next();
                    if (TextUtils.equals(next.groupId, group.groupId)) {
                        iterator.remove();
                        break;
                    }
                }
                mGroupListAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onGroupUpdated(Group group, User user) {
            int position = mGroupListAdapter.findPosition(group.groupId);
            if (position != -1) {
                mGroupListAdapter.setData(position,group);
            } else {
                mOffset = 0;
                mGroupListAdapter.getData().clear();
                initData();
            }
        }

        @Override
        public void onMasterAdded(Group group, User user, List<User> list) {
            ToastUtil.showToast(App.getContext(),"添加管理员成功");
        }

        @Override
        public void onMasterRemoved(Group group, User user, List<User> list) {
            ToastUtil.showToast(App.getContext(),"移除管理员成功");
        }


        @Override
        public void onGroupFailed(Group group, CubeError cubeError) {

        }
    };

}
