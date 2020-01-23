package cube.ware.service.message.chat.adapter;

import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.ScreenUtil;
import cube.ware.service.message.R;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.chat.panel.messagelist.MessageListPanel;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天消息列表适配器
 *
 * @author Wangxx
 * @date 2017/1/9
 */

public class ChatMessageAdapter extends BaseMultiItemQuickAdapter<CubeMessageViewModel, BaseViewHolder> {

    private             ViewHolderEventListener                         eventListener;
    public              String                                          mChatId;    // 聊天id
    public              boolean                                         isShowMore; // 是否显示更多界面
    public              MessageListPanel                                mListPanel; // 聊天列表操作面板
    private             SparseArray<LongSparseArray<BaseMsgViewHolder>> mMsgViewHolderMap = new SparseArray<>();
    public static final int                                             MIN_IMG_SIZE      = ScreenUtil.dip2px(72);
    /**
     * 存储已选中的信息
     * key: cubeMessageSn
     * value: cubeMessage
     */
    public              Map<String, CubeMessage>                        mSelectedMap      = new HashMap<>();

    public ChatMessageAdapter(List<CubeMessageViewModel> data, String chatId, boolean isShowMore, MessageListPanel messageListPanel) {
        super(data);
        this.mChatId = chatId;
        this.isShowMore = isShowMore;
        this.mListPanel = messageListPanel;
        super.addItemType(CubeMessageType.Text.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.Voice.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.Video.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.File.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.Image.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.CustomTips.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.CustomCallVideo.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.CustomCallAudio.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.RECALLMESSAGETIPS.value, R.layout.item_chat_message);

        //目前不支持的消息
        super.addItemType(CubeMessageType.CustomShake.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.Unknown.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.Whiteboard.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.CustomShare.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.RICHTEXT.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.Emoji.value, R.layout.item_chat_message);
        super.addItemType(CubeMessageType.REPLYMESSAGE.value, R.layout.item_chat_message);

        mMsgViewHolderMap.put(CubeMessageType.Text.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.Voice.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.Video.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.File.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.Image.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.CustomTips.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.CustomCallVideo.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.CustomCallAudio.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.RECALLMESSAGETIPS.value, new LongSparseArray<BaseMsgViewHolder>());

        //目前不支持的消息
        mMsgViewHolderMap.put(CubeMessageType.CustomShake.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.Unknown.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.Whiteboard.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.CustomShare.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.RICHTEXT.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.Emoji.value, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(CubeMessageType.REPLYMESSAGE.value, new LongSparseArray<BaseMsgViewHolder>());
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, CubeMessageViewModel data) {
        int position = viewHolder.getAdapterPosition();
        int itemViewType = viewHolder.getItemViewType();
        final long messageSN = data.mMessage.getMessageSN();
        final LongSparseArray<BaseMsgViewHolder> baseMsgViewHolderLongSparseArray = mMsgViewHolderMap.get(itemViewType);
        BaseMsgViewHolder msgViewHolder = baseMsgViewHolderLongSparseArray.get(messageSN);

        if (itemViewType == CubeMessageType.Text.value) {
            new MsgViewHolderText(this, viewHolder, data, position, mSelectedMap);
        }
        else if (itemViewType == CubeMessageType.Image.value) {
            if (msgViewHolder == null) {
                baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderImg(this, viewHolder, data, position, mSelectedMap));
            }
            else {
                msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
            }
        }
        else if (itemViewType == CubeMessageType.Video.value) {
            if (msgViewHolder == null) {
                baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderVideo(this, viewHolder, data, position, mSelectedMap));
            }
            else {
                msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
            }
        }
        else if (itemViewType == CubeMessageType.File.value) {
            if (msgViewHolder == null) {
                baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderFile(this, viewHolder, data, position, mSelectedMap));
            }
            else {
                msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
            }
        }
        else if (itemViewType == CubeMessageType.Voice.value) {
            if (msgViewHolder == null) {
                baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderAudio(this, viewHolder, data, position, mSelectedMap));
            }
            else {
                msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
            }
        }
        else if (itemViewType == CubeMessageType.CustomTips.value) {
            new MsgViewHolderNotification(this, viewHolder, data, position, mSelectedMap);
        }
        else if (itemViewType == CubeMessageType.CustomCallVideo.value || itemViewType == CubeMessageType.CustomCallAudio.value) {
            new MsgViewHolderCall(this, viewHolder, data, position, mSelectedMap);
        }
        else if (itemViewType == CubeMessageType.RECALLMESSAGETIPS.value) {
            new MsgViewHolderRecallMessageTips(this, viewHolder, data, position, mSelectedMap);
        }
        else {
            new MsgViewHolderUnknown(this, viewHolder, data, position, mSelectedMap);
        }

        /*switch (itemViewType) {
            case CubeMessageType.Text.value://文字消息
                new MsgViewHolderText(this, viewHolder, data, position, mSelectedMap);
                break;
            case CubeMessageType.Image.value://图片消息
                if (msgViewHolder == null) {
                    baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderImg(this, viewHolder, data, position, mSelectedMap));
                }
                else {
                    msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
                }
                break;
            case CubeMessageType.Video.value://视频消息
                if (msgViewHolder == null) {
                    baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderVideo(this, viewHolder, data, position, mSelectedMap));
                }
                else {
                    msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
                }
                break;
            case CubeMessageType.File.value://文件消息
                if (msgViewHolder == null) {
                    baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderFile(this, viewHolder, data, position, mSelectedMap));
                }
                else {
                    msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
                }
                break;
            case CubeMessageType.Voice.value://语音消息
                if (msgViewHolder == null) {
                    baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderAudio(this, viewHolder, data, position, mSelectedMap));
                }
                else {
                    msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
                }
                break;
            case CubeMessageType.CustomTips.value://自定义消息提示
                new MsgViewHolderNotification(this, viewHolder, data, position, mSelectedMap);
                break;
            case CubeMessageType.CustomCallVideo.value://视频，语音呼叫消息
            case CubeMessageType.CustomCallAudio.value:
                new MsgViewHolderCall(this, viewHolder, data, position, mSelectedMap);
                break;
            case CubeMessageType.RICHTEXT.value://富文本消息
                break;
            case CubeMessageType.RECALLMESSAGETIPS.value:
                new MsgViewHolderRecallMessageTips(this, viewHolder, data, position, mSelectedMap);
                break;
            default:
                new MsgViewHolderUnknown(this, viewHolder, data, position, mSelectedMap);
                break;
        }*/
    }

    //@Override
    //public void addDataList(List<CubeMessageViewModel> dataList) {
    //    if (null != dataList && !dataList.isEmpty()) {
    //        // 数据排序
    //        sortMessage(dataList);
    //
    //        this.mData.addAll(0, dataList);
    //        this.notifyItemRangeInserted(getHeaderViewCount(), dataList.size());
    //    }
    //}

    //@Override
    //public void onViewRecycled(BaseView holder) {
    //    super.onViewRecycled(holder);
    //    CountdownChronometer view = holder.getView(R.id.chat_item_secret_time);
    //    view.onPause();
    //}

    public void addRefreshDataList(List<CubeMessageViewModel> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            // 数据排序
            this.mData.addAll(0, dataList);
            sortMessage(mData);
            this.notifyDataSetChanged();
        }
    }

    /**
     * 添加或更新一条item
     *
     * @param cubeMessage
     */
    public void addOrUpdateItem(CubeMessageViewModel cubeMessage) {
        if (!isInsert(this.mData, cubeMessage.mMessage.getTimestamp())) {
            return;
        }
        int position = this.find(this.mData, cubeMessage.mMessage.getMessageSN());
        if (position != -1) {
            setData(position, cubeMessage);
        }
        else {
            addData(cubeMessage);
        }
    }

    /**
     * 根据消息的sn删除已回执的消息
     *
     * @param sn
     */
    public void delMsg(long sn) {
        final int itemPosition = findCurrentPosition(sn);
        if (itemPosition != -1) {
            remove(itemPosition);
            refreshMsgNum();
        }
    }

    public void refreshMsgNum() {
        this.mListPanel.refreshMsgCount();
    }

    public void setEventListener(ViewHolderEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public ViewHolderEventListener getEventListener() {
        return eventListener;
    }

    public void onDestroy() {
        /**
         * 这段代码的意义：每次convert的时候都new一个BaseMsgViewHolder实际中对内存影响没有那么大 但是在File Video Image等等的holder中 都设置了文件下载监听而没有很好的管理其何时取消监听
         * 迫于无奈 我只好在adapter保留一个对于这些holder的引用 并在onDestroy的时候 在holder内取消监听器 否则就要改动这个聊天界面的加载架构 谁有更好的方法 请尽快改掉他
         * by cloudzh 2017/9/16
         */
        int size = mMsgViewHolderMap.size();
        for (int i = 0; i < size; i++) {
            LongSparseArray<BaseMsgViewHolder> longBaseMsgViewHolderHashMap = mMsgViewHolderMap.get(i);
            if (longBaseMsgViewHolderHashMap != null && longBaseMsgViewHolderHashMap.size() > 0) {
                int holderMapSize = longBaseMsgViewHolderHashMap.size();
                for (int i1 = 0; i1 < holderMapSize; i1++) {
                    long l = longBaseMsgViewHolderHashMap.keyAt(i1);
                    BaseMsgViewHolder msgViewHolder = longBaseMsgViewHolderHashMap.get(l);
                    if (msgViewHolder != null) {
                        msgViewHolder.onDestroy();
                    }
                }
            }
        }
    }

    public void onEvent(String text, CubeMessage cubeMessage) {
        mListPanel.onEvent(text, cubeMessage);
    }

    public interface ViewHolderEventListener {
        // 长按事件响应处理
        boolean onViewHolderLongClick(View clickView, CubeMessage cubeMessage, ChatMessageAdapter adapter, int position);

        // 发送失败或者多媒体文件下载失败指示按钮点击响应处理
        void onFailedBtnClick(View clickView, CubeMessage cubeMessage);
    }

    /**
     * 在List<CubeMessage>中查找消息sn相等的MessageViewModel
     *
     * @param cubeMessages
     * @param messageSn
     *
     * @return
     */
    private int find(List<CubeMessageViewModel> cubeMessages, long messageSn) {
        if (null != cubeMessages && !cubeMessages.isEmpty()) {
            for (int i = 0; i < cubeMessages.size(); i++) {
                if (this.mData.get(i).mMessage.getMessageSN() == messageSn) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void sortMessage(List<CubeMessageViewModel> dataList) {
        Collections.sort(dataList, new Comparator<CubeMessageViewModel>() {
            @Override
            public int compare(CubeMessageViewModel o1, CubeMessageViewModel o2) {
                CubeMessage message1 = o1.mMessage;
                CubeMessage message2 = o2.mMessage;
                return message1.getTimestamp() - message2.getTimestamp() > 0 ? 1 : -1;
            }
        });
    }

    /**
     * 在List<CubeMessage>中判断是否更新还是插入的MessageViewModel
     *
     * @param cubeMessages
     * @param time
     *
     * @return
     */
    private boolean isInsert(List<CubeMessageViewModel> cubeMessages, long time) {
        return !(null != cubeMessages && !cubeMessages.isEmpty()) || time >= cubeMessages.get(0).mMessage.getTimestamp();
    }

    /**
     * 更具消息SN查询出当前消息的位置
     *
     * @param messageSn
     *
     * @return
     */
    public int findCurrentPosition(long messageSn) {
        return this.find(this.mData, messageSn);
    }

    /**
     * 是否是添加消息
     *
     * @param messageViewModel
     *
     * @return
     */
    public boolean contains(CubeMessageViewModel messageViewModel) {
        return this.mData.contains(messageViewModel);
    }
}

