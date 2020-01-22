package cube.ware.service.message.chat.adapter;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.DeviceUtil;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.log.LogUtil;
import cube.ware.R;
import cube.ware.core.CubeCore;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeCustomMessageType;
import cube.ware.data.room.model.CubeMessage;
import java.util.Map;

public class MsgViewHolderNotification extends BaseMsgViewHolder {
    private static final String   TAG = MsgViewHolderNotification.class.getSimpleName();
    protected            TextView notificationTextView;

    public MsgViewHolderNotification(ChatMessageAdapter adapter, BaseViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
        super(adapter, viewHolder, data, position, selectedMap);
    }

    @Override
    protected int getContentResId() {
        return R.layout.item_chat_message_notification;
    }

    @Override
    protected void initView() {
        notificationTextView = findViewById(R.id.message_item_notification_label);
    }

    @Override
    protected void bindView() {
        notificationTextView.setBackgroundResource(R.drawable.ic_message_tips);
        notificationTextView.setPadding(ScreenUtil.dip2px(8), ScreenUtil.dip2px(4), ScreenUtil.dip2px(8), ScreenUtil.dip2px(4));
        handleTextNotification(getDisplayText());
    }

    protected String getDisplayText() {
        return super.mData.mMessage.getContent();
    }

    private void handleTextNotification(String text) {
        if (CubeCustomMessageType.SecretTip.getType().equals(mData.mMessage.getOperate())) {
            mData.mMessage.setShowTime(false);
            setChatDate();
        }
        notificationTextView.setMovementMethod(LinkMovementMethod.getInstance());
        notificationTextView.setText(text);
        //不是群消息并且发送发不是自己
        if (CubeCustomMessageType.DownLoad_Complete.getType().equals(mData.mMessage.getOperate()) && !mData.mMessage.isGroupMessage() && !mData.mMessage.getSenderId().equals(CubeCore.getInstance().getCubeId())) {
            LogUtil.i(TAG, "setCompoundDrawables");
            SpannableStringBuilder mSpannableString = new SpannableStringBuilder(text);
            mSpannableString.insert(0, "  ");
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.download_success);
            drawable.setBounds(0, 0, DeviceUtil.sp2px(mContext, 13), DeviceUtil.sp2px(mContext, 13));
            ImageSpan imageSpan = new ImageSpan(drawable);
            mSpannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            notificationTextView.setText(mSpannableString);
        }
    }

    @Override
    public void setChatDate() {
        mChatDate.setVisibility(View.GONE);
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }

    @Override
    public void onDestroy() {

    }
}

