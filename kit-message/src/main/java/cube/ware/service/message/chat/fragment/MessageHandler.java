package cube.ware.service.message.chat.fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.LinkedList;

import cube.service.message.MessageEntity;
import cube.ware.service.message.manager.MessageManager;

/**
 * @author Wangxx
 * @date 2017/5/8
 */

public class MessageHandler extends Handler {
    private LinkedList<MessageEntity> bufferList;
    private                 boolean        start        = true;
    private static volatile Looper         mLooper      = null;
    private static volatile MessageHandler mHandler     = null;
    private static final    int            READ_MESSAGE = 0;
    private static final    int            SAVE_MESSAGE = 1;
    private static final    long           SPACE_TIME   = 600;

    public static MessageHandler getInstance() {
        if (null == mHandler) {
            synchronized (MessageHandler.class) {
                if (null == mHandler) {
                    HandlerThread receiverThread = new HandlerThread("[receiver_message]");
                    receiverThread.start();
                    mLooper = receiverThread.getLooper();
                    mHandler = new MessageHandler(mLooper);
                }
            }
        }
        return mHandler;
    }

    private MessageHandler(Looper looper) {
        super(looper);
        if (bufferList == null) {
            bufferList = new LinkedList<>();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        // 执行任务操作
        switch (msg.what) {
            case READ_MESSAGE:
                if (msg.obj instanceof MessageEntity) {
                    MessageEntity obj = (MessageEntity) msg.obj;
                    bufferList.add(obj);
                    if (start) {
                        start = false;
                        sendEmptyMessageDelayed(SAVE_MESSAGE, SPACE_TIME);
                    }
                }
                break;
            case SAVE_MESSAGE:
                start = true;
                LinkedList<MessageEntity> list = new LinkedList<>();
                if (mListener != null) {
                    list.addAll(bufferList);
                    clear();
                    mListener.onReceiveMessages(list);
                }
                break;
        }
    }

    public void read(MessageEntity msg) {
        sendMessage(obtainMessage(READ_MESSAGE, msg));
    }

    private void clear() {
        if (this.bufferList != null) {
            this.bufferList.clear();
        }
    }

    public void onSaveInstanceState() {
        MessageManager.getInstance().addMessagesToLocal(bufferList);
    }

    private MessageDataListener mListener;

    public interface MessageDataListener {
        void onReceiveMessages(LinkedList<MessageEntity> data);
    }

    public void setListener(MessageDataListener listener) {
        mListener = listener;
    }

    public void onDestroy() {
        // 销毁时要调用quit方法，将工作线程的消息循环停止
        if (mLooper != null) {
            mLooper.quit();
            mHandler = null;
        }
    }
}
