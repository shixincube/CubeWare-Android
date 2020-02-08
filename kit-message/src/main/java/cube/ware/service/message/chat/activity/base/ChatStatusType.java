package cube.ware.service.message.chat.activity.base;

/**
 * 聊天类型
 *
 * @author LiuFeng
 * @data 2020/2/8 17:39
 */
public enum ChatStatusType {
    None(-1),              // 未知
    NonRegistration(0),    // 非注册单聊
    Normal(1),             // 正常单聊
    NotFriend(2),          // 非好友单聊
    Group(3),              // 群聊
    Anonymous(4),          // 好友私密聊天
    ServiceNumber(5);      // 服务号

    private int type;

    ChatStatusType(int type) {
        this.type = type;
    }

    public static ChatStatusType parse(int type) {
        for (ChatStatusType ct : ChatStatusType.values()) {
            if (ct.type == type) {
                return ct;
            }
        }
        return None;
    }

    public int getType() {
        return this.type;
    }
}