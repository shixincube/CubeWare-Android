package cube.ware.data.room.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cube.ware.AppConstants;
import cube.ware.data.model.HeaderMap;
import cube.ware.data.model.dataModel.enmu.CubeFileMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageDirection;
import cube.ware.data.model.dataModel.enmu.CubeMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.service.message.MessageHandle;
import cube.ware.ui.chat.message.Listener.FileMessageDownloadListener;
import cube.ware.ui.chat.message.Listener.FileMessageUploadListener;
import cube.ware.utils.SpUtil;
import cube.ware.widget.recyclerview.entity.MultiItemEntity;

/**
 * Created by dth
 * Des: 应用层消息表，根据引擎MessageEntity转换而来
 * Date: 2018/8/30.
 */
@Entity
public class CubeMessage implements Serializable, MultiItemEntity {
    /**
     * 消息唯一序列号
     */
    @PrimaryKey
    private long messageSN;

    /**
     * 消息类型{@link CubeMessageType}
     */
    private String messageType;

    /**
     * 消息状态{@link CubeMessageStatus}
     */
    private int messageStatus;

    /**
     * 消息方向{@link CubeMessageDirection}
     */
    private int messageDirection = -1;

    /**
     * 发送者cube号
     */
    private String senderId;

    /**
     * 发送者名字
     */
    private String senderName;

    /**
     * 接收者cube号
     */
    private String receiverId;

    /**
     * 群组cube号
     */
    private String groupId;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 发送的时间戳
     */
    private long sendTimestamp;

    /**
     * 接收的时间戳
     */
    private long receiveTimestamp;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件url
     */
    private String fileUrl;

    /**
     * 处理大小
     */
    private long processedSize = 0L;

    /**
     * 文件总大小
     */
    private long fileSize = 0L;

    /**
     * 文件消息状态{@link CubeFileMessageStatus}
     */
    private int fileMessageStatus;

    /**
     * 最后修改时间
     */
    private long lastModified = 0L;

    /**
     * 图片的宽度
     */
    private int imgWidth = 0;

    /**
     * 图片的高度
     */
    private int imgHeight = 0;

    /**
     * 缩略图路径
     */
    private String thumbPath;

    /**
     * 缩略图url
     */
    private String thumbUrl;

    /**
     * 文本消息内容/Tips消息内容
     */
    private String content;

    /**
     * 贴图表情 格式[cube_emoji:key]
     */
    private String emojiContent;

    /**
     * 语音/视频时长
     */
    private int duration = 0;

    /**
     * 消息是否回执
     */
    public boolean isReceipt;

    /**
     * 是否为匿名消息
     */
    private boolean isAnonymous;

    /**
     * 匿名消息的时间戳
     */
    private long anonymousTimestamp;

    /**
     * 消息失效的时间戳
     */
    private long invalidTimestamp;

    /**
     * 消息是否已读
     */
    public boolean isRead;

    /**
     * 语音消息是否播放
     */
    public boolean isPlay;

    /**
     * 消息是否显示发送或接收时间
     */
    private boolean isShowTime;

    /**
     * 自定义消息操作类型
     */
    private String operate;

    /**
     * 对应卡片消息的title
     */
    private String cardTitle;

    /**
     * 对应卡片消息的icon
     */
    private String cardIcon;

    /**
     * 对应卡片消息的json
     */
    private String cardContentJson;

    /**
     * 对应回复消息的source和reply
     */
    private String replyContentJson;

    /**
     * 消息 Headers
     * JSONArray 类型
     */
    private String customHeaders;

    public String getEmojiContent() {
        return emojiContent;
    }

    public void setEmojiContent(String emojiContent) {
        this.emojiContent = emojiContent;
    }

    public long getMessageSN() {
        return messageSN;
    }

