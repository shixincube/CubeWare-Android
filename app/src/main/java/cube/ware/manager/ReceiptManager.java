package cube.ware.manager;


import com.common.mvp.rx.RxBus;
import com.common.utils.utils.log.LogUtil;

import java.util.List;

import cube.service.common.model.DeviceInfo;
import cube.service.message.model.MessageEntity;
import cube.ware.data.repository.CubeMessageRepository;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.eventbus.Event;
import cube.ware.utils.SpUtil;
import rx.functions.Action1;

/**
 * 拦截消息处理流程中关于回执的部分
 *
 * @author CloudZhang
 * @date 2018/1/25 14:16
 */

public class ReceiptManager {
    private static final String          TAG                = ReceiptManager.class.getSimpleName();
    //该缓存的作用：  因为其他终端会出现回执消息非常快的情况 导致我们收到消息后入库还没有成功立马收到了回执或者先收到回执才收到消息
    private              MessageEntity[] mReceiptedMessages = new MessageEntity[5];

    public static ReceiptManager getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final ReceiptManager INSTANCE = new ReceiptManager();
    }

    public void onReceiptedAll(MessageEntity messageEntity, DeviceInfo deviceInfo) {
        LogUtil.i(TAG, "onReceiptedAll==> 收到回执消息回执");
        String senderId = messageEntity.getSender().getCubeId();
        String receiverId = messageEntity.getReceiver().getCubeId();
        boolean isSelf = senderId.equals(SpUtil.getCubeId());
        final String sessionId;
        boolean isGroup;
        if (messageEntity.isGroupMessage()) {
            sessionId = messageEntity.getGroupId();
            isGroup = true;
        }
        else {
            sessionId = isSelf ? receiverId : senderId;
            isGroup = false;
        }
        //收到回执后缓存一下
        System.arraycopy(mReceiptedMessages, 1, mReceiptedMessages, 0, mReceiptedMessages.length - 1);
        mReceiptedMessages[mReceiptedMessages.length - 1] = messageEntity;
        updateIsReceipted(sessionId,messageEntity.getTimestamp(),false);

    }

    /**
     * 更新本地数据库消息回执
     * @param chatId
     * @param time
     * @param isReceipted
     */
    public void updateIsReceipted(String chatId, long time, boolean isReceipted) {
        CubeMessageRepository.getInstance().updateIsReceipted(chatId, time, isReceipted).subscribe(new Action1<List<CubeMessage>>() {
            @Override
            public void call(List<CubeMessage> cubeMessages) {
                // 消息变化通知最近消息界面
                RxBus.getInstance().post(Event.EVENT_REFRESH_RECENT_SESSION_SINGLE, chatId);
            }
        });
    }

    /**
     * 是否是已经回执过的消息
     */
    public boolean isAlreadyReceiptedMessage(long timestamp) {
        for (int i = mReceiptedMessages.length - 1; i >= 0; i--) { //反向遍历命中概率更大
            MessageEntity receiptedMessage = mReceiptedMessages[i];
            if (receiptedMessage == null) {
                continue;
            }
            if (timestamp <= receiptedMessage.getTimestamp()) {
                return true;
            }
        }
        return false;
    }

    public void release() {
        if (mReceiptedMessages != null) {
            for (int i = 0; i < mReceiptedMessages.length; i++) {
                mReceiptedMessages[i] = null;
            }
        }
    }
}
