package cube.ware.service.message.chat.panel.input;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.common.mvp.rx.RxPermissionUtil;
import com.common.mvp.rx.RxSchedulers;
import com.common.utils.utils.KeyBoardUtil;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.message.Receiver;
import cube.service.message.TextMessage;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.ChatContainer;
import cube.ware.service.message.chat.activity.base.BaseChatActivity;
import cube.ware.service.message.chat.activity.base.ChatCustomization;
import cube.ware.service.message.chat.activity.base.ChatStatusType;
import cube.ware.service.message.chat.helper.AtHelper;
import cube.ware.service.message.chat.panel.input.emoticon.EmoticonSelectedListener;
import cube.ware.service.message.chat.panel.input.emoticon.model.StickerItem;
import cube.ware.service.message.chat.panel.input.emoticon.widget.EmoticonPickerView;
import cube.ware.service.message.chat.panel.input.function.BaseFunction;
import cube.ware.service.message.chat.panel.input.function.FunctionPanel;
import cube.ware.service.message.chat.panel.input.voicefragment.RecordFragment;
import cube.ware.service.message.chat.panel.messagelist.MessageListPanel;
import cube.ware.service.message.manager.MessageManager;
import cube.ware.service.message.manager.MessageJudge;
import cube.ware.utils.SpUtil;
import cube.ware.utils.StringUtil;
import cube.ware.widget.CubeEmoticonEditText;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.view.View.GONE;

/**
 * 底部输入面板
 *
 * @author Wangxx
 * @date 2017/1/3
 */
public class InputPanel implements EmoticonSelectedListener, View.OnClickListener {
    public static final String TAG = InputPanel.class.getSimpleName();

    //用于在EditText中的ImageSpan中识别出回复消息添加的ImageSpan System.currentTimeMillis() % 9为标识增加一点随机性
    public static final String REPLY_MARK = "r1p2y" + System.currentTimeMillis() % 9;
    public static final String REPLY_END  = "\r";

    private ChatContainer        mChatContainer;    // 聊天容器
    private CubeSessionType      mChatType;         // 聊天类型
    private List<BaseFunction>   mFunctionViewList; // 表情更多功能组件列表
    private ChatCustomization    mCustomization;    // 聊天页面定制化参数
    private String               mCubeId;
    private MessageListPanel     mListPanel;
    private View                 rootView;
    private LinearLayout         mChatBottomLayout;      // 聊天页面底部布局
    private View                 mFunctionMoreLayout;    // 聊天页面更多布局
    private FrameLayout          mVoiceLayout;           // 聊天页面语音布局
    private CubeEmoticonEditText mChatMessageEt;         // 聊天消息输入框
    private Button               mChatSendBtn;           // 聊天发送按钮
    private ImageView            mChatImageBtn;          // 聊天选择发送图片按钮
    private ImageView            mChatCameraBtn;         // 聊天选中发送拍照按钮
    private ImageView            mChatFileBtn;           // 聊天选择发送文件按钮
    private ImageView            mChatVoiceBtn;          // 聊天选择发送语音按钮
    private ImageView            mChatEmojiBtn;          // 聊天选择发送表情按钮  在线客服用
    private ImageView            mChatFaceBtn;           // 聊天选择发送表情按钮
    private ImageView            mChatMoreBtn;           // 聊天更多功能按钮
    //针对服务号的布局
    private Button               chat_send_btn;          //发送按钮
    private ImageView            chat_more_btn;          //更多按钮
    private ImageView            chat_numes_btn;         //底部菜单切换按钮
    private ImageView            chat_servic_face_btn;   //底部表情按钮
    private CubeEmoticonEditText chat_message_service_et;//底部输入面板
    private LinearLayout         service_muns_check_ture;
    private LinearLayout         service_muns_check_false;//底部布局两种切换模式
    private int                  switching_times = 1;

    private int type;  //1 是服务号  0是正常聊天

    private boolean isShowView = true;

    private boolean isMessageSearch = false;

    private EmoticonPickerView mEmoticonPickerView;  // 贴图表情控件

    private boolean mHasFunctionPanelLayout;    // 是否已设置更多功能操作面板
    private boolean mHasVoiceLayout;            // 是否已经设置语音功能面板
    private boolean mIsKeyboardShowed;          // 是否显示键盘

    private OnBottomNavigationListener mOnBottomNavigationListener;     // 底部导航栏监听器
    private List<MessageEditWatcher>   mEditWatchers = new ArrayList<MessageEditWatcher>();    // 文本输入框监听

    private CubeMessage mCubeMessage;  //回复的消息

    // keyboard常量
    private static final int SHOW_LAYOUT_DELAY         = 200;
    private static final int SHOW_EMOTICON_LAYOUT      = 1001;
    private static final int SHOW_KEYBOARD             = 1002;
    private static final int SHOW_VOICE_LAYOUT         = 1003;
    private static final int SHOW_MORE_FUNCTION_LAYOUT = 1004;
    private static final int HIDE_ALL_LAYOUT           = 1005;

