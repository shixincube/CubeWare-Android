package cube.ware.ui.recent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.common.mvp.base.BasePresenter;
import com.common.mvp.rx.RxBus;
import com.common.sdk.RouterUtil;
import com.common.utils.receiver.NetworkStateReceiver;
import com.common.utils.utils.DateUtil;
import com.common.utils.utils.NetworkUtil;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.CubeCallback;
import cube.service.common.CubeState;
import cube.service.common.model.CubeError;
import cube.service.recent.RecentSession;
import cube.service.recent.RecentSessionListener;
import cube.ware.AppConstants;
import cube.ware.CubeUI;
import cube.ware.R;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.eventbus.CubeEvent;
import cube.ware.eventbus.UpdateRecentListAboutGroup;
import cube.ware.service.engine.CubeEngineWorkerListener;
import cube.ware.ui.chat.activity.group.GroupChatCustomization;
import cube.ware.ui.chat.activity.p2p.P2PChatCustomization;
import cube.ware.ui.recent.adapter.RecentSessionAdapter;
import cube.ware.widget.DividerItemDecoration;
import cube.ware.widget.emptyview.EmptyView;
import cube.ware.widget.emptyview.EmptyViewUtil;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by dth
 * Des: 基于引擎最近会话的fragment,不需要应用层额外添加处理逻辑
 * Date: 2018/9/28.
 */

public class RecentSessionFragment extends BaseFragment implements NetworkStateReceiver.NetworkStateChangedListener, CubeEngineWorkerListener, RecentSessionListener {

    private RecyclerView         mMessageRecyclerView;
    private ImageView            mToolbarSearch;
    private ImageView            mToolbarAdd;
    private TextView             mTitleTv;
    private RelativeLayout       mToolBarLayout;
    private View                 mHeaderView;
    private TextView             mRecentMessageTv;// 消息大文字标题
    private TextView             mRecentMessageErrorTv;// 引擎连接失败标题
    private ProgressBar          mRecentMessagePb;
    private TextView             mRecentStatusTv;// 引擎连接状态标题
    private RelativeLayout       mRecentMessageRl;
    private LinearLayout         mNoNetworkTipLl; //网络未连接tips
    private ImageView            mOtherPlatLoginTipIv;//其他端登录文字
    private TextView             mOtherPlatLoginTipTv;//网络未连接文字
    private LinearLayout         mOtherPlatLoginTipLl;//其他端登录tips
    private RecentSessionAdapter mRecentSessionAdapter;
    private EmptyView            mEmptyView;
    private PopupWindow          popupWindow;

