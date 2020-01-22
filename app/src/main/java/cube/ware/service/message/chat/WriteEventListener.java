package cube.ware.service.message.chat;


import cube.service.message.model.MessageEntity;

public interface WriteEventListener {
    public void onWriting(MessageEntity message);
}