    // 底部输入面板导航栏常量
    private static final int NAVIGATION_TYPE_CAMERA = 1010;
    private static final int NAVIGATION_TYPE_FILE   = 1011;
    private static final int NAVIGATION_TYPE_IMAGE  = 1012;
    private static final int NAVIGATION_TYPE_VOICE  = 1013;

    // TODO: 2017/9/9 这种先让表情View消失 在让语音View显示的方法很容易在快速切换时造成闪屏
    //优化方法考虑 inputPanel携带的View都用Fragment进行替换 2 用一个View撑住 切换的时候只是表层View消失 用来撑住的View不消失
    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_EMOTICON_LAYOUT:
                    if (mCubeId.equals("s10001")) {
                        mChatEmojiBtn.setSelected(true);
                    }
                    else {
                        mChatFaceBtn.setSelected(true);
                    }
                    mEmoticonPickerView.setVisibility(View.VISIBLE);
                    break;
                case SHOW_KEYBOARD:
                    showKeyboard();
                    break;
                case SHOW_MORE_FUNCTION_LAYOUT:
                    mChatMoreBtn.setSelected(true);
                    mFunctionMoreLayout.setVisibility(View.VISIBLE);
                    break;
                case SHOW_VOICE_LAYOUT:
                    mChatVoiceBtn.setSelected(true);
                    mVoiceLayout.setVisibility(View.VISIBLE);
                    break;
                case HIDE_ALL_LAYOUT:
                    hideKeyboard();
                    hideFunctionMoreLayout();
                    hideEmoticonLayout();
                    hideVoiceLayout();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private int drawableHeight;

    /**
     * 构造方法
     *
     * @param chatContainer    聊天容器
     * @param functionViewList 表情更多功能组件列表
     */
    public InputPanel(MessageListPanel listPanel, String cubeId, ChatContainer chatContainer, CubeSessionType chatType, View view, List<BaseFunction> functionViewList) {
        mListPanel = listPanel;
        this.mCubeId = cubeId;
        this.mChatContainer = chatContainer;
        this.mChatType = chatType;
        this.rootView = view;
        this.mFunctionViewList = functionViewList;
    }

    public void setIsMessageSearch(boolean isMessageSearch) {
        this.isMessageSearch = isMessageSearch;
    }

    /**
     * 同步消息fragment的onResume生命周期
     */
    public void onResume() {
    }

    /**
     * 同步消息fragment的onPause生命周期
     */
    public void onPause() {
        this.saveDraft(this.mChatContainer.mChatId);
    }

    /**
     * 同步消息fragment的onDestroy生命周期
     */
    public void onDestroy() {
        mEmoticonPickerView.onDestroy();
    }

    /**
     * 初始化
     */
    private void init() {
        this.initViews();
        this.initListener();
        this.initMessageEditText();

        if (this.mFunctionViewList != null && !this.mFunctionViewList.isEmpty()) {
            for (int i = 0; i < this.mFunctionViewList.size(); ++i) {
                this.mFunctionViewList.get(i).setIndex(i);
                this.mFunctionViewList.get(i).setChatContainer(this.mChatContainer);
                this.mFunctionViewList.get(i).setInputPanel(this);
            }
        }
    }

    /**
     * 设置聊天页面定制化参数
     *
     * @param customization
     */
    public void setChatCustomization(ChatCustomization customization, boolean isReload) {
        this.mCustomization = customization;
        if (!isReload) {
            this.init();
        }
        if (customization != null) {
            this.mEmoticonPickerView.setHasSticker(customization.hasCustomSticker);
        }
    }

    /**
     * 重新加载
     *
     * @param container
     * @param customization
     */
    public void reload(ChatContainer container, ChatCustomization customization) {
        this.mChatContainer = container;
        this.setChatCustomization(customization, true);
    }