    private RelativeLayout tool_bar_layout;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_recent;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        mMessageRecyclerView = (RecyclerView) mRootView.findViewById(R.id.message_rv);
        mToolbarSearch = (ImageView) mRootView.findViewById(R.id.toolbar_search);
        mToolbarAdd = (ImageView) mRootView.findViewById(R.id.toolbar_add);
        mTitleTv = (TextView) mRootView.findViewById(R.id.title_tv);
        mToolBarLayout = (RelativeLayout) mRootView.findViewById(R.id.tool_bar_layout);
        mRecentSessionAdapter = new RecentSessionAdapter(R.layout.item_recent);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST, 2, getResources().getColor(R.color.primary_divider));
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

        mRecentSessionAdapter.addHeaderView(mHeaderView);//添加头视图
        mMessageRecyclerView.setAdapter(mRecentSessionAdapter);

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
        CubeUI.getInstance().addCubeEngineWorkerListener(this);
        CubeEngine.getInstance().getRecentSessionService().addRecentSessionListener(this);
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

        mRecentSessionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                RecentSession recentSession = mRecentSessionAdapter.getData().get(position);

                if (recentSession.sessionType == CubeSessionType.Group.getType()) {
                    ARouter.getInstance().build(AppConstants.Router.GroupChatActivity).withString(AppConstants.EXTRA_CHAT_ID, recentSession.sessionId).withString(AppConstants.EXTRA_CHAT_NAME, TextUtils.isEmpty(recentSession.displayName) ? recentSession.sessionId : recentSession.displayName).withSerializable(AppConstants.EXTRA_CHAT_CUSTOMIZATION, new GroupChatCustomization()).navigation();
                }
                else {
                    ARouter.getInstance().build(AppConstants.Router.P2PChatActivity).withString(AppConstants.EXTRA_CHAT_ID, recentSession.sessionId).withString(AppConstants.EXTRA_CHAT_NAME, TextUtils.isEmpty(recentSession.displayName) ? recentSession.sessionId : recentSession.displayName).withSerializable(AppConstants.EXTRA_CHAT_CUSTOMIZATION, new P2PChatCustomization()).navigation();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.toolbar_add:
                showMorePopWindow();
                break;
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
        popupWindow.setOnDismissListener(() -> ScreenUtil.setBackgroundAlpha(getActivity(), 1.0f));

        // 添加好友
        addFriendTv.setOnClickListener(view -> {
            popupWindow.dismiss();
            RouterUtil.navigation(getContext(), AppConstants.Router.AddFriendActivity);
            getActivity().overridePendingTransition(R.anim.activity_open, 0);
        });
        // 创建群组
        createGroupTv.setOnClickListener(view -> {
            popupWindow.dismiss();
            RouterUtil.navigation(AppConstants.Router.SelectContactActivity);
            getActivity().overridePendingTransition(R.anim.activity_open, 0);
        });
    }

    @Override
    protected void initData() {

        CubeEngine.getInstance().getRecentSessionService().queryRecentSessions(new CubeCallback<List<RecentSession>>() {
            @Override
            public void onSucceed(List<RecentSession> recentSessions) {
                LogUtil.i("queryRecentSessions: " + recentSessions);
                mRecentSessionAdapter.setNewData(recentSessions);
                RxBus.getInstance().post(CubeEvent.EVENT_UNREAD_MESSAGE_SUM, calcAllUnreadCount(recentSessions));
            }

            @Override
            public void onFailed(CubeError error) {

                LogUtil.e("queryRecentSessions failed: " + error.toString());
            }
        });
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

    /**
     * 当列表中的群组有会议产生时，更新群组头，作为提示
     *
     * @param updateRecentListAboutGroup
     */
    @Subscribe
    private void updateGroupIcon(UpdateRecentListAboutGroup updateRecentListAboutGroup) {
        if (updateRecentListAboutGroup != null) {

        }
    }

    @Override
    public void onNetworkStateChanged(boolean isNetAvailable) {
        if (isNetAvailable) {
            if (mEmptyView != null) {
                mEmptyView.setIcon(R.drawable.ic_nodata_message);
                mEmptyView.setEmptyText(CubeUI.getInstance().getContext().getString(R.string.no_data_message));
            }
            mNoNetworkTipLl.setVisibility(View.GONE);
            RxBus.getInstance().post(CubeEvent.EVENT_REFRESH_SYSTEM_MESSAGE, true);
            //            queryOtherPlayLoginTip();
        }
        else {
            if (mEmptyView != null) {
                mEmptyView.setIcon(R.drawable.ic_nodata_no_net);
                mEmptyView.setEmptyText(CubeUI.getInstance().getContext().getString(R.string.no_data_no_net_tip));
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
            mRecentStatusTv.setText(CubeUI.getInstance().getContext().getString(R.string.connecting));
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
            mRecentStatusTv.setText(CubeUI.getInstance().getContext().getString(R.string.receiving));
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
                mRecentStatusTv.setText(CubeUI.getInstance().getContext().getString(R.string.no_connection));
                mRecentStatusTv.setVisibility(View.VISIBLE);
                mRecentMessageTv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRecentSessionAdded(List<RecentSession> recentSessions) {
        LogUtil.i("onRecentSessionAdded: ----> " + recentSessions);
    }

    @Override
    public void onRecentSessionDeleted(List<RecentSession> recentSessions) {

        mRecentSessionAdapter.getData().removeAll(recentSessions);
        mRecentSessionAdapter.notifyDataSetChanged();
        RxBus.getInstance().post(CubeEvent.EVENT_UNREAD_MESSAGE_SUM, calcAllUnreadCount(mRecentSessionAdapter.getData()));
    }

    @Override
    public void onRecentSessionChanged(List<RecentSession> recentSessions) {
        LogUtil.i("onRecentSessionChanged: ----> " + recentSessions);
        if (recentSessions != null && recentSessions.size() > 1) {
            CubeEngine.getInstance().getRecentSessionService().queryRecentSessions(new CubeCallback<List<RecentSession>>() {
                @Override
                public void onSucceed(List<RecentSession> recentSessions) {
                    mRecentSessionAdapter.replaceData(recentSessions);
                    RxBus.getInstance().post(CubeEvent.EVENT_UNREAD_MESSAGE_SUM, calcAllUnreadCount(recentSessions));
                }

                @Override
                public void onFailed(CubeError error) {

                }
            });
        }
        else if (recentSessions != null && recentSessions.size() == 1) {
            List<RecentSession> data = mRecentSessionAdapter.getData();
            RecentSession recentSession = recentSessions.get(0);
            if (data.contains(recentSession)) {
                data.remove(recentSession);
                data.add(0, recentSession);
                mRecentSessionAdapter.notifyDataSetChanged();
            }
            else {
                mRecentSessionAdapter.addData(0, recentSession);
            }
            RxBus.getInstance().post(CubeEvent.EVENT_UNREAD_MESSAGE_SUM, calcAllUnreadCount(data));
            //            ThreadUtil.request(new Runnable() {
            //                @Override
            //                public void run() {
            //                    int allUnreadCount = CubeEngine.getInstance().getRecentSessionService().getAllUnreadCount();
            //                    RxBus.getInstance().post(CubeEvent.EVENT_UNREAD_MESSAGE_SUM,allUnreadCount);
            //                }
            //            });
        }
    }

    private int calcAllUnreadCount(List<RecentSession> recentSessions) {
        int count = 0;
        for (RecentSession recentSession : recentSessions) {
            count += recentSession.unreadCount;
        }

        return count;
    }
}
