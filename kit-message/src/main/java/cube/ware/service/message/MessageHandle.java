package cube.ware.service.message;

import android.text.TextUtils;
import com.common.mvp.rx.RxSchedulers;
import com.common.utils.utils.EmptyUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.CubeErrorCode;
import cube.service.DeviceInfo;
import cube.service.message.FileMessageStatus;
import cube.service.message.MessageListener;
import cube.service.message.MessageType;
import cube.service.message.CustomMessage;
import cube.service.message.FileMessage;
import cube.service.message.MessageEntity;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.chat.message.Listener.FileMessageDownloadListener;
import cube.ware.service.message.chat.message.Listener.FileMessageUploadListener;
import cube.ware.service.message.chat.message.MessageHandler;
import cube.ware.service.message.manager.MessageManager;
import cube.ware.service.message.manager.ReceiptManager;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import rx.schedulers.Schedulers;

/**
 * 引擎消息服务处理
 *
 * @author LiuFeng
 * @date 2018-8-09
 */
public class MessageHandle implements MessageListener {

    private static MessageHandle instance = new MessageHandle();

    private Map<Long, Map<String, FileMessageUploadListener>>   uploadListenerMap   = new ConcurrentHashMap<>();   // 文件上传监听
    private Map<Long, Map<String, FileMessageDownloadListener>> downloadListenerMap = new ConcurrentHashMap<>();   // 文件下载监听

    private MessageHandle() {}

    public static MessageHandle getInstance() {
        return instance;
    }

    /**
     * 启动监听
     */
    public void start() {
        CubeEngine.getInstance().getMessageService().addMessageListener(this);
        //添加消息接收处理监听
        MessageHandler.getInstance().setListener(new MessageHandler.MessageDataListener() {
            @Override
            public void onReceiveMessages(LinkedList<MessageEntity> data) {
                LogUtil.d("onReceiveMessages" + (data == null ? "data is null" : data.size()));
                MessageManager.getInstance().addMessagesToLocal(data);
            }
        });
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().getMessageService().removeMessageListener(this);
        MessageHandler.getInstance().onDestroy();
    }

    /**
     * 消息发送成功时回调。
     *
     * @param message
     */
    @Override
    public void onSent(MessageEntity message) {
        LogUtil.i("发送的消息: " + message.toString());
        //if (message instanceof ReceiptMessage) {
        //    ReceiptManager.getInstance().onReceiptedAll(message, message.getFromDevice());
        //    return;
        //}
        if (isFromMyDevice(message)) {
            MessageManager.getInstance().updateMessageInLocal(message).subscribe();
        }
        else {
            MessageHandler.getInstance().read(message);
        }
    }

    /**
     * 上传进度
     *
     * @param message
     * @param processed
     * @param total
     */
    @Override
    public void onUploading(MessageEntity message, long processed, long total) {
        LogUtil.i("文件上传中的消息: sn:" + message.getSerialNumber() + "当前大小:" + processed + " 总大小:" + total);

        // 监听上传中回调
        Map<String, FileMessageUploadListener> uploadMap = uploadListenerMap.get(message.getSerialNumber());
        if (EmptyUtil.isNotEmpty(uploadMap)) {
            for (Map.Entry<String, FileMessageUploadListener> entry : uploadMap.entrySet()) {
                entry.getValue().onUploading((FileMessage) message, processed, total);
            }
        }
    }

    /**
     * 消息数据上传完成
     *
     * @param message
     */
    @Override
    public void onUploadCompleted(MessageEntity message) {
        LogUtil.i("文件上传完成的消息:" + message.toString());

        // 更新消息到数据库
        MessageManager.getInstance().updateMessageInLocal(message).subscribeOn(Schedulers.io()).compose(RxSchedulers.<CubeMessage>io_main()).subscribe();

        // 监听上传完成回调
        Map<String, FileMessageUploadListener> uploadMap = uploadListenerMap.get(message.getSerialNumber());
        if (EmptyUtil.isNotEmpty(uploadMap)) {
            for (Map.Entry<String, FileMessageUploadListener> entry : uploadMap.entrySet()) {
                entry.getValue().onUploadCompleted((FileMessage) message);
            }
            // 上传结束后移除此消息监听
            uploadListenerMap.remove(message.getSerialNumber());
        }
    }

