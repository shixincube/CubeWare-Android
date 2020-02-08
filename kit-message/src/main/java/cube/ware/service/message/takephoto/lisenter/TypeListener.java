package cube.ware.service.message.takephoto.lisenter;

/**
 * 录制拍照成功后回调
 *
 * @author Wangxx
 * @date 2017/5/25
 */
public interface TypeListener {
    /**
     * 取消
     */
    void cancel();

    /**
     * 发送
     */
    void confirm();
}
