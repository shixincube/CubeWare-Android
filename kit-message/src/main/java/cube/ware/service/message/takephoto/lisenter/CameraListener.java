package cube.ware.service.message.takephoto.lisenter;

import java.io.File;

/**
 * 照相view回调监听
 *
 * @author Wangxx
 * @date 2017/5/25
 */
public interface CameraListener {
    /**
     * 拍照成功
     *
     * @param url
     */
    void captureSuccess(String url);

    /**
     * 录制视频成功
     *
     * @param url
     */
    void recordSuccess(String url);

    /**
     * 退出
     */
    void quit();

    /**
     * 成功
     * @param file
     */
    void success(File file);

    /**
     * 取消
     */
    void cancel();
}