    public void setMessageSN(long messageSN) {
        this.messageSN = messageSN;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public int getMessageDirection() {
        return messageDirection;
    }

    public void setMessageDirection(int messageDirection) {
        this.messageDirection = messageDirection;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getSendTimestamp() {
        return sendTimestamp;
    }

    public void setSendTimestamp(long sendTimestamp) {
        this.sendTimestamp = sendTimestamp;
    }

    public long getReceiveTimestamp() {
        return receiveTimestamp;
    }

    public void setReceiveTimestamp(long receiveTimestamp) {
        this.receiveTimestamp = receiveTimestamp;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getProcessedSize() {
        return processedSize;
    }

    public void setProcessedSize(long processedSize) {
        this.processedSize = processedSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getFileMessageStatus() {
        return fileMessageStatus;
    }

    public void setFileMessageStatus(int fileMessageStatus) {
        this.fileMessageStatus = fileMessageStatus;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isReceipt() {
        return isReceipt;
    }

    public void setReceipt(boolean receipt) {
        isReceipt = receipt;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public long getAnonymousTimestamp() {
        return anonymousTimestamp;
    }

    public void setAnonymousTimestamp(long anonymousTimestamp) {
        this.anonymousTimestamp = anonymousTimestamp;
    }

    public long getInvalidTimestamp() {
        return invalidTimestamp;
    }

    public void setInvalidTimestamp(long invalidTimestamp) {
        this.invalidTimestamp = invalidTimestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public boolean isShowTime() {
        return isShowTime;
    }

    public void setShowTime(boolean showTime) {
        isShowTime = showTime;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public String getCardIcon() {
        return cardIcon;
    }

    public void setCardIcon(String cardIcon) {
        this.cardIcon = cardIcon;
    }

    public String getCardContentJson() {
        return cardContentJson;
    }

    public void setCardContentJson(String cardContentJson) {
        this.cardContentJson = cardContentJson;
    }

    public String getCustomHeaders() {
        return this.customHeaders;
    }

    public List<HeaderMap> getCustomHeaderMap() {
        if (TextUtils.isEmpty(customHeaders)) {
            return new ArrayList<HeaderMap>();
        }
        return new Gson().fromJson(customHeaders, new TypeToken<ArrayList<HeaderMap>>() {}.getType());
    }

    public void setCustomHeaders(String customHeaders) {
        this.customHeaders = customHeaders;
    }

    public String getCardContentName() {
        String cardContentName = "";
        try {
            JSONArray jsonArray = new JSONArray(cardContentJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                cardContentName = jsonObject.optString("name");
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cardContentName;
    }

    public String getCardContentIcon() {
        String cardContentIcon = "";
        try {
            JSONArray jsonArray = new JSONArray(cardContentJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                cardContentIcon = jsonObject.optString("icon");
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cardContentIcon;
    }

    public String getCardContentUrl() {
        String cardContentUrl = "";
        try {
            JSONArray jsonArray = new JSONArray(cardContentJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                cardContentUrl = jsonObject.optString("url");
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cardContentUrl;
    }

    @Override
    public int getItemType() {
        if (this.messageType.equals(CubeMessageType.Text.getType())) {
            return AppConstants.MessageType.CHAT_TXT;
        }
        else if (this.messageType.equals(CubeMessageType.File.getType())) {
            return AppConstants.MessageType.CHAT_FILE;
        }
        else if (this.messageType.equals(CubeMessageType.Image.getType())) {
            return AppConstants.MessageType.CHAT_IMAGE;
        }
        else if (this.messageType.equals(CubeMessageType.Voice.getType())) {
            return AppConstants.MessageType.CHAT_AUDIO;
        }
        else if (this.messageType.equals(CubeMessageType.Video.getType())) {
            return AppConstants.MessageType.CHAT_VIDEO;
        }
        else if (this.messageType.equals(CubeMessageType.Whiteboard.getType())) {
            return AppConstants.MessageType.CHAT_WHITEBOARD;
        }
        else if (this.messageType.equals(CubeMessageType.CustomTips.getType())) {
            return AppConstants.MessageType.CUSTOM_TIPS;
        }
        else if (this.messageType.equals(CubeMessageType.CustomCallVideo.getType())) {
            return AppConstants.MessageType.CUSTOM_CALL_VIDEO;
        }
        else if (this.messageType.equals(CubeMessageType.CustomCallAudio.getType())) {
            return AppConstants.MessageType.CUSTOM_CALL_AUDIO;
        }
        else if (this.messageType.equals(CubeMessageType.CustomShare.getType())) {
            return AppConstants.MessageType.CUSTOM_SHARE;
        }
        else if (this.messageType.equals(CubeMessageType.CustomShake.getType())) {  //屏幕抖动
            return AppConstants.MessageType.CUSTOM_SHAKE;
        }
        else if (this.messageType.equals(CubeMessageType.CARD.getType())) {
            return AppConstants.MessageType.CHAT_CARD;
        }
        else if (this.messageType.equals(CubeMessageType.RICHTEXT.getType())) {
            return AppConstants.MessageType.CHAT_RICH_TEXT;
        }
        else if (this.messageType.equals(CubeMessageType.GroupShareCard.getType())) {
            return AppConstants.MessageType.GroupShareCard;
        }
        else if (this.messageType.equals(CubeMessageType.UserShareCard.getType())) {
            return AppConstants.MessageType.UserShareCard;
        }
        else if (this.messageType.equals(CubeMessageType.GroupTaskNew.getType())) {
            return AppConstants.MessageType.GroupTaskNew;
        }
        else if (this.messageType.equals(CubeMessageType.GroupTaskComplete.getType())) {
            return AppConstants.MessageType.GroupTaskComplete;
        }
        else if (this.messageType.equals(CubeMessageType.Emoji.getType())) {
            return AppConstants.MessageType.CHAT_EMOJI;
        }
        else if (this.messageType.equals(CubeMessageType.RECALLMESSAGETIPS.getType())) {
            return AppConstants.MessageType.RECALL_MESSAGE_TIPS;
        }
        else if (messageType.equals(CubeMessageType.REPLYMESSAGE.getType())) {
            return AppConstants.MessageType.REPLY_MESSAGE;
        }
        else if (messageType.equals(CubeMessageType.ServiceNumber.getType())) {
            return AppConstants.MessageType.SERVICE_NUMBER;
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
        else {
            if (o instanceof CubeMessage) {
                CubeMessage message = (CubeMessage) o;
                return this.messageSN == message.messageSN;
            }
            else {
                return false;
            }
        }
    }

    /**
     * 是否是群消息
     *
     * @return
     */
    public boolean isGroupMessage() {
        return !TextUtils.isEmpty(this.groupId);
    }

    /**
     * 是否是接收的消息
     *
     * @return
     */
    public boolean isReceivedMessage() {
        if (this.messageDirection == CubeMessageDirection.None.getDirection()) {
            throw new IllegalArgumentException("Message not sent direction");
        }
        return this.messageDirection == CubeMessageDirection.Received.getDirection();
    }

    /**
     * 获取聊天的cube号
     *
     * @return 群组的cube号、会议的cube号或者对方的cube号
     */
    public String getChatId() {
        if (this.isGroupMessage()) {
            return this.groupId;
        }
        else {
            boolean isMyself = SpUtil.getCubeId().equals(this.senderId); // 发送者是否是自己
            return isMyself ? this.receiverId : this.senderId;
        }
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    /**
     * 添加文件上传监听器
     *
     * @param sn
     * @param listener
     */
    public void addFileMessageUploadListener(long sn, FileMessageUploadListener listener) {
            MessageHandle.getInstance().addUploadListener(sn, CubeMessage.class.getSimpleName(), listener);
    }

    /**
     * 移除文件上传监听器
     *
     * @param sn
     */
    public void removeFileMessageUploadListener(long sn) {
            MessageHandle.getInstance().removeUploadListener(sn, CubeMessage.class.getSimpleName());
    }

    /**
     * 添加文件下载监听器
     *
     * @param sn
     * @param listener
     */
    // TODO: 2017/9/15 需要处理缩略图与原文件不能同时监听
    public void addFileMessageDownloadListener(long sn, FileMessageDownloadListener listener) {
            MessageHandle.getInstance().addDownloadListener(sn, CubeMessage.class.getSimpleName(), listener);
    }

    /**
     * 移除文件下载监听器
     *
     * @param sn
     */
    public void removeFileMessageDownloadListener(long sn) {
            MessageHandle.getInstance().removeDownloadListener(sn, CubeMessage.class.getSimpleName());
    }

    public String getReplyContentJson() {
        return replyContentJson;
    }

    public void setReplyContentJson(String replyContentJson) {
        this.replyContentJson = replyContentJson;
    }

    @Override
    public String toString() {
        return "CubeMessage{" + "messageSN=" + messageSN + ", messageType='" + messageType + '\'' + ", messageStatus=" + messageStatus + ", messageDirection=" + messageDirection + ", senderId='" + senderId + '\'' + ", receiverId='" + receiverId + '\'' + ", groupId='" + groupId + '\'' + ", timestamp=" + timestamp + ", sendTimestamp=" + sendTimestamp + ", receiveTimestamp=" + receiveTimestamp + ", filePath='" + filePath + '\'' + ", fileName='" + fileName + '\'' + ", fileUrl='" + fileUrl + '\'' + ", processedSize=" + processedSize + ", fileSize=" + fileSize + ", fileMessageStatus=" + fileMessageStatus + ", lastModified=" + lastModified + ", imgWidth=" + imgWidth + ", imgHeight=" + imgHeight + ", thumbPath='" + thumbPath + '\'' + ", thumbUrl='" + thumbUrl + '\'' + ", content='" + content + '\'' + ", emojiContent='" + emojiContent + '\'' + ", duration=" + duration + ", isReceipt=" + isReceipt + ", isAnonymous=" + isAnonymous + ", anonymousTimestamp=" + anonymousTimestamp + ", invalidTimestamp=" + invalidTimestamp + ", isRead=" + isRead + ", isPlay=" + isPlay + ", isShowTime=" + isShowTime + ", operate='" + operate + '\'' + ", cardTitle='" + cardTitle + '\'' + ", cardIcon='" + cardIcon + '\'' + ", cardContentJson='" + cardContentJson + '\'' + ", customHeaders=" + customHeaders + '}';
    }

    public void clone(CubeMessage cubeMessage) {
        if (cubeMessage == null || cubeMessage.getMessageSN() != messageSN) {
            return;
        }
        //主键不可以更改
        messageType = cubeMessage.messageType;
        messageStatus = cubeMessage.messageStatus;
        messageDirection = cubeMessage.messageDirection;
        senderId = cubeMessage.senderId;
        senderName = cubeMessage.senderName;
        receiverId = cubeMessage.receiverId;
        groupId = cubeMessage.groupId;
        timestamp = cubeMessage.timestamp;
        sendTimestamp = cubeMessage.sendTimestamp;
        receiveTimestamp = cubeMessage.receiveTimestamp;
        filePath = cubeMessage.filePath;
        fileName = cubeMessage.fileName;
        fileUrl = cubeMessage.fileUrl;
        processedSize = cubeMessage.processedSize;
        fileSize = cubeMessage.fileSize;
        fileMessageStatus = cubeMessage.fileMessageStatus;
        lastModified = cubeMessage.lastModified;
        imgWidth = cubeMessage.imgWidth;
        imgHeight = cubeMessage.imgHeight;
        thumbPath = cubeMessage.thumbPath;
        thumbUrl = cubeMessage.thumbUrl;
        content = cubeMessage.content;
        duration = cubeMessage.duration;
        isReceipt = cubeMessage.isReceipt;
        isAnonymous = cubeMessage.isAnonymous;
        anonymousTimestamp = cubeMessage.anonymousTimestamp;
        invalidTimestamp = cubeMessage.invalidTimestamp;
        isRead = cubeMessage.isRead;
        isPlay = cubeMessage.isPlay;
        isShowTime = cubeMessage.isShowTime;
        operate = cubeMessage.operate;
        cardTitle = cubeMessage.cardTitle;
        cardIcon = cubeMessage.cardIcon;
        emojiContent = cubeMessage.emojiContent;
        cardContentJson = cubeMessage.cardContentJson;
        replyContentJson = cubeMessage.replyContentJson;
    }
}
