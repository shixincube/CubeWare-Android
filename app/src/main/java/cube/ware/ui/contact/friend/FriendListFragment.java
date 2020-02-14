package cube.ware.ui.contact.friend;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.base.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.Iterator;
import java.util.List;

import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.data.room.model.CubeUser;
import cube.ware.ui.contact.adapter.FriendListAdapter;
import cube.ware.utils.SpUtil;

/**
 * Created by dth
 * Des: 联系人列表
 * Date: 2018/8/27.
 */

public class FriendListFragment extends BaseFragment<FriendListContract.Presenter> implements FriendListContract.View {
    private RecyclerView mFriendListView;
    private FriendListAdapter mFriendListAdapter;
    private SmartRefreshLayout mRefreshLayout;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_friend_list;
    }

    @Override
    protected FriendListContract.Presenter createPresenter() {
        return new FriendListPresenter(getActivity(), this);
    }

    @Override
    protected void initView() {

        mRefreshLayout = (SmartRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        mFriendListView = (RecyclerView) mRootView.findViewById(R.id.friend_list_view);
        mFriendListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mFriendListView.setItemAnimator(new DefaultItemAnimator());
        mFriendListAdapter = new FriendListAdapter(R.layout.item_friend_list);
        mFriendListView.setAdapter(mFriendListAdapter);
    }

    @Override
    protected void initData() {

        mPresenter.getCubeList();
    }

    @Override
    protected void initListener() {
        mFriendListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CubeUser user = mFriendListAdapter.getData().get(position);
                ARouter.getInstance().build(AppConstants.Router.FriendDetailsActivity)
                        .withObject("user",user)
                        .navigation();
            }
        });

        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mFriendListAdapter.getData().clear();
                initData();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });
    }

    @Override
    public void onResponseUserList(List<CubeUser> list) {
        mRefreshLayout.finishRefresh();
        Iterator<CubeUser> iterator = list.iterator();
        while (iterator.hasNext()) {
            CubeUser cubeUser = iterator.next();
            if (TextUtils.equals(cubeUser.getCubeId(), SpUtil.getCubeId())) {
                list.remove(cubeUser);
                break;
            }

        }
        mFriendListAdapter.setNewData(list);
    }
}
