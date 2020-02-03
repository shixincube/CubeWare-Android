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
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.mvp.base.BaseFragment;
import com.common.mvp.rx.RxBus;
import com.common.sdk.RouterUtil;
import com.common.utils.receiver.NetworkStateReceiver;
import com.common.utils.utils.DateUtil;
import com.common.utils.utils.NetworkUtil;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.CubeEngineListener;
import cube.service.CubeState;
import cube.service.CubeError;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.CubeRecentViewModel;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.room.model.CubeRecentSession;
import cube.ware.eventbus.CubeEvent;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.activity.group.GroupChatCustomization;
import cube.ware.service.message.chat.activity.p2p.P2PChatCustomization;
import cube.ware.service.message.recent.adapter.RecentAdapter;
import cube.ware.widget.emptyview.EmptyView;
import cube.ware.widget.emptyview.EmptyViewUtil;
import java.util.List;

/**
 * Created by dth
 * Des: 基于本地数据构建的最近会话的fragment
 * Date: 2018/8/27.
 */

public class RecentFragment extends BaseFragment<RecentPresenter> implements RecentContract.View, NetworkStateReceiver.NetworkStateChangedListener, CubeEngineListener {

    private RecyclerView   mMessageRecyclerView;
    private ImageView      mToolbarSearch;
    private ImageView      mToolbarAdd;
    private TextView       mTitleTv;
    private RelativeLayout mToolBarLayout;
    private View           mHeaderView;
    private TextView       mRecentMessageTv;// 消息大文字标题
    private TextView       mRecentMessageErrorTv;// 引擎连接失败标题
    private ProgressBar    mRecentMessagePb;
    private TextView       mRecentStatusTv;// 引擎连接状态标题
    private RelativeLayout mRecentMessageRl;
    private LinearLayout   mNoNetworkTipLl; //网络未连接tips
    private ImageView      mOtherPlatLoginTipIv;//其他端登录文字
    private TextView       mOtherPlatLoginTipTv;//网络未连接文字
    private LinearLayout   mOtherPlatLoginTipLl;//其他端登录tips
    private RecentAdapter  mRecentAdapter;
    private EmptyView      mEmptyView;
    private PopupWindow    popupWindow;

    private RelativeLayout tool_bar_layout;

    String EXTRA_CHAT_ID   = "chat_id";
    String EXTRA_CHAT_NAME = "chat_name";
    String EXTRA_CHAT_CUSTOMIZATION = "chat_customization";

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
        mToolBarLayout = (RelativeLayout) mRootView.findViewById(R.id.tool_bar_layout);
        mRecentAdapter = new RecentAdapter(R.layout.item_recent);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        mMessageRecyclerView.addItemDecoration(itemDecoration);
        tool_bar_layout = ((RelativeLayout) mRootView.findViewById(R.id.tool_bar_layout));

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
        CubeState cubeEngineState = CubeEngine.getInstance().getCubeEngineState();
        onStateChange(cubeEngineState);

        mRecentAdapter.addHeaderView(mHeaderView);//添加头视图
        mMessageRecyclerView.setAdapter(mRecentAdapter);
        closeRecyclerViewAnimator(mMessageRecyclerView);

