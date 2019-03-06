package cube.ware.ui.chat.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.common.utils.utils.DateUtil;
import com.common.utils.utils.DeviceUtil;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.glide.GlideUtil;
import com.common.utils.utils.log.LogUtil;

import cube.ware.ui.chat.ChatEventHandle;
import cube.ware.ui.chat.ChatEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cube.ware.AppConstants;
import cube.ware.CubeUI;
import cube.ware.R;
import cube.ware.data.model.dataModel.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.manager.MessageManager;
import cube.ware.manager.MessagePopupManager;
import cube.ware.utils.SpUtil;
import cube.ware.widget.CountdownChronometer;
import cube.ware.widget.InterceptRelativeLayout;
import cube.ware.widget.recyclerview.BaseRecyclerViewHolder;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/3.
 */

public abstract class BaseMsgViewHolder implements MessagePopupManager.OnPopMenuHandleListener{



    public BaseRecyclerViewHolder mViewHolder;
    public ChatMessageAdapter     mAdapter;
    public CubeMessageViewModel   mData;
    public Context                mContext;
    public int                    mPosition;

    protected InterceptRelativeLayout  mChatRootView;
    protected LinearLayout             mChatMoreLl;
    protected CheckBox                 mChatMoreCb;       // 更多选择按钮
    protected ImageView                mRepeatButton;     // 消息重发view
    protected LinearLayout             mChatDivideLineLayout; // 聊天分割线布局
    protected TextView                 mChatDate;         // 聊天日期view
    protected ProgressBar              mProgressBar;      // 发送进度条
    protected TextView                 mUserNickName;     // 聊天用户昵称(在群组聊天中显示)
    protected FrameLayout              mContentContainer; // 聊天内容容器
    protected CountdownChronometer     mSecretTime;       // 密聊倒计时
    protected LinearLayout             nameContainer;
    protected ImageView                nameIconView;
    protected TextView                 mReadReceipt;      // 聊天回执view
    protected ImageView                mUserHeadSend;     // 发送用户头像
    protected ImageView                mUserHeadReceive;  // 接收用户头像
    protected View                     mInflate;          // 不同消息添加不同的view
    /**
     * 存储已选中的信息
     * key: cubeMessageSn
     * value: cubeMessage
     */
    public    Map<String, CubeMessage> mSelectedMap;
    public    String                   mCubeMessageSn;

    public BaseMsgViewHolder(ChatMessageAdapter adapter, BaseRecyclerViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
        this.mAdapter = adapter;
        this.mViewHolder = viewHolder;
        this.mData = data;
        this.mPosition = position;
        this.mSelectedMap = selectedMap;
        this.mContext = viewHolder.getConvertView().getContext();
        this.inflate();
        this.initView();
        this.refresh();
    }

    public void update(ChatMessageAdapter adapter, BaseRecyclerViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
        this.mAdapter = adapter;
        this.mViewHolder = viewHolder;
        this.mData = data;
        this.mPosition = position;
        this.mSelectedMap = selectedMap;
        this.mContext = viewHolder.getConvertView().getContext();
        this.inflate();
        this.initView();
        this.refresh();
    }

    private void inflate() {
        this.mChatDivideLineLayout = this.mViewHolder.getView(R.id.chat_divide_line_ll);
        this.mChatDate = this.mViewHolder.getView(R.id.chat_date_tv);
        this.mChatRootView = this.mViewHolder.getView(R.id.chat_root_view);
        if(mAdapter.getItemCount() == (mPosition + 1)){
            int left = DeviceUtil.dp2px(mContext, 6);
            int top = DeviceUtil.dp2px(mContext, 10);
            int right = DeviceUtil.dp2px(mContext, 6);
            int bottom = DeviceUtil.dp2px(mContext, 18);
            mChatRootView.setPadding(left, top, right, bottom);
        }
        this.mChatMoreLl = this.mViewHolder.getView(R.id.chat_more_layout);
        this.mChatMoreCb = this.mViewHolder.getView(R.id.chat_more_cb);
        this.mUserHeadSend = this.mViewHolder.getView(R.id.user_head_iv_left);
        this.mUserHeadReceive = this.mViewHolder.getView(R.id.user_head_iv_right);
        this.mRepeatButton = this.mViewHolder.getView(R.id.chat_item_alert);
        this.mProgressBar = this.mViewHolder.getView(R.id.chat_item_progress);
        this.mUserNickName = this.mViewHolder.getView(R.id.chat_user_nickname);
        this.mContentContainer = this.mViewHolder.getView(R.id.chat_item_content);
        this.mSecretTime = this.mViewHolder.getView(R.id.chat_item_secret_time);
        this.nameIconView = this.mViewHolder.getView(R.id.chat_user_name_icon);
        this.nameContainer = this.mViewHolder.getView(R.id.chat_user_name_layout);
        this.mReadReceipt = this.mViewHolder.getView(R.id.chat_item_read);

        //因为复用原因，添加子布局时，一定要移除之前的布局
        if (this.mContentContainer != null) {
            this.mContentContainer.removeAllViews();
        }
        this.mInflate = View.inflate(this.mContext, getContentResId(), this.mContentContainer);
    }

