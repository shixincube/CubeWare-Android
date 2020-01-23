package cube.ware;

import cube.ware.service.message.chat.ChatEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息监听全局管理类
 *
 * @author LiuFeng
 * @data 2020/1/23 11:31
 */
public class MessageListenerManager {

    private static MessageListenerManager mInstance = new MessageListenerManager();

    // 会话窗口消息列表一些点击事件的响应处理回调
    private List<ChatEventListener> sChatEventListeners = new ArrayList<>();

    /**
     * 私有化构造方法
     */
    private MessageListenerManager() {}

    /**
     * 获取 CubeUI 单例对象
     *
     * @return
     */
    public static MessageListenerManager getInstance() {
        return mInstance;
    }

    /**
     * 获取聊天界面事件监听器
     *
     * @return
     */
    public List<ChatEventListener> getChatEventListeners() {
        return sChatEventListeners;
    }

    /**
     * 设置聊天界面的事件监听器
     *
     * @param chatEventListener
     */
    public void addChatEventListener(ChatEventListener chatEventListener) {
        if (chatEventListener != null && !sChatEventListeners.contains(chatEventListener)) {
            sChatEventListeners.add(chatEventListener);
        }
    }

    /**
     * 删除聊天界面的事件监听器
     *
     * @param chatEventListener
     */
    public void removeChatEventListener(ChatEventListener chatEventListener) {
        if (chatEventListener != null && sChatEventListeners.contains(chatEventListener)) {
            sChatEventListeners.remove(chatEventListener);
        }
    }
}
