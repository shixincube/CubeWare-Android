package cube.ware.service.message.recent.listener;

/**
 * 未读消息总数监听器
 *
 * @author PengZhenjin
 * @date 2017-2-25
 */
public interface UnreadMessageCountListener {

    /**
     * 设置未读消息总数
     *
     * @param count
     *
     * @return
     */
    void setUnreadMessageCount(int count, boolean isSecret);
}
