package cube.ware.ui.chat.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.utils.utils.ScreenUtil;

import cube.ware.service.message.MessageHandle;
import java.util.Map;

import cube.service.CubeEngine;
import cube.ware.R;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeMessageStatus;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.manager.MessagePopupManager;
import cube.ware.ui.chat.listener.FileMessageReceiveListener;
import cube.ware.ui.chat.listener.FileMessageSendListener;
import cube.ware.ui.chat.listener.VoicePlayClickListener;
import cube.ware.widget.recyclerview.BaseRecyclerViewHolder;

/**
 * 聊天消息语音模块
 *
 * @author Wangxx
 * @date 2017/1/10
 */

public class MsgViewHolderAudio extends BaseMsgViewHolder {

    public static int MAX_AUDIO_TIME_SECOND = 120;

    private TextView    mDurationLabel;
    private FrameLayout mContainerView;
    public  ImageView   mIndicator;
    private ImageView   mAnimationView;

    public MsgViewHolderAudio(ChatMessageAdapter adapter, BaseRecyclerViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
        super(adapter, viewHolder, data, position, selectedMap);
    }

    @Override
    protected int getContentResId() {
        return R.layout.item_chat_message_audio;
    }

    @Override
    protected void initView() {
        this.mDurationLabel = findViewById(R.id.item_message_audio_duration);
        this.mContainerView = findViewById(R.id.item_message_audio_container);
        this.mIndicator = findViewById(R.id.item_message_audio_unread_indicator);
        this.mAnimationView = findViewById(R.id.item_message_audio_playing_animation);
    }

    @Override
    protected void bindView() {
        this.layoutByDirection();
        this.refreshStatus();
        this.setAudioWidth(mData.mMessage.getDuration());

        if (!TextUtils.isEmpty(mData.mMessage.getFilePath())) {
            mContainerView.setClickable(true);
            mContainerView.setOnClickListener(new VoicePlayClickListener(mContext, this, mData.mMessage, mAnimationView, isReceivedMessage()));
        }
        else {
            mContainerView.setClickable(false);
            CubeEngine.getInstance().getMessageService().acceptMessage(mData.mMessage.getMessageSN(),null);
        }

        if (!mData.mMessage.isAnonymous()) {
            //长安弹出菜单
            MessagePopupManager.showMessagePopup(this, mContainerView,this);
        }
    }

    private void layoutByDirection() {
        if (isReceivedMessage()) {
            MessageHandle.getInstance().addDownloadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName(), new FileMessageReceiveListener(mContext, mData.mMessage, mViewHolder, mInflate));

            setAudioGravity(mAnimationView, Gravity.LEFT | Gravity.CENTER_VERTICAL);
            setAudioGravity(mDurationLabel, Gravity.RIGHT | Gravity.CENTER_VERTICAL);

            mContainerView.setBackgroundResource(R.drawable.selector_chat_receive_bg);
            mContainerView.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(10), ScreenUtil.dip2px(8));
            mDurationLabel.setTextColor(Color.BLACK);
            if (isShowSecretMessage()) {
                mAnimationView.setImageResource(R.drawable.ic_chat_secret_voice_message);
            }
            else {
                mAnimationView.setImageResource(R.drawable.ic_audio_animation_list_left_3);
            }
        }
        else {
//            FileSendManager.getInstance().resendFileMessageIfNeeded(mData.mMessage);
            MessageHandle.getInstance().addUploadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName(), new FileMessageSendListener(mContext, mData.mMessage, mViewHolder, mInflate));

            setAudioGravity(mAnimationView, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            setAudioGravity(mDurationLabel, Gravity.LEFT | Gravity.CENTER_VERTICAL);
            mIndicator.setVisibility(View.GONE);

            mContainerView.setBackgroundResource(R.drawable.selector_chat_send_bg);
            mContainerView.setPadding(ScreenUtil.dip2px(10), ScreenUtil.dip2px(8), ScreenUtil.dip2px(15), ScreenUtil.dip2px(8));
            mAnimationView.setImageResource(R.drawable.ic_audio_animation_list_right_3);
            mDurationLabel.setTextColor(Color.WHITE);
        }
    }

    private void refreshStatus() {// 消息状态
        String path = mData.mMessage.getFilePath();

        // alert button
        if (TextUtils.isEmpty(path)) {
            if (super.mData.mMessage.getMessageStatus() == CubeMessageStatus.Failed.getStatus() && !mData.mMessage.isReceivedMessage()) {
                super.mRepeatButton.setVisibility(View.VISIBLE);
            }
            else {
                super.mRepeatButton.setVisibility(View.GONE);
            }
        }

        if (isReceivedMessage() && !mData.mMessage.isPlay()) {
            mIndicator.setVisibility(View.VISIBLE);
        }
        else {
            mIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * 设置语音消息长度
     *
     * @param milliseconds
     */
    private void setAudioWidth(int milliseconds) {
        updateTime(milliseconds);
        int currentBubbleWidth = calculateBubbleWidth(milliseconds, MAX_AUDIO_TIME_SECOND);
        ViewGroup.LayoutParams layoutParams = mContainerView.getLayoutParams();
        layoutParams.width = currentBubbleWidth;
        mContainerView.setLayoutParams(layoutParams);
    }

    private int calculateBubbleWidth(long seconds, int MAX_TIME) {
        int maxAudioBubbleWidth = getAudioMaxEdge();
        int minAudioBubbleWidth = getAudioMinEdge();

        int currentBubbleWidth;
        if (seconds <= 0) {
            currentBubbleWidth = minAudioBubbleWidth;
        }
        else if (seconds > 0 && seconds <= MAX_TIME) {
            currentBubbleWidth = (int) ((maxAudioBubbleWidth - minAudioBubbleWidth) * (2.0 / Math.PI) * Math.atan(seconds / 10.0) + minAudioBubbleWidth);
        }
        else {
            currentBubbleWidth = maxAudioBubbleWidth;
        }

        if (currentBubbleWidth < minAudioBubbleWidth) {
            currentBubbleWidth = minAudioBubbleWidth;
        }
        else if (currentBubbleWidth > maxAudioBubbleWidth) {
            currentBubbleWidth = maxAudioBubbleWidth;
        }

        return currentBubbleWidth;
    }

    public static int getAudioMaxEdge() {
        return (int) (0.6 * ScreenUtil.screenMin);
    }

    public static int getAudioMinEdge() {
        return (int) (0.1875 * ScreenUtil.screenMin);
    }

    public void updateTime(long milliseconds) {
        if (milliseconds >= 0) {
            mDurationLabel.setText(milliseconds + "\"");
        }
        else {
            mDurationLabel.setText("");
        }
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    @Override
    public void onDestroy() {
        if (mData.mMessage != null) {
            MessageHandle.getInstance().removeUploadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName());
            MessageHandle.getInstance().removeDownloadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName());
        }
    }
}
