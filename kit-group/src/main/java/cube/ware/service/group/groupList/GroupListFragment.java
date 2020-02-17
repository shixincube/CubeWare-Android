package cube.ware.service.group.groupList;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.base.BaseFragment;
import com.common.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.group.Group;
import cube.service.group.GroupListener;
import cube.service.group.GroupQueryListener;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.service.group.R;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by dth
 * Des: 群组列表
 * Date: 2018/8/27.
 */

public class GroupListFragment extends BaseFragment<GroupListContract.Presenter> implements GroupListContract.View, GroupListener {

    private GroupListAdapter   mGroupListAdapter;
    private SmartRefreshLayout mRefreshLayout;
    private int                mOffset = 0;
    private int                mLimit  = 10;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_group_list;
    }

    @Override
    protected GroupListContract.Presenter createPresenter() {
        return new GroupListPresenter(getActivity(), this);
    }

    @Override
    protected void initView() {
        RecyclerView mGroupListView = (RecyclerView) mRootView.findViewById(R.id.group_list_view);
        mRefreshLayout = (SmartRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        mGroupListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mGroupListView.setItemAnimator(new DefaultItemAnimator());
        mGroupListAdapter = new GroupListAdapter(R.layout.item_friend_list);
        mGroupListView.setAdapter(mGroupListAdapter);
    }

    @Override
    protected void initData() {
        CubeEngine.getInstance().getGroupService().queryGroups(new GroupQueryListener() {
            @Override
            public void onQueryGroups(Map<Group, List<String>> groupMap) {
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
                if (groupMap != null && !groupMap.isEmpty()) {
                    mGroupListAdapter.addData(groupMap.keySet());
                }
            }
        });
    }

    @Override
    protected void initListener() {
        CubeEngine.getInstance().getGroupService().addGroupListener(this);
        mGroupListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Group group = mGroupListAdapter.getData().get(position);
                ARouter.getInstance().build(CubeConstants.Router.GroupDetailsActivity).withString("groupId", group.getGroupId()).withObject("group", group).navigation();
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
                //if (mOffset >= mTotal) {
                //    refreshLayout.finishLoadMoreWithNoMoreData();
                //    return;
                //}
                initData();
            }
        });
    }

    @Override
    public void onDestroy() {
        CubeEngine.getInstance().getGroupService().removeGroupListener(this);
        super.onDestroy();
    }

    private boolean isMaster(Group group) {
        String cubeId = CubeCore.getInstance().getCubeId();
        return TextUtils.equals(group.getFounder(), cubeId) || group.getMasters().contains(cubeId);
    }

    @Override
    public void onGroupFailed(CubeError error) {

    }

    @Override
    public void onGroupCreated(Group group) {
        mGroupListAdapter.addData(group);
    }

    @Override
    public void onGroupDeleted(Group group) {
        ToastUtil.showToast( "删除群组成功");
        int position = mGroupListAdapter.findPosition(group.getGroupId());
        if (position != -1) {
            mGroupListAdapter.getData().remove(position);
            mGroupListAdapter.notifyDataSetChanged();
        }
        else {
            mOffset = 0;
            mGroupListAdapter.getData().clear();
            initData();
        }
    }

    @Override
    public void onMemberAdded(Group group, List<String> addedMembers) {

    }

    @Override
    public void onMemberRemoved(Group group, List<String> removedMembers) {
        if (removedMembers.contains(CubeCore.getInstance().getCubeId())) {
            ToastUtil.showToast( "退出群组成功");
            List<Group> data = mGroupListAdapter.getData();
            Iterator<Group> iterator = data.iterator();
            while (iterator.hasNext()) {
                Group next = iterator.next();
                if (TextUtils.equals(next.getGroupId(), group.getGroupId())) {
                    iterator.remove();
                    break;
                }
            }
            mGroupListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMasterAdded(Group group, String addedMaster) {
        ToastUtil.showToast( "添加管理员成功");
    }

    @Override
    public void onMasterRemoved(Group group, String removedMaster) {
        ToastUtil.showToast( "移除管理员成功");
    }

    @Override
    public void onGroupNameChanged(Group group) {
        int position = mGroupListAdapter.findPosition(group.getGroupId());
        if (position != -1) {
            mGroupListAdapter.setData(position, group);
        }
        else {
            mOffset = 0;
            mGroupListAdapter.getData().clear();
            initData();
        }
    }
}
