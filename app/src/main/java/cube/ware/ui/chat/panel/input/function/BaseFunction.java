package cube.ware.ui.chat.panel.input.function;

import android.content.Intent;

import java.io.Serializable;

import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.ui.chat.BaseChatActivity;
import cube.ware.ui.chat.ChatContainer;
import cube.ware.ui.chat.panel.input.InputPanel;

/**
 * 更多功能组件基类
 * 注意:requestCode仅能使用最低8位。
 *
 * @author Wangxx
 * @date 2017/1/3
 */
public abstract class BaseFunction implements Serializable {

    private int mIconResId; // 图标资源id
    private int mTitleId;   // 标题string资源id

    private transient int           mIndex; // 下标位置
    private transient ChatContainer mChatContainer; // 聊天容器
    private transient InputPanel    mInputPanel; // 聊天控制器

    /**
     * 构造方法
     *
     * @param iconResId 图标资源id
     * @param titleId   标题string资源id
     */
    protected BaseFunction(int iconResId, int titleId) {
        this.mIconResId = iconResId;
        this.mTitleId = titleId;
    }

    public BaseChatActivity getActivity() {
        return (BaseChatActivity) this.mChatContainer.mChatActivity;
    }

    public String getChatId() {
        return this.mChatContainer.mChatId;
    }

    public String getChatName() {
        return this.mChatContainer.mChatName;
    }

    public CubeSessionType getChatType() {
        return this.mChatContainer.mSessionType;
    }

    public int getIconResId() {
        return this.mIconResId;
    }

    public int getTitleId() {
        return this.mTitleId;
    }

    public ChatContainer getChatContainer() {
        return this.mChatContainer;
    }

    public void setChatContainer(ChatContainer container) {
        this.mChatContainer = container;
    }

    public InputPanel getInputPanel() {
        return mInputPanel;
    }

    public void setInputPanel(InputPanel inputPanel) {
        mInputPanel = inputPanel;
    }

    public void setIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public abstract void onClick();

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // default: empty
    }

    protected int makeRequestCode(int requestCode) {
        if ((requestCode & 0xffffff00) != 0) {
            throw new IllegalArgumentException("Can only use lower 8 bits for requestCode");
        }
        return ((this.mIndex + 1) << 8) + (requestCode & 0xff);
    }
}