    /**
     * 初始化组件
     */
    private void initViews() {
        this.mChatBottomLayout = (LinearLayout) rootView.findViewById(R.id.chat_bottom_layout);
        mChatBottomLayout.setVisibility(isShowView ? View.VISIBLE : GONE);
        this.mChatMessageEt = (CubeEmoticonEditText) rootView.findViewById(R.id.chat_message_et);
        this.mChatSendBtn = (Button) rootView.findViewById(R.id.chat_send_btn);
        this.mChatImageBtn = (ImageView) rootView.findViewById(R.id.chat_image_btn);
        this.mChatCameraBtn = (ImageView) rootView.findViewById(R.id.chat_camera_btn);
        this.mChatFileBtn = (ImageView) rootView.findViewById(R.id.chat_file_btn);
        this.mChatFaceBtn = (ImageView) rootView.findViewById(R.id.chat_face_btn);
        this.mChatEmojiBtn = (ImageView) rootView.findViewById(R.id.chat_emoji_btn);
        this.mChatVoiceBtn = (ImageView) rootView.findViewById(R.id.chat_voice_btn);
        this.mChatMoreBtn = (ImageView) rootView.findViewById(R.id.chat_more_btn);
        this.mVoiceLayout = (FrameLayout) rootView.findViewById(R.id.voice_view);

        //针对服务号布局
        this.chat_send_btn = rootView.findViewById(R.id.chat_service_send_btn);
        this.chat_more_btn = rootView.findViewById(R.id.chat_service_more_btn);
        this.chat_numes_btn = rootView.findViewById(R.id.chat_numes_btn);
        this.chat_servic_face_btn = rootView.findViewById(R.id.chat_service_face_btn);
        this.chat_message_service_et = rootView.findViewById(R.id.chat_message_service_et);
        this.service_muns_check_false = rootView.findViewById(R.id.service_muns_check_false);
        this.service_muns_check_ture = rootView.findViewById(R.id.service_muns_check_ture);

        this.refreshStatus(mCustomization.typ);
        if (MessageJudge.isAssistantSession(mCubeId)) {
            this.mChatMoreBtn.setVisibility(View.GONE);
        }

        if (mCubeId.equals("s10001")) {
            this.mChatVoiceBtn.setVisibility(View.GONE);
            this.mChatCameraBtn.setVisibility(View.GONE);
            this.mChatFileBtn.setVisibility(View.GONE);
            this.mChatFaceBtn.setVisibility(GONE);
            this.mChatMoreBtn.setVisibility(View.GONE);
            this.mChatEmojiBtn.setVisibility(View.VISIBLE);
        }

        // 表情
        this.mEmoticonPickerView = (EmoticonPickerView) rootView.findViewById(R.id.emoticon_picker_view);

        if (isMessageSearch) {
            this.mChatBottomLayout.setVisibility(GONE);
        }
    }

