package cube.ware.ui.chat.panel.messagelist;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.common.mvp.rx.RxManager;
import com.common.mvp.rx.RxSchedulers;
import com.common.utils.utils.DateUtil;
import com.common.utils.utils.NetworkUtil;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.UIHandler;
import com.common.utils.utils.log.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import cube.service.CubeEngine;
import cube.service.message.FileMessageStatus;
import cube.service.message.model.ReceiptMessage;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.data.model.dataModel.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.eventbus.CubeEvent;
import cube.ware.manager.MessageManager;
import cube.ware.manager.PlayerManager;
import cube.ware.ui.chat.BaseChatActivity;
import cube.ware.ui.chat.ChatContainer;
import cube.ware.ui.chat.ChatCustomization;
import cube.ware.ui.chat.adapter.ChatMessageAdapter;
import cube.ware.ui.chat.panel.input.emoticon.EmoticonUtil;
import cube.ware.ui.chat.panel.input.emoticon.gif.AnimatedImageSpan;
import cube.ware.ui.chat.panel.itemdecoration.DividerGridItemDecoration;
import cube.ware.utils.BitmapDecoder;
import cube.ware.utils.ImageUtil;
import cube.ware.utils.RecyclerViewUtil;
import cube.ware.utils.SpUtil;
import cube.ware.widget.CubeSwipeRefreshLayout;
import cube.ware.widget.recyclerview.CubeRecyclerView;
import cube.ware.widget.toolbar.ICubeToolbar;
import cube.ware.widget.toolbar.ToolBarOptions;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static cube.ware.manager.MessagePopupManager.REPLY;

/**
 * Created by dth
 * Des: 消息列表面板
 * Date: 2018/8/31.
 */

public class MessageListPanel implements ICubeToolbar.OnTitleItemClickListener{
    private static final String TAG = MessageListPanel.class.getSimpleName();

    private static final int LOAD_NUM = 20;

    private static Pair<String, Bitmap> background;  // 背景图片缓存

    private BaseChatActivity       mContext;
    private ChatMessageAdapter     mChatMessageAdapter;  // 消息列表适配器
    private ChatContainer          mChatContainer;       // 聊天容器
    private ChatCustomization      mChatCustomization;   // 聊天定制化参数
    private long                   mChatMessageSn;       // 指定聊天消息 用于跳转指定位置
    private View                   rootView;             // 消息面板布局
    private CubeRecyclerView       mContentRv;           // message view
    private CubeSwipeRefreshLayout mRefreshLayout;       // message 刷新
    private ImageView              mViewBK;              // 消息列表背景图片
    private EditText               mChatMessageEt;       // 消息输入文本框
    private ProgressBar            mProgressBar;         // 进度
    private LinearLayout           mOptionLayout;
    private LinearLayoutManager    mLayoutManager;
    private LinearLayout           mMessageNotifyLy;
    private ImageView              mMessageNotifyHeadIv;
    private TextView               mMessageNotifyHeadTv;
    private TextView               mNewMessageTipTv;    //底部弹出新消息数目的提示

    private List<CubeMessageViewModel> mChatMessages = new ArrayList<>();  // 消息数据集
    private boolean                    isFirstLoad   = false;              // 是否是首次加载
    private boolean                    isHistory     = false;              // 是否是查询历史聊天记录
    private boolean                    isShowMore    = false;              // 是否显示更多布局
    private boolean                    isAnonymous   = false;              // 是否为匿名聊天

    private long mTime = -1;                 // 上次查询消息的时间

    private int     mCurrentUnreadNum   = 0;                  // 当前聊天页面未读消息
    private int     mAllMessageSum      = 0;                  // 消息总数
    private long    mAtMessageSn        = -1;                 // @我消息的sn
    private int     mAtPosition         = 0;                  // @我消息的位置
    private int     mNewMessageTipCount = 0;
    private boolean mIsShowDivideLine   = false; // 是否显示新消息分割线
    private String myCubeId;

    public void onEvent(String text, CubeMessage cubeMessage) {
        switch (text) {
            case REPLY:
                mChatContainer.mPanelProxy.onReplyMessage(cubeMessage);
                break;
        }
    }

    public MessageListPanel(ChatContainer container, ChatCustomization chatCustomization, View rootView, long messageSn) {
        this.mChatContainer = container;
        this.mChatMessageSn = messageSn;
        this.rootView = rootView;
        this.mChatCustomization = chatCustomization;
        this.mContext = (BaseChatActivity) this.mChatContainer.mChatActivity;
        this.isAnonymous = mChatCustomization.typ == ChatCustomization.ChatStatusType.Anonymous;
        init();
    }

