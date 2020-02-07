package cube.ware.service.message.chat.panel.input.emoticon.model;

/**
 * 表情底部栏目
 *
 * @author LiuFeng
 * @data 2020/2/7 20:36
 */
public enum EmoticonType {
    /**
     * 贴图表情
     */
    SYSTEM(1),

    /**
     * 收藏表情
     */
    COLLECT(2);

    public int value;

    EmoticonType(int num) {
        this.value = num;
    }
}
