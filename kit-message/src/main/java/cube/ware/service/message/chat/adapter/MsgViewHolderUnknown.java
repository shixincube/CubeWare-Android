package cube.ware.service.message.chat.adapter;

import android.view.View;
import com.chad.library.adapter.base.BaseViewHolder;
import cube.ware.service.message.R;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.widget.CubeEmoticonTextView;
import java.util.Map;

/**
 * 聊天消息文本模块
 *
 * @author Wangxx
 * @date 2017/1/9
 */

public class MsgViewHolderUnknown extends BaseMsgViewHolder {

    protected CubeEmoticonTextView mContentTv;

    public MsgViewHolderUnknown(ChatMessageAdapter adapter, BaseViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
        super(adapter, viewHolder, data, position, selectedMap);
    }

    @Override
    protected int getContentResId() {
        return R.layout.item_chat_message_unknown;
    }

    @Override
    protected void initView() {
        this.mContentTv = findViewById(R.id.chat_message_item_text_body);
    }

    @Override
    protected void bindView() {
        mContentTv.setText(mContext.getString(R.string.unknown_message_type));
        this.mContentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(mContentTv);
            }
        });
    }

    private String getDisplayText() {
        return super.mData.mMessage.getContent();
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

    }

    @Override
    protected void onItemClick(View view) {}
}
