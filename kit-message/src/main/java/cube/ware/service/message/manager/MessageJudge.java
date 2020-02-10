package cube.ware.service.message.manager;

import android.text.TextUtils;
import cube.service.message.CustomMessage;
import cube.service.message.MessageEntity;
import cube.ware.common.MessageConstants;
import cube.ware.data.model.dataModel.enmu.CubeCustomMessageType;

/**
 * 消息类型和会话判断
 *
 * @author LiuFeng
 * @data 2020/2/10 20:19
 */
public class MessageJudge {

    /**
     * 是否是系统会话
     *
     * @param sessionId
     *
     * @return
     */
    public static boolean isSystemSession(String sessionId) {
        return TextUtils.equals(MessageConstants.SystemSession.SYSTEM, sessionId);
    }

    /**
     * 是否是应用助手
     *
     * @param sessionId
     *
     * @return
     */
    public static boolean isAssistantSession(String sessionId) {
        return TextUtils.equals(MessageConstants.SystemSession.ASSISTANT, sessionId);
    }

    /**
     * 是否是验证会话
     *
     * @param sessionId
     *
     * @return
     */
    public static boolean isVerifySession(String sessionId) {
        return TextUtils.equals(MessageConstants.SystemSession.VERIFY, sessionId);
    }

    /**
     * 是否是系统验证消息
     *
     * @param messageEntity
     *
     * @return
     */
    public static boolean isVerifyUpdateMessage(MessageEntity messageEntity) {
        return isType(messageEntity, CubeCustomMessageType.VerificationUpdate);
    }

    /**
     * 是否是添加好友
     *
     * @param messageEntity
     *
     * @return
     */
    public static boolean isAddFriend(MessageEntity messageEntity) {
        return isType(messageEntity, CubeCustomMessageType.AddFriend);
    }

    /**
     * 判断是否给定自定义消息类型
     *
     * @param message
     * @param type
     *
     * @return
     */
    private static boolean isType(MessageEntity message, CubeCustomMessageType type) {
        if (!(message instanceof CustomMessage)) {
            return false;
        }

        CustomMessage customMessage = (CustomMessage) message;
        String operate = customMessage.getHeader("operate");
        if (TextUtils.isEmpty(operate)) {
            return false;
        }

        return operate.equals(type.getType());
    }
}