    // 以下接口可由子类覆盖或实现

    /**
     * 返回具体消息类型内容展示区域的layout res id
     *
     * @return
     */
    protected abstract int getContentResId();

    /**
     * 在该接口中根据layout对各控件成员变量赋值
     */
    protected abstract void initView();

    /**
     * 将消息数据项与内容的view进行绑定
     */
    protected abstract void bindView();

    /**
     * 内容区域点击事件响应处理
     *
     * @param view
     */
    protected void onItemClick(View view) {
        LogUtil.i("内容区域点击事件响应处理");
    }

    /**
     * 判断消息方向，是否是接收到的消息
     *
     * @return
     */
    public boolean isReceivedMessage() {
        return this.mData.isReceivedMessage() && !mData.mMessage.getSenderId().equals(SpUtil.getCubeId());
    }

    /**
     * 是否显示密聊内容
     *
     * @return
     */
    public boolean isShowSecretMessage() {
        return mData.mMessage.isAnonymous() && isReceivedMessage() && mData.mMessage.getAnonymousTimestamp() == 0;
    }

    /**
     * 内容区域长按事件响应处理
     * 该接口的优先级比adapter中有长按事件的处理监听高，当该接口返回为true时，adapter的长按事件监听不会被调用到
     *
     * @return
     */
    protected boolean onItemLongClick() {
        return false;
    }

    /**
     * 返回该消息是不是居中显示
     *
     * @return
     */
    protected boolean isMiddleItem() {
        return false;
    }

    /**
     * 是否显示头像，默认为显示
     *
     * @return
     */
    protected boolean isShowHeadImage() {
        return true;
    }

    /**
     * 是否显示气泡背景，默认为显示
     *
     * @return
     */
    protected boolean isShowBubble() {
        return true;
    }

    /**
     * 当是接收到的消息时，内容区域背景的drawable id
     *
     * @return
     */
    protected int leftBackground() {
        return R.drawable.selector_chat_receive_bg;
    }

    /**
     * 当是发送出去的消息时，内容区域背景的drawable id
     *
     * @return
     */
    protected int rightBackground() {
        return R.drawable.selector_chat_send_bg;
    }

    /**
     * 根据layout id查找对应的控件
     *
     * @param id
     * @param <T>
     *
     * @return
     */
    protected <T extends View> T findViewById(int id) {
        return (T) this.mInflate.findViewById(id);
    }

    /**
     * 刷新布局
     */
    public final void refresh() {
        mContentContainer.setTag(R.id.base_msg_view_holder_msgsn, mData.mMessage.getMessageSN()); //当前View容器存的是mData.mMessage.getMessageSN() 的消息
        this.setHeadImageViewAddUserNickName();
        this.setDivideLine();
        setChatDateByTimeStamp(mPosition);
        this.setChatDate();
        this.setStatus();
        this.setOnClickListener(mAdapter.isShowMore);
        this.setLongClickListener(mAdapter.isShowMore);
        this.setChatLayout();
        this.setMoreLayout();
        this.setSecretTime();
        this.setReadReceipt();
        this.bindView();
    }

    /**
     * 设置密聊时间
     */
    private void setSecretTime() {
        // TODO: 2017/7/25 设置密聊时间倒计时
        if (isReceivedMessage()) {
            long current = System.currentTimeMillis();
            long end = mData.mMessage.getInvalidTimestamp();
            int time = (int) ((end - current) / 1000);
            //end ！ =0
            if (mData.mMessage.isAnonymous() && end != 0 && time > 0) {
                this.mSecretTime.setVisibility(View.VISIBLE);
                this.mSecretTime.initTime(time);
            }
            else {
                this.mSecretTime.setVisibility(View.GONE);
                this.mSecretTime.initTime(time); //开始
                this.mSecretTime.onResume();     //再次进入继续走当前时间
            }
        }
        else {
            this.mSecretTime.setVisibility(View.GONE);
        }
    }

