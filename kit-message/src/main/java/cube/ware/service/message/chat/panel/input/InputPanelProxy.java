package cube.ware.service.message.chat.panel.input;


import cube.ware.data.room.model.CubeMessage;

/**
 * 底部输入面板提供给子模块的代理接口
 *
 * @author Wangxx
 * @date 2017/1/6
 */

public interface InputPanelProxy {
    /**
     * 发送消息的回调
     *
     * @param cubeMessage
     *
     * @return
     */
    boolean onMessageSend(CubeMessage cubeMessage);

    /**
     * 消息持久化到数据库回调
     *
     * @param cubeMessage
     *
     * @return
     */
    boolean onMessagePersisted(CubeMessage cubeMessage);

    /**
     * 数据库中的消息状态被更新
     *
     * @param cubeMessage
     *
     * @return
     */
    boolean onMessageInLocalUpdated(CubeMessage cubeMessage);

    /**
     * 删除聊天记录
     */
    void deleteMessage();

    /**
     * 输入面板已展开
     */
    void inputPanelExpanded();

    /**
     * 收缩输入面板
     */
    void collapseInputPanel();

    /**
     * 是否正在录音
     *
     * @return
     */
    boolean isLongClickEnabled();

    void onReplyMessage(CubeMessage cubeMessage);
}
