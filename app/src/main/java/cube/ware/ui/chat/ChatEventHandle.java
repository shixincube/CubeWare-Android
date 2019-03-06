package cube.ware.ui.chat;

import java.util.ArrayList;
import java.util.List;

/**
 * 会话窗口消息列表一些点击事件的响应处理回调
 *
 * @author LiuFeng
 * @data 2019/3/6 16:13
 */
public class ChatEventHandle {
    private static ChatEventHandle instance = new ChatEventHandle();

    private List<ChatEventListener> mListeners = new ArrayList<>();

    public static ChatEventHandle getInstance() {
        return instance;
    }

    /**
     * 获取聊天界面事件监听器
     *
     * @return
     */
    public List<ChatEventListener> getChatEventListeners() {
        return mListeners;
    }

    /**
     * 设置聊天界面的事件监听器
     *
     * @param chatEventListener
     */
    public void addChatEventListener(ChatEventListener chatEventListener) {
        if (chatEventListener != null && !mListeners.contains(chatEventListener)) {
            mListeners.add(chatEventListener);
        }
    }

    /**
     * 删除聊天界面的事件监听器
     *
     * @param chatEventListener
     */
    public void removeChatEventListener(ChatEventListener chatEventListener) {
        if (chatEventListener != null && mListeners.contains(chatEventListener)) {
            mListeners.remove(chatEventListener);
        }
    }
}
