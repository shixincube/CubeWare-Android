package cube.ware.service.message.chat.message.Listener;

import java.util.HashMap;
import java.util.List;

import cube.service.common.model.CubeError;
import cube.service.message.MessageListener;
import cube.service.message.model.MessageEntity;

/**
 * @author CloudZhang
 * @date 2017/9/23 16:16
 */

public class MessageListenerAdapter implements MessageListener {
    @Override
    public void onMessageSent(MessageEntity messageEntity) {

    }

    @Override
    public void onMessageUploading(MessageEntity messageEntity, long l, long l1) {

    }

    @Override
    public void onMessageUploadCompleted(MessageEntity messageEntity) {

    }

    @Override
    public void onMessageDownloading(MessageEntity messageEntity, long l, long l1) {

    }

    @Override
    public void onMessageDownloadCompleted(MessageEntity messageEntity) {

    }

    @Override
    public void onMessageRecalled(MessageEntity messageEntity) {

    }

    @Override
    public void onMessageReceived(MessageEntity messageEntity) {

    }

    @Override
    public void onMessageFailed(MessageEntity messageEntity, CubeError cubeError) {

    }

    @Override
    public void onMessageCanceled(MessageEntity messageEntity) {

    }

    @Override
    public void onMessagePaused(MessageEntity messageEntity) {

    }

    @Override
    public void onMessageResumed(MessageEntity messageEntity) {

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
}
