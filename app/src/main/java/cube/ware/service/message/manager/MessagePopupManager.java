package cube.ware.service.message.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import com.common.mvp.rx.RxSchedulers;
import com.common.utils.utils.ToastUtil;
import cube.service.CubeEngine;
import cube.ware.data.model.HeaderMap;
import cube.ware.data.model.dataModel.enmu.CubeMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.chat.adapter.BaseMsgViewHolder;
import cube.ware.service.message.chat.adapter.ChatMessageAdapter;
import cube.ware.service.message.chat.panel.input.emoticon.manager.StickerManager;
import cube.ware.utils.ClipBoardUtil;
import cube.ware.widget.PopupHorizontalMenu;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;

/**
 * 显示消息弹出框管理器
 *
 * @author Wangxx
 * @date 2017/3/15
 */

public class MessagePopupManager {
    public static final String COPY            = "复制";
    public static final String FORWARD         = "转发";
    public static final String RECALL          = "撤回";
    public static final String DEL             = "删除";
    public static final String MORE            = "更多";
    public static final String COLLECT         = "存表情";
    public static final String REPLY           = "回复";
    public static final String CREATE_SCHEDULE = "添加到日程";

    public enum PopMenuItem {
        UNKNOWN("", 0), COPY_ITEM(COPY, 1), FORWARD_ITEM(FORWARD, 2), RECALL_ITEM(RECALL, 3), DEL_ITEM(DEL, 4), MORE_ITEM(MORE, 5), COLLECT_ITEM(COLLECT, 6), REPLY_ITEM(REPLY, 7), CREATE_SCHEDULE_ITEM(CREATE_SCHEDULE, 8);

        private String mText;
        private int    mCode;

        PopMenuItem(String text, int code) {
            mText = text;
            mCode = code;
        }

        public String getText() {
            return mText;
        }

        public int getCode() {
            return mCode;
        }

        public static PopMenuItem parseByText(String text) {
            switch (text) {
                case COPY:
                    return COPY_ITEM;
                case FORWARD:
                    return FORWARD_ITEM;
                case RECALL:
                    return RECALL_ITEM;
                case DEL:
                    return DEL_ITEM;
                case MORE:
                    return MORE_ITEM;
                case COLLECT:
                    return COLLECT_ITEM;
                case REPLY:
                    return REPLY_ITEM;
                case CREATE_SCHEDULE:
                    return CREATE_SCHEDULE_ITEM;
            }
            return UNKNOWN;
        }

        public static PopMenuItem parseByCode(int code) {
            switch (code) {
                case 1:
                    return COPY_ITEM;
                case 2:
                    return FORWARD_ITEM;
                case 3:
                    return RECALL_ITEM;
                case 4:
                    return DEL_ITEM;
                case 5:
                    return MORE_ITEM;
                case 6:
                    return COLLECT_ITEM;
                case 7:
                    return REPLY_ITEM;
                case 8:
                    return CREATE_SCHEDULE_ITEM;
            }
            return UNKNOWN;
        }

        public int compare(PopMenuItem popMenuItem) {
            return mCode - popMenuItem.mCode;
        }
    }

    //监听PopMenu处理的事件
    public interface OnPopMenuHandleListener {
        void onEvent(String text, CubeMessage cubeMessage);
    }

    public static final long RECALL_LIMIT_TIME = 2 * 58 * 1000; //产品要求撤回2分钟 考虑到网络延迟偷偷的减两秒保证到达服务器的消息一定不会被服务器的两分钟限制

