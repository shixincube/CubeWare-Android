package cube.ware.data.model.dataModel.enmu;

import android.arch.persistence.room.TypeConverter;
import java.io.Serializable;

/**
 * cube消息类型
 *
 * @author LiuFeng
 * @data 2020/1/21 17:29
 */
public enum CubeMessageType implements Serializable {
    /**
     * 未知消息
     */
    Unknown(-1, "unknown"),

    /**
     * 文本消息
     */
    Text(1, "text"),

    /**
     * 文件消息
     */
    File(2, "file"),

    /**
     * 图片消息
     */
    Image(3, "image"),

    /**
     * 语音消息
     */
    Voice(4, "voice"),

    /**
     * 视频消息
     */
    Video(5, "video"),

    /**
     * 白板消息
     */
    Whiteboard(6, "whiteboard"),

    /**
     * 自定义提示消息
     */
    CustomTips(7, "custom_tips"),

    /**
     * 自定义视频通话消息
     */
    CustomCallVideo(8, "custom_call_video"),

    /**
     * 自定义语音通话消息
     */
    CustomCallAudio(9, "custom_call_audio"),

    /**
     * 自定义分享消息
     */
    CustomShare(10, "custom_share"),

    /**
     * 自定义抖动消息
     */
    CustomShake(11, "custom_shake"),

    /**
     * 卡片消息
     */
    CARD(12, "card"),

    /**
     * 富文本消息
     */
    RICHTEXT(13, "richtext"),

    /**
     * 贴图表情消息
     */
    Emoji(14, "emoji"),

    /**
     * 撤回的提示消息
     */
    RECALLMESSAGETIPS(15, "recall_message_tips"),

    /**
     * 回复消息
     */
    REPLYMESSAGE(16, "reply_message"),

    /**
     * 服务号
     */
    ServiceNumber(17, "service_number"),

    /**
     * 推荐群
     */
    GroupShareCard(18, "group_share_card"),

    /**
     * 推荐联系人
     */
    UserShareCard(19, "user_share_card"),

    /**
     * 新建群任务
     */
    GroupTaskNew(20, "group_task_new"),

    /**
     * 标记群任务
     */
    GroupTaskComplete(21, "group_task_complete"),

    /**
     * 在其他端修改密码
     */
    UpdateUserPwd(22, "update_user_pwd");

    public int    value;
    public String type;

    CubeMessageType(int value, String type) {
        this.value = value;
        this.type = type;
    }

    @TypeConverter
    public static CubeMessageType parse(String type) {
        for (CubeMessageType messageType : CubeMessageType.values()) {
            if (messageType.type.equals(type)) {
                return messageType;
            }
        }
        return Unknown;
    }

    @TypeConverter
    public static String getType(CubeMessageType messageType) {
        return messageType.type;
    }

    public static boolean isFileMessage(CubeMessageType messageType) {
        return messageType == File || messageType == Image || messageType == Video || messageType == Voice;
    }
}
