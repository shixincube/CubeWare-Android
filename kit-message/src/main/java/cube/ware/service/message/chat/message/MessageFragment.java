package cube.ware.service.message.chat.message;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.common.mvp.rx.RxManager;
import com.common.utils.utils.log.LogUtil;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.eventbus.CubeEvent;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.ChatContainer;
import cube.ware.service.message.chat.ChatCustomization;
import cube.ware.service.message.chat.panel.input.InputPanel;
import cube.ware.service.message.chat.panel.input.InputPanelProxy;
import cube.ware.service.message.chat.panel.input.MessageEditWatcher;
import cube.ware.service.message.chat.panel.input.function.AudioFunction;
import cube.ware.service.message.chat.panel.input.function.BaseFunction;
import cube.ware.service.message.chat.panel.input.function.VideoFunction;
import cube.ware.service.message.chat.panel.input.function.WhiteboardFunction;
import cube.ware.service.message.chat.panel.messagelist.MessageListPanel;
import cube.ware.service.message.manager.MessageManager;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by dth
 * Des: 聊天页面fragment
 * Date: 2018/8/30.
 */

public class MessageFragment extends Fragment implements InputPanelProxy, MessageEditWatcher {
    private static final String  TAG            = MessageFragment.class.getSimpleName();
    private              boolean hasParseIntent = false;

    /**
     * fragment根布局
     */
    protected View mRootView;

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

    /**
     * 当前聊天界面是否第一次加载
     */
    protected boolean   isFist = true;
    private   Bundle    mBundle;
    private   int       containerId;  //fragment布局资源id
    private   RxManager mRxManager;

    public static final String EXTRA_CHAT_ID            = "chat_id";
    public static final String EXTRA_CHAT_NAME          = "chat_name";
    public static final String EXTRA_CHAT_TYPE          = "chat_type";
    public static final String EXTRA_CHAT_CUSTOMIZATION = "chat_customization";
    public static final String EXTRA_CHAT_MESSAGE       = "chat_message";

    public int getContainerId() {
        return containerId;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    /**
     * 实例化MessageFragment
     *
     * @param sessionType 聊天类型
     * @param arguments   聊天页面定制化信息
     *
     * @return
     */
    public static MessageFragment newInstance(CubeSessionType sessionType, Bundle arguments) {
        MessageFragment fragment = new MessageFragment();
        arguments.putSerializable(EXTRA_CHAT_TYPE, sessionType);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mBundle = savedInstanceState.getBundle("cube_saved_bundle");
            super.onViewStateRestored(savedInstanceState);
            this.parseIntent(mBundle);
        }
        else {
            super.onViewStateRestored(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle("cube_saved_bundle", mBundle);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = inflater.inflate(R.layout.cube_fragment_message, container, false);
        this.parseIntent(getArguments());
        return this.mRootView;
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
        if (isFist) {
            this.isFist = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isFist) {
            this.isFist = true;
        }
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
        mRxManager.clear();
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
    private void parseIntent(Bundle bundle) {
        if (hasParseIntent) {
            return;
        }
        mRxManager = new RxManager();
        hasParseIntent = true;
        this.mChatId = bundle.getString(EXTRA_CHAT_ID);
        this.mChatName = bundle.getString(EXTRA_CHAT_NAME);
        this.mSessionType = (CubeSessionType) bundle.getSerializable(EXTRA_CHAT_TYPE);
        this.mChatCustomization = (ChatCustomization) bundle.getSerializable(EXTRA_CHAT_CUSTOMIZATION);
        this.mChatMessageSn = bundle.getLong(EXTRA_CHAT_MESSAGE, -1);
        ChatContainer container = new ChatContainer(getActivity(), this.mChatId, this.mChatName, this.mSessionType, this, mRxManager);

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

        mRxManager.on(CubeEvent.EVENT_SYNCING_MESSAGE, new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (!(o instanceof List)) {
                    return;
                }
                LogUtil.i(TAG, "receive message sync and updateMessageIsRead");
                final List<CubeMessage> list = (List<CubeMessage>) o;
                mMessageListPanel.onMessageSync(list);
            }
        });

        mRxManager.on(CubeEvent.EVENT_REMOVE_RECENT_SESSION_SINGLE, new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o == null) {
                    return;
                }
                getActivity().finish();
            }
        });

        mRxManager.on(CubeEvent.EVENT_UPDATE_GROUP, new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o == null) {
                    return;
                }
                mMessageListPanel.setTitleName((String) o);
            }
        });
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
     *
     * @return
     */
    @Override
    public boolean onMessageSend(CubeMessage cubeMessage) {
        if (!isAllowSendMessage(cubeMessage)) {
            return false;
        }
        // send message to server and save to db
        return mMessageListPanel.onMessageSend(cubeMessage);
    }

    /**
     * 接收消息回调方法
     *
     * @param cubeMessage
     *
     * @return
     */
    @Override
    public boolean onMessagePersisted(CubeMessage cubeMessage) {
        LogUtil.i(TAG, "onMessagePersisted message=" + cubeMessage.getMessageSN());
        return mMessageListPanel.addMessage(cubeMessage);
    }

    private boolean isShowSetMessageIsRead(CubeMessage cubeMessage) {
        if (cubeMessage == null) {
            return false;
        }
        return isDisplaying && null != cubeMessage && cubeMessage.getChatId() != null && cubeMessage.getChatId().equals(this.mChatId) && mSessionType != CubeSessionType.Secret;
    }

    /**
     * 更新消息回调方法
     *
     * @param cubeMessage
     *
     * @return
     */
    @Override
    public boolean onMessageInLocalUpdated(CubeMessage cubeMessage) {
        return mMessageListPanel.updateMessage(cubeMessage);
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
        ChatContainer container = new ChatContainer(getActivity(), this.mChatId, this.mChatName, this.mSessionType, this, mRxManager);
        List<BaseFunction> functionViewList = new ArrayList<>();
        functionViewList.add(new AudioFunction(mSessionType, container));
        functionViewList.add(new VideoFunction(mSessionType));
        functionViewList.add(new WhiteboardFunction(mSessionType));
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