    /**
     * 显示消息弹出框
     *
     * @param base
     * @param view
     */
    public static void showMessagePopup(final BaseMsgViewHolder base, View view, final OnPopMenuHandleListener listener) {
        List<String> popupMenuItemList = new ArrayList<>();
        PopupHorizontalMenu popupHorizontalMenu = new PopupHorizontalMenu();
        CubeMessageType messageType = base.mData.mMessage.getMessageType();
        CubeMessageStatus status = CubeMessageStatus.parse(base.mData.mMessage.getMessageStatus());
        if (messageType == CubeMessageType.Text) {
            popupMenuItemList.add(COPY);
            if (!base.mData.isReceivedMessage() && isRecall(base.mData.mMessage)) {
                if (status != CubeMessageStatus.Sending) {
                    popupMenuItemList.add(RECALL);
                    //                    popupMenuItemList.add(FORWARD);
                }
            }
            else {
                //                popupMenuItemList.add(FORWARD);
            }
            popupMenuItemList.add(DEL);           //需考虑到发送中不能撤回跟转发
        }
        else if (messageType == CubeMessageType.Voice) {
            if (!base.mData.isReceivedMessage() && isRecall(base.mData.mMessage)) {
                if (status != CubeMessageStatus.Sending) {
                    popupMenuItemList.add(RECALL);
                }
            }
            popupMenuItemList.add(DEL);
        }
        else if (messageType == CubeMessageType.CustomCallVideo) {
            popupMenuItemList.add(DEL);
        }
        else if (messageType == CubeMessageType.CustomCallAudio) {
            popupMenuItemList.add(DEL);
        }
        else if (messageType == CubeMessageType.CustomShare) {
            popupMenuItemList.add(DEL);
        }
        else if (messageType == CubeMessageType.CustomShake) {
            popupMenuItemList.add(DEL);
        }
        else {
            if (!base.mData.isReceivedMessage() && isRecall(base.mData.mMessage)) {
                if (status != CubeMessageStatus.Sending) {
                    popupMenuItemList.add(RECALL);
                    //                    popupMenuItemList.add(FORWARD);
                }
            }
            else {
                //                popupMenuItemList.add(FORWARD);
            }
            popupMenuItemList.add(DEL);
        }
        if (messageType == CubeMessageType.Image) {
            //            popupMenuItemList.add(COLLECT);
        }

        if (messageType == CubeMessageType.CARD) {
            List<HeaderMap> customHeaders = base.mData.mMessage.getCustomHeaderMap();
            boolean isGroupTask = false;
            for (HeaderMap customHeader : customHeaders) {
                // 群任务
                if (customHeader.key.equals("operate") && customHeader.value.equals("groupTask")) {
                    isGroupTask = true;
                }
                // 日程
                else {
                }
            }

            popupMenuItemList.clear();
            //            popupMenuItemList.add(FORWARD);
            if (!isGroupTask) {
                popupMenuItemList.add(CREATE_SCHEDULE);
            }
            popupMenuItemList.add(DEL);
        }
        //popupMenuItemList.add(MORE);
        if (base.mData.isReceivedMessage() && base.mData.isGroupMessage()) {
            //暂时隐藏回复消息选项
            //            popupMenuItemList.add(REPLY);
        }
        //排序
        //Collections.sort(popupMenuItemList, new Comparator<String>() {
        //    @Override
        //    public int compare(String o1, String o2) {
        //        PopMenuItem popMenuItem = PopMenuItem.parseByText(o1);
        //        PopMenuItem popMenuItem1 = PopMenuItem.parseByText(o2);
        //        return popMenuItem.compare(popMenuItem1);
        //    }
        //});
        popupHorizontalMenu.init(base.mContext, view, popupMenuItemList, new PopupHorizontalMenu.OnPopupListClickListener() {
            @Override
            public void onPopupListClick(View contextView, int contextPosition, String type, int position) {
                switch (type) {
                    case COPY:
                        ClipBoardUtil.copyText(base.mContext, base.mData.mMessage.getContent());
                        break;

                    case RECALL:
                        recallMessage(base.mContext, base.mData.mMessage);
                        break;

                    case FORWARD:
                        forwardMessage(base.mContext, base.mData.mMessage);
                        break;

                    case DEL:
                        deleteMessage(base.mData.mMessage, base.mAdapter);
                        break;

                    case MORE:
                        doMore(base.mAdapter, base);
                        break;

                    case COLLECT:
                        collectEmoji(base.mContext, base.mData.mMessage);
                        break;

                    case CREATE_SCHEDULE:
                        addToSchedule(base.mContext, base.mData.mMessage);
                        break;
                    case REPLY:
                        listener.onEvent(REPLY, base.mData.mMessage);
                        break;
                }
            }
        });
        popupHorizontalMenu.setIndicatorView(popupHorizontalMenu.getDefaultIndicatorView(32, 16, 0xFF212121));
    }

    // TODO: 2017/11/20 本地时间与服务器时间不准的话 会出现没有撤回按钮的问题
    private static boolean isRecall(CubeMessage message) {
        final long spaceTime = 2 * 60 * 1000;
        return System.currentTimeMillis() - message.getSendTimestamp() < spaceTime;
    }

    private static void doMore(ChatMessageAdapter adapter, BaseMsgViewHolder viewHolder) {
        adapter.mListPanel.initToolBar(true);
        adapter.isShowMore = true;
        adapter.notifyDataSetChanged();
    }

    /**
     * 转发消息
     *
     * @param context
     * @param message
     */
    private static void forwardMessage(Context context, CubeMessage message) {
        //        ForwardActivity.start(context, message.getMessageSN());
        //        Activity activity = (Activity) context;
        //        activity.overridePendingTransition(R.anim.activity_open, 0);
    }

    /**
     * 撤回消息
     *
     * @param context
     * @param message
     */
    private static void recallMessage(Context context, CubeMessage message) {
        long receiveTimestamp = message.getSendTimestamp();
        if (System.currentTimeMillis() - receiveTimestamp > RECALL_LIMIT_TIME) {
            ToastUtil.showToast(context, "发送时间超过两分钟的消息不能撤回。");
        }
        else {
            CubeEngine.getInstance().getMessageService().recallMessage(message.getMessageSN());
            //ToastUtil.showToast(context, "撤回失败");
        }
    }

