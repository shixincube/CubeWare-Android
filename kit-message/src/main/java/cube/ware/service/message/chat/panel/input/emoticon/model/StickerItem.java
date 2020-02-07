package cube.ware.service.message.chat.panel.input.emoticon.model;

/**
 * 贴图表情Item
 *
 * @author Wangxx
 * @date 2017/1/4
 */
public class StickerItem {
    private String category;    //类 别名
    private String packgeId;
    private String name;
    private String key; // 表情 key
    private String path;
    private String thumbUrl;
    private String url;
    private int    type; // 表情类型 1：贴图表情， 2：收藏表情

    public String getPackgeId() {
        return packgeId;
    }

    public void setPackgeId(String packgeId) {
        this.packgeId = packgeId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StickerItem) {
            StickerItem item = (StickerItem) o;
            return item.getCategory().equals(category) && item.getName().equals(name);
        }
        return false;
    }
}
