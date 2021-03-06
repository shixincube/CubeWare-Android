package cube.ware.service.conference.manager;


/**
 * 一对一音视频通话管理器，主要管理当前是否正在进行通话
 * Created by zzy on 2018/8/30.
 */

public class ConferenceCallManager {
    private static volatile ConferenceCallManager mInstance = null;

    private volatile boolean    isInvited;

    private boolean isCalling;

    public static ConferenceCallManager getInstance() {
        if (null == mInstance) {
            synchronized (ConferenceCallManager.class) {
                if (null == mInstance) {
                    mInstance = new ConferenceCallManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 是否正在会话
     *
     * @return
     */
    public boolean isCalling() {
        return isCalling;
    }

    /**
     * 有会话邀请
     *
     * @param calling
     */
    public void setCalling(boolean calling) {
        this.isCalling = calling;
    }

    /**
     * 重置会话
     */
    public void restCalling() {
        this.isCalling = false;
    }

    /**
     * 是否处于正在被其他人邀请进会议中
     *
     * @return
     */
    public boolean isInvited() {
        return isInvited;
    }

    public void setInvited(boolean b) {
        isInvited = b;
    }
}
