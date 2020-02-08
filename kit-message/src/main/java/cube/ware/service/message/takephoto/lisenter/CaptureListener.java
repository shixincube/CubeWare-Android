package cube.ware.service.message.takephoto.lisenter;

/**
 * 拍照按钮事件监听
 *
 * @author Wangxx
 * @date 2017/5/25
 */

public interface CaptureListener {
    /**
     * 照相
     */
    void takePictures();

    /**
     * 录制时间过短
     *
     * @param time
     */
    void recordShort(long time);

    /**
     * 开始录制
     */
    void recordStart();

    /**
     * 结束录制
     *
     * @param time
     */
    void recordEnd(long time);

    /**
     * 录制时对焦
     *
     * @param zoom
     */
    void recordZoom(float zoom);
}
