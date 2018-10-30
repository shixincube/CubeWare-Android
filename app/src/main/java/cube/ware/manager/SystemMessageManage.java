package cube.ware.manager;

import android.content.Context;
import android.text.TextUtils;

import cube.service.message.model.CustomMessage;
import cube.service.message.model.MessageEntity;
import cube.ware.data.model.dataModel.enmu.CubeCustomMessageType;

/**
 * 负责系统消息
 *
 * @author CloudZhang
 * @date 2017/9/13 11:40
 */

public class SystemMessageManage {
    private static final String TAG = SystemMessageManage.class.getSimpleName();

    private CubeCustomMessageType[] systemTypeArray = new CubeCustomMessageType[] {
        CubeCustomMessageType.VerificationUpdate, CubeCustomMessageType.RefuseInviteToGroup, CubeCustomMessageType.AddGroupMember, CubeCustomMessageType.DelGroupMember, CubeCustomMessageType.ApplyFriend, CubeCustomMessageType.AddFriend, CubeCustomMessageType.DismissGroup, CubeCustomMessageType.InviteToGroup, CubeCustomMessageType.AddGroupMember, CubeCustomMessageType.ApplyOrAgreeToGroup
    };

    private static SystemMessageManage sInstance = new SystemMessageManage();
    private Context mContext;

    public enum SystemSession {
        UNKNOWN("", "UNKNOWN"),             //未知类型
        SYSTEM("10000", "系统消息"),        //系统消息
        SPAP_ASSISTANT("10001", "司派助手"),//司派助手
        VERIFY("10002", "验证消息");        //验证消息

        private String mSessionId;
        private String mSessionName;

        SystemSession(String sessionId, String sessionName) {
            mSessionId = sessionId;
            mSessionName = sessionName;
        }

        public String getSessionId() {
            return mSessionId;
        }

        public String getSessionName() {
            return mSessionName;
        }

        public SystemSession parse(String sessionId) {
            switch (sessionId) {
                case "10001":
                    return VERIFY;
                default:
                    return UNKNOWN;
            }
        }
    }

    public static SystemMessageManage getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void release() {
        mContext = null;
    }

    /**
     * 是否是系统验证消息
     *
     * @param messageEntity
     *
     * @return
     */
    public boolean isFromVerify(MessageEntity messageEntity) {
        if (messageEntity == null || !(messageEntity instanceof CustomMessage)) {
            return false;
        }
        CustomMessage customMessage = (CustomMessage) messageEntity;
        String operate = customMessage.getHeader("operate");
        if (TextUtils.isEmpty(operate)) {
            return false;
        }
        for (CubeCustomMessageType cubeCustomMessageType : systemTypeArray) {
            if (operate.equals(cubeCustomMessageType.VerificationUpdate.getType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是系统会话
     *
     * @param sessionId
     *
     * @return
     */
    public boolean isSystemSessionId(String sessionId) {
        SystemSession[] values = SystemSession.values();
        for (SystemSession value : values) {
            if (value.getSessionId().equals(sessionId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是添加好友
     * @param messageEntity
     * @return
     */
    public boolean isAddFriend(MessageEntity messageEntity) {
        if (messageEntity == null || !(messageEntity instanceof CustomMessage)) {
            return false;
        }
        CustomMessage customMessage = (CustomMessage) messageEntity;
        String operate = customMessage.getHeader("operate");
        if (TextUtils.isEmpty(operate)) {
            return false;
        }
        for (CubeCustomMessageType cubeCustomMessageType : systemTypeArray) {
            if (operate.equals(cubeCustomMessageType.AddFriend.getType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是CustomTips消息
     *
     * @param messageEntity
     *
     * @return
     */
    //public boolean isFromCustomTips(MessageEntity messageEntity) {
    //    if (messageEntity == null || !(messageEntity instanceof CustomMessage)) {
    //        return false;
    //    }
    //    if (!"10001".equals(messageEntity.getSender().getCubeId())) {
    //        return false;
    //    }
    //    CustomMessage customMessage = (CustomMessage) messageEntity;
    //    String operate = customMessage.getHeader("operate");
    //    if (TextUtils.isEmpty(operate)) {
    //        return false;
    //    }
    //    //for (CubeCustomMessageType cubeCustomMessageType : systemTypeArray) {
    //    //    if (operate.equals(cubeCustomMessageType.getType())) {
    //    //        return true;
    //    //    }
    //    //}
    //    switch (operate) {
    //        case "apply_conference":
    //            return true;
    //        case "close_conference":
    //            return true;
    //        case "new_group":
    //            return true;
    //        case "add_group_member":
    //            return true;
    //    }
    //    return false;
    //}
}