    /**
     * 下载进度
     *
     * @param message
     * @param processed
     * @param total
     */
    @Override
    public void onDownloading(MessageEntity message, long processed, long total) {

        LogUtil.i("文件下载中的消息: sn:" + message.getSerialNumber() + "当前大小:" + processed + " 总大小:" + total);

        // 监听下载中回调
        Map<String, FileMessageDownloadListener> downloadMap = downloadListenerMap.get(message.getSerialNumber());
        if (EmptyUtil.isNotEmpty(downloadMap)) {
            for (Map.Entry<String, FileMessageDownloadListener> entry : downloadMap.entrySet()) {
                entry.getValue().onDownloading((FileMessage) message, processed, total);
            }
        }
    }

    /**
     * 消息数据下载完成
     *
     * @param message
     */
    @Override
    public void onDownloadCompleted(MessageEntity message) {
        LogUtil.i("文件下载完成的消息:" + message.toString());

        if (message.getType().equals(MessageType.File) && !message.isGroupMessage() && !message.getSender().getCubeId().equals(CubeCore.getInstance().getCubeId())) {
            FileMessage fileMessage = (FileMessage) message;
            CustomMessage customMessage = MessageManager.getInstance().buildCustomMessage(CubeSessionType.P2P, CubeCore.getInstance().getCubeId(), message.getSender().getCubeId(), "");
            customMessage.setHeader("operate", "download");
            customMessage.setHeader("type", "notify");
            customMessage.setHeader("sn", String.valueOf(message.getSerialNumber()));
            customMessage.setHeader("fileName", fileMessage.getFileName());
            customMessage.setHeader("fileSize", String.valueOf(fileMessage.getFileSize()));
            customMessage.setHeader("fileMD5", fileMessage.getMd5());
            //customMessage.setSyncType(SyncType.RECEIVER);
            customMessage.setExpires(3 * 24 * 3600); // 设置过期时间
            CubeEngine.getInstance().getMessageService().sendMessage(customMessage);
        }

        // 更新消息到数据库
        MessageManager.getInstance().updateMessageInLocal(message).compose(RxSchedulers.<CubeMessage>io_main()).subscribe();

        // 监听下载完成回调
        Map<String, FileMessageDownloadListener> downloadMap = downloadListenerMap.get(message.getSerialNumber());
        if (EmptyUtil.isNotEmpty(downloadMap)) {
            for (Map.Entry<String, FileMessageDownloadListener> entry : downloadMap.entrySet()) {
                entry.getValue().onDownloadCompleted((FileMessage) message);
            }
            downloadListenerMap.remove(message.getSerialNumber());
        }
    }

    @Override
    public void onForwarded(List<MessageEntity> list, List<MessageEntity> list1) {

    }

    /**
     * 消息撤回成功时回调。
     *
     * @param message
     */
    @Override
    public void onRecalled(MessageEntity message) {
        LogUtil.i("撤回的消息:" + message.toString());
        MessageManager.getInstance().reCallMessage(CubeCore.getContext(), message);
        //取消消息的后续处理，如：下载等
        CubeEngine.getInstance().getMessageService().pauseMessage(message.getSerialNumber());
    }

    @Override
    public void onReceiptedAll(String s, long l, DeviceInfo deviceInfo) {

    }

    @Override
    public void onReceipted(List<MessageEntity> list, DeviceInfo deviceInfo) {

    }

    /**
     * 收到消息时回调。
     *
     * @param message
     */
    @Override
    public void onReceived(MessageEntity message) {
        LogUtil.i("接收的消息: " + message.toString());
        //if (message instanceof ReceiptMessage) {
        //    //收到的回执消息，表示会话对端已回执（已读），无需求暂时不处理。
        //    ReceiptManager.getInstance().onReceiptedAll(message, message.getFromDevice());
        //    return;
        //}
        MessageHandler.getInstance().read(message);
    }

    /**
     * 消息恢复
     *
     * @param message
     */
    @Override
    public void onResumed(MessageEntity message) {

    }

    /**
     * 开始同步消息
     */
    @Override
    public void onMessageSyncBegin() {
        LogUtil.i("消息同步开始");
    }

    /**
     * 同步未拉取消息
     *
     * @param map 未拉取消息集合（key 聊天对象 value 未拉取消息数组）
     */
    @Override
    public void onMessagesSyncing(HashMap<String, List<MessageEntity>> map) {
        LogUtil.i("消息同步中--> 消息数量为：" + (map != null ? map.values().size() : 0));
        MessageManager.getInstance().onSyncingMessage(CubeCore.getContext(), map);
    }

    /**
     * 结束同步消息
     */
    @Override
    public void onMessageSyncEnd() {
        LogUtil.i("消息同步结束");
    }

    @Override
    public long getSyncBeginTime() {
        return 0;
    }

