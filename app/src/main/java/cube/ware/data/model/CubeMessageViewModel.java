package cube.ware.data.model;

import android.text.TextUtils;

import java.io.Serializable;

import cube.ware.AppConstants;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.widget.recyclerview.entity.MultiItemEntity;

/**
 * 消息列表展示
 *
 * @author Wangxx
 * @date 2017/1/18
 */

public class CubeMessageViewModel implements Serializable, MultiItemEntity {
    public CubeMessage mMessage;
    public String      userNme;
    public String      userFace;
    public String      remark;

    @Override
    public int getItemType() {
        CubeMessageType messageType = mMessage.getMessageType();
        if (messageType == CubeMessageType.Text) {
            return AppConstants.MessageType.CHAT_TXT;
        }
        else if (messageType == CubeMessageType.Emoji) {
            return AppConstants.MessageType.CHAT_EMOJI;
        }
        else if (messageType == CubeMessageType.File) {
            return AppConstants.MessageType.CHAT_FILE;
        }
        else if (messageType == CubeMessageType.Image) {
            return AppConstants.MessageType.CHAT_IMAGE;
        }
        else if (messageType == CubeMessageType.Voice) {
            return AppConstants.MessageType.CHAT_AUDIO;
        }
        else if (messageType == CubeMessageType.Video) {
            return AppConstants.MessageType.CHAT_VIDEO;
        }
        else if (messageType == CubeMessageType.Whiteboard) {
            return AppConstants.MessageType.CHAT_WHITEBOARD;
        }
        else if (messageType == CubeMessageType.CustomTips) {
            return AppConstants.MessageType.CUSTOM_TIPS;
        }
        else if (messageType == CubeMessageType.CustomCallVideo) {
            return AppConstants.MessageType.CUSTOM_CALL_VIDEO;
        }
        else if (messageType == CubeMessageType.CustomCallAudio) {
            return AppConstants.MessageType.CUSTOM_CALL_AUDIO;
        }
        else if (messageType == CubeMessageType.CustomShare) {
            return AppConstants.MessageType.CUSTOM_SHARE;
        }
        else if (messageType == CubeMessageType.CustomShake) {
            return AppConstants.MessageType.CUSTOM_SHAKE;
        }
        else if (messageType == CubeMessageType.CARD) {
            return AppConstants.MessageType.CHAT_CARD;
        }
        else if (messageType == CubeMessageType.RICHTEXT) {
            return AppConstants.MessageType.CHAT_RICH_TEXT;
        }
        else if(messageType == CubeMessageType.ServiceNumber){
            return AppConstants.MessageType.SERVICE_NUMBER;
        }
        else if (messageType == CubeMessageType.GroupShareCard) {
            return AppConstants.MessageType.GroupShareCard;
        }
        else if (messageType == CubeMessageType.UserShareCard) {
            return AppConstants.MessageType.UserShareCard;
        }
        else if (messageType == CubeMessageType.GroupTaskNew) {
            return AppConstants.MessageType.GroupTaskNew;
        }
        else if (messageType == CubeMessageType.GroupTaskComplete) {
            return AppConstants.MessageType.GroupTaskComplete;
        }
        else if (messageType == CubeMessageType.RECALLMESSAGETIPS) {
            return AppConstants.MessageType.RECALL_MESSAGE_TIPS;
        }
        else if (messageType == CubeMessageType.REPLYMESSAGE) {
            return AppConstants.MessageType.REPLY_MESSAGE;
        }
        else {
            return AppConstants.MessageType.UNKNOWN;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (null == o) {
            return false;
        }
        if (o instanceof CubeMessageViewModel) {
            CubeMessageViewModel message = (CubeMessageViewModel) o;
            return this.mMessage.getMessageSN() == message.mMessage.getMessageSN();
        }
        return false;
    }

    /**
     * 是否是群消息
     *
     * @return
     */
    public boolean isGroupMessage() {
        return !TextUtils.isEmpty(this.mMessage.getGroupId());
    }

    /**
     * 是否是接收的消息
     *
     * @return
     */
    public boolean isReceivedMessage() {
        return this.mMessage.isReceivedMessage();
    }

    @Override
    public String toString() {
        return "CubeMessageViewModel{" + "mMessage=" + mMessage + ", userNme='" + userNme + '\'' + ", userFace='" + userFace + '\'' + '}';
    }
}