    /**
     * 设置未读回执
     */
    private void setReadReceipt() {
        if (!isReceivedMessage() && mData.mMessage.isAnonymous() && !isMiddleItem()) {
            mReadReceipt.setVisibility(View.VISIBLE);
        }
        else {
            mReadReceipt.setVisibility(View.GONE);
        }
    }

    /**
     * 设置更多界面
     */
    private void setMoreLayout() {
        this.mCubeMessageSn = String.valueOf(mData.mMessage.getMessageSN());
        if (!(mData.getItemType() == AppConstants.MessageType.CUSTOM_TIPS)) {
            this.mChatMoreLl.setVisibility(mAdapter.isShowMore ? View.VISIBLE : View.GONE);
            this.mChatMoreCb.setVisibility(mAdapter.isShowMore ? View.VISIBLE : View.GONE);
        }
        if (mSelectedMap.containsKey(mCubeMessageSn)) {
            mChatMoreCb.setChecked(true);
        }
        else {
            mChatMoreCb.setChecked(false);
        }
    }

    /**
     * 设置用户头像和设置消息用户名称
     */
    private void setHeadImageViewAddUserNickName() {
        final ImageView show = isReceivedMessage() ? this.mUserHeadSend : this.mUserHeadReceive;
        ImageView hide = isReceivedMessage() ? this.mUserHeadReceive : this.mUserHeadSend;
        hide.setVisibility(View.GONE);
        if (!isShowHeadImage()) {
            show.setVisibility(View.GONE);
            return;
        }
        if (isMiddleItem()) {
            show.setVisibility(View.GONE);
        }
        else {
            show.setVisibility(View.VISIBLE);
            //加载用户头像
            if (this.mData.isGroupMessage()) {
                if (isReceivedMessage()) {
                    //设置显示用户昵称
                    nameContainer.setVisibility(View.VISIBLE);
                    mUserNickName.setText(mData.userNme);
                    mUserNickName.setVisibility(View.VISIBLE);
                }
                else {
                    nameContainer.setVisibility(View.GONE);
                    mUserNickName.setVisibility(View.GONE);
                }
                GlideUtil.loadSignatureCircleImage(mData.userFace,mContext, show, R.drawable.default_head_group);
            }
            else {
                nameContainer.setVisibility(View.GONE);
                if (mData.mMessage.isAnonymous()) {
                    Glide.with(mContext).load(R.drawable.ic_chat_secret_face_1).into(show);
                }
                else {
                    GlideUtil.loadSignatureCircleImage(mData.userFace, mContext, show, R.drawable.default_head_user);
                }
            }
        }
    }

