package cube.ware.data.model.dataModel.enmu;

/**
 * 会话类型
 *
 * @author Wangxx
 * @date 2017/1/3
 */

public enum CubeSessionType {
    None(-1),   // 无类型
    P2P(0),     // 单聊
    Group(1),   // 群聊
    Secret(2),  // 密聊
    SystemMessage(3), //验证消息
    ServiceNumber(4);   // 服务号

    private int type;

    CubeSessionType(int type) {
        this.type = type;
    }

    public static CubeSessionType parse(int type) {
        for (CubeSessionType ct : CubeSessionType.values()) {
            if (ct.type == type) {
                return ct;
            }
        }
        return P2P;
    }

    public int getType() {
        return this.type;
    }
}
