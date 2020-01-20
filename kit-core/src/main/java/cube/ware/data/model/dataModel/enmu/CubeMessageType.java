package cube.ware.data.model.dataModel.enmu;

import android.arch.persistence.room.TypeConverter;
import java.io.Serializable;

/**
 * cube消息类型
 *
 * @author PengZhenjin
 * @date 2017-1-17
 */
public enum CubeMessageType implements Serializable {

    Unknown("unknown"), // 未知消息
    Text("text"),   // 文本消息
    File("file"),   // 文件消息
    Image("image"), // 图片消息
    Voice("voice"), // 语音消息
    Video("video"), // 视频消息
    Whiteboard("whiteboard"),   // 白板消息
    CustomTips("custom_tips"),   // 自定义提示消息
    CustomCallVideo("custom_call_video"),   // 自定义视频通话消息
    CustomCallAudio("custom_call_audio"),   // 自定义语音通话消息
    CustomShare("custom_share"),      // 自定义分享消息
    CustomShake("custom_shake"),     //自定义抖动消息
    CARD("card"),                     //卡片消息
    RICHTEXT("richtext"),           //富文本消息
    Emoji("emoji"), // 贴图表情消息
    UpdateUserPwd("update_user_pwd"), // 在其他端修改密码
    RECALLMESSAGETIPS("recall_message_tips"),//撤回的提示消息
    REPLYMESSAGE("reply_message"),//回复消息
    ServiceNumber("service_number"), // 服务号
    GroupShareCard("group_share_card"),// 推荐群
    UserShareCard("user_share_card"),// 推荐联系人
    GroupTaskNew("group_task_new"),// 新建群任务
    GroupTaskComplete("group_task_complete");// 标记群任务

    private String type;

    CubeMessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
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
