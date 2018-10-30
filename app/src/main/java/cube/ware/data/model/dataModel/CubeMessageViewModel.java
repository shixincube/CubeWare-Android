package cube.ware.data.model.dataModel;

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
        String messageType = mMessage.getMessageType();
        if (messageType.equals(CubeMessageType.Text.getType())) {
            return AppConstants.MessageType.CHAT_TXT;
        }
        else if (messageType.equals(CubeMessageType.Emoji.getType())) {
            return AppConstants.MessageType.CHAT_EMOJI;
        }
        else if (messageType.equals(CubeMessageType.File.getType())) {
            return AppConstants.MessageType.CHAT_FILE;
        }
        else if (messageType.equals(CubeMessageType.Image.getType())) {
            return AppConstants.MessageType.CHAT_IMAGE;
        }
        else if (messageType.equals(CubeMessageType.Voice.getType())) {
            return AppConstants.MessageType.CHAT_AUDIO;
        }
        else if (messageType.equals(CubeMessageType.Video.getType())) {
            return AppConstants.MessageType.CHAT_VIDEO;
        }
        else if (messageType.equals(CubeMessageType.Whiteboard.getType())) {
            return AppConstants.MessageType.CHAT_WHITEBOARD;
        }
        else if (messageType.equals(CubeMessageType.CustomTips.getType())) {
            return AppConstants.MessageType.CUSTOM_TIPS;
        }
        else if (messageType.equals(CubeMessageType.CustomCallVideo.getType())) {
            return AppConstants.MessageType.CUSTOM_CALL_VIDEO;
        }
        else if (messageType.equals(CubeMessageType.CustomCallAudio.getType())) {
            return AppConstants.MessageType.CUSTOM_CALL_AUDIO;
        }
        else if (messageType.equals(CubeMessageType.CustomShare.getType())) {
            return AppConstants.MessageType.CUSTOM_SHARE;
        }
        else if (messageType.equals(CubeMessageType.CustomShake.getType())) {
            return AppConstants.MessageType.CUSTOM_SHAKE;
        }
        else if (messageType.equals(CubeMessageType.CARD.getType())) {
            return AppConstants.MessageType.CHAT_CARD;
        }
        else if (messageType.equals(CubeMessageType.RICHTEXT.getType())) {
            return AppConstants.MessageType.CHAT_RICH_TEXT;
        }
        else if(messageType.equals(CubeMessageType.ServiceNumber.getType())){
            return AppConstants.MessageType.SERVICE_NUMBER;
        }
        else if (messageType.equals(CubeMessageType.GroupShareCard.getType())) {
            return AppConstants.MessageType.GroupShareCard;
        }
        else if (messageType.equals(CubeMessageType.UserShareCard.getType())) {
            return AppConstants.MessageType.UserShareCard;
        }
        else if (messageType.equals(CubeMessageType.GroupTaskNew.getType())) {
            return AppConstants.MessageType.GroupTaskNew;
        }
        else if (messageType.equals(CubeMessageType.GroupTaskComplete.getType())) {
            return AppConstants.MessageType.GroupTaskComplete;
        }
        else if (messageType.equals(CubeMessageType.RECALLMESSAGETIPS.getType())) {
            return AppConstants.MessageType.RECALL_MESSAGE_TIPS;
        }
        else if (messageType.equals(CubeMessageType.REPLYMESSAGE.getType())) {
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
