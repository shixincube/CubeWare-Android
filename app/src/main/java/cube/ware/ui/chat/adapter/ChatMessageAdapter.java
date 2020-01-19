package cube.ware.ui.chat.adapter;

import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;

import com.common.utils.utils.ScreenUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.ui.chat.panel.messagelist.MessageListPanel;
import cube.ware.widget.CountdownChronometer;
import cube.ware.widget.recyclerview.BaseMultiItemAdapter;
import cube.ware.widget.recyclerview.BaseRecyclerViewHolder;

/**
 * 聊天消息列表适配器
 *
 * @author Wangxx
 * @date 2017/1/9
 */

public class ChatMessageAdapter extends BaseMultiItemAdapter<CubeMessageViewModel, BaseRecyclerViewHolder> {
    
    private ViewHolderEventListener eventListener;
    public  String                  mChatId;    // 聊天id
    public  boolean                 isShowMore; // 是否显示更多界面
    public  MessageListPanel        mListPanel; // 聊天列表操作面板
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
        super.addItemType(AppConstants.MessageType.CHAT_TXT, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.CHAT_AUDIO, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.CHAT_VIDEO, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.CHAT_FILE, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.CHAT_IMAGE, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.CUSTOM_TIPS, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.CUSTOM_CALL_VIDEO, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.CUSTOM_CALL_AUDIO, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.RECALL_MESSAGE_TIPS, R.layout.item_chat_message);

        //目前不支持的消息
        super.addItemType(AppConstants.MessageType.CUSTOM_SHAKE, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.UNKNOWN, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.CHAT_WHITEBOARD, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.CUSTOM_SHARE, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.CHAT_RICH_TEXT, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.CHAT_EMOJI, R.layout.item_chat_message);
        super.addItemType(AppConstants.MessageType.REPLY_MESSAGE, R.layout.item_chat_message);

        mMsgViewHolderMap.put(AppConstants.MessageType.CHAT_TXT, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.CHAT_AUDIO, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.CHAT_VIDEO, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.CHAT_FILE, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.CHAT_IMAGE, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.CUSTOM_TIPS, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.CUSTOM_CALL_VIDEO, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.CUSTOM_CALL_AUDIO, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.RECALL_MESSAGE_TIPS, new LongSparseArray<BaseMsgViewHolder>());

        //目前不支持的消息
        mMsgViewHolderMap.put(AppConstants.MessageType.CUSTOM_SHAKE, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.UNKNOWN, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.CHAT_WHITEBOARD, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.CUSTOM_SHARE, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.CHAT_RICH_TEXT, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.CHAT_EMOJI, new LongSparseArray<BaseMsgViewHolder>());
        mMsgViewHolderMap.put(AppConstants.MessageType.REPLY_MESSAGE, new LongSparseArray<BaseMsgViewHolder>());
    }


    @Override
    protected void convert(final BaseRecyclerViewHolder viewHolder, final CubeMessageViewModel data, final int position) {
        int itemViewType = viewHolder.getItemViewType();
        final long messageSN = data.mMessage.getMessageSN();
        final LongSparseArray<BaseMsgViewHolder> baseMsgViewHolderLongSparseArray = mMsgViewHolderMap.get(itemViewType);
        BaseMsgViewHolder msgViewHolder = baseMsgViewHolderLongSparseArray.get(messageSN);
        switch (itemViewType) {
            case AppConstants.MessageType.CHAT_TXT://文字消息
                new MsgViewHolderText(this, viewHolder, data, position, mSelectedMap);
                break;
            case AppConstants.MessageType.CHAT_IMAGE://图片消息
                if (msgViewHolder == null) {
                    baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderImg(this, viewHolder, data, position, mSelectedMap));
                } else {
                    msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
                }
                break;
            case AppConstants.MessageType.CHAT_VIDEO://视频消息
                if (msgViewHolder == null) {
                    baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderVideo(this, viewHolder, data, position, mSelectedMap));
                } else {
                    msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
                }
                break;
            case AppConstants.MessageType.CHAT_FILE://文件消息
                if (msgViewHolder == null) {
                    baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderFile(this, viewHolder, data, position, mSelectedMap));
                } else {
                    msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
                }
                break;
            case AppConstants.MessageType.CHAT_AUDIO://语音消息
                if (msgViewHolder == null) {
                    baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderAudio(this, viewHolder, data, position, mSelectedMap));
                } else {
                    msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
                }
                break;
            case AppConstants.MessageType.CUSTOM_TIPS://自定义消息提示
                new MsgViewHolderNotification(this, viewHolder, data, position, mSelectedMap);
                break;
            case AppConstants.MessageType.CUSTOM_CALL_VIDEO://视频，语音呼叫消息
            case AppConstants.MessageType.CUSTOM_CALL_AUDIO:
                new MsgViewHolderCall(this, viewHolder, data, position, mSelectedMap);
                break;
            case AppConstants.MessageType.CHAT_RICH_TEXT://富文本消息
//                if (msgViewHolder == null) {
//                    baseMsgViewHolderLongSparseArray.put(messageSN, new MsgViewHolderRichText(this, viewHolder, data, position, mSelectedMap));
//                } else {
//                    msgViewHolder.update(this, viewHolder, data, position, mSelectedMap);
//                }
                break;
            case AppConstants.MessageType.RECALL_MESSAGE_TIPS:
                new MsgViewHolderRecallMessageTips(this, viewHolder, data, position, mSelectedMap);
                break;
            default:
                new MsgViewHolderUnknown(this, viewHolder, data, position, mSelectedMap);
                break;
        }
    }

    @Override
    public void addDataList(List<CubeMessageViewModel> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            // 数据排序
            sortMessage(dataList);

            this.mDataList.addAll(0, dataList);
            this.notifyItemRangeInserted(getHeaderViewCount(), dataList.size());
        }
    }

    @Override
    public void onViewRecycled(BaseRecyclerViewHolder holder) {
        super.onViewRecycled(holder);
        CountdownChronometer view = holder.getView(R.id.chat_item_secret_time);
        view.onPause();
    }

    public void addRefreshDataList(List<CubeMessageViewModel> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            // 数据排序
            this.mDataList.addAll(0, dataList);
            sortMessage(mDataList);
            this.notifyDataSetChanged();
        }
    }

    /**
     * 添加或更新一条item
     *
     * @param cubeMessage
     */
    public void addOrUpdateItem(CubeMessageViewModel cubeMessage) {
        if (!isInsert(this.mDataList, cubeMessage.mMessage.getTimestamp())) {
            return;
        }
        int position = this.find(this.mDataList, cubeMessage.mMessage.getMessageSN());
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
            removeData(itemPosition);
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
                if (this.mDataList.get(i).mMessage.getMessageSN() == messageSn) {
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
        return this.find(this.mDataList, messageSn);
    }

    /**
     * 是否是添加消息
     *
     * @param messageViewModel
     *
     * @return
     */
    public boolean contains(CubeMessageViewModel messageViewModel) {
        return this.mDataList.contains(messageViewModel);
    }
}