    /**
     * 当消息处理失败时回调。
     *
     * @param message
     * @param cubeError
     */
    @Override
    public void onMessageFailed(MessageEntity message, CubeError cubeError) {
        LogUtil.i("消息错误：" + cubeError.desc + "code: " + cubeError.code);
        if (CubeErrorCode.NoReceiver == CubeErrorCode.convert(cubeError.code)) {
            ToastUtil.showToast(CubeCore.getContext(), "消息发送失败 没有接收者！");
            return;
        }
        if (CubeErrorCode.NoPullMessage == CubeErrorCode.convert(cubeError.code)) {
            ToastUtil.showToast(CubeCore.getContext(), "没有更多消息啦~");
            return;
        }
        if (null != message) {
            if (message instanceof FileMessage) {
                FileMessage fileMessage = (FileMessage) message;
                fileMessage.setFileStatus(FileMessageStatus.Failed);

                // 监听上传失败回调
                Map<String, FileMessageUploadListener> uploadMap = uploadListenerMap.get(message.getSerialNumber());
                if (EmptyUtil.isNotEmpty(uploadMap)) {
                    for (Map.Entry<String, FileMessageUploadListener> entry : uploadMap.entrySet()) {
                        entry.getValue().onUploadFailed(fileMessage, cubeError);
                    }
                    uploadListenerMap.remove(message.getSerialNumber());
                }
            }
            MessageManager.getInstance().updateMessageInLocal(message).subscribe();
        }
    }

    @Override
    public void onFileMessageFailed(boolean b, MessageEntity messageEntity, CubeError cubeError) {

    }

    @Override
    public void onMessageCanceled(MessageEntity messageEntity) {

    }

    @Override
    public void onUploadStart(MessageEntity messageEntity) {

    }

    @Override
    public void onDownloadStart(MessageEntity messageEntity) {

    }

    @Override
    public void onUploadPaused(MessageEntity messageEntity) {

    }

    @Override
    public void onDownloadPaused(MessageEntity messageEntity) {

    }

    /**
     * 添加上传监听
     *
     * @param sn       文件sn
     * @param key      同一上传文件的不同地方的监听的key不同
     * @param listener
     */
    public void addUploadListener(long sn, String key, FileMessageUploadListener listener) {
        Map<String, FileMessageUploadListener> uploadMap = uploadListenerMap.get(sn);
        if (uploadMap == null) {
            uploadMap = new ConcurrentHashMap<>();
        }
        uploadMap.put(key, listener);
        uploadListenerMap.put(sn, uploadMap);
    }

    /**
     * 删除上传监听
     *
     * @param sn  文件sn
     * @param key 同一上传文件的不同地方的监听的key不同
     */
    public void removeUploadListener(long sn, String key) {
        Map<String, FileMessageUploadListener> uploadMap = uploadListenerMap.get(sn);
        if (EmptyUtil.isNotEmpty(uploadMap) && uploadMap.containsKey(key)) {
            uploadMap.remove(key);
        }
    }

    /**
     * 添加下载监听
     *
     * @param sn       文件sn
     * @param key      同一下载文件的不同地方的监听的key不同
     * @param listener
     */
    public void addDownloadListener(long sn, String key, FileMessageDownloadListener listener) {
        Map<String, FileMessageDownloadListener> downloadMap = downloadListenerMap.get(sn);
        if (downloadMap == null) {
            downloadMap = new ConcurrentHashMap<>();
        }
        downloadMap.put(key, listener);
        downloadListenerMap.put(sn, downloadMap);
    }

    /**
     * 删除下载监听
     *
     * @param sn  文件sn
     * @param key 同一下载文件的不同地方的监听的key不同
     */
    public void removeDownloadListener(long sn, String key) {
        Map<String, FileMessageDownloadListener> downloadMap = downloadListenerMap.get(sn);
        if (EmptyUtil.isNotEmpty(downloadMap) && downloadMap.containsKey(key)) {
            downloadMap.remove(key);
        }
    }

    /**
     * 判断一个文件是否在下载中
     */
    public boolean isFileMessageDownloading(long sn) {
        return this.downloadListenerMap.keySet().contains(sn);
    }

    private boolean isFromMyDevice(MessageEntity messageEntity) {
        DeviceInfo fromDevice = messageEntity.getFromDevice();
        if (fromDevice == null) {
            fromDevice = CubeEngine.getInstance().getDeviceInfo();
        }
        if (fromDevice == null) {
            return true;
        }
        if (TextUtils.isEmpty(fromDevice.getDeviceId()) || TextUtils.isEmpty(fromDevice.getDeviceId())) {
            return false;
        }
        return TextUtils.equals(fromDevice.getDeviceId(), fromDevice.getDeviceId());
    }
}