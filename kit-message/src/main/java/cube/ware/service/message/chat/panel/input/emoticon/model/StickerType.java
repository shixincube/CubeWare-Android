package cube.ware.service.message.chat.panel.input.emoticon.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 贴图表情类
 *
 * @author Wangxx
 * @date 2017/1/4
 */
public class StickerType implements Serializable {
    private static final long serialVersionUID = -81692490861539040L;

    private String name; // 贴纸包名
    private String title; // 显示的标题
    private int    type; // 终端本地用于区分的表情包类型（1，贴图表情 ；2，收藏表情）
    private String packageId;

    private transient List<StickerItem> stickerList;

    public StickerType(CubeEmojiStructure cubeEmojiStructure) {
        buildStickerType(cubeEmojiStructure);
    }

    public static String COLLECT_SETTING = "ic_emoji_collect_setting.png";
    public static String COLLECT_NAME    = "collect";

    /**
     * 构建 StickerType
     *
     * @param data
     */
    private void buildStickerType(CubeEmojiStructure data) {
        this.name = data.name;
        this.title = data.name;
        this.type = data.category;
        this.packageId = data.packageId;
        List<StickerItem> stickers = new ArrayList<>();
        if (data.emojis != null) {
            for (CubeEmojiSingle model : data.emojis) {
                StickerItem item = new StickerItem();
                item.setName(model.name);
                item.setCategory(data.name);
                item.setKey(model.key);
                item.setPath(model.path);
                item.setThumbUrl(model.thumbUrl);
                item.setUrl(model.url);
                item.setType(1);
                item.setPackgeId(model.packageId);
                stickers.add(item);
            }
        }
        if (data.collects != null) {
            for (CubeEmojiCollect collect : data.collects) {
                if (collect.path.contains(COLLECT_SETTING)) {
                    StickerItem item = new StickerItem();
                    item.setCategory(COLLECT_NAME);
                    item.setPath(collect.path);
                    item.setType(EmoticonType.COLLECT.value);
                    stickers.add(item);
                    break;
                }
            }
            for (CubeEmojiCollect collect : data.collects) {
                if (collect.path.contains(COLLECT_SETTING)) {
                    continue;
                }
                StickerItem item = new StickerItem();
                item.setCategory(COLLECT_NAME);
                item.setPath(collect.path);
                item.setType(EmoticonType.COLLECT.value);
                stickers.add(item);
            }
        }
        this.stickerList = stickers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStickerList(List<StickerItem> stickerList) {
        this.stickerList = stickerList;
    }

    public List<StickerItem> getStickerList() {
        return stickerList;
    }

    public boolean hasStickerList() {
        return stickerList != null && stickerList.size() > 0;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StickerType)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        StickerType r = (StickerType) o;
        return r.getName().equals(getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
