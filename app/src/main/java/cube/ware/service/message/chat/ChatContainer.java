package cube.ware.service.message.chat;

import android.app.Activity;

import com.common.mvp.rx.RxManager;

import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.message.chat.panel.input.InputPanelProxy;


/**
 * 聊天容器
 *
 * @author Wangxx
 * @date 2017/1/3
 */
// TODO: 2017/9/9
// 因为聊天界面下划分了很多例如MessgeListPannel inputPanel等子组件 该类为了方便子组件与其宿主的通信
// 该类为了方便与附着在聊天界面下的子组件的沟通 不应该用来放在MessageManager中监听消息的事件
public class ChatContainer {
    public final Activity        mChatActivity;
    public final String          mChatId;
    public String          mChatName;//会话名称可能需要改变
    public final CubeSessionType mSessionType;
    public final InputPanelProxy mPanelProxy;
    public final RxManager       mRxManager;

    public ChatContainer(Activity chatActivity, String chatId, String chatName, CubeSessionType sessionType, InputPanelProxy panelProxy, RxManager rxManager) {
        this.mChatActivity = chatActivity;
        this.mChatId = chatId;
        this.mChatName = chatName;
        this.mSessionType = sessionType;
        this.mPanelProxy = panelProxy;
        this.mRxManager = rxManager;
    }

    @Override
    public String toString() {
        return "ChatContainer{" + "mChatId='" + mChatId + '\'' + ", mChatName='" + mChatName + '\'' + ", mSessionType=" + mSessionType + '}';
    }
}