    /**
     * 根据聊天类型加载不同的聊天键盘
     *
     * @param chatStatusType
     */
    /**
     * None(-1),              // 未知
     * NonRegistration(0),    // 非注册单聊
     * Normal(1),             // 正常单聊
     * NotFriend(2),          // 非好友单聊
     * Group(3),              // 群聊
     * Anonymous(4);            // 好友私密聊天
     */
    private void refreshStatus(ChatStatusType chatStatusType) {
        switch (chatStatusType.getType()) {
            case 0:
                this.mChatMoreBtn.setVisibility(View.GONE);
                this.mChatFileBtn.setVisibility(View.VISIBLE);
                break;
            case 1:
            case 2:
            case 3:
                this.mChatMoreBtn.setVisibility(View.VISIBLE);
                this.mChatFileBtn.setVisibility(View.VISIBLE);
                break;
            case 4:
                this.mChatMoreBtn.setVisibility(View.GONE);
                this.mChatFileBtn.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        this.mChatSendBtn.setOnClickListener(this);
        this.mChatImageBtn.setOnClickListener(this);
        this.mChatCameraBtn.setOnClickListener(this);
        this.mChatFileBtn.setOnClickListener(this);
        this.mChatVoiceBtn.setOnClickListener(this);
        this.mChatFaceBtn.setOnClickListener(this);
        this.mChatEmojiBtn.setOnClickListener(this);
        this.mChatMoreBtn.setOnClickListener(this);
        this.chat_send_btn.setOnClickListener(this);
        this.chat_servic_face_btn.setOnClickListener(this);
        this.chat_more_btn.setOnClickListener(this);
        this.chat_numes_btn.setOnClickListener(this);
        chat_message_service_et.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    chat_send_btn.setVisibility(View.VISIBLE);
                    chat_more_btn.setVisibility(View.GONE);
                    return;
                }
                else {
                    chat_send_btn.setVisibility(View.GONE);
                    chat_more_btn.setVisibility(View.VISIBLE);
                    return;
                }
            }
        });
    }

    /**
     * 初始化消息输入框
     */
    private void initMessageEditText() {
        this.initDraft(this.mChatContainer.mChatId);
        this.mChatMessageEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        this.mChatMessageEt.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mChatSendBtn.setBackgroundResource(R.drawable.selector_chat_send_btn);
                float y = event.getY();
                if (y < drawableHeight + 15 && mChatMessageEt.isInReplyMode()) {
                    mListPanel.scrollToSn(mCubeMessage.getMessageSN());
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleKeyboard(true);
                }
                return false;
            }
        });
        this.mChatMessageEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mChatMessageEt.setHint(R.string.chat_message_edit_hint);
                checkSendButtonEnable(mChatMessageEt);
            }
        });
        checkSendButtonEnable(mChatMessageEt);
        if (this.mChatMessageEt.getText().toString().length() > 0) {
            this.mChatSendBtn.setBackgroundResource(R.drawable.shape_chat_send_enabled);
            this.mChatSendBtn.setEnabled(true);
        }
        this.mChatMessageEt.addTextChangedListener(new TextWatcher() {
            private int start;
            private int count;
            private int before;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                this.start = start;
                this.count = count;
                this.before = before;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.count = count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkSendButtonEnable(mChatMessageEt);
                if (mEditWatchers.size() > 0) {
                    for (int i = 0; i < mEditWatchers.size(); i++) {
                        mEditWatchers.get(i).afterTextChanged(s, start, before, count);
                    }
                }

                int editEnd = mChatMessageEt.length();
                mChatMessageEt.removeTextChangedListener(this);
                while (StringUtil.counterChars(s.toString()) > 5000 && editEnd > 0) {
                    s.delete(editEnd - 1, editEnd);
                    editEnd--;
                    mChatMessageEt.setSelection(editEnd);
                }
                mChatMessageEt.addTextChangedListener(this);
                String replyCotentSource = mChatMessageEt.getReplyContentSource();
                if (!TextUtils.isEmpty(replyCotentSource) && replyCotentSource.length() == mChatMessageEt.getText().length()) {
                    mChatMessageEt.getText().clear();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        //发送按钮
        if (v.getId() == R.id.chat_send_btn || v.getId() == R.id.chat_service_send_btn) {
            sendMessage(v.getId());
        }
        //点击照相
        else if (v.getId() == R.id.chat_camera_btn) {
            hideAllLayout(true);
            this.hasBottomNavigationListener(NAVIGATION_TYPE_CAMERA);
        }
        //发送文件
        else if (v.getId() == R.id.chat_file_btn) {
            hideAllLayout(true);
            RxPermissionUtil.requestStoragePermission(mChatContainer.mChatActivity).compose(RxSchedulers.<Boolean>io_main()).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if (aBoolean) {
                        hasBottomNavigationListener(NAVIGATION_TYPE_FILE);
                    }
                    else {
                        ToastUtil.showToast(mChatContainer.mChatActivity, 0, mChatContainer.mChatActivity.getString(R.string.request_storage_permission));
                    }
                }
            });
        }
        //发送图片按钮
        else if (v.getId() == R.id.chat_image_btn) {
            hideAllLayout(true);
            this.hasBottomNavigationListener(NAVIGATION_TYPE_IMAGE);
        }
        //录制语音按钮
        else if (v.getId() == R.id.chat_voice_btn) {
            if (CubeCore.getInstance().isCalling()) {
                hideEmoticonLayout();
                ToastUtil.showToast(mChatContainer.mChatActivity, 0, mChatContainer.mChatActivity.getString(R.string.calling_please_try_again_later));
                return;
            }
            RxPermissionUtil.requestRecordPermission(mChatContainer.mChatActivity).compose(RxSchedulers.<Boolean>io_main()).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if (aBoolean) {
                        toggleVoiceLayout();
                    }
                    else {
                        ToastUtil.showToast(mChatContainer.mChatActivity, 0, mChatContainer.mChatActivity.getString(R.string.request_record_permission));
                    }
                }
            });
        }
        //聊天表情按钮
        else if (v.getId() == R.id.chat_face_btn || v.getId() == R.id.chat_service_face_btn) {
            this.toggleEmoticonLayout();
        }
        //点击加号 更多
        else if (v.getId() == R.id.chat_more_btn || v.getId() == R.id.chat_service_more_btn) {
            hideEmoticonLayout();
            this.toggleFunctionMoreLayout();
        }
        else if (v.getId() == R.id.chat_numes_btn) {
            hideEmoticonLayout();
            switching_times += 1;
            if (switching_times % 2 == 0) {
                service_muns_check_false.setVisibility(View.GONE);
                service_muns_check_ture.setVisibility(View.VISIBLE);
            }
            else {
                service_muns_check_false.setVisibility(View.VISIBLE);
                service_muns_check_ture.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 执行发送按钮事件
     */
    private void sendMessage(int type) {
        String text = this.mChatMessageEt.getText().toString();
        boolean isSecret = mChatContainer.mSessionType == CubeSessionType.Secret;
        if (mChatMessageEt.isInReplyMode()) {
            //// TODO: 2018/1/31 目前移动端只需要能回复文本消息即可
            String replyContentSource = mChatMessageEt.getReplyContentSource();
            text = text.substring(replyContentSource.length() + 1, text.length());
            TextMessage textMessage = MessageManager.getInstance().buildTextMessage(mChatType, CubeCore.getInstance().getCubeId(), mChatContainer.mChatId, mChatContainer.mChatName, text, isSecret);
            MessageManager.getInstance().replyMessage(mCubeMessage, textMessage);
            mChatMessageEt.getText().clear();
            mListPanel.scrollToBottom();
        }
        else {
            final TextMessage textMessage = MessageManager.getInstance().buildTextMessage(mChatType, CubeCore.getInstance().getCubeId(), mChatContainer.mChatId, mChatContainer.mChatName, text, isSecret);
            MessageManager.getInstance().sendMessage(mChatContainer.mChatActivity, textMessage).compose(RxSchedulers.<Boolean>io_main()).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    restoreText(aBoolean);
                }
            });
        }
    }

    /**
     * 显示发送按钮可用
     *
     * @param editText
     */
    private void checkSendButtonEnable(EditText editText) {
        String textMessage = editText.getText().toString();
        if (mChatMessageEt.isInReplyMode()) {
            String replyContentSource = mChatMessageEt.getReplyContentSource();
            int replyContentLength = replyContentSource.length();
            if (replyContentLength == textMessage.length() || TextUtils.equals(textMessage, replyContentSource + REPLY_END)) {
                this.mChatSendBtn.setEnabled(false);
            }
            else {
                innercheckSendButtonEnable(textMessage);
            }
        }
        else {
            innercheckSendButtonEnable(textMessage);
        }
    }

    private void innercheckSendButtonEnable(String textMessage) {
        if (!TextUtils.isEmpty(textMessage)) {
            this.mChatSendBtn.setEnabled(true);
        }
        else {
            this.mChatSendBtn.setEnabled(false);
        }
    }

    /**
     * 清空文本框内容
     *
     * @param clearText
     */
    private void restoreText(boolean clearText) {
        if (clearText) {
            this.mChatMessageEt.setText("");
            this.chat_message_service_et.setText("");
        }
    }

    /**
     * 添加更多功能操作面板
     */
    private void addFunctionPanelLayout() {//布局是写死的么 对  但是里边的数据是动态添加的  就是个  GridView
        if (this.mFunctionMoreLayout == null) {
            View.inflate(this.mChatContainer.mChatActivity, R.layout.cube_more_function_layout, this.mChatBottomLayout);
            this.mFunctionMoreLayout = mChatBottomLayout.findViewById(R.id.functions_layout);
            this.mHasFunctionPanelLayout = false;
        }
        this.initFunctionPanelLayout();
    }

    /**
     * 初始化具体的更多功能操作面板
     */
    private void initFunctionPanelLayout() {
        if (this.mHasFunctionPanelLayout) {
            return;
        }
        FunctionPanel.init((BaseChatActivity) this.mChatContainer.mChatActivity, this.mFunctionViewList);
        this.mHasFunctionPanelLayout = true;
    }

    /**
     * 切换更多功能布局
     */
    private void toggleFunctionMoreLayout() {
        if (this.mFunctionMoreLayout == null || this.mFunctionMoreLayout.getVisibility() == GONE) {
            this.showFunctionMoreLayout();
        }
        else {
            this.hideFunctionMoreLayout();
        }
    }

    /**
     * 隐藏更多功能布局
     */
    private void hideFunctionMoreLayout() {
        this.mUIHandler.removeMessages(SHOW_MORE_FUNCTION_LAYOUT);
        if (this.mFunctionMoreLayout != null) {
            this.mChatMoreBtn.setSelected(false);
            this.mFunctionMoreLayout.setVisibility(GONE);
        }
    }

    /**
     * 显示更多功能布局
     */
    private void showFunctionMoreLayout() {
        if (mIsKeyboardShowed) {
            this.mUIHandler.sendEmptyMessageDelayed(SHOW_MORE_FUNCTION_LAYOUT, SHOW_LAYOUT_DELAY);
        }
        else {
            this.mUIHandler.sendEmptyMessage(SHOW_MORE_FUNCTION_LAYOUT);
        }
        this.addFunctionPanelLayout(); // 加载更多布局
        this.hideVoiceLayout();
        this.hideEmoticonLayout();
        this.hideKeyboard();
        this.mChatContainer.mPanelProxy.inputPanelExpanded();
    }

    /**
     * 切换语音功能布局
     */
    private void toggleVoiceLayout() {
        BaseChatActivity activity = (BaseChatActivity) mChatContainer.mChatActivity;
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.voice_view, new RecordFragment(mChatContainer, activity)).commit();
        if (this.mVoiceLayout == null || this.mVoiceLayout.getVisibility() == GONE) {
            this.showVoiceLayout();
        }
        else {
            this.hideVoiceLayout();
        }
    }

    /**
     * 隐藏语音布局
     */
    private void hideVoiceLayout() {
        this.mUIHandler.removeMessages(SHOW_VOICE_LAYOUT);
        if (this.mVoiceLayout != null) {
            this.mChatVoiceBtn.setSelected(false);
            this.mVoiceLayout.setVisibility(GONE);
        }
    }

    /**
     * 显示语音布局
     */
    private void showVoiceLayout() {
        if (mIsKeyboardShowed) {
            this.mUIHandler.sendEmptyMessageDelayed(SHOW_VOICE_LAYOUT, SHOW_LAYOUT_DELAY);
        }
        else {
            this.mUIHandler.sendEmptyMessage(SHOW_VOICE_LAYOUT);
        }
        this.mChatMessageEt.requestFocus();
        this.hideEmoticonLayout();
        this.hideFunctionMoreLayout();
        this.hideKeyboard();
        this.mChatContainer.mPanelProxy.inputPanelExpanded();
    }

    /**
     * 切换表情布局
     */
    private void toggleEmoticonLayout() {
        if (this.mEmoticonPickerView == null || this.mEmoticonPickerView.getVisibility() == GONE) {
            this.showEmoticonLayout();
        }
        else {
            this.hideEmoticonLayout();
        }
    }

    /**
     * 隐藏表情布局
     */
    private void hideEmoticonLayout() {
        //因为有可能handle里可能有一个runnable SHOW_LAYOUT_DELAY后会打开Emotion布局 所以先移除runnable 在隐藏布局
        this.mUIHandler.removeMessages(SHOW_EMOTICON_LAYOUT);
        if (this.mEmoticonPickerView != null) {
            if (mCubeId.equals("s10001")) {
                this.mChatEmojiBtn.setSelected(false);
            }
            else {
                this.mChatFaceBtn.setSelected(false);
            }
            this.mEmoticonPickerView.setVisibility(GONE);
        }
    }

    /**
     * 显示表情布局
     */
    private void showEmoticonLayout() {
        if (mIsKeyboardShowed) {
            //如果当前正在显示键盘则先把键盘隐藏
            this.mUIHandler.sendEmptyMessageDelayed(SHOW_EMOTICON_LAYOUT, SHOW_LAYOUT_DELAY);
        }
        else {
            this.mUIHandler.sendEmptyMessage(SHOW_EMOTICON_LAYOUT);
        }

        if (type == 0) {
            this.mChatMessageEt.requestFocus();
        }
        else {
            this.chat_message_service_et.requestFocus();
        }

        // 表情的view
        this.mEmoticonPickerView.show(this);
        this.hideVoiceLayout();
        this.hideFunctionMoreLayout();
        this.hideKeyboard();
        this.mChatContainer.mPanelProxy.inputPanelExpanded();
    }

    /**
     * 切换键盘显示或隐藏
     *
     * @param isShowKeyboard 是否需要显示键盘
     */
    private void toggleKeyboard(boolean isShowKeyboard) {
        this.mChatMessageEt.setVisibility(View.VISIBLE);
        if (isShowKeyboard) {
            this.mUIHandler.sendEmptyMessage(SHOW_KEYBOARD);
        }
        else {
            this.hideKeyboard();
        }
        this.hideEmoticonLayout();
        this.hideFunctionMoreLayout();
        this.hideVoiceLayout();
    }

    /**
     * 隐藏键盘布局
     */
    private void hideKeyboard() {
        this.mIsKeyboardShowed = false;
        this.mUIHandler.removeMessages(SHOW_KEYBOARD);
        KeyBoardUtil.closeSoftKeyboard(this.mChatContainer.mChatActivity, this.mChatMessageEt);
    }

    /**
     * 显示键盘布局
     */
    private void showKeyboard() {
        // 如果已经显示,则继续操作时不需要把光标定位到最后
        if (!this.mIsKeyboardShowed) {
            //messageEt.setSelection(messageEt.getText().length());
            this.mIsKeyboardShowed = true;
        }
        KeyBoardUtil.openKeyboard(mChatContainer.mChatActivity, mChatMessageEt);
        this.mChatContainer.mPanelProxy.inputPanelExpanded();
    }

    /**
     * 隐藏所有布局
     *
     * @param immediately 是否立刻隐藏
     */
    public void hideAllLayout(boolean immediately) {
        long delay = immediately ? 0 : ViewConfiguration.getDoubleTapTimeout();
        this.mUIHandler.sendEmptyMessageDelayed(HIDE_ALL_LAYOUT, delay);
    }

    /**
     * 收缩
     *
     * @param immediately 是否立刻收缩
     *
     * @return
     */
    public boolean collapse(boolean immediately) {
        boolean respond = (this.mFunctionMoreLayout != null && this.mFunctionMoreLayout.getVisibility() == View.VISIBLE);
        hideAllLayout(immediately);
        return respond;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 从@返回
        //        if (requestCode == REQUEST_CODE_AT) {
        //            insertAtMember(data);
        //            return;
        //        }

        int index = (requestCode << 16) >> 24;
        if (index != 0) {
            index--;
            if (index < 0 | index >= this.mFunctionViewList.size()) {
                LogUtil.d("request code out of actions' range");
                return;
            }
            BaseFunction action = this.mFunctionViewList.get(index);
            if (action != null) {
                action.onActivityResult(requestCode & 0xff, resultCode, data);
            }
        }
    }

    public void longClickAtMember(String member) {
        if (mChatContainer.mSessionType.equals(CubeSessionType.Group)) {
            atMember(Collections.singletonList(member), this.mChatMessageEt.getSelectionStart(), false);
        }
        else {
            if (mChatMessageEt != null) {
                mChatMessageEt.setText(mChatMessageEt.getText().append(member));
            }
        }
    }

    /**
     * @param atMemberData
     * @param selectionStart
     * @param hasAt
     *
     * @成员
     */
    private void atMember(final List<String> atMemberData, final int selectionStart, final boolean hasAt) {
        if (null != atMemberData && !atMemberData.isEmpty()) {
            this.mChatMessageEt.post(new Runnable() {
                @Override
                public void run() {
                    StringBuilder sb = new StringBuilder();
                    for (String selectedMember : atMemberData) {
                        sb.append(selectedMember);
                    }
                    LogUtil.i("fldy", " selectionStart:" + selectionStart);
                    if (hasAt) {
                        if (selectionStart > 0) {
                            int index = selectionStart - 1;
                            mChatMessageEt.getText().replace(index, selectionStart, sb.toString());
                            mChatMessageEt.setSelection(index + sb.length());
                        }
                    }
                    else {
                        mChatMessageEt.getText().insert(selectionStart, sb.toString());
                    }
                }
            });
        }
        //显示键盘
        this.mUIHandler.sendEmptyMessageDelayed(SHOW_KEYBOARD, SHOW_LAYOUT_DELAY);
    }

    /**
     * @param atGroupData
     * @param selectionStart
     * @param hasAt
     *
     * @全体成员
     */
    private void atAll(final String atGroupData, final int selectionStart, final boolean hasAt) {
        LogUtil.i("InputPanel ---> atGroupData：" + atGroupData + "，selectionStart：" + selectionStart);
        if (!StringUtil.isEmpty(atGroupData)) {
            this.mChatMessageEt.post(new Runnable() {
                @Override
                public void run() {
                    if (hasAt) {
                        if (selectionStart > 0) {
                            int index = selectionStart - 1;
                            mChatMessageEt.getText().replace(index, selectionStart, atGroupData);
                            mChatMessageEt.setSelection(index + atGroupData.length());
                        }
                    }
                    else {
                        mChatMessageEt.getText().insert(selectionStart, atGroupData);
                    }
                }
            });
        }
    }

    /**
     * 初始化草稿信息
     */
    public void initDraft(String chatId) {
        String draft = SpUtil.getDraftMessage(chatId);
        if (type == 0) {
            this.mChatMessageEt.setText(draft);
            this.mChatMessageEt.setSelection(draft.length());//设置光标位置
        }
        else {
            this.chat_message_service_et.setText(draft);
            this.chat_message_service_et.setSelection(draft.length());//设置光标位置
        }
    }

    /**
     * 保存草稿信息
     *
     * @param chatId
     */
    public void saveDraft(String chatId) {
        String content = this.mChatMessageEt.getText().toString().trim();
        String replyContentSource = mChatMessageEt.getReplyContentSource();
        if (!TextUtils.isEmpty(replyContentSource)) {
            if (content.length() <= replyContentSource.length()) {
                SpUtil.setDraftMessage(chatId, "");
                //如果输入框里只剩下回复消息内容 则清空草稿信息
                return;
            }
            else {
                content = content.substring(replyContentSource.length() + 1, content.length());
            }
        }
        if (!TextUtils.isEmpty(content)) {
            SpUtil.setDraftMessage(chatId, content);
        }
        else {
            SpUtil.setDraftMessage(chatId, "");
        }
    }

    //打开软键盘代码貌似不好用 模拟点击
    private void mockClick(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 100;
        MotionEvent upEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }

    public void setVisibility(boolean show) {
        isShowView = show;
    }

    public void onReplyMessage(CubeMessage cubeMessage) {
        LogUtil.i(TAG, "onReplyMessage==>");
        this.mCubeMessage = cubeMessage;
        mChatMessageEt.requestFocus();
        Editable text = mChatMessageEt.getText();
        StringBuilder stringBuilder = new StringBuilder();
        ImageSpan[] imageSpans = text.getSpans(0, text.length(), ImageSpan.class);
        if (imageSpans != null && imageSpans.length > 0) {
            for (ImageSpan imageSpan : imageSpans) {
                String source = imageSpan.getSource();
                if (source != null && source.contains(REPLY_MARK)) {
                    text.removeSpan(imageSpan);
                    text.delete(0, source.length() + 1);
                }
            }
        }
        String content = cubeMessage.getContent();
        if (content.length() > 100) {
            //超长消息的特殊处理 100个字符足够多填满一条 不需要剩下的字符
            content = content.substring(0, 100);
        }
        //由于分享二维码消息使用了自定西消息去做 因此需要特殊处理
        if (cubeMessage.getMessageType() == CubeMessageType.CustomShare) {
            content = "[二维码]";
        }
        else {
            //原本显示@｛。。。｝这种@消息要转化为@成某个人的具体消息
            content = AtHelper.replaceAtTagToText(content);
            //空格 制表符 换行 统统去掉
            content = StringUtil.replaceSpec(content);
        }
        Context applicationContext = CubeCore.getContext();
        //将TextView放在一个LinearLayout里 方便TextView的属性生效
        LinearLayout linearLayout = new LinearLayout(applicationContext);
        EllipsizedTextView textView = new EllipsizedTextView(applicationContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(ScreenUtil.sp2px(5), 0, 20, 0);
        content = textView.getAdaptString(content, mChatMessageEt.getMeasuredWidth());
        String contentWithReplyMark = content + REPLY_MARK;
        text.insert(0, contentWithReplyMark);
        text.insert(contentWithReplyMark.length(), REPLY_END);
        textView.setWidth(mChatMessageEt.getMeasuredWidth());
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setSingleLine(true);
        textView.setMaxLines(1);
        textView.setText(stringBuilder.append("“").append(content));
        textView.setBackgroundColor(applicationContext.getResources().getColor(R.color.reply_content_in_edittext));
        linearLayout.addView(textView, layoutParams);
        Drawable drawable = AtHelper.convertViewToDrawable(applicationContext, linearLayout);
        int drawableWidth = drawable.getIntrinsicWidth();
        drawableHeight = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, drawableWidth, drawableHeight);
        ImageSpan span = new ImageSpan(drawable, contentWithReplyMark);
        text.setSpan(span, 0, contentWithReplyMark.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mUIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //模拟点击消息输入框 模拟点击enter按键 不要删
                mockClick(mChatMessageEt, mChatMessageEt.getWidth() + mChatMessageEt.getX(), mChatMessageEt.getHeight() + mChatMessageEt.getY());
                //mChatMessageEt.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            }
        }, 100);
        checkSendButtonEnable(mChatMessageEt);
    }

    /**
     * 选中表情回调方法
     *
     * @param key
     */
    @Override
    public void onEmoticonSelected(String key) {
        this.mChatMessageEt.requestFocus();
        Editable editable = this.mChatMessageEt.getText();
        if (key.equals("/DEL")) {
            this.mChatMessageEt.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        }
        else {
            int start = this.mChatMessageEt.getSelectionStart();
            int end = this.mChatMessageEt.getSelectionEnd();
            start = (start < 0 ? 0 : start);
            end = (start < 0 ? 0 : end);
            editable.replace(start, end, key);
        }
        this.chat_message_service_et.requestFocus();
        Editable editables = this.chat_message_service_et.getText();
        if (key.equals("/DEL")) {
            this.chat_message_service_et.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        }
        else {
            int start = this.chat_message_service_et.getSelectionStart();
            int end = this.chat_message_service_et.getSelectionEnd();
            start = (start < 0 ? 0 : start);
            end = (start < 0 ? 0 : end);
            editables.replace(start, end, key);
        }
    }

    /**
     * 选中贴图表情回调方法
     */
    @Override
    public void onStickerSelected(StickerItem stickerItem) {
        String emojiText = "[cube_emoji:" + stickerItem.getKey() + "]";
        boolean isSecret = mChatContainer.mSessionType == CubeSessionType.Secret;
        final TextMessage textMessage = MessageManager.getInstance().buildTextMessage(mChatType, CubeCore.getInstance().getCubeId(), mChatContainer.mChatId, mChatContainer.mChatName, emojiText, isSecret);
        textMessage.setHeader("textType", "customemoji");
        textMessage.setHeader("key", stickerItem.getKey());
        textMessage.setHeader("packageId", stickerItem.getPackgeId());
        textMessage.setHeader("thumbUrl", stickerItem.getUrl());
        textMessage.setHeader("url", stickerItem.getUrl());
        textMessage.setHeader("emojiCName", stickerItem.getName());
        MessageManager.getInstance().sendMessage(mChatContainer.mChatActivity, textMessage).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    LogUtil.i("emoji message : 贴图发送成功");
                }
                else {
                    LogUtil.i("emoji message : 贴图发送失败");
                }
            }
        });
    }

    @Override
    public void onCollectSelected(String path) {
        MessageManager.getInstance().sendFileMessage(mChatContainer.mChatActivity, mChatType, new Receiver(mChatContainer.mChatId, mChatContainer.mChatName), path, false, true);
    }

    /**
     * 底部导航栏监听器
     */
    public interface OnBottomNavigationListener {
        /**
         * 点击照相机回调事件
         */
        void onCameraListener();

        /**
         * 发送文件回调事件
         */
        void onSendFileListener();

        /**
         * 发送图片回调事件
         */
        void onSendImageListener();
    }

    /**
     * 设置底部导航栏监听器
     *
     * @param listener
     */
    public void setOnBottomNavigationListener(OnBottomNavigationListener listener) {
        this.mOnBottomNavigationListener = listener;
    }

    /**
     * 是否有底部导航栏监听器
     *
     * @param navigationType
     */
    public void hasBottomNavigationListener(int navigationType) {
        if (this.mOnBottomNavigationListener != null) {
            switch (navigationType) {
                case NAVIGATION_TYPE_CAMERA:
                    this.mOnBottomNavigationListener.onCameraListener();
                    break;
                case NAVIGATION_TYPE_FILE:
                    this.mOnBottomNavigationListener.onSendFileListener();
                    break;
                case NAVIGATION_TYPE_IMAGE:
                    this.mOnBottomNavigationListener.onSendImageListener();
                    break;
            }
        }
    }

    /**
     * 设置文本框输入监听
     *
     * @param watcher
     */
    public void addWatcher(MessageEditWatcher watcher) {
        if (watcher != null && !mEditWatchers.contains(watcher)) {
            this.mEditWatchers.add(watcher);
        }
    }

    /**
     * 移除文本框输入监听
     *
     * @param watcher
     */
    public void removeWatcher(MessageEditWatcher watcher) {
        if (watcher != null && mEditWatchers.contains(watcher)) {
            this.mEditWatchers.add(watcher);
        }
    }
}
