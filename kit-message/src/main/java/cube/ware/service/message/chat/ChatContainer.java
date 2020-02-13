package cube.ware.service.message.chat;

import android.app.Activity;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.message.chat.fragment.MessagePresenter;
import cube.ware.service.message.chat.panel.input.InputPanelProxy;

/**
 * 聊天容器
 * 描述：为了方便子组件与其宿主的通信
 *
 * @author Wangxx
 * @date 2017/1/3
 */
public class ChatContainer {
    public final Activity        mChatActivity;
    public final String          mChatId;
    public       String          mChatName;
    public final CubeSessionType mSessionType;
    public final InputPanelProxy mPanelProxy;
    public final MessagePresenter mPresenter;

    public ChatContainer(Activity chatActivity, String chatId, String chatName, CubeSessionType sessionType, InputPanelProxy panelProxy, MessagePresenter presenter) {
        this.mChatActivity = chatActivity;
        this.mChatId = chatId;
        this.mChatName = chatName;
        this.mSessionType = sessionType;
        this.mPanelProxy = panelProxy;
        this.mPresenter = presenter;
    }

    @Override
    public String toString() {
        return "ChatContainer{" + "mChatId='" + mChatId + '\'' + ", mChatName='" + mChatName + '\'' + ", mSessionType=" + mSessionType + '}';
    }
}
