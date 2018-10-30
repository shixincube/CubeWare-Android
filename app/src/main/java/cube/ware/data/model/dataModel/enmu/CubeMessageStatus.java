package cube.ware.data.model.dataModel.enmu;

/**
 * cube消息状态
 *
 * @author PengZhenjin
 * @date 2017-1-17
 */
public enum CubeMessageStatus {
    None(0),     // 无
    Sending(1),     // 正在发送
    Receiving(2),   // 正在接收
    Succeed(3),     // 成功
    Failed(-1);     // 失败

    private int status;

    CubeMessageStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public static CubeMessageStatus parse(int status) {
        for (CubeMessageStatus ms : CubeMessageStatus.values()) {
            if (ms.status == status) {
                return ms;
            }
        }
        return None;
    }
}
