package cube.ware.data.mapper;

import android.text.TextUtils;
import com.common.utils.log.LogUtil;
import cube.service.message.CustomMessage;
import cube.service.message.FileMessage;
import cube.service.message.ImageMessage;
import cube.service.message.MessageDirection;
import cube.service.message.MessageEntity;
import cube.service.message.Sender;
import cube.service.message.TextMessage;
import cube.service.message.VideoClipMessage;
import cube.service.message.VoiceClipMessage;
import cube.ware.common.MessageConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeMessageDirection;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.room.model.CubeRecentSession;
import cube.ware.service.message.manager.MessageJudge;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: LiuFeng
 * @data: 2020/2/10
 */
public class SessionMapper {
    /**
     * 批量将MessageEntity转化为CubeRecentSession
     *
     * @param messages
     *
     * @return
     */
    public static List<CubeRecentSession> convertTo(List<MessageEntity> messages) {
        List<CubeRecentSession> list = new ArrayList<>();
        for (MessageEntity messageEntity : messages) {
            try {
                CubeRecentSession cubeRecentSession = convertTo(messageEntity);
                if (cubeRecentSession != null) {
                    list.add(cubeRecentSession);
                }
            } catch (Exception e) {
                LogUtil.e("将MessageEntity转换为CubeRecentSession出错");
                e.printStackTrace();
                return list;
            }
        }
        return list;
    }

    /**
     * 将MessageEntity转化为CubeRecentSession
     *
     * @param messageEntity
     *
     * @return
     */
    public static CubeRecentSession convertTo(MessageEntity messageEntity) {
        if (null == messageEntity) {
            throw new IllegalArgumentException("MessageEntity can't be null");
        }
        String senderId;
        String receiverId = messageEntity.getReceiver().getCubeId();
        if (MessageJudge.isAddFriend(messageEntity)) {
            CustomMessage customMessage = (CustomMessage) messageEntity;
            String applyCube = customMessage.getHeader("applyUserCube");
            String acceptCube = customMessage.getHeader("acceptUserCube");
            if (TextUtils.equals(applyCube, CubeCore.getInstance().getCubeId())) {
                senderId = acceptCube;
            }
            else {
                senderId = applyCube;
            }
        }
        else {
            senderId = messageEntity.getSender().getCubeId();
        }
        if ((senderId.equals(receiverId) && senderId.equals(CubeCore.getInstance().getCubeId()))) {
            //业务层不显示自己发给自己的消息
            return null;
        }
        try {
            CubeRecentSession cubeRecentSession = new CubeRecentSession();
            if (messageEntity.isGroupMessage()) {
                buildGroupRecentSession(messageEntity, cubeRecentSession);
            }
            else {
                if (TextUtils.isEmpty(CubeCore.getInstance().getCubeId())) {
                    LogUtil.e("将MessageEntity转换为CubeRecentSession出错 for myCubeId is null");
                    return null;
                }
                boolean isSelf = senderId.equals(CubeCore.getInstance().getCubeId());
                String sessionId;
                if (messageEntity.isAnonymous()) {
                    sessionId = "secret_chat";
                }
                else {
                    sessionId = isSelf ? receiverId : senderId;
                }
                cubeRecentSession.setSessionId(sessionId);
                String sessionName;
                String receiverName = messageEntity.getReceiver().getDisplayName();
                String senderName = messageEntity.getSender().getDisplayName();
                sessionName = isSelf ? receiverName : senderName;
                LogUtil.i("sessionName -------> " + sessionName);
                cubeRecentSession.setSessionName(TextUtils.isEmpty(sessionName) ? sessionId : sessionName);
                if (sessionId.equals(CubeCore.getInstance().getCubeId()) || MessageJudge.isSystemSession(senderId)) {
                    //业务层不显示自己发给自己的消息   一对一不显示自定义消息
                    return null;
                }
                buildP2PRecentSession(messageEntity, cubeRecentSession);
            }

            cubeRecentSession.setContent(getContent(messageEntity));
            cubeRecentSession.setMessageDirection(getDirection(messageEntity));
            cubeRecentSession.setTimestamp(messageEntity.getTimestamp());
            cubeRecentSession.setTop(false);
            return cubeRecentSession;
        } catch (Exception e) {
            LogUtil.e("将MessageEntity转换为CubeRecentSession出错");
            e.printStackTrace();
            return null;
        }
    }

    private static String getContent(MessageEntity messageEntity) {
        Sender sender = messageEntity.getSender();
        String content = "";
        if (messageEntity.isGroupMessage() && !TextUtils.equals(sender.getCubeId(), CubeCore.getInstance().getCubeId())) {
            String displayName = TextUtils.isEmpty(sender.getDisplayName()) ? sender.getCubeId() : sender.getDisplayName();
            content = displayName + ": ";
        }
        if (messageEntity.isRecalled()) {
            content += "[撤回消息]";
        }
        else if (messageEntity instanceof TextMessage) {
            content += ((TextMessage) messageEntity).getContent();
        }
        else if (messageEntity instanceof ImageMessage) {
            content += "[图片消息]";
        }
        else if (messageEntity instanceof VoiceClipMessage) {
            content += "[语音消息]";
        }
        else if (messageEntity instanceof VideoClipMessage) {
            content += "[视频消息]";
        }
        else if (messageEntity instanceof CustomMessage) {
            content = "[系统消息]";
        }
        else if (messageEntity instanceof FileMessage) {
            content += "[文件消息]";
        }
        else {
            content += "[未知消息]";
        }

        return content;
    }

    /**
     * 构建群组RecentSession
     *
     * @param messageEntity
     * @param cubeRecentSession
     */
    private static void buildGroupRecentSession(MessageEntity messageEntity, CubeRecentSession cubeRecentSession) {
        cubeRecentSession.setSessionId(messageEntity.getGroupId());
        String displayName = messageEntity.getGroup() == null ? messageEntity.getReceiver().getDisplayName() : messageEntity.getGroup().getDisplayName();
        cubeRecentSession.setSessionName(TextUtils.isEmpty(displayName) ? messageEntity.getGroupId() : displayName);
        cubeRecentSession.setSessionType(CubeSessionType.Group.getType());
    }

    /**
     * 构建P2PRecentSession
     *
     * @param messageEntity
     * @param cubeRecentSession
     */
    private static void buildP2PRecentSession(MessageEntity messageEntity, CubeRecentSession cubeRecentSession) {
        if (MessageJudge.isVerifySession(messageEntity.getSender().getCubeId()) && MessageJudge.isVerifyUpdateMessage(messageEntity)) {
            cubeRecentSession.setSessionId(MessageConstants.SystemSession.VERIFY);
        }
        if (messageEntity.isAnonymous()) {
            cubeRecentSession.setSessionType(CubeSessionType.Secret.getType());
        }
        else {
            cubeRecentSession.setSessionType(CubeSessionType.P2P.getType());
        }
    }

    /**
     * 获取消息方向
     *
     * @param entity
     *
     * @return
     */
    public static int getDirection(MessageEntity entity) {
        if (entity.getDirection() == MessageDirection.Sent) {
            return CubeMessageDirection.Sent.getDirection();
        }
        else if (entity.getDirection() == MessageDirection.Received) {
            return CubeMessageDirection.Received.getDirection();
        }
        else {
            return CubeMessageDirection.None.getDirection();
        }
    }
}
