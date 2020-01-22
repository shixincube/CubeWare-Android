package cube.ware.service.message.chat.activity.file.entity;

import java.io.Serializable;

/**
 * 本地文件
 *
 * @author Wangxx
 * @date 2017/2/10
 */
public class LocalMedia implements Serializable {
    private String  name;
    private String  path;
    private long    duration;
    private long    lastUpdateAt;
    private boolean isChecked;
    public  int     position;
    private int     num;
    private int     type;
    private long     size;

    public LocalMedia(String name, String path, long lastUpdateAt, long duration, int type) {
        this.name = name;
        this.path = path;
        this.duration = duration;
        this.lastUpdateAt = lastUpdateAt;
        this.type = type;
    }

    public LocalMedia(String name, String path, long lastUpdateAt, long duration, int type, long size) {
        this.name = name;
        this.path = path;
        this.duration = duration;
        this.lastUpdateAt = lastUpdateAt;
        this.type = type;
        this.size = size;
    }

    public LocalMedia(String name, String path, long duration, long lastUpdateAt, boolean isChecked, int position, int num, int type) {
        this.name = name;
        this.path = path;
        this.duration = duration;
        this.lastUpdateAt = lastUpdateAt;
        this.isChecked = isChecked;
        this.position = position;
        this.num = num;
        this.type = type;
    }

    public LocalMedia() {
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
