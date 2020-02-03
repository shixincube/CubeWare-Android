package cube.ware.service.message.chat.message.Listener;

import cube.service.DeviceInfo;
import java.util.HashMap;
import java.util.List;

import cube.service.CubeError;
import cube.service.message.MessageListener;
import cube.service.message.MessageEntity;

/**
 * @author CloudZhang
 * @date 2017/9/23 16:16
 */

public class MessageListenerAdapter implements MessageListener {

    @Override
    public void onSent(MessageEntity messageEntity) {

    }

    @Override
    public void onUploading(MessageEntity messageEntity, long l, long l1) {

    }

    @Override
    public void onUploadCompleted(MessageEntity messageEntity) {

    }

    @Override
    public void onDownloading(MessageEntity messageEntity, long l, long l1) {

    }

    @Override
    public void onDownloadCompleted(MessageEntity messageEntity) {

    }

    @Override
    public void onForwarded(List<MessageEntity> list, List<MessageEntity> list1) {

    }

    @Override
    public void onRecalled(MessageEntity messageEntity) {

    }

    @Override
    public void onReceiptedAll(String s, long l, DeviceInfo deviceInfo) {

    }

    @Override
    public void onReceipted(List<MessageEntity> list, DeviceInfo deviceInfo) {

    }

    @Override
    public void onReceived(MessageEntity messageEntity) {

    }

    @Override
    public void onMessageFailed(MessageEntity messageEntity, CubeError cubeError) {

    }

    @Override
    public void onFileMessageFailed(boolean b, MessageEntity messageEntity, CubeError cubeError) {

    }

    @Override
    public void onMessageCanceled(MessageEntity messageEntity) {

    }

    @Override
    public void onUploadStart(MessageEntity messageEntity) {

    }

    @Override
    public void onDownloadStart(MessageEntity messageEntity) {

    }

    @Override
    public void onUploadPaused(MessageEntity messageEntity) {

    }

    @Override
    public void onDownloadPaused(MessageEntity messageEntity) {

    }

    @Override
    public void onResumed(MessageEntity messageEntity) {

    }

    @Override
    public void onMessageSyncBegin() {

    }

    @Override
    public void onMessagesSyncing(HashMap<String, List<MessageEntity>> hashMap) {

    }

    @Override
    public void onMessageSyncEnd() {

    }

    @Override
    public long getSyncBeginTime() {
        return 0;
    }
}
