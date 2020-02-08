package cube.ware.service.message.chat.fragment;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Editable;
import com.common.mvp.base.BaseFragment;
import com.common.mvp.base.BasePresenter;
import com.common.mvp.eventbus.Event;
import com.common.utils.utils.log.LogUtil;
import cube.ware.common.MessageConstants;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.ChatContainer;
import cube.ware.service.message.chat.activity.base.ChatCustomization;
import cube.ware.service.message.chat.panel.input.InputPanel;
import cube.ware.service.message.chat.panel.input.InputPanelProxy;
import cube.ware.service.message.chat.panel.input.MessageEditWatcher;
import cube.ware.service.message.chat.panel.input.function.AudioFunction;
import cube.ware.service.message.chat.panel.input.function.BaseFunction;
import cube.ware.service.message.chat.panel.input.function.VideoFunction;
import cube.ware.service.message.chat.panel.messagelist.MessageListPanel;
import cube.ware.service.message.manager.MessageManager;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天页面fragment
 *
 * @author LiuFeng
 * @data 2020/2/8 13:38
 */
public class MessageFragment extends BaseFragment implements InputPanelProxy, MessageEditWatcher {

    private static final String TAG = MessageFragment.class.getSimpleName();

    /**
     * 聊天id
     */
    protected String mChatId;

    /**
     * 聊天名称
     */
    protected String mChatName;

    /**
     * 指定聊天消息
     */
    protected long mChatMessageSn = -1;

    /**
     * 聊天类型，默认：单聊
     */
    protected CubeSessionType mSessionType = CubeSessionType.P2P;

    /**
     * 聊天页面定制化信息
     */
    protected ChatCustomization mChatCustomization;

    /**
     * 聊天底部输入面板
     */
    protected InputPanel mInputPanel;

    /**
     * 消息列表面板
     */
    protected MessageListPanel mMessageListPanel;

    /**
     * 聊天底部输入面板的功能导航监听器
     */
    protected InputPanel.OnBottomNavigationListener mOnBottomNavigationListener;

    /**
     * 当前聊天界面是否显示
     */
    protected boolean isDisplaying;

    public static final String CHAT_ID            = "chat_id";
    public static final String CHAT_NAME          = "chat_name";
    public static final String CHAT_TYPE          = "chat_type";
    public static final String CHAT_CUSTOMIZATION = "chat_customization";
    public static final String CHAT_MESSAGE       = "chat_message";

