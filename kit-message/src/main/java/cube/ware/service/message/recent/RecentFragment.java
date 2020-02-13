package cube.ware.service.message.recent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.mvp.base.BaseFragment;
import com.common.mvp.eventbus.Event;
import com.common.mvp.eventbus.EventBusUtil;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.NetworkUtil;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.CubeEngineListener;
import cube.service.CubeError;
import cube.service.CubeState;
import cube.ware.api.CubeUI;
import cube.ware.common.MessageConstants;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.model.CubeRecentViewModel;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.room.model.CubeRecentSession;
import cube.ware.service.message.R;
import cube.ware.widget.emptyview.EmptyView;
import cube.ware.widget.emptyview.EmptyViewUtil;
import java.util.List;

/**
 * 最近会话消息
 *
 * @author LiuFeng
 * @data 2020/2/12 15:20
 */
public class RecentFragment extends BaseFragment<RecentPresenter> implements RecentContract.View, CubeEngineListener {

    private RecyclerView   mMessageRecyclerView;
    private ImageView      mToolbarSearch;
    private ImageView      mToolbarAdd;
    private TextView       mTitleTv;
    private View           mHeaderView;
    private TextView       mRecentMessageTv;        // 消息大文字标题
    private TextView       mRecentMessageErrorTv;   // 引擎连接失败标题
    private ProgressBar    mRecentMessagePb;
    private TextView       mRecentStatusTv;         // 引擎连接状态标题
    private RelativeLayout mRecentMessageRl;
    private LinearLayout   mNoNetworkTipLl;         //网络未连接tips
    private ImageView      mOtherPlatLoginTipIv;    //其他端登录文字
    private TextView       mOtherPlatLoginTipTv;    //网络未连接文字
    private LinearLayout   mOtherPlatLoginTipLl;    //其他端登录tips
    private RecentAdapter  mRecentAdapter;
    private EmptyView      mEmptyView;
    private PopupWindow    popupWindow;
    private RelativeLayout mToolBarLayout;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_recent;
    }

    @Override
    protected RecentPresenter createPresenter() {
        return new RecentPresenter(getActivity(), this);
    }

    @Override
    protected void initView() {
        mMessageRecyclerView = (RecyclerView) mRootView.findViewById(R.id.message_rv);
        mToolbarSearch = (ImageView) mRootView.findViewById(R.id.toolbar_search);
        mToolbarAdd = (ImageView) mRootView.findViewById(R.id.toolbar_add);
        mTitleTv = (TextView) mRootView.findViewById(R.id.title_tv);
        mRecentAdapter = new RecentAdapter(R.layout.item_recent);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        mMessageRecyclerView.addItemDecoration(itemDecoration);
        mToolBarLayout = ((RelativeLayout) mRootView.findViewById(R.id.tool_bar_layout));

        //        mHeaderView = View.inflate(getContext(),R.layout.header_recent_session_recyclerview, null);
        mHeaderView = getLayoutInflater().inflate(R.layout.header_recent_session_recyclerview, (ViewGroup) mMessageRecyclerView.getParent(), false);
        mRecentMessageTv = (TextView) mHeaderView.findViewById(R.id.recent_message_tv);
        mRecentMessageErrorTv = (TextView) mHeaderView.findViewById(R.id.recent_message_error_tv);
        mRecentMessagePb = (ProgressBar) mHeaderView.findViewById(R.id.recent_message_pb);
        mRecentStatusTv = (TextView) mHeaderView.findViewById(R.id.recent_status_tv);
        mRecentMessageRl = (RelativeLayout) mHeaderView.findViewById(R.id.recent_message_rl);
        mNoNetworkTipLl = (LinearLayout) mHeaderView.findViewById(R.id.no_network_tip_ll);
        mOtherPlatLoginTipIv = (ImageView) mHeaderView.findViewById(R.id.other_plat_login_tip_iv);
        mOtherPlatLoginTipTv = (TextView) mHeaderView.findViewById(R.id.other_plat_login_tip_tv);
        mOtherPlatLoginTipLl = (LinearLayout) mHeaderView.findViewById(R.id.other_plat_login_tip_ll);

        mRecentMessageTv.setVisibility(View.GONE);
        mRecentMessageErrorTv.setVisibility(View.GONE);
        mRecentMessagePb.setVisibility(View.GONE);
        mRecentStatusTv.setVisibility(View.VISIBLE);

        mRecentAdapter.addHeaderView(mHeaderView);//添加头视图
        mMessageRecyclerView.setAdapter(mRecentAdapter);
        closeRecyclerViewAnimator(mMessageRecyclerView);

        if (NetworkUtil.isNetworkConnected(getContext()) && NetworkUtil.isNetAvailable(getContext())) {
            // 设置无数据时显示内容
            mEmptyView = EmptyViewUtil.EmptyViewBuilder.getInstance(getActivity()).setItemCountToShowEmptyView(1).setEmptyText(R.string.no_data_message).setShowText(true).setIconSrc(R.drawable.ic_nodata_message).setShowIcon(true).bindView(this.mMessageRecyclerView);
            mNoNetworkTipLl.setVisibility(View.GONE);
        }
        else {
            //设置无网络无数据时显示内容
            mEmptyView = EmptyViewUtil.EmptyViewBuilder.getInstance(getActivity()).setItemCountToShowEmptyView(1).setEmptyText(R.string.no_data_no_net_tip).setShowText(true).setIconSrc(R.drawable.ic_nodata_no_net).setShowIcon(true).bindView(this.mMessageRecyclerView);
            mNoNetworkTipLl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initListener() {
        this.mNoNetworkTipLl.setOnClickListener(this);
        this.mOtherPlatLoginTipLl.setOnClickListener(this);
        this.mToolbarAdd.setOnClickListener(this);
        // 引擎状态监听
        CubeEngine.getInstance().addCubeEngineListener(this);

        // 滚动距离
        this.mMessageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int result = getScrollYDistance(recyclerView);
                if (result < 100) {
                    mTitleTv.setVisibility(View.GONE);
                }
                else {
                    mTitleTv.setVisibility(View.VISIBLE);
                }
            }
        });

        // 会话点击
        mRecentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CubeRecentViewModel cubeRecentViewModel = mRecentAdapter.getData().get(position);
                CubeRecentSession cubeRecentSession = cubeRecentViewModel.cubeRecentSession;
                String chatName = TextUtils.isEmpty(cubeRecentSession.getSessionName()) ? cubeRecentSession.getSessionId() : cubeRecentSession.getSessionName();
                if (cubeRecentSession.getSessionType() == CubeSessionType.Group.getType()) {
                    CubeUI.getInstance().startGroupChat(getContext(), cubeRecentSession.getSessionId(), chatName);
                }
                else {
                    CubeUI.getInstance().startP2PChat(getContext(), cubeRecentSession.getSessionId(), chatName);
                }
            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.refreshRecentSessions();
        onStateChange(CubeEngine.getInstance().getCubeEngineState());
    }

    @Override
    protected boolean openNetworkListener() {
        return true;
    }

    @Override
    protected boolean openEventBus() {
        return true;
    }

    @Override
    public <T> void onReceiveEvent(Event<T> event) {
        mPresenter.handleReceiveEvent(event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.toolbar_add) {
            showMorePopWindow();
        }
    }

    /**
     * 弹出popWindow
     */
    private void showMorePopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        View popView = LayoutInflater.from(getActivity()).inflate(R.layout.main_plus_popupwindow, null);
        TextView createGroupTv = (TextView) popView.findViewById(R.id.create_group_tv);
        TextView addFriendTv = (TextView) popView.findViewById(R.id.add_friend_tv);
        TextView scanTv = (TextView) popView.findViewById(R.id.scan_tv);

        popupWindow = new PopupWindow(popView, ScreenUtil.dip2px(134), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);// 设置弹出窗体可触摸
        popupWindow.setOutsideTouchable(true); // 设置点击弹出框之外的区域后，弹出框消失
        popupWindow.setAnimationStyle(R.style.TitleMorePopAnimationStyle); // 设置动画
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景透明
        ScreenUtil.setBackgroundAlpha(getActivity(), 0.9f);
        popupWindow.getContentView().measure(0, 0);
        int popWidth = popupWindow.getContentView().getMeasuredWidth();
        int windowWidth = ScreenUtil.getDisplayWidth();
        int xOff = windowWidth - popWidth - ScreenUtil.dip2px(12);    // x轴的偏移量
        popupWindow.showAsDropDown(mToolBarLayout, xOff, -ScreenUtil.dip2px(4));  // 设置弹出框显示的位置
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ScreenUtil.setBackgroundAlpha(RecentFragment.this.getActivity(), 1.0f);
            }
        });

        // 添加好友
        addFriendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                RouterUtil.navigation(RecentFragment.this.getContext(), CubeConstants.Router.AddFriendActivity);
                RecentFragment.this.getActivity().overridePendingTransition(R.anim.activity_open, 0);
            }
        });
        // 创建群组
        createGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                RouterUtil.navigation(CubeConstants.Router.SelectContactActivity);
                RecentFragment.this.getActivity().overridePendingTransition(R.anim.activity_open, 0);
            }
        });
    }

    public void closeRecyclerViewAnimator(RecyclerView recyclerView) {
        recyclerView.getItemAnimator().setAddDuration(0);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.getItemAnimator().setMoveDuration(0);
        recyclerView.getItemAnimator().setRemoveDuration(0);
    }

    private int getScrollYDistance(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisibleChildView.getHeight();
        return (position) * itemHeight - firstVisibleChildView.getTop();
    }

    /**
     * 设置其他平台登录提示显示或隐藏
     *
     * @param visible
     */
    public void setOtherPlatVisibility(boolean visible) {
        if (visible) {
            mOtherPlatLoginTipLl.setVisibility(View.VISIBLE);
        }
        else {
            mOtherPlatLoginTipLl.setVisibility(View.GONE);
        }
    }

    /**
     * 设置其他平台登录提示字、提示图标
     *
     * @param tip
     * @param iconSrc
     */
    public void setOtherPlatTipAndIcon(String tip, int iconSrc) {
        mOtherPlatLoginTipTv.setText(tip);
        mOtherPlatLoginTipIv.setImageResource(iconSrc);
    }

    @Override
    public void onNetworkStateChanged(boolean isNetAvailable) {
        if (isNetAvailable) {
            if (mEmptyView != null) {
                mEmptyView.setIcon(R.drawable.ic_nodata_message);
                mEmptyView.setEmptyText(CubeCore.getContext().getString(R.string.no_data_message));
            }
            mNoNetworkTipLl.setVisibility(View.GONE);
            EventBusUtil.post(MessageConstants.Event.EVENT_REFRESH_SYSTEM_MESSAGE, true);
        }
        else {
            if (mEmptyView != null) {
                mEmptyView.setIcon(R.drawable.ic_nodata_no_net);
                mEmptyView.setEmptyText(CubeCore.getContext().getString(R.string.no_data_no_net_tip));
            }
            mNoNetworkTipLl.setVisibility(View.VISIBLE);
            mOtherPlatLoginTipLl.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStarted() {
        mRecentMessageTv.setVisibility(View.GONE);
        if (mRecentMessagePb != null) {
            mRecentMessagePb.setVisibility(View.VISIBLE);
        }
        mRecentStatusTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStateChange(CubeState cubeState) {
        LogUtil.i("onStateChange -> cubeState: " + cubeState);
        mRecentMessageTv.setVisibility(View.GONE);
        mRecentMessageErrorTv.setVisibility(View.GONE);
        mRecentStatusTv.setVisibility(View.VISIBLE);

        // 引擎正在连接
        if (cubeState == CubeState.START) {
            if (mRecentMessagePb != null) {
                mRecentMessagePb.setVisibility(View.VISIBLE);
            }
            mRecentStatusTv.setText(CubeCore.getContext().getString(R.string.connecting));
        }
        // 未连接
        else if (cubeState == CubeState.PAUSE) {
            if (mRecentMessagePb != null) {
                mRecentMessagePb.setVisibility(View.GONE);
            }
            mRecentStatusTv.setVisibility(View.GONE);
            mRecentMessageTv.setVisibility(View.VISIBLE);
            mRecentMessageErrorTv.setVisibility(View.VISIBLE);
        }
        // 正在收消息
        else if (cubeState == CubeState.BUSY) {
            if (mRecentMessagePb != null) {
                mRecentMessagePb.setVisibility(View.VISIBLE);
            }
            mRecentStatusTv.setText(CubeCore.getContext().getString(R.string.receiving));
        }
        else {
            mRecentMessageTv.setVisibility(View.VISIBLE);
            if (mRecentMessagePb != null) {
                mRecentMessagePb.setVisibility(View.GONE);
            }
            mRecentStatusTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStopped() {

    }

    @Override
    public void onFailed(CubeError cubeError) {
        LogUtil.i("CubeEngine_onFailed ->  " + cubeError.toString());
        if (mRecentMessagePb != null) {
            mRecentMessagePb.setVisibility(View.GONE);
            if (null != mRecentStatusTv) {
                mRecentStatusTv.setText(CubeCore.getContext().getString(R.string.no_connection));
                mRecentStatusTv.setVisibility(View.VISIBLE);
                mRecentMessageTv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRefreshList(List<CubeRecentViewModel> cubeRecentViewModels) {
        if (cubeRecentViewModels.size() == 1) {
            onRefresh(cubeRecentViewModels.get(0));
        }
        else {
            mRecentAdapter.replaceData(cubeRecentViewModels);
        }
    }

    @Override
    public void onRefresh(CubeRecentViewModel cubeRecentViewModel) {
        List<CubeRecentViewModel> data = mRecentAdapter.getData();
        int position = data.indexOf(cubeRecentViewModel);
        if (position != -1) {
            //如果是回执消息引起的刷新
            if (cubeRecentViewModel.cubeRecentSession.getTimestamp() == data.get(position).cubeRecentSession.getTimestamp()) {
                mRecentAdapter.setData(position, cubeRecentViewModel);
            }
            else {
                mRecentAdapter.setData(0, cubeRecentViewModel);
            }
        }
        else {
            mRecentAdapter.addData(0, cubeRecentViewModel);
        }
    }

    @Override
    public void onRemoveSession(String sessionId) {
        int position = mRecentAdapter.findPosition(sessionId);
        mRecentAdapter.remove(position);
    }

    @Override
    public void onRefreshListAvatar() {
        mRecentAdapter.notifyDataSetChanged();
    }
}