    private void init() {
        myCubeId = SpUtil.getCubeId();
        initView();
        initToolBar(false);
        initData();
        initListener();

    }

    @IntDef({MessageStatus.SEND_MESSAGE, MessageStatus.ADD_MESSAGE, MessageStatus.UPDATE_MESSAGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageStatus {
        int SEND_MESSAGE   = 0;
        int ADD_MESSAGE    = 1;
        int UPDATE_MESSAGE = 2;
    }


    /**
     * 初始化视图
     */
    private void initView() {
        //消息提示按钮布局
        this.mMessageNotifyLy = (LinearLayout) rootView.findViewById(R.id.message_notify_ly);
        this.mMessageNotifyHeadIv = (ImageView) rootView.findViewById(R.id.message_notify_head_iv);
        this.mMessageNotifyHeadTv = (TextView) rootView.findViewById(R.id.message_notify_head_tv);
        this.mNewMessageTipTv = (TextView) rootView.findViewById(R.id.new_message_tip_tv);
        this.mViewBK = (ImageView) rootView.findViewById(R.id.message_background);
        this.mChatMessageEt = (EditText) rootView.findViewById(R.id.chat_message_et);
        this.mChatMessageAdapter = new ChatMessageAdapter(mChatMessages, mChatContainer.mChatId, isShowMore, this);
        this.mChatMessageAdapter.setEventListener(new MsgItemEventListener());
        //自定义属性控件
        this.mRefreshLayout = (CubeSwipeRefreshLayout) rootView.findViewById(R.id.message_refresh);
        this.mRefreshLayout.setHeaderViewBackgroundColor(0x00000000);
        this.mRefreshLayout.setHeaderView(createHeaderView());// add headerView
        this.mRefreshLayout.setTargetScrollWithLayout(true);

        this.mContentRv = (CubeRecyclerView) rootView.findViewById(R.id.message_rv);
        closeRecyclerViewAnimator(mContentRv);
        this.mContentRv.requestDisallowInterceptTouchEvent(true);
        this.mContentRv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mLayoutManager = new LinearLayoutManager(mContext);
        //        List<CubeMessageViewModel> messageViewModelsFromCache = RecentSessionDataCenter.getInstance().getMessageViewModelsFromCache(mChatContainer.mChatId);
        //        setStackFromEnd(messageViewModelsFromCache);
        this.mContentRv.setLayoutManager(mLayoutManager);
        // mAdapter
        this.mContentRv.setAdapter(mChatMessageAdapter);
        mContentRv.addItemDecoration(new DividerGridItemDecoration(mContext));
        //view加载完成时回调
        //rootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    public void initToolBar(boolean showMore) {
        this.isShowMore = showMore;
        if (isShowMore) {
            if (mOptionLayout != null) {
                mOptionLayout.setVisibility(View.GONE);
            }
            ToolBarOptions options = new ToolBarOptions();
            options.setBackVisible(true);
            options.setBackText(mContext.getString(R.string.cancel));
            options.setBackTextColor(R.color.selector_back_text);
            setTitleName(options);
            options.setOnTitleClickListener(this);
            mContext.setToolBar(options);
        } else {
            ToolBarOptions options = new ToolBarOptions();
            options.setBackVisible(true);
            options.setBackText(mContext.getString(R.string.chat_msg_back));
            options.setBackTextColor(R.color.selector_back_text);
            options.setBackIcon(R.drawable.selector_title_back);
            options.setRightEnabled(true);
            setTitleName(options);
            options.setOnTitleClickListener(this);
            mContext.setToolBar(options);
            if (mOptionLayout != null) {
                mOptionLayout.setVisibility(View.VISIBLE);
                return;
            }
            if (this.mChatCustomization.optionButtonList != null) {
                mOptionLayout = mContext.addTitleOptionButton(mContext, mChatCustomization.optionButtonList);
            }
        }
        mContext.getToolBar().setBackgroundResource(R.drawable.ic_blur_chat);
    }

    /**
     * 设置聊天标题类型
     *
     * @param options
     */
    private void setTitleName(ToolBarOptions options) {
        if (isAnonymous) {
            options.setLeftTitleIcon(R.drawable.ic_chat_private_name);
        } else {
            options.setTitle(mChatContainer.mChatName);
        }
    }

    /**
     * 修改群名同步chatname
     * @param titleName
     */
    public void setTitleName(String titleName) {
        if (mContext != null) {
            mChatContainer.mChatName = titleName;
            mContext.setChatName(titleName);
            mContext.setTitle(mChatContainer.mChatName);
        }
    }

    public void showWritingTips() {
        if (mContext != null && handler != null) {
            mContext.getToolBar().setTitle(mContext.getString(R.string.writing_chat));
            handler.sendEmptyMessageDelayed(RESET, 10 * 1000);
        }
    }

    private final int RESET = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESET:
                    handler.removeMessages(RESET);
                    ToolBarOptions options = mContext.getToolBarOptions();
                    if (mContext != null && options != null) {
                        setTitleName(mContext.getToolBarOptions());
                        mContext.setToolBar(options);
                    }
                    break;
            }
        }
    };

    /**
     * 初始化消息列表数据
     */
    private void initData() {
        refreshMsgCount();
        mTime = System.currentTimeMillis();
        LogUtil.i("MessageListPanel ---> 消息SN：" + mChatMessageSn);
        if (mChatMessageSn == -1) {
            show();
        } else {
            queryHistoryList(true, true);
        }

        mRxManager.on(CubeEvent.EVENT_REFRESH_CUBE_AVATAR, new Action1<Object>() {
            @Override
            public void call(Object o) {
                mChatMessageAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 刷新消息数量
     */
    public void refreshMsgCount() {
//                mAllMessageSum = UnReadMessageManager.getInstance().getUnRead(mChatContainer.mChatId);
    }

    /**
     * 显示消息页面
     */
    private void show() {
        queryHistoryList(true, false);
    }

    private RxManager mRxManager = new RxManager();

    public void onMessageSync(List<CubeMessage> list) {
        if (list != null && !list.isEmpty() && isMy(list.get(0))) {
            Subscription subscribe = convertMessageModel(list).compose(RxSchedulers.<List<CubeMessageViewModel>>io_main()).subscribe(new Action1<List<CubeMessageViewModel>>() {
                @Override
                public void call(List<CubeMessageViewModel> cubeMessageViewModels) {
                    List<CubeMessageViewModel> anonymousList = new ArrayList<CubeMessageViewModel>();
                    List<CubeMessageViewModel> messageList = new ArrayList<CubeMessageViewModel>();
                    for (CubeMessageViewModel model : cubeMessageViewModels) {
                        if (model.mMessage.isAnonymous()) {
                            anonymousList.add(model);
                        } else {
                            messageList.add(model);
                        }
                    }
                    if (isAnonymous && anonymousList.size() > 0) {
                        //                        setStackFromEnd(anonymousList);
                        mChatMessageAdapter.addRefreshDataList(anonymousList);
                        isShowMessageTip(anonymousList.get(anonymousList.size() - 1));
                    } else if (messageList.size() > 0) {
                        //                        setStackFromEnd(messageList);
                        mChatMessageAdapter.addRefreshDataList(messageList);
                        isShowMessageTip(messageList.get(messageList.size() - 1));
                    }
                }
            });
            mRxManager.add(subscribe);
        }
    }

    /**
     * 显示聊天提示按钮
     *
     * @param text
     * @param color
     * @param drawable
     * @param isShow
     */
    private void showNotify(int text, int color, int drawable, boolean isShow) {
        if (mCurrentUnreadNum == 0) {
            mMessageNotifyLy.setVisibility(View.GONE);
            return;
        }
        mMessageNotifyLy.setVisibility(View.VISIBLE);
        mIsShowDivideLine = true;
        if (isShow) {
            mMessageNotifyHeadTv.setText(text);
        } else {
            mMessageNotifyHeadTv.setText(mContext.getResources().getString(text, mCurrentUnreadNum));
        }
        mMessageNotifyHeadTv.setTextColor(mContext.getResources().getColor(color));
        Drawable d = mContext.getResources().getDrawable(drawable);
        /// 这一步必须要做,否则不会显示.
        d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
        mMessageNotifyHeadTv.setCompoundDrawables(null, null, d, null);
    }

    /**
     * 显示查询历史消息页面
     *
     * @param firstLoad
     * @param isHistory
     */
    private void queryHistoryList(final boolean firstLoad, final boolean isHistory) {
        this.isFirstLoad = firstLoad;
        this.isHistory = isHistory;
        Subscription subscribe = MessageManager.getInstance().queryHistoryMessage(mChatContainer.mChatId, mChatContainer.mSessionType.getType(), LOAD_NUM, mTime, isAnonymous)
                .compose(RxSchedulers.<List<CubeMessageViewModel>>io_main())
                .subscribe(new Observer<List<CubeMessageViewModel>>() {
                    @Override
                    public void onNext(List<CubeMessageViewModel> cubeMessageViewModels) {
                        if (cubeMessageViewModels != null) {
                            updateView(cubeMessageViewModels);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(e);
                        hideRefreshProgress();
                    }

                    @Override
                    public void onCompleted() {
                        onCompletedView();
                        if (isFirstLoad && !isHistory) {
                            mContentRv.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isShowAt()) {
//                                        showNotify(R.string.message_notify_at, R.color.tips_text, R.drawable.ic_message_notify_arrow_at, true);
                                    } else {
                                        mAtPosition = 0;
                                        showNotify(R.string.message_notify, R.color.cube_link_text, R.drawable.ic_message_notify_arrow, false);
                                    }
                                }
                            }, 200);
                        }
                    }

                });
        this.mChatContainer.mRxManager.add(subscribe);
    }

    /**
     * 从消息仓库中读取消息
     *
     * @param firstLoad
     */
    private void queryMessageList(final boolean firstLoad) {
        this.isFirstLoad = firstLoad;
        Subscription subscribe = MessageManager.getInstance().queryHistoryMessage(mChatContainer.mChatId, mChatContainer.mSessionType.getType(), LOAD_NUM, mTime, isAnonymous)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CubeMessageViewModel>>() {
            @Override
            public void onNext(List<CubeMessageViewModel> cubeMessageViewModels) {
                if (cubeMessageViewModels != null) {
                    updateView(cubeMessageViewModels);
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(e.toString());
                hideRefreshProgress();
            }

            @Override
            public void onCompleted() {
                onCompletedView();
            }
        });
        mChatContainer.mRxManager.add(subscribe);
    }

    /**
     * 加载完成聊天界面
     */
    private void onCompletedView() {
        LogUtil.i(TAG, "onCompletedView==>");
        hideRefreshProgress();
    }

    /**
     * 加载刷新聊天界面
     *
     * @param cubeMessages
     */
    private void updateView(List<CubeMessageViewModel> cubeMessages) {
        LogUtil.d(TAG, "updateView cubeMessages size=" + cubeMessages.size());
        if (!cubeMessages.isEmpty() && cubeMessages.get(0) != null) {
            Collections.sort(cubeMessages, new Comparator<CubeMessageViewModel>() {
                @Override
                public int compare(CubeMessageViewModel o1, CubeMessageViewModel o2) {
                    CubeMessage m1 = o1.mMessage;
                    CubeMessage m2 = o2.mMessage;
                    return m1.getTimestamp() - m2.getTimestamp() > 0 ? 1 : -1;
                }
            });
            mTime = cubeMessages.get(0).mMessage.getTimestamp();
        }
        // 首次加载
        if (this.isFirstLoad) {
            LogUtil.i("MessageListPanel ---> 第一次加载，是否是历史消息：" + isHistory);
            this.mChatMessageAdapter.refreshDataList(cubeMessages);
            if (isHistory) {
                scrollToSn(mChatMessageSn);
            } else {
                RecyclerViewUtil.scrollToBottom(mContentRv);
            }
        } else {
            List<CubeMessageViewModel> mDataList = this.mChatMessageAdapter.getDataList();
            Iterator<CubeMessageViewModel> iterator = cubeMessages.iterator();
            while (iterator.hasNext()) {
                CubeMessageViewModel messageViewModel = iterator.next();
                if (mDataList.contains(messageViewModel)) {
                    iterator.remove();
                }
            }
            if (cubeMessages.size() > 0) {
                mChatMessageAdapter.addOrUpdateItemWithOutAnimator(0, mContentRv);
                //                setStackFromEnd(cubeMessages);
                this.mChatMessageAdapter.addDataList(cubeMessages);
                int refreshedMessageNum = cubeMessages.size();
                int position = refreshedMessageNum > 1 ? refreshedMessageNum - 1 : 0;
                RecyclerViewUtil.scrollToPosition(this.mContentRv, position);
            }
            //else {
            //    this.mRefreshLayout.setEnabled(false);
            //}
        }

        //// 是否有更多消息可以查看
        //if (this.mChatMessageAdapter.getItemCount() < mAllMessageSum) {
        //    this.mRefreshLayout.setEnabled(true);
        //}
        //else {
        //    this.mRefreshLayout.setEnabled(false);
        //}
    }

    public void scrollToSn(long sn) {
        int currentPosition = this.mChatMessageAdapter.findCurrentPosition(sn);
        RecyclerViewUtil.scrollToPosition(this.mContentRv, currentPosition);
    }

    /**
     * 发送消息回执
     *
     * @param message
     */
    private void receiptMsg(CubeMessage message) {
        if (!message.isReceipt && !isAnonymous && message.isReceivedMessage()) {
            ReceiptMessage receiptMessage = new ReceiptMessage(message.isGroupMessage() ? message.getGroupId() : message.getSenderId(), SpUtil.getCubeId());
            receiptMessage.setTraceless(true);//设置回执消息是否无踪迹的 true 不存数据库
            CubeEngine.getInstance().getMessageService().sendMessage(receiptMessage);
        }
    }

    /**
     * 判断at提示是否显示
     *
     * @return
     */
    private boolean isShowAt() {
        if (mAtMessageSn == -1) {
            return false;
        }
        mAtPosition = mChatMessageAdapter.findCurrentPosition(mAtMessageSn);
        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        // 获取 RecycleView第一个子view
        return mAtPosition < firstVisibleItemPosition;
    }

    /**
     * 隐藏刷新布局
     */
    private void hideRefreshProgress() {
        this.mRefreshLayout.setRefreshing(false);
        this.mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        this.mContentRv.setEventListener(new CubeRecyclerView.OnEventListener() {
            @Override
            public void onStartTouch() {
                mChatContainer.mPanelProxy.collapseInputPanel();
            }
        });
        this.mContentRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mMessageNotifyLy.getVisibility() != View.GONE) {
                    int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                    // 判断是否滚动到指定未读消息
                    if (firstVisibleItemPosition == mAtPosition) {
                        mMessageNotifyLy.setVisibility(View.GONE);
                    }
                }

                if (mNewMessageTipTv.getVisibility() != View.GONE) {
                    int lastVisiblePosition = mLayoutManager.findLastVisibleItemPosition();
                    int itemCount = mLayoutManager.getItemCount();
                    if (lastVisiblePosition >= itemCount - 1) {
                        hideNewMessageTip();
                    }
                }
            }
        });
        this.mChatMessageEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                //    mLayoutManager.setStackFromEnd(true);
                //}
                //else {
                //    mLayoutManager.setStackFromEnd(false);
                //}
                return false;
            }
        });
        this.mMessageNotifyLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageNotifyLy.setVisibility(View.GONE);
                RecyclerViewUtil.smoothScrollToPosition(mContentRv, mAtPosition);
            }
        });
        this.mNewMessageTipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideNewMessageTip();
                RecyclerViewUtil.scrollToBottom(mContentRv);
            }
        });
    }

    /**
     * 显示新消息提示
     */
    private void showNewMessageTip() {
        LogUtil.i(TAG, "hideNewMessageTip");
        this.mNewMessageTipCount++;
        if (mNewMessageTipCount > 99) {
            this.mNewMessageTipTv.setText("99+");
        } else {
            this.mNewMessageTipTv.setText(String.valueOf(mNewMessageTipCount));
        }
        this.mNewMessageTipTv.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏新消息提示
     */
    private void hideNewMessageTip() {
        LogUtil.i(TAG, "hideNewMessageTip");
        if (mNewMessageTipTv.getVisibility() != View.GONE) {
            this.mNewMessageTipCount = 0;
            this.mNewMessageTipTv.setVisibility(View.GONE);
        }
    }

    /**
     * 是否展现新消息提示
     *
     * @param messageViewModel 更新或添加的消息
     * @return true：展现，false：不展现
     */
    private boolean isShowNewMessageTip(CubeMessageViewModel messageViewModel) {
        return null != messageViewModel && !messageViewModel.mMessage.getSenderId().equals(myCubeId);
    }

    /**
     * 显示最后一条消息
     */
    public void scrollToBottom() {
        RecyclerViewUtil.scrollToBottom(mContentRv);
        hideNewMessageTip();
    }

    /**
     * 重新加载
     *
     * @param container
     * @param chatCustomization
     * @param messageSn
     */
    public void reload(ChatContainer container, ChatCustomization chatCustomization, long messageSn) {
        this.mChatContainer = container;
        this.mChatCustomization = chatCustomization;
        this.mChatMessageSn = messageSn;
        this.mChatMessages.clear();
    }

    /**
     * 刷新消息列表
     */
    public void deleteMessageList() {
        this.mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //                setStackFromEnd(null);
                mChatMessageAdapter.refreshDataList(null);
            }
        });
    }

    /**
     * 发送消息后，更新本地消息列表
     *
     * @param cubeMessage
     */
    public boolean onMessageSend(CubeMessage cubeMessage) {
        LogUtil.i(TAG, "onMessageSend==> cube message sn=" + cubeMessage.getMessageSN());
        if (isMy(cubeMessage) && cubeMessage.isAnonymous() == isAnonymous) {
            buildMessage(cubeMessage, MessageStatus.SEND_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * 收到消息后，更新本地列表
     *
     * @param cubeMessage
     * @return
     */
    public boolean addMessage(CubeMessage cubeMessage) {
        if (isMy(cubeMessage) && cubeMessage.isAnonymous() == isAnonymous) {
            receiptMsg(cubeMessage);
            buildMessage(cubeMessage, MessageStatus.ADD_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * 更新消息后，更新本地列表
     *
     * @param cubeMessage
     * @return
     */
    public boolean updateMessage(CubeMessage cubeMessage) {
        LogUtil.i(TAG, "updateMessage==> sn=" + cubeMessage.getMessageSN());
        if (isMy(cubeMessage) && cubeMessage.isAnonymous() == isAnonymous) {
            buildMessage(cubeMessage, MessageStatus.UPDATE_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * 是否为当前聊天界面
     *
     * @param cubeMessage
     * @return
     */
    private boolean isMy(CubeMessage cubeMessage) {
        return null != cubeMessage && cubeMessage.getChatId().equals(this.mChatContainer.mChatId);
    }

    private Observable<List<CubeMessageViewModel>> convertMessageModel(List<CubeMessage> cubeMessages) {
        return Observable.just(cubeMessages).flatMap(new Func1<List<CubeMessage>, Observable<List<CubeMessageViewModel>>>() {
            @Override
            public Observable<List<CubeMessageViewModel>> call(List<CubeMessage> cubeMessages) {
                return Observable.from(cubeMessages).flatMap(new Func1<CubeMessage, Observable<CubeMessageViewModel>>() {
                    @Override
                    public Observable<CubeMessageViewModel> call(final CubeMessage cubeMessage) {
                        //自定义消息特殊处理
                        if (cubeMessage.getMessageType().equals(CubeMessageType.CustomTips.getType())) {
                            return MessageManager.getInstance().buildCustom(cubeMessage);
                        }
                        else {
                            return MessageManager.getInstance().buildUserInfo(cubeMessage);
                        }
                    }
                }).toList();
            }
        });
    }

    /**
     * 构建聊天消息展示model
     *
     * @param cubeMessage
     */
    private void buildMessage(final CubeMessage cubeMessage, final int messageStatus) {
        LogUtil.d(TAG, "buildMessage message status=" + messageStatus);
        //自定义消息特殊处理
        if (cubeMessage.getMessageType().equals(CubeMessageType.CustomTips.getType())) {
            CubeMessageViewModel viewModel = new CubeMessageViewModel();
            viewModel.mMessage = cubeMessage;
            onRefresh(viewModel, messageStatus);
            return;
        }

        UIHandler.run(new Runnable() {
            @Override
            public void run() {
                CubeMessageViewModel viewModel = new CubeMessageViewModel();
                viewModel.userNme = cubeMessage.getSenderName();
                viewModel.userFace = AppConstants.AVATAR_URL+cubeMessage.getSenderId();
                viewModel.remark = cubeMessage.getSenderName();
                viewModel.mMessage = cubeMessage;
                onRefresh(viewModel, messageStatus);
            }
        });


//        if (mChatContainer.mSessionType == CubeSessionType.Group) {
//            CubeUserRepository.getInstance().queryUser(cubeMessage.getSenderId()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CubeUser>() {
//                @Override
//                public void call(CubeUser cubeUser) {
//                    CubeMessageViewModel viewModel = new CubeMessageViewModel();
//                    viewModel.userNme = TextUtils.isEmpty(cubeUser.getDisplayName()) ? cubeUser.getCubeId()+"" : cubeUser.getDisplayName()+"";
//                    viewModel.userFace = cubeUser.getAvatar();
//                    viewModel.remark = cubeUser.getDisplayName();
//                    viewModel.mMessage = cubeMessage;
//                    onRefresh(viewModel, messageStatus);
//                }
//            });
//        } else {
//            CubeUserRepository.getInstance().queryUser(cubeMessage.getSenderId()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CubeUser>() {
//                @Override
//                public void call(CubeUser cubeUser) {
//                    CubeMessageViewModel viewModel = new CubeMessageViewModel();
//                    viewModel.userNme = TextUtils.isEmpty(cubeUser.getDisplayName()) ? cubeUser.getCubeId() : cubeUser.getDisplayName();
//                    viewModel.userFace = cubeUser.getAvatar();
//                    viewModel.remark = cubeUser.getDisplayName();
//                    viewModel.mMessage = cubeMessage;
//                    onRefresh(viewModel, messageStatus);
//                }
//            });
//        }
    }

    /**
     * 是否展现新消息提醒
     */
    private void isShowMessageTip(CubeMessageViewModel cubeMessageViewModel) {
        boolean isShowMessageTip = isShowNewMessageTip(cubeMessageViewModel);
        LogUtil.i(TAG, "isShowMessageTip=" + isShowMessageTip);
        if (RecyclerViewUtil.isScrollToBottom(mContentRv)) {
            hideNewMessageTip();
        } else {
            if (isShowMessageTip) {
                showNewMessageTip();
            } else {
                hideNewMessageTip();
            }
        }
    }

    /**
     * 刷新界面
     *
     * @param viewModel
     * @param messageStatus
     */
    private void onRefresh(CubeMessageViewModel viewModel, int messageStatus) {
        LogUtil.i(TAG, "cloudz onRefresh model messageStatus=" + messageStatus);
        if (this.mChatMessageAdapter == null) {
            return;
        }
        switch (messageStatus) {
            case MessageStatus.SEND_MESSAGE:
                mChatMessageAdapter.addOrUpdateItem(viewModel);
                RecyclerViewUtil.scrollToBottom(mContentRv);
                break;
            case MessageStatus.ADD_MESSAGE:
                mChatMessageAdapter.addOrUpdateItem(viewModel);
                isShowMessageTip(viewModel);
                break;
            case MessageStatus.UPDATE_MESSAGE:
                //如果是正在下载视频 不刷新
                if (CubeMessageType.isFileMessage(viewModel.mMessage.getMessageType()) && (viewModel.mMessage.getFileMessageStatus() == FileMessageStatus.Downloading.getStatusCode())) {
                    LogUtil.i(TAG, "cloudz onRefresh model messageStatus=" + messageStatus + " is FileMessage");
                    return;
                }
                mChatMessageAdapter.addOrUpdateItem(viewModel);
                break;
        }
        if (viewModel.mMessage.getMessageType().equals(CubeMessageType.REPLYMESSAGE.getType())) {
            scrollToBottom();
        }
    }

    /**
     * 设置聊天背景
     *
     * @param uriString
     * @param color
     */
    public void setChattingBackground(String uriString, int color) {
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            if (uri.getScheme().equalsIgnoreCase("file") && uri.getPath() != null) {
                Bitmap background = getBackground(uri.getPath());
                this.mViewBK.setImageBitmap(background);
                ImageUtil.recycleBitmap(background);
            } else if (uri.getScheme().equalsIgnoreCase("android.resource")) {
                List<String> paths = uri.getPathSegments();
                if (paths == null || paths.size() != 2) {
                    return;
                }
                String type = paths.get(0);
                String name = paths.get(1);
                String pkg = uri.getHost();
                int resId = this.mContext.getResources().getIdentifier(name, type, pkg);
                if (resId != 0) {
                    this.mViewBK.setBackgroundResource(resId);
                }
            }
        } else if (color != 0) {
            this.mViewBK.setBackgroundColor(color);
        }
    }

    /**
     * 获取聊天类型
     *
     * @return
     */
    public CubeSessionType getChatTye() {
        return mChatContainer.mSessionType;
    }

    /**
     * 获取bitmap对象
     *
     * @param path
     * @return
     */
    private Bitmap getBackground(String path) {
        if (background != null && path.equals(background.first) && background.second != null) {
            return background.second;
        }

        if (background != null && background.second != null) {
            background.second.recycle();
        }

        Bitmap bitmap = null;
        if (path.startsWith("/android_asset")) {
            String asset = path.substring(path.indexOf("/", 1) + 1);
            try {
                InputStream ais = this.mContext.getAssets().open(asset);
                bitmap = BitmapDecoder.decodeSampled(ais, ScreenUtil.screenWidth, ScreenUtil.screenHeight);
                ais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            bitmap = BitmapDecoder.decodeSampled(path, ScreenUtil.screenWidth, ScreenUtil.screenHeight);
        }
        background = new Pair<>(path, bitmap);
        return bitmap;
    }

    //构建刷新头布局
    private View createHeaderView() {
        LogUtil.i(TAG, "createHeaderView==>");
        View headerView = LayoutInflater.from(this.mRefreshLayout.getContext()).inflate(R.layout.cube_refreshlayout_head, null);
        mProgressBar = (ProgressBar) headerView.findViewById(R.id.pb_view);
        //刷新效果
        this.mRefreshLayout.setOnPullRefreshListener(new CubeSwipeRefreshLayout.OnPullRefreshListener() {

            @Override
            public void onRefresh() {
                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
                if (isHistory) {
                    queryHistoryList(false, true);
                } else {
                    queryMessageList(false);
                }
            }

            @Override
            public void onPullDistance(int distance) {
                // pull distance
            }

            @Override
            public void onPullEnable(boolean enable) {
            }
        });
        return headerView;
    }

    public void onDestroy() {
        mRxManager.clear();
        ArrayList<AnimatedImageSpan> imageSpans = EmoticonUtil.imageSpans;
        for (AnimatedImageSpan imageSpan : imageSpans) {
            imageSpan.stopAnimate();
        }
        imageSpans.clear();
        PlayerManager.getInstance().stop();
        handler.removeCallbacksAndMessages(null);
        mChatMessageAdapter.onDestroy();
    }

    public void onResume() {
        if (isAnonymous && mChatMessageAdapter != null) {
            mChatMessageAdapter.notifyDataSetChanged();
        }
    }

    public void onPause() {

    }

    public void setUnreadMessageCount(int count, boolean isSecret) {
        if (isSecret == isAnonymous) {
            LogUtil.i("未读消息数量" + count + isShowMore);
            if (isShowMore) {
                return;
            }
            //setNonMessageNum(UnReadMessageManager.getInstance().getUnreadCount(mChatContainer.mChatId));
        }
    }

    /**
     * 设置消息未读数量
     *
     * @param count
     */
    private void setNonMessageNum(int count) {
        LogUtil.i(TAG, "setNonMessageNum==> count=" + count + "this=" + this);
        String s;
        if (count > 0) {
            if (count > 99) {
                s = "99+";
            } else {
                s = String.valueOf(count);
            }
            if (isAnonymous) {
                mContext.getToolBar().setBackText(mContext.getString(R.string.secret_chat_msg_num_back, s));
            } else {
                mContext.getToolBar().setBackText(mContext.getString(R.string.chat_msg_num_back, s));
            }
        } else {
            if (isAnonymous) {
                mContext.getToolBar().setBackText(mContext.getString(R.string.secret_chat_msg_back));
            } else {
                mContext.getToolBar().setBackText(mContext.getString(R.string.chat_msg_back));
            }
        }
    }

    @Override
    public void onTitleItemClick(View v) {
        if (v.getId() == R.id.back) {
            if (isShowMore) {
                mChatMessageAdapter.isShowMore = false;
                mChatMessageAdapter.notifyDataSetChanged();
                initToolBar(false);
            } else {
                mContext.onBackPressed();
            }
        }
    }

    /**
     * 消息条目长安回调和重发回调
     */
    private class MsgItemEventListener implements ChatMessageAdapter.ViewHolderEventListener {

        @Override
        public boolean onViewHolderLongClick(View clickView, CubeMessage cubeMessage, ChatMessageAdapter adapter, int position) {
            return true;
        }

        @Override
        public void onFailedBtnClick(View clickView, CubeMessage cubeMessage) {
            // 重置状态为unsent
            if (!NetworkUtil.isNetworkConnected(mContext)) {
                ToastUtil.showToast(mContext, "网络未连接，发送失败");
                return;
            }
            int index = mChatMessageAdapter.findCurrentPosition(cubeMessage.getMessageSN());
            if (index >= 0 && index < mChatMessageAdapter.getDataList().size()) {
                CubeMessageViewModel item = mChatMessageAdapter.getData(index);
                item.mMessage.setMessageStatus(CubeMessageStatus.Sending.getStatus());
                item.mMessage.setSendTimestamp(DateUtil.getCurrentTimeMillis());
                mChatMessageAdapter.removeData(index);
                mChatMessageAdapter.refreshMsgNum();
                onMessageSend(item.mMessage);
            }
            if (CubeMessageType.isFileMessage(cubeMessage.getMessageType())) {
                MessageManager.getInstance().resumeMessage(cubeMessage.getMessageSN());
            } else {
                MessageManager.getInstance().resendMessage(cubeMessage.getMessageSN());
            }
        }
    }

    /**
     * 是否显示分割线`
     *
     * @return
     */
    public boolean isShowDivideLine() {
        return mIsShowDivideLine && isFirstLoad;
    }

    public void closeRecyclerViewAnimator(RecyclerView recyclerView) {
        recyclerView.getItemAnimator().setAddDuration(0);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.getItemAnimator().setMoveDuration(0);
        recyclerView.getItemAnimator().setRemoveDuration(0);
    }
}
