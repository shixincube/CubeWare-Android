package cube.ware.service.message.chat.listener;

import android.content.Context;

import cube.ware.data.room.model.CubeMessage;


/**
 * 会话窗口消息列表一些点击事件的响应处理函数
 *
 * @author Wangxx
 * @date 2017/1/14
 */
public interface ChatEventListener {

    // 头像点击事件处理，一般用于打开用户资料页面
    void onAvatarClicked(Context context, CubeMessage cubeMessage);

    // 头像长按事件处理，一般用于群组@功能
    void onAvatarLongClicked(Context context, CubeMessage cubeMessage);
}
