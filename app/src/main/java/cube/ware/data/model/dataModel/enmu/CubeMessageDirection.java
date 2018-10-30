package cube.ware.data.model.dataModel.enmu;

/**
 * cube消息方向
 *
 * @author PengZhenjin
 * @date 2017-1-17
 */
public enum CubeMessageDirection {

    Sent(0),    // 发送
    Received(1),    // 接收
    None(-1);   // 无

    private int direction;

    CubeMessageDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return this.direction;
    }

    public static CubeMessageDirection parse(int direction) {
        for (CubeMessageDirection md : CubeMessageDirection.values()) {
            if (md.direction == direction) {
                return md;
            }
        }
        return None;
    }
}
