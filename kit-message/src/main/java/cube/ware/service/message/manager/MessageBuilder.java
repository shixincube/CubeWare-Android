package cube.ware.service.message.manager;

import cube.service.message.CustomMessage;
import cube.service.message.FileMessage;
import cube.service.message.ImageMessage;
import cube.service.message.MessageDirection;
import cube.service.message.MessageEntity;
import cube.service.message.MessageStatus;
import cube.service.message.Receiver;
import cube.service.message.Sender;
import cube.service.message.TextMessage;
import cube.service.message.VideoClipMessage;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.utils.ImageUtil;
import cube.ware.utils.SpUtil;
import java.io.File;

/**
 * 消息构建者
 *
 * @author LiuFeng
 * @data 2020/2/13 10:20
 */
public class MessageBuilder {

    /**
     * 构建文本消息
     *
     * @param sessionType
     * @param senderId
     * @param receiverId
     * @param content
     *
     * @return
     */
    public static TextMessage buildTextMessage(CubeSessionType sessionType, String senderId, String receiverId, String content) {
        TextMessage message = new TextMessage(content);
        return buildMessage(sessionType, senderId, receiverId, message, false, false);
    }

    /**
     * 构建文件消息
     *
     * @param sessionType
     * @param sender
     * @param receiver
     * @param filePath
     *
     * @return
     */
    public static FileMessage buildFileMessage(CubeSessionType sessionType, Sender sender, Receiver receiver, String filePath) {
        FileMessage message = new FileMessage(new File(filePath));
        return buildMessage(sessionType, sender, receiver, message, false, false);
    }

    /**
     * 构建视频消息
     *
     * @param sessionType
     * @param sender
     * @param receiver
     * @param anonymous
     *
     * @return
     */
    public static VideoClipMessage buildVideoMessage(CubeSessionType sessionType, Sender sender, Receiver receiver, String videoPath, boolean anonymous) {
        VideoClipMessage message = new VideoClipMessage(new File(videoPath));
        message.computeWidthHeight();
        return buildMessage(sessionType, sender, receiver, message, anonymous, false);
    }

    /**
     * 构建图片消息
     *
     * @param sessionType
     * @param sender
     * @param receiver
     * @param imagePath
     * @param origin      原图
     * @param anonymous   匿名
     *
     * @return
     */
    public static ImageMessage buildImageMessage(CubeSessionType sessionType, Sender sender, Receiver receiver, String imagePath, boolean origin, boolean anonymous) {
        // 如果不是gif图，也不是原图，则生成缩略图
        if (!ImageUtil.isGif(imagePath) && !origin) {
            String fileName = new File(imagePath).getName();
            String thumbImagePath = new File(SpUtil.getImagePath(), fileName).getPath();
            imagePath = ImageUtil.createThumbnailImage(imagePath, thumbImagePath, 1920, 20);
        }

        ImageMessage message = new ImageMessage(new File(imagePath));
        message.computeWidthHeight();
        return buildMessage(sessionType, sender, receiver, message, anonymous, false);
    }

    /**
     * 构建自定义消息
     *
     * @param sessionType
     * @param senderId
     * @param receiverId
     * @param content
     *
     * @return
     */
    public static CustomMessage buildCustomMessage(CubeSessionType sessionType, String senderId, String receiverId, String content) {
        return buildCustomMessage(sessionType, new Sender(senderId), new Receiver(receiverId), content);
    }

    /**
     * 构建自定义消息
     *
     * @param sessionType
     * @param sender
     * @param receiver
     * @param content
     *
     * @return
     */
    public static CustomMessage buildCustomMessage(CubeSessionType sessionType, Sender sender, Receiver receiver, String content) {
        CustomMessage message = new CustomMessage(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setStatus(MessageStatus.Succeed);
        message.setTimestamp(System.currentTimeMillis());
        // 设置群消息ID
        if (sessionType == CubeSessionType.Group) {
            message.setGroupId(receiver.getCubeId());
        }
        return message;
    }

    /**
     * 构建消息
     *
     * @param sessionType 会话类型：P2P、Group
     * @param senderId    发送至ID
     * @param receiverId  接受者ID
     * @param message     消息实体
     * @param <T>
     *
     * @return
     */
    public static <T extends MessageEntity> T buildMessage(CubeSessionType sessionType, String senderId, String receiverId, T message) {
        return buildMessage(sessionType, senderId, receiverId, message, false, false);
    }

    /**
     * 构建消息
     *
     * @param sessionType 会话类型：P2P、Group
     * @param senderId    发送至ID
     * @param receiverId  接受者ID
     * @param message     消息实体
     * @param anonymous   是否匿名
     * @param secret      是否加密
     * @param <T>
     *
     * @return
     */
    public static <T extends MessageEntity> T buildMessage(CubeSessionType sessionType, String senderId, String receiverId, T message, boolean anonymous, boolean secret) {
        return buildMessage(sessionType, new Sender(senderId), new Receiver(receiverId), message, anonymous, secret);
    }

    /**
     * 构建消息
     *
     * @param sessionType 会话类型：P2P、Group
     * @param sender      发送者
     * @param receiver    接受者
     * @param message     消息实体
     * @param anonymous   是否匿名
     * @param secret      是否加密
     * @param <T>
     *
     * @return
     */
    public static <T extends MessageEntity> T buildMessage(CubeSessionType sessionType, Sender sender, Receiver receiver, T message, boolean anonymous, boolean secret) {
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setDirection(MessageDirection.Sent);
        message.setStatus(MessageStatus.Sending);
        // 加密
        message.setSecret(secret);
        // 匿名
        message.setAnonymous(anonymous);
        // 设置群消息ID
        if (sessionType == CubeSessionType.Group) {
            message.setGroupId(receiver.getCubeId());
        }
        return message;
    }
}
