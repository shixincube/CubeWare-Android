package cube.ware.data.room.mapper;

import android.content.Context;
import android.text.TextUtils;
import com.common.utils.utils.log.LogUtil;
import cube.service.message.FileMessageStatus;
import cube.service.message.MessageDirection;
import cube.service.message.MessageStatus;
import cube.service.message.model.CardMessage;
import cube.service.message.model.CustomMessage;
import cube.service.message.model.FileMessage;
import cube.service.message.model.ImageMessage;
import cube.service.message.model.MessageEntity;
import cube.service.message.model.ReceiptMessage;
import cube.service.message.model.ReplyMessage;
import cube.service.message.model.RichContentMessage;
import cube.service.message.model.TextMessage;
import cube.service.message.model.UnKnownMessage;
import cube.service.message.model.VideoClipMessage;
import cube.service.message.model.VoiceClipMessage;
import cube.service.message.model.WhiteboardMessage;
import cube.ware.core.CubeCore;
import cube.ware.core.R;
import cube.ware.data.model.dataModel.enmu.CubeFileMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageDirection;
import cube.ware.data.model.dataModel.enmu.CubeMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.model.CubeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: LiuFeng
 * @data: 2020/1/19
 */
public class MessageMapper {
    /**
     * 批量转换 将引擎的MessageEntity转换为CubeMessage
     *
     * @param messages
     * @param isSync   是否为同步下来的消息
     * @return
     */
    public static List<CubeMessage> convertTo(List<MessageEntity> messages, boolean isSync) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        List<CubeMessage> cubeMessageList = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            MessageEntity messageEntity = messages.get(i);
            try {
                CubeMessage cubeMessage = convertTo(messageEntity, isSync);
                if (cubeMessage != null) {
                    cubeMessageList.add(cubeMessage);
                }
            } catch (Exception e) {
                LogUtil.e("将MessageEntity转换为CubeMessage出错!" + e.getMessage());
                return cubeMessageList;
            }
        }
        return cubeMessageList;
    }

    /**
     * 将引擎的MessageEntity转换为CubeMessage
     *
     * @param messageEntity
     * @param isSync
     * @return
     */
    public static CubeMessage convertTo(MessageEntity messageEntity, boolean isSync) {
        if (null == messageEntity) {
            throw new IllegalArgumentException("MessageEntity can't be null!");
        }
        try {
            CubeMessage cubeMessage = new CubeMessage();
            if (messageEntity instanceof UnKnownMessage) {
                cubeMessage.setMessageType(CubeMessageType.Unknown);
                cubeMessage.setContent(CubeCore.getContext().getString(R.string.unknown_message_type));
            }
            if (messageEntity instanceof ReceiptMessage) {
                LogUtil.i("引擎回执消息转化处理====>");
                return null;
            } else if (messageEntity instanceof TextMessage) {         // 文本消息
                TextMessage textMessage = (TextMessage) messageEntity;
                String textContent = textMessage.getContent();
                cubeMessage.setContent(textContent);
                if (isEmoji(textContent)) {
                    cubeMessage.setMessageType(CubeMessageType.Emoji);
                    cubeMessage.setContent("[图片]");
                    cubeMessage.setEmojiContent(textContent);
                } else {
                    cubeMessage.setMessageType(CubeMessageType.Text);
                    cubeMessage.setContent(textContent);
                }
            } else if (messageEntity instanceof CardMessage) {    //卡片消息
                CardMessage cardMessage = (CardMessage) messageEntity;
                cubeMessage.setMessageType(CubeMessageType.CARD);
                cubeMessage.setContent(cardMessage.getContent());
                cubeMessage.setCardTitle(cardMessage.getTitle());
                cubeMessage.setCardIcon(cardMessage.getIcon());
                JSONObject json = cardMessage.toJSON();
                cubeMessage.setCardContentJson(json.getJSONArray("cardContents").toString());
            } else if (messageEntity instanceof FileMessage) {    // 文件消息
                FileMessage fileMessage = (FileMessage) messageEntity;
                File file = fileMessage.getFile();
                if (null != file && file.exists()) {
                    cubeMessage.setFilePath(file.getAbsolutePath());
                }
                cubeMessage.setFileUrl(fileMessage.getUrl());
                cubeMessage.setFileName(fileMessage.getFileName());
                cubeMessage.setProcessedSize(fileMessage.getProcessed());
                cubeMessage.setFileSize(fileMessage.getFileSize());
                cubeMessage.setLastModified(fileMessage.getFileLastModified());
                cubeMessage.setFileMessageStatus(getFileMessageStatus(fileMessage).getStatus());
                cubeMessage.setMessageType(CubeMessageType.File);
                cubeMessage.setContent(CubeCore.getContext().getString(R.string.file_message));

                if (fileMessage instanceof ImageMessage) {  // 图片
                    ImageMessage imageMessage = (ImageMessage) fileMessage;
                    File thumbFile = imageMessage.getThumbFile();
                    if (null != thumbFile && thumbFile.exists()) {
                        cubeMessage.setThumbPath(thumbFile.getAbsolutePath());
                    }
                    cubeMessage.setThumbUrl(imageMessage.getThumbUrl());
                    cubeMessage.setImgWidth(imageMessage.getWidth());
                    cubeMessage.setImgHeight(imageMessage.getHeight());
                    cubeMessage.setMessageType(CubeMessageType.Image);
                    cubeMessage.setContent(CubeCore.getContext().getString(R.string.image_message));
                } else if (fileMessage instanceof VoiceClipMessage) { // 语音
                    VoiceClipMessage voiceClipMessage = (VoiceClipMessage) fileMessage;
                    cubeMessage.setDuration(voiceClipMessage.getDuration());
                    cubeMessage.setMessageType(CubeMessageType.Voice);
                    cubeMessage.setContent(CubeCore.getContext().getString(R.string.voice_message));
                    //                    cubeMessage.setPlay(messageEntity.getDirection() == MessageDirection.Sent || messageEntity.isReceipted());
                } else if (fileMessage instanceof VideoClipMessage) { // 视频
                    VideoClipMessage videoClipMessage = (VideoClipMessage) fileMessage;
                    File thumbFile = videoClipMessage.getThumbFile();
                    if (null != thumbFile && thumbFile.exists()) {
                        cubeMessage.setThumbPath(thumbFile.getAbsolutePath());
                    }
                    cubeMessage.setThumbUrl(videoClipMessage.getThumbUrl());
                    cubeMessage.setDuration(videoClipMessage.getDuration());
                    cubeMessage.setImgWidth(videoClipMessage.getWidth());
                    cubeMessage.setImgHeight(videoClipMessage.getHeight());
                    cubeMessage.setMessageType(CubeMessageType.Video);
                    cubeMessage.setContent(CubeCore.getContext().getString(R.string.video_message));
                } else if (fileMessage instanceof WhiteboardMessage) {    // 白板
                    WhiteboardMessage whiteboardMessage = (WhiteboardMessage) fileMessage;
                    File thumbFile = whiteboardMessage.getThumbFile();
                    if (null != thumbFile && thumbFile.exists()) {
                        cubeMessage.setThumbPath(thumbFile.getAbsolutePath());
                    }
                    cubeMessage.setThumbUrl(whiteboardMessage.getThumbUrl());
                    cubeMessage.setMessageType(CubeMessageType.Whiteboard);
                    cubeMessage.setContent(CubeCore.getContext().getString(R.string.whiteboard_message));
                }
            } else if (messageEntity instanceof CustomMessage) {      // 自定义消息
                //如果是验证消息 通知刷新最近列表
                //if (SystemMessageManage.getInstance().isFromVerify(messageEntity) && !isSync) {
                //    LogUtil.i("EVENT_REFRESH_SYSTEM_MESSAGE");
                //    RxBus.getInstance().post(CubeEvent.EVENT_REFRESH_SYSTEM_MESSAGE, true);
                //}
                CustomMessage customMessage = (CustomMessage) messageEntity;
                String operate = customMessage.getHeader("operate");
                cubeMessage.setOperate(operate);
                cubeMessage.setMessageDirection(getMessageDirection(messageEntity).getDirection());
                cubeMessage.setCustomHeaders(getHeaders(messageEntity));
                if (!messageEntity.getOnlyReceivers().isEmpty() && !messageEntity.getOnlyReceivers().contains(messageEntity.getReceiver().getCubeId())) {
                    LogUtil.i("不是发给自己的自定义消息不做处理====>" + customMessage.toString());
                    return null;
                }
                //if (!CustomMessageManager.getCustomText(cubeMessage, customMessage, operate, isSync)) {
                //    LogUtil.i("不做处理的自定义消息====>" + customMessage.toString());
                //    return null;
                //}
            } else if (messageEntity instanceof RichContentMessage) {
                cubeMessage.setMessageType(CubeMessageType.RICHTEXT);
                StringBuilder stringBuilder = new StringBuilder();
                RichContentMessage richContentMessage = (RichContentMessage) messageEntity;
                //获取富文本消息中的消息
                List<MessageEntity> messageEntities = richContentMessage.getMessages();
                for (MessageEntity entity : messageEntities) {
                    if (entity instanceof TextMessage) {
                        String content = ((TextMessage) entity).getContent();
                        if (isEmoji(content)) {
                            stringBuilder.append("[图片]");
                        } else {
                            stringBuilder.append(content);
                        }
                    } else if (entity instanceof ImageMessage) {
                        stringBuilder.append("[图片]");
                    } else if (entity instanceof FileMessage) {
                        stringBuilder.append("[文件]");
                    }
                }
                cubeMessage.setContent(stringBuilder.toString());  //给最近聊天列表显示的内容
            } else if (messageEntity instanceof ReplyMessage) {
                cubeMessage.setMessageType(CubeMessageType.REPLYMESSAGE);
                ReplyMessage messageEntity1 = (ReplyMessage) messageEntity;
                cubeMessage.setReplyContentJson(messageEntity1.toString());
                MessageEntity reply = messageEntity1.getReply();
                if (!(reply instanceof ReplyMessage)) {
                    cubeMessage.setContent(getMessageContent(reply));
                } else {
                    //回复消息在最近列表中显示reply消息的内容 如果reply消息仍然 intanceof ReplyMessage则说明出错了 概率很低 简单容错处理一下
                    cubeMessage.setContent("回复消息");
                }
            } else {
                cubeMessage.setMessageType(CubeMessageType.Unknown);
                cubeMessage.setContent(CubeCore.getContext().getString(R.string.unknown_message_type));
            }
            cubeMessage.setMessageSN(messageEntity.getSerialNumber());
            cubeMessage.setMessageDirection(getMessageDirection(messageEntity).getDirection());
            cubeMessage.setMessageStatus(getMessageStatus(messageEntity).getStatus());
            cubeMessage.setSenderId(messageEntity.getSender().getCubeId());
            cubeMessage.setSenderName(TextUtils.isEmpty(messageEntity.getSender().getDisplayName()) ? messageEntity.getSender().getCubeId() : messageEntity.getSender().getDisplayName());
            cubeMessage.setSendTimestamp(messageEntity.getSendTimestamp());
            cubeMessage.setReceiverId(messageEntity.getReceiver().getCubeId());
            cubeMessage.setReceiveTimestamp(messageEntity.getReceiveTimestamp());
            cubeMessage.setGroupId(messageEntity.getGroupId());
            cubeMessage.setTimestamp(messageEntity.getTimestamp());
            cubeMessage.setRead(messageEntity.getDirection() == MessageDirection.Sent || cubeMessage.getMessageType() == CubeMessageType.CustomTips || messageEntity.isReceipted());
            cubeMessage.setReceipt(messageEntity.isReceipted());
            cubeMessage.setAnonymous(messageEntity.isAnonymous());
            //boolean alreadyReceiptedMessage = ReceiptManager.getInstance().isAlreadyReceiptedMessage(cubeMessage.getTimestamp());
            //if (alreadyReceiptedMessage) {
            //    cubeMessage.setRead(true);
            //    cubeMessage.setReceipt(true);
            //}
            if (cubeMessage.getCustomHeaders() == null || cubeMessage.getCustomHeaders().isEmpty()) {

                cubeMessage.setCustomHeaders(getHeaders(messageEntity));
            }
            return cubeMessage;
        } catch (Exception e) {
            LogUtil.e("将MessageEntity转换为CubeMessage出错!：" + messageEntity.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取CubeWare的文件消息状态
     *
     * @param fileMessage
     * @return
     */
    public static CubeFileMessageStatus getFileMessageStatus(FileMessage fileMessage) {
        if (fileMessage.getFileStatus() == FileMessageStatus.Uploading) {
            return CubeFileMessageStatus.Uploading;
        } else if (fileMessage.getFileStatus() == FileMessageStatus.Downloading) {
            return CubeFileMessageStatus.Downloading;
        } else if (fileMessage.getFileStatus() == FileMessageStatus.Succeed) {
            return CubeFileMessageStatus.Succeed;
        } else if (fileMessage.getFileStatus() == FileMessageStatus.Failed) {
            return CubeFileMessageStatus.Failed;
        } else {
            return CubeFileMessageStatus.Unknown;
        }
    }

    /**
     * 获取CubeWare的消息接收方向
     *
     * @param messageEntity
     * @return
     */
    public static CubeMessageDirection getMessageDirection(MessageEntity messageEntity) {
        if (messageEntity.getDirection() == MessageDirection.Sent) {
            return CubeMessageDirection.Sent;
        } else if (messageEntity.getDirection() == MessageDirection.Received) {
            return CubeMessageDirection.Received;
        } else {
            return CubeMessageDirection.None;
        }
    }

    /**
     * 获取CubeWare的消息状态
     *
     * @param messageEntity
     * @return
     */
    public static CubeMessageStatus getMessageStatus(MessageEntity messageEntity) {
        if (messageEntity.getStatus() == MessageStatus.Sending) {
            return CubeMessageStatus.Sending;
        } else if (messageEntity.getStatus() == MessageStatus.Receiving) {
            return CubeMessageStatus.Receiving;
        } else if (messageEntity.getStatus() == MessageStatus.Succeed) {
            return CubeMessageStatus.Succeed;
        } else if (messageEntity.getStatus() == MessageStatus.Failed) {
            return CubeMessageStatus.Failed;
        } else {
            return CubeMessageStatus.None;
        }
    }

    private static String getHeaders(MessageEntity messageEntity) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<String, Object> entry : messageEntity.getHeaders().entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", entry.getKey());
            if (entry.getValue() instanceof Map) {
                jsonObject.put("value", entry.getValue().toString());
            } else {
                jsonObject.put("value", entry.getValue());
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    /**
     * note:如果convertTo中setContent发生变化 这里也要发生变化 建议不要将convertto中setContent都交给该方法
     * 因为只有回复消息真正需要这个方法 其他消息走这个方法增加消耗
     *
     * @param messageEntity
     * @return
     */
    public static String getMessageContent(MessageEntity messageEntity) {
        Context context = CubeCore.getContext();
        if (messageEntity == null || messageEntity instanceof UnKnownMessage) {
            return context.getString(R.string.unknown_message_type);
        } else if (messageEntity instanceof TextMessage) {
            String textContent = ((TextMessage) messageEntity).getContent();
            if (isEmoji(textContent)) {
                return "[图片]";
            } else {
                return textContent;
            }
        } else if (messageEntity instanceof CardMessage) {
            CardMessage cardMessage = (CardMessage) messageEntity;
            return cardMessage.getContent();
        } else if (messageEntity instanceof FileMessage) {
            if (messageEntity instanceof ImageMessage) {  // 图片
                return context.getString(R.string.image_message);
            } else if (messageEntity instanceof VoiceClipMessage) { // 语音
                return context.getString(R.string.voice_message);
            } else if (messageEntity instanceof VideoClipMessage) { // 视频
                return context.getString(R.string.video_message);
            } else if (messageEntity instanceof WhiteboardMessage) {    // 白板
                return context.getString(R.string.whiteboard_message);
            }
        } else if (messageEntity instanceof RichContentMessage) {
            StringBuilder stringBuilder = new StringBuilder();
            RichContentMessage richContentMessage = (RichContentMessage) messageEntity;
            //获取富文本消息中的消息
            List<MessageEntity> messageEntities = richContentMessage.getMessages();
            for (MessageEntity entity : messageEntities) {
                if (entity instanceof TextMessage) {
                    String content = ((TextMessage) entity).getContent();
                    if (isEmoji(content)) {
                        stringBuilder.append("[图片]");
                    } else {
                        stringBuilder.append(content);
                    }
                } else if (entity instanceof ImageMessage) {
                    stringBuilder.append("[图片]");
                } else if (entity instanceof FileMessage) {
                    stringBuilder.append("[文件]");
                }
            }
            return stringBuilder.toString();  //给最近聊天列表显示的内容
        } else if (messageEntity instanceof ReplyMessage) {
            ReplyMessage replyMessage = (ReplyMessage) messageEntity;
            MessageEntity reply = replyMessage.getReply();
            if (reply instanceof ReplyMessage) {
                return "回复消息";
            } else {
                return getMessageContent(reply);
            }
        } else if (messageEntity instanceof CustomMessage) {
            //CustomMessage customMessage = (CustomMessage) messageEntity;
            //String type = customMessage.getHeader("operate");
            //if (type.equals(CubeCustomMessageType.GroupShareQr.getType())) {
            //    return "[分享]";
            //}
            //else if (type.equals(CubeCustomMessageType.UserShareQr.getType())) {
            //    return "[分享]";
            //}
            //目前能回复的自定义消息只有二维码分享消息
            return "[二维码]";
        }
        return context.getString(R.string.unknown_message_type);
    }

    /**
     * 判断是否包含 表情 key
     *
     * @param str
     */
    private static boolean isEmoji(String str) {
        boolean temp = false;
        Pattern p = Pattern.compile("\\[cube_emoji:[a-fA-F0-9]{32}\\]{1}");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }
}