    /**
     * 删除消息
     *
     * @param message
     * @param adapter
     */
    private static void deleteMessage(CubeMessage message, final ChatMessageAdapter adapter) {
        final int itemPosition = adapter.findCurrentPosition(message.getMessageSN());
        MessageManager.getInstance().deleteMessage(message).compose(RxSchedulers.<Boolean>io_main()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    adapter.remove(itemPosition);
                    adapter.refreshMsgNum();
                }
            }
        });
    }

    /**
     * 判断是否是最后一条消息
     *
     * @param adapter
     * @param itemPosition
     *
     * @return
     */
    private static boolean isLastItemMessage(ChatMessageAdapter adapter, int itemPosition) {
        return itemPosition == (adapter.getItemCount() - 1);
    }

    /**
     * 添加到日程（解密日程ID）
     *
     * @param context
     * @param message
     */
    private static void addToSchedule(Context context, CubeMessage message) {
        //        String data = message.getCardContentUrl();
        //        int index = message.getCardContentUrl().lastIndexOf("/");
        //        String scheIdEnc = data.substring((index + 1), data.length());
        //        long scheduleId = Long.valueOf(EncryptUtil.decrypt(scheIdEnc, "ZuoBiao-Qr"));
        //        CubeUI.getInstance().getCubeDataProvider().addToSchedule(context, scheduleId, scheIdEnc);
    }

    /**
     * 收藏表情，先判断本地是否存在后添加
     *
     * @param context
     * @param message
     */
    private static void collectEmoji(final Context context, final CubeMessage message) {
        //        File stickerFile = new File(context.getFilesDir() + "/sticker/" + CubeSpUtil.getCubeUser().getCubeId());
        //        if (!stickerFile.exists()) {
        //            stickerFile.mkdirs();
        //        }
        //        final File collectFile = new File(stickerFile.getPath() + "/collect");
        //        if (!collectFile.exists()) {
        //            collectFile.mkdirs();
        //        }
        //        final String emojiPath = collectFile.getPath() + "/" + message.getFileName();
        //        // 先检测本地是否存在该表情
        //        CubeEmojiRepository.getInstance().getEmojiSinglePackage(CubeSpUtil.getCubeUser().getCubeId()).compose(RxSchedulers.<CubeEmojiStructure>io_main()).subscribe(new ApiSubscriber<CubeEmojiStructure>(context) {
        //            @Override
        //            protected void _onNext(CubeEmojiStructure structure) {
        //                boolean exist = false;
        //                if (structure.collects != null && structure.collects.size() > 0) {
        //                    for (CubeEmojiCollect collect : structure.collects) {
        //                        if (collect.path.equals(emojiPath)) {
        //                            exist = true;
        //                            break;
        //                        }
        //                    }
        //                }
        //                if (exist) {
        //                    ToastUtil.showToast(context, "该表情已添加");
        //                }
        //                else {
        //                    createEmojiCollect(context, message.getFileUrl(), collectFile.getPath(), emojiPath);
        //                }
        //            }
        //
        //            @Override
        //            protected void _onError(String message) {}
        //
        //            @Override
        //            protected void _onCompleted() {}
        //        });
    }

    /**
     * 确定本地不存在的时候调用收藏表情
     *
     * @param context
     * @param url
     * @param packagePath
     * @param emojiPath
     */
    private static void createEmojiCollect(final Context context, final String url, final String packagePath, final String emojiPath) {
        //        new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                try {
        //                    URL urlStr = new URL(url);
        //                    InputStream inputStream = urlStr.openStream();
        //                    byte[] getData = readInputStream(inputStream);
        //                    File emojiFile = new File(emojiPath);
        //                    FileOutputStream fos = new FileOutputStream(emojiFile);
        //                    fos.write(getData);
        //                    if (fos != null) {
        //                        fos.close();
        //                    }
        //                    if (inputStream != null) {
        //                        inputStream.close();
        //                    }
        //                    CubeEmojiRepository.getInstance().insertOrUpdata(2, packagePath, emojiPath).subscribe(new ApiSubscriber<CubeEmojiStructure>(context) {
        //                        @Override
        //                        protected void _onNext(CubeEmojiStructure structure) {
        //                            Message msg = emojiHandler.obtainMessage();
        //                            msg.obj = context;
        //                            msg.what = 0;
        //                            emojiHandler.sendMessage(msg);
        //                        }
        //
        //                        @Override
        //                        protected void _onError(String message) {}
        //
        //                        @Override
        //                        protected void _onCompleted() {}
        //                    });
        //                } catch (Exception e) {
        //                    Message msg = emojiHandler.obtainMessage();
        //                    msg.obj = context;
        //                    msg.what = 1;
        //                    emojiHandler.sendMessage(msg);
        //                }
        //            }
        //        }).start();
    }

    /**
     * 获取字节
     *
     * @param inputStream
     *
     * @return
     *
     * @throws IOException
     */
    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 表情添加成功后UI的操作
     */
    private static Handler emojiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Context context = (Context) msg.obj;
            if (msg.what == 0) {
                ToastUtil.showToast(context, "表情添加成功");
                StickerManager.getInstance(context).refreshStickerType(null);
            }
            else {
                ToastUtil.showToast(context, "不支持的图片");
            }
        }
    };
}