    /**
     * 实例化MessageFragment
     *
     * @param chatId
     * @param chatName
     * @param sessionType
     * @param chatCustomization
     *
     * @return
     */
    public static MessageFragment newInstance(String chatId, String chatName, CubeSessionType sessionType, ChatCustomization chatCustomization) {
        MessageFragment fragment = new MessageFragment();
        Bundle arguments = new Bundle();
        arguments.putString(CHAT_ID, chatId);
        arguments.putString(CHAT_NAME, chatName);
        arguments.putSerializable(CHAT_TYPE, sessionType);
        arguments.putSerializable(CHAT_CUSTOMIZATION, chatCustomization);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.cube_fragment_message;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        this.parseArguments();
        ChatContainer container = new ChatContainer(getActivity(), this.mChatId, this.mChatName, this.mSessionType, this);

        //必须给消息管理器设置聊天容器
        MessageManager.getInstance().addContainer(this, container);
        if (this.mMessageListPanel == null) {
            this.mMessageListPanel = new MessageListPanel(container, mChatCustomization, mRootView, mChatMessageSn);
        }
        else {
            this.mMessageListPanel.reload(container, mChatCustomization, mChatMessageSn);
        }

        if (this.mInputPanel == null) {
            this.mInputPanel = new InputPanel(mMessageListPanel, CubeCore.getInstance().getCubeId(), container, container.mSessionType, mRootView, this.buildFunctionViewList());
            this.mInputPanel.setChatCustomization(this.mChatCustomization, false);
        }
        else {
            this.mInputPanel.reload(container, this.mChatCustomization);
        }

        if (this.mInputPanel != null && this.mOnBottomNavigationListener != null) {
            this.mInputPanel.setOnBottomNavigationListener(this.mOnBottomNavigationListener);
        }

        // 设置定制化聊天背景
        if (this.mChatCustomization != null) {
            this.mMessageListPanel.setChattingBackground(this.mChatCustomization.backgroundUri, this.mChatCustomization.backgroundColor);
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    public void onReceiveEvent(Event event) {
        mMessageListPanel.handleReceiveEvent(event);
        switch (event.eventName) {
            case CubeConstants.Event.UPDATE_GROUP:
                mMessageListPanel.setTitleName((String) event.data);
                break;

            case MessageConstants.Event.EVENT_SYNCING_MESSAGE:
                final List<CubeMessage> list = (List<CubeMessage>) event.data;
                mMessageListPanel.onMessageSync(list);
                break;

            case MessageConstants.Event.EVENT_REMOVE_RECENT_SESSION_SINGLE:
                getActivity().finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setMessageReadStatus();
        this.isDisplaying = true;
        this.mInputPanel.onResume();
        this.mMessageListPanel.onResume();
        getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);   // 默认使用听筒播放
        addWritingListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mInputPanel.onPause();
        this.mMessageListPanel.onPause();
        removeWritingListener();
    }

    private void setMessageReadStatus() {//每次onPause时将当前会话消息置为已读 处理某些情况(网络不好等)消息未回执导致最近未读消息数未更新

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isDisplaying = false;
        MessageManager.getInstance().onDestroy(this);
        this.mMessageListPanel.onDestroy();
        this.mInputPanel.onDestroy();
    }

    public boolean onBackPressed() {
        if (this.mInputPanel.collapse(true)) {
            return true;
        }

        return false;
    }

    /**
     * 初始化聊天控制面板
     */
    private void parseArguments() {
        Bundle data = getArguments();
        this.mChatId = data.getString(CHAT_ID);
        this.mChatName = data.getString(CHAT_NAME);
        this.mSessionType = (CubeSessionType) data.getSerializable(CHAT_TYPE);
        this.mChatCustomization = (ChatCustomization) data.getSerializable(CHAT_CUSTOMIZATION);
        this.mChatMessageSn = data.getLong(CHAT_MESSAGE, -1);
    }

    /**
     * 是否允许发送消息
     *
     * @param message
     *
     * @return
     */
    public boolean isAllowSendMessage(CubeMessage message) {
        return true;
    }

    /**
     * 发送消息回调方法
     *
     * @param cubeMessage
     */
    @Override
    public void onMessageSend(CubeMessage cubeMessage) {
        if (!isAllowSendMessage(cubeMessage)) {
            return;
        }
        // send message to server and save to db
        mMessageListPanel.onMessageSend(cubeMessage);
    }

    /**
     * 接收消息回调方法
     *
     * @param cubeMessage
     */
    @Override
    public void onMessagePersisted(CubeMessage cubeMessage) {
        LogUtil.i(TAG, "onMessagePersisted message=" + cubeMessage.getMessageSN());
        mMessageListPanel.addMessage(cubeMessage);
    }

    private boolean isShowSetMessageIsRead(CubeMessage cubeMessage) {
        if (cubeMessage == null) {
            return false;
        }
        return isDisplaying && cubeMessage.getChatId() != null && cubeMessage.getChatId().equals(this.mChatId) && mSessionType != CubeSessionType.Secret;
    }

    /**
     * 更新消息回调方法
     *
     * @param cubeMessage
     */
    @Override
    public void onMessageInLocalUpdated(CubeMessage cubeMessage) {
        mMessageListPanel.updateMessage(cubeMessage);
    }

    @Override
    public void deleteMessage() {
        mMessageListPanel.deleteMessageList();
    }

    /**
     * 输入面已板展开回调方法
     */
    @Override
    public void inputPanelExpanded() {
        mRootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMessageListPanel.scrollToBottom();
            }
        }, 200);
    }

    /**
     * 收缩输入面板回调方法
     */
    @Override
    public void collapseInputPanel() {
        this.mInputPanel.collapse(false);
    }

    /**
     * 是否正在录音回调方法
     *
     * @return
     */
    @Override
    public boolean isLongClickEnabled() {
        return false;
    }

    @Override
    public void onReplyMessage(CubeMessage cubeMessage) {
        mInputPanel.onReplyMessage(cubeMessage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.mInputPanel.onActivityResult(requestCode, resultCode, data);
        //this.mMessageListPanel.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 构建表情布局更多展开后的功能组件列表
     *
     * @return
     */
    protected List<BaseFunction> buildFunctionViewList() {
        List<BaseFunction> functionViewList = new ArrayList<>();
        functionViewList.add(new AudioFunction());
        functionViewList.add(new VideoFunction());
        if (this.mChatCustomization != null && this.mChatCustomization.functionViewList != null) {
            functionViewList.addAll(this.mChatCustomization.functionViewList);
        }
        return functionViewList;
    }

    /**
     * 设置底部面板功能栏监听器
     *
     * @param listener
     */
    public void setBottomNavigationListener(InputPanel.OnBottomNavigationListener listener) {
        this.mOnBottomNavigationListener = listener;
    }

    public void longClickAtMember(String member) {
        if (this.mInputPanel != null) {
            this.mInputPanel.longClickAtMember(member);
        }
    }

    @Override
    public void afterTextChanged(Editable s, int start, int before, int count) {
    }

    private void addWritingListener() {
        if (mInputPanel != null) {
            mInputPanel.addWatcher(this);
        }
    }

    private void removeWritingListener() {
        if (mInputPanel != null) {
            mInputPanel.removeWatcher(this);
        }
    }
}
