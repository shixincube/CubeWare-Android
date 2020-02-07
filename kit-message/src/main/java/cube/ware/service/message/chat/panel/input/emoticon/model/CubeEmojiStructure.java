package cube.ware.service.message.chat.panel.input.emoticon.model;

import java.io.Serializable;
import java.util.List;

/**
 * 表情模型类
 *
 * @author LiuFeng
 * @data 2020/2/7 20:31
 */
public class CubeEmojiStructure implements Serializable {

    public String packageId;  // 表情包ID

    public String packagePath;// 表情包存在本地的路径

    public long date; // 表情包创建日期

    public String cover;// 表情包封面图

    public String pc_banner; // pc表情包详情页横幅

    public int size; // 表情包总大小

    public int count; // 表情包总表情数

    public String chatPanel; // 表情包聊天面板图标

    public String name; // 表情包名称

    public int type; // 表情包类型

    public int category; // 终端本地用于区分的表情包类型（1，贴图表情 ；2，收藏表情）

    public List<CubeEmojiSingle> emojis; // 表情集合

    public List<CubeEmojiCollect> collects; // 收藏的表情集合

    public CubeEmojiStructure() {}

    public CubeEmojiStructure(String packageId, String packagePath, long date, String cover, String pc_banner, int size, int count, String chatPanel, String name, int type, List<CubeEmojiSingle> emojis, List<CubeEmojiCollect> collects) {
        this.packageId = packageId;
        this.packagePath = packagePath;
        this.date = date;
        this.cover = cover;
        this.pc_banner = pc_banner;
        this.size = size;
        this.count = count;
        this.chatPanel = chatPanel;
        this.name = name;
        this.type = type;
        this.emojis = emojis;
        this.collects = collects;
    }

    public List<CubeEmojiCollect> getCollects() {
        return collects;
    }

    public void setCollects(List<CubeEmojiCollect> collects) {
        this.collects = collects;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPc_banner() {
        return pc_banner;
    }

    public void setPc_banner(String pc_banner) {
        this.pc_banner = pc_banner;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getChatPanel() {
        return chatPanel;
    }

    public void setChatPanel(String chatPanel) {
        this.chatPanel = chatPanel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<CubeEmojiSingle> getEmojis() {
        return emojis;
    }

    public void setEmojis(List<CubeEmojiSingle> emojis) {
        this.emojis = emojis;
    }

    @Override
    public String toString() {
        return "CubeEmojiStructure{" + "packageId='" + packageId + '\'' + ", packagePath='" + packagePath + '\'' + ", date=" + date + ", cover='" + cover + '\'' + ", pc_banner='" + pc_banner + '\'' + ", size=" + size + ", count=" + count + ", chatPanel='" + chatPanel + '\'' + ", name='" + name + '\'' + ", type=" + type + ", emojis=" + emojis + '}';
    }
}