        if (NetworkUtil.isNetworkConnected(getContext()) && NetworkUtil.isNetAvailable(getContext())) {
            //if (!CubeSpUtil.isFirstSync(CubeSpUtil.getCubeUser().getCubeId())) {
            //    设置无数据时显示内容
            mEmptyView = EmptyViewUtil.EmptyViewBuilder.getInstance(getActivity()).setItemCountToShowEmptyView(1).setEmptyText(R.string.no_data_message).setShowText(true).setIconSrc(R.drawable.ic_nodata_message).setShowIcon(true).bindView(this.mMessageRecyclerView);
            //}
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
        NetworkStateReceiver.getInstance().addNetworkStateChangedListener(this);
        CubeEngine.getInstance().addCubeEngineListener(this);
        this.mNoNetworkTipLl.setOnClickListener(this);
        this.mOtherPlatLoginTipLl.setOnClickListener(this);

        this.mToolbarAdd.setOnClickListener(this);

        this.mMessageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int result = getScollYDistance(recyclerView);
                if (result < 100) {
                    mTitleTv.setVisibility(View.GONE);
                }
                else if (result >= 100) {
                    mTitleTv.setVisibility(View.VISIBLE);
                }
            }
        });

        mRecentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                CubeRecentViewModel cubeRecentViewModel = mRecentAdapter.getData().get(position);
                CubeRecentSession cubeRecentSession = cubeRecentViewModel.cubeRecentSession;

                if (cubeRecentSession.getSessionType() == CubeSessionType.Group.getType()) {
                    ARouter.getInstance().build(CubeConstants.Router.GroupChatActivity).withString(EXTRA_CHAT_ID, cubeRecentSession.getSessionId()).withString(EXTRA_CHAT_NAME, TextUtils.isEmpty(cubeRecentSession.getSessionName()) ? cubeRecentSession.getSessionId() : cubeRecentSession.getSessionName()).withSerializable(EXTRA_CHAT_CUSTOMIZATION, new GroupChatCustomization()).navigation();
                }
                else {
                    ARouter.getInstance().build(CubeConstants.Router.P2PChatActivity).withString(EXTRA_CHAT_ID, cubeRecentSession.getSessionId()).withString(EXTRA_CHAT_NAME, TextUtils.isEmpty(cubeRecentSession.getSessionName()) ? cubeRecentSession.getSessionId() : cubeRecentSession.getSessionName()).withSerializable(EXTRA_CHAT_CUSTOMIZATION, new P2PChatCustomization()).navigation();
                }
            }
        });
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
        popupWindow.showAsDropDown(tool_bar_layout, xOff, -ScreenUtil.dip2px(4));  // 设置弹出框显示的位置
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

    @Override
    protected void initData() {

        mPresenter.subscribeChange();
        mPresenter.getRecentSessionList();

        //        CubeRecentSessionRepository.getInstance().queryAllUnReadCubeRecentSession()
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .subscribe(new Action1<List<CubeRecentViewModel>>() {
        //                    @Override
        //                    public void call(List<CubeRecentViewModel> cubeRecentViewModels) {
        //
        //                        mRecentAdapter.setNewData(cubeRecentViewModels);
        //                    }
        //                });

    }

    public void closeRecyclerViewAnimator(RecyclerView recyclerView) {
        recyclerView.getItemAnimator().setAddDuration(0);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.getItemAnimator().setMoveDuration(0);
        recyclerView.getItemAnimator().setRemoveDuration(0);
    }

    private int getScollYDistance(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
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
            RxBus.getInstance().post(CubeEvent.EVENT_REFRESH_SYSTEM_MESSAGE, true);
            //            queryOtherPlayLoginTip();
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
        LogUtil.i("CubeEngine_state -> time: " + DateUtil.formatTimestamp(System.currentTimeMillis()) + " , state: " + cubeState);
        mRecentMessageTv.setVisibility(View.GONE);
        mRecentMessageErrorTv.setVisibility(View.GONE);
        mRecentStatusTv.setVisibility(View.VISIBLE);
        if (cubeState == CubeState.START) {  // 引擎正在连接
            if (mRecentMessagePb != null) {
                mRecentMessagePb.setVisibility(View.VISIBLE);
            }
            mRecentStatusTv.setText(CubeCore.getContext().getString(R.string.connecting));
        }
        else if (cubeState == CubeState.PAUSE) { // 未连接
            if (mRecentMessagePb != null) {
                mRecentMessagePb.setVisibility(View.GONE);
            }
            mRecentStatusTv.setVisibility(View.GONE);
            mRecentMessageTv.setVisibility(View.VISIBLE);
            mRecentMessageErrorTv.setVisibility(View.VISIBLE);
        }
        else if (cubeState == CubeState.BUSY) {    // 正在收消息
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
        if (cubeRecentViewModels == null) {
            return;
        }
        mRecentAdapter.replaceData(cubeRecentViewModels);
    }

    @Override
    public void onRefresh(CubeRecentViewModel cubeRecentViewModel) {
        List<CubeRecentViewModel> data = mRecentAdapter.getData();
        int position = mRecentAdapter.findPosition(cubeRecentViewModel.cubeRecentSession.getSessionId());
        LogUtil.i("onRefresh -------------" + position);
        if (position != -1) {
            //如果是回执消息引起的刷新
            if (cubeRecentViewModel.cubeRecentSession.getTimestamp() == data.get(position).cubeRecentSession.getTimestamp()) {
                mRecentAdapter.setData(position, cubeRecentViewModel);
            }
            else {
                //                mRecentAdapter.remove(position);
                //                mRecentAdapter.addData(0,cubeRecentViewModel);
                data.remove(position);
                data.add(0, cubeRecentViewModel);
                mRecentAdapter.notifyDataSetChanged();
            }
        }
        else {
            mRecentAdapter.addData(0, cubeRecentViewModel);
        }
    }

    @Override
    public void onRemoveSession(String sessionId) {
        int position = mRecentAdapter.findPosition(sessionId);
        LogUtil.i("onRemoveSession -------------" + position);
        mRecentAdapter.remove(position);
    }

    @Override
    public void onRefreshListAvatar() {
        mRecentAdapter.notifyDataSetChanged();
    }
}
