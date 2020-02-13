package cube.ware.service.message;

import android.support.annotation.NonNull;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.message.FileMessage;
import cube.service.message.FileMessageStatus;
import cube.service.message.MessageEntity;
import cube.service.message.ReplyMessage;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.repository.CubeMessageRepository;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.recent.manager.RecentSessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 消息api
 *
 * @author LiuFeng
 * @data 2020/2/13 18:30
 */
public class MessageApi {

    /**
     * 发送消息 封装引擎发送消息方法
     *
     * @param messageEntity
     */
    public static Observable<CubeMessage> sendMessage(@NonNull final MessageEntity messageEntity) {
        LogUtil.d("sendMessage --> sn: " + messageEntity.getSerialNumber());
        if (messageEntity instanceof FileMessage) {
            ((FileMessage) messageEntity).setFileStatus(FileMessageStatus.Uploading);
        }

        return CubeMessageRepository.getInstance().addMessage(messageEntity).map(new Func1<CubeMessage, CubeMessage>() {
            @Override
            public CubeMessage call(CubeMessage cubeMessage) {
                CubeEngine.getInstance().getMessageService().sendMessage(messageEntity);
                RecentSessionManager.getInstance().addOrUpdateRecentSession(messageEntity);
                return cubeMessage;
            }
        });
    }

    /**
     * 回复消息
     *
     * @param cubeMessage
     * @param reply
     */
    public static void replyMessage(CubeMessage cubeMessage, MessageEntity reply) {
        try {
            JSONObject jsonObject = new JSONObject(cubeMessage.getReplyContentJson());
            MessageEntity source = CubeEngine.getInstance().getMessageService().parseMessage(jsonObject.getString("source"));
            // 构建回复消息
            ReplyMessage message = new ReplyMessage(source, reply, reply.getReceiver(), reply.getSender(), reply.getSerialNumber());
            CubeEngine.getInstance().getMessageService().sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重发消息
     *
     * @param messageSn
     */
    public static void resendMessage(long messageSn) {
        CubeEngine.getInstance().getMessageService().reSendMessage(messageSn);
    }

    /**
     * 继续发送或者继续下载文件消息
     *
     * @param messageSN
     */
    public static void resumeMessage(long messageSN) {
        CubeEngine.getInstance().getMessageService().resumeMessage(messageSN);
    }

    /**
     * 删除数据库中的一条消息
     *
     * @param message
     */
    public static Observable<Boolean> deleteMessage(@NonNull final CubeMessage message) {
        LogUtil.i("deleteMessage --> sessionId: " + message.getChatId() + " sn: " + message.getMessageSN());
        return CubeMessageRepository.getInstance().deleteMessageBySN(message.getMessageSN()).doOnNext(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (message.getMessageType() == CubeMessageType.File) {
                    CubeEngine.getInstance().getMessageService().pauseMessage(message.getMessageSN());
                }
            }
        });
    }
}
