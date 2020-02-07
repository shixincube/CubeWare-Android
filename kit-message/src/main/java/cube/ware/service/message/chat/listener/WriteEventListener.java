package cube.ware.service.message.chat.listener;


import cube.service.message.MessageEntity;

public interface WriteEventListener {
    public void onWriting(MessageEntity message);
}
