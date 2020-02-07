package cube.ware.service.message.chat.panel.input.emoticon.model;

import java.io.Serializable;

/**
 * Created by Guoxin on 2018/1/8.
 */

public class CubeEmojiCollect implements Serializable {

    public String path; // 收藏表情的路径

    public String cubeId; // 表情所属人

    public long updateTime; // 表情更新时间

    @Override
    public String toString() {
        return "CubeEmojiCollect{" + "path='" + path + '\'' + ", cubeId='" + cubeId + '\'' + ", updateTime=" + updateTime + '}';
    }
}
