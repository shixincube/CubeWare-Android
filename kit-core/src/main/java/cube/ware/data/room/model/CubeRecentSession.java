package cube.ware.data.room.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import cube.ware.data.model.dataModel.enmu.CubeMessageDirection;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;

/**
 * 最近会话信息表
 *
 * @author LiuFeng
 * @data 2020/2/12 20:29
 */
@Entity
public class CubeRecentSession {
    /**
     * 最近会话的cube号
     */
    @NonNull
    @PrimaryKey
    private String sessionId;

    /**
     * 最近会话名字
     */
    private String sessionName;

    /**
     * 最近会话的头像
     */
    private String faceUrl;

    /**
     * 聊天类型 {@link CubeSessionType}，默认值：none(-1)
     */
    private int sessionType = CubeSessionType.None.getType();

    /**
     * 时间戳，默认值：0
     */
    private long timestamp = 0;

    /**
     * 消息方向{@link CubeMessageDirection}
     */
    private int messageDirection = -1;

    /**
     * 是否置顶，默认值：false
     */
    private boolean isTop = false;

    /**
     * 未读消息数目
     */
    private int unRead;

    /**
     * 最近消息content
     *
     * @return
     */
    private String content;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getMessageDirection() {
        return messageDirection;
    }

    public void setMessageDirection(int messageDirection) {
        this.messageDirection = messageDirection;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUnRead() {
        return unRead;
    }

    public void setUnRead(int unRead) {
        this.unRead = unRead;
    }

    @Override
    public String toString() {
        return "CubeRecentSession{" + "sessionId='" + sessionId + '\'' + ", sessionName='" + sessionName + '\'' + ", sessionType=" + sessionType + ", timestamp=" + timestamp + ", messageDirection=" + messageDirection + ", isTop=" + isTop + ", unRead=" + unRead + ", content='" + content + '\'' + '}';
    }
}
