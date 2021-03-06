package cube.ware.ui.chat.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.Map;

import cube.ware.R;
import cube.ware.data.model.dataModel.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeCustomMessageType;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.widget.recyclerview.BaseRecyclerViewHolder;


/**
 * @author Wangxx
 * @date 2017/3/23
 */

public class MsgViewHolderCall extends MsgViewHolderText {
    public MsgViewHolderCall(ChatMessageAdapter adapter, BaseRecyclerViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
        super(adapter, viewHolder, data, position, selectedMap);
    }

    @Override
    protected int getContentResId() {
        return R.layout.item_chat_message_call;
    }


    @Override
    protected void bindView() {
        super.bindView();
        Drawable drawable;
        mContentTv.setCompoundDrawablePadding(20);
        if (isReceivedMessage()) {
            if (mData.mMessage.getMessageType().equals(CubeMessageType.CustomCallVideo.getType())) {
                drawable = mContext.getResources().getDrawable(R.drawable.ic_video_left_end);
            }
            else {
                drawable = mContext.getResources().getDrawable(R.drawable.ic_audio_left_end);
            }
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mContentTv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
        else {
            if (mData.mMessage.getMessageType().equals(CubeMessageType.CustomCallVideo.getType())) {
                drawable = mContext.getResources().getDrawable(R.drawable.ic_video_right_end);
            }
            else {
                drawable = mContext.getResources().getDrawable(R.drawable.ic_audio_right_end);
            }
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mContentTv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }
    }

    @Override
    protected void onItemClick(View view) {
        synchronized (mData.mMessage) {
            final String operate = mData.mMessage.getOperate();
            final String chatId = mData.mMessage.getChatId();
            if (CubeCustomMessageType.VideoCall.type.equals(operate) || CubeCustomMessageType.AudioCall.type.equals(operate)) {
//                P2PCallActivity.start(view.getContext(), chatId, CubeCustomMessageType.VideoCall.type.equals(operate) ? CallStatus.VIDEO_OUTGOING : CallStatus.AUDIO_OUTGOING);
            }
        }
    }
}
