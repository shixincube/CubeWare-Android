package cube.ware.data.model.dataModel.enmu;

/**
 * cube文件消息状态
 *
 * @author PengZhenjin
 * @date 2017-1-17
 */
public enum CubeFileMessageStatus {

    Unknown(0),    // 未知
    Uploading(1),   // 正在上传
    Downloading(2), // 正在下载
    Succeed(3), // 成功
    Failed(4);  // 失败

    private int status;

    CubeFileMessageStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public static CubeFileMessageStatus parse(int status) {
        for (CubeFileMessageStatus ms : CubeFileMessageStatus.values()) {
            if (ms.status == status) {
                return ms;
            }
        }
        return Unknown;
    }
}
