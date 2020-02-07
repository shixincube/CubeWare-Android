package cube.ware.service.message.chat.panel.input.emoticon.model;

import java.io.Serializable;

/**
 * Created by Guoxin on 2017/12/18.
 */

public class CubeEmojiSingle implements Serializable {

    public String key; // 表情ID

    public String packageId; // 表情包ID

    public int size; // 表情大小

    public String name; // 表情对应签名

    public int prefixName; // 表情名称除去后缀的名字

    public String path; // 表情路径

    public String thumbUrl;

    public String url;

    public CubeEmojiSingle() {
    }

    @Override
    public String toString() {
        return "CubeEmojiSingle{" + "key='" + key + '\'' + ", size=" + size + ", name='" + name + '\'' + ", prefixName=" + prefixName + ", path='" + path + '\'' + '}';
    }
}
