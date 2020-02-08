package cube.ware.service.message.takephoto.lisenter;

/**
 * 功能按钮事件
 *
 * @author Wangxx
 * @date 2017/5/25
 */
public interface ReturnListener {
    /**
     * 返回退出事件
     */
    void onReturn();

    /**
     * 切换摄像头
     *
     * @param isPost
     */
    void onSwitch(boolean isPost);
}