    /**
     * 初始化监听
     *
     * @param isShowMore
     */
    private void setOnClickListener(boolean isShowMore) {
        this.mChatRootView.setIntercept(isShowMore);
        if (isShowMore) {
            this.mChatRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedMap.containsKey(mCubeMessageSn)) {
                        mSelectedMap.remove(mCubeMessageSn);
                        mChatMoreCb.setChecked(false);
                        if (mOnItemSelectedListener != null) {
                            mOnItemSelectedListener.onItemUnselected(mCubeMessageSn);
                        }
                    }
                    else {
                        mSelectedMap.put(mCubeMessageSn, mData.mMessage);
                        mChatMoreCb.setChecked(true);
                        if (mOnItemSelectedListener != null) {
                            mOnItemSelectedListener.onItemSelected(mCubeMessageSn);
                        }
                    }
                    if (null != mOnItemSelectedListener) {
                        mOnItemSelectedListener.onSelectedList(mSelectedMap);
                    }
                }
            });
        }
        // 重发/重收按钮响应事件
        if (this.mAdapter.getEventListener() != null) {
            this.mRepeatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.getEventListener().onFailedBtnClick(mRepeatButton, mData.mMessage);
                }
            });
        }

        // 内容区域点击事件响应， 相当于点击了整项
        this.mContentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(mContentContainer);
            }
        });

        // 头像点击事件响应
        List<ChatEventListener> mListeners = ChatEventHandle.getInstance().getChatEventListeners();
        if (mListeners.size() > 0) {
            View.OnClickListener portraitListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mListeners.size(); i++) {
                        mListeners.get(i).onAvatarClicked(mViewHolder.getConvertView().getContext(), mData.mMessage);
                    }
                }
            };
            if (!mData.mMessage.isAnonymous()) {
                this.mUserHeadSend.setOnClickListener(portraitListener);
                this.mUserHeadReceive.setOnClickListener(portraitListener);
            }
        }

        //密聊时间倒计时完成
        //this.mSecretTime.setOnTimeCompleteListener(new CountdownChronometer.OnTimeCompleteListener() {
        //    @Override
        //    public void onTimeComplete() {
        //        mAdapter.delSecretMsg(mData.mMessage.getMessageSN());
        //    }
        //});
    }

    /**
     * item长按事件监听
     *
     * @param isShowMore
     */
    private void setLongClickListener(boolean isShowMore) {
        if (isShowMore) {
            return;
        }

        // 头像长按事件响应处理
        List<ChatEventListener> mListeners = ChatEventHandle.getInstance().getChatEventListeners();
        if (mListeners.size() > 0) {
            View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    for (int i = 0; i < mListeners.size(); i++) {
                        mListeners.get(i).onAvatarLongClicked(v.getContext(), mData.mMessage);
                    }
                    return true;
                }
            };
            this.mUserHeadSend.setOnLongClickListener(longClickListener);
            //this.mUserHeadReceive.setOnLongClickListener(longClickListener);
        }

        if (mData.mMessage.getMessageType().equals(CubeMessageType.Text.getType())) {
            return;
        }
        else if (mData.mMessage.getMessageType().equals(CubeMessageType.Voice.getType())) {
            return;
        }
        else if (mData.mMessage.isAnonymous()) {
            return;
        }
        //长安弹出菜单
        MessagePopupManager.showMessagePopup(this, mContentContainer, this);
    }

    /**
     * 设置聊天布局
     */
    private void setChatLayout() {
        if (!isShowBubble() && !isMiddleItem()) {
            return;
        }

        LinearLayout bodyContainer = this.mViewHolder.getView(R.id.chat_item_body);

        // 调整container的位置
        int index = isReceivedMessage() ? 0 : 3;
        if (bodyContainer.getChildAt(index) != this.mContentContainer) {
            bodyContainer.removeView(this.mContentContainer);
            bodyContainer.addView(this.mContentContainer, index);
        }

        if (isMiddleItem()) {
            setGravity(bodyContainer, Gravity.CENTER);
        }
        else {
            if (isReceivedMessage()) {
                setGravity(bodyContainer, Gravity.LEFT);
                this.mContentContainer.setBackgroundResource(leftBackground());
            }
            else {
                setGravity(bodyContainer, Gravity.RIGHT);
                this.mContentContainer.setBackgroundResource(rightBackground());
            }
        }
    }

    /**
     * 设置FrameLayout子控件的gravity参数
     */
    protected final void setGravity(View view, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        if (gravity == Gravity.CENTER) {
            FrameLayout rootContainer = this.mViewHolder.getView(R.id.chat_item_root);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rootContainer.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.setMargins(0, 0, 0, 0);
        }
        else if (gravity == Gravity.LEFT) {
            if (mData.mMessage.isAnonymous()) {
                params.setMargins(0, ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), 0);
            }
        }
        else {
            params.setMargins(0, ScreenUtil.dip2px(10), 0, 0);
        }
        params.gravity = gravity;
    }

    /**
     * 设置FrameLayout子控件的gravity参数
     */
    protected final void setAudioGravity(View view, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = gravity;
    }

    /**
     * 设置消息发送状态
     */
    private void setStatus() {
        CubeMessageStatus status = CubeMessageStatus.parse(this.mData.mMessage.getMessageStatus());
        long time = DateUtil.getCurrentTimeMillis() - this.mData.mMessage.getSendTimestamp();
        switch (status) {
            case Failed:
                if (this.mData.mMessage.isReceivedMessage()) {
                    // TODO: 2017/6/28
                    return;
                }
                this.mProgressBar.setVisibility(View.GONE);
                this.mRepeatButton.setVisibility(View.VISIBLE);
                break;
            case Sending:
                if (time > AppConstants.TIME_OUT && !CubeMessageType.isFileMessage(this.mData.mMessage.getMessageType())) {
                    this.mProgressBar.setVisibility(View.GONE);
                    this.mRepeatButton.setVisibility(View.VISIBLE);
                    this.mData.mMessage.setMessageStatus(CubeMessageStatus.Failed.getStatus());
                    // TODO: 2018/9/3 更新本地数据库
//                    CubeMessageRepository.getInstance().updateMessage(mData.mMessage);
                    return;
                }
                this.mProgressBar.setVisibility(View.VISIBLE);
                this.mRepeatButton.setVisibility(View.GONE);
                break;
            default:
                this.mProgressBar.setVisibility(View.GONE);
                this.mRepeatButton.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 设置是否分割线
     */
    private void setDivideLine() {
        try {
            boolean isShow = this.mAdapter.mListPanel.isShowDivideLine() && this.mPosition == 0;
            if (isShow) {
                this.mChatDivideLineLayout.setVisibility(View.VISIBLE);
            }
            else {
                this.mChatDivideLineLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.mChatDivideLineLayout.setVisibility(View.GONE);
        }
    }

    private void setChatDateByTimeStamp(int currentPos) {
        if (currentPos > 0) {
            CubeMessageViewModel cubeMessageViewModel = mAdapter.getDataList().get(mPosition - 1);
            if (cubeMessageViewModel != null) {
                long lastMessageTimestamp = cubeMessageViewModel.mMessage.getTimestamp();
                long thisMessageTimestamp = mData.mMessage.getTimestamp();
                if (thisMessageTimestamp - lastMessageTimestamp > MessageManager.SHOW_TIME_PERIOD) {
                    mData.mMessage.setShowTime(true);
                }
                else {
                    mData.mMessage.setShowTime(false);
                }
            }
            else {
                mData.mMessage.setShowTime(true);
            }
        }
        else {
            //position是0说明是页头 没有上一条和他比较时间 等待下拉刷新后改变状态
            mData.mMessage.setShowTime(true);
        }
    }

    /**
     * 设置时间显示
     */
    public void setChatDate() {
        if (this.mData.mMessage.isShowTime()) {
            mChatDate.setVisibility(View.VISIBLE);
        }
        else {
            mChatDate.setVisibility(View.GONE);
            return;
        }
        String text = DateUtil.getTimeShowString(mData.mMessage.getTimestamp());
        mChatDate.setText(text);
    }

    public void startSecretTime(final int time) {
//        CubeMessageRepository.getInstance().updateMessageAnonymousTime(mData.mMessage, time).compose(RxSchedulers.<CubeMessage>io_main()).subscribe(new Action1<CubeMessage>() {
//            @Override
//            public void call(CubeMessage cubeMessage) {
//                mSecretTime.initTime(time);
//                mSecretTime.setVisibility(View.VISIBLE);
//                new SecretTask(time, mData.mMessage);
//                RxBus.getInstance().post(CubeEvent.EVENT_REFRESH_RECENT_SECRET_SESSION_SINGLE, mData.mMessage.getChatId());
//                RxBus.getInstance().post(CubeEvent.EVENT_REFRESH_RECENT_SESSION_SINGLE, "secret_chat");
//            }
//        });
    }

    /**
     * 获取匿名用户头像
     *
     * @return
     */
    private int getSecretFace() {
        int userFace[] = new int[] { R.drawable.ic_chat_secret_face_1, R.drawable.ic_chat_secret_face_2, R.drawable.ic_chat_secret_face_3, R.drawable.ic_chat_secret_face_4, R.drawable.ic_chat_secret_face_5, R.drawable.ic_chat_secret_face_6 };
        int number = new Random().nextInt(6);
        return userFace[number];
    }

    /**
     * 匿名消息回执
     */
    public void receipt() {
        if (!mData.mMessage.isReceipt && mData.mMessage.isAnonymous()) {
            List<Long> list = new ArrayList<>(1);
            list.add(mData.mMessage.getMessageSN());
            //CubeEngine.getInstance().getMessageService().receiptMessages(list);
        }
    }

    public OnItemSelectedListener mOnItemSelectedListener;

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    public abstract void onDestroy();

    @Override
    public void onEvent(String text, CubeMessage cubeMessage) {
        mAdapter.onEvent(text, cubeMessage);
    }

    /**
     * item选中状态监听器
     */
    public interface OnItemSelectedListener {
        void onItemSelected(String selectedCube);

        void onItemUnselected(String selectedCube);

        void onSelectedList(Map<String, CubeMessage> list);
    }

}
