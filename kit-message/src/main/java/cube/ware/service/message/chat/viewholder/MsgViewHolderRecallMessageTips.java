package cube.ware.service.message.chat.viewholder;

import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.ScreenUtil;
import cube.ware.service.message.R;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.chat.adapter.ChatMessageAdapter;
import java.util.Map;

/**
 * 撤回消息提示语
 */
public class MsgViewHolderRecallMessageTips extends BaseMsgViewHolder {

    protected TextView notificationTextView;

    public MsgViewHolderRecallMessageTips(ChatMessageAdapter adapter, BaseViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
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
        handleTextNotification(getDisplayText());
    }

    protected String getDisplayText() {
        return super.mData.mMessage.getContent();
    }

    private void handleTextNotification(String text) {

        this.notificationTextView.setBackgroundResource(R.drawable.ic_message_tips);
        this.notificationTextView.setPadding(ScreenUtil.dip2px(8), ScreenUtil.dip2px(4), ScreenUtil.dip2px(8), ScreenUtil.dip2px(4));
        notificationTextView.setMovementMethod(LinkMovementMethod.getInstance());
        notificationTextView.setText(text);
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }

    @Override
    public void onDestroy() {

    }
}

