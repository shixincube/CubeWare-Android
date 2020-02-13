package cube.ware.service.message.manager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.common.mvp.eventbus.EventBusUtil;
import com.common.mvp.rx.RxSchedulers;
import com.common.mvp.rx.subscriber.OnNoneSubscriber;
import com.common.utils.utils.EmptyUtil;
import com.common.utils.utils.ThreadUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.message.MessageEntity;
import cube.service.message.Receiver;
import cube.service.message.Sender;
import cube.service.message.VideoClipMessage;
import cube.ware.api.CubeUI;
import cube.ware.common.MessageConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.mapper.MessageMapper;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.repository.CubeMessageRepository;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.MessageApi;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.ChatContainer;
import cube.ware.service.message.recent.manager.RecentSessionManager;
import cube.ware.utils.FileUtil;
import cube.ware.utils.ImageUtil;
import cube.ware.utils.SpUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 消息管理器，用于处理引擎和本地消息的交互
 *
 * @author LiuFeng
 * @data 2020/2/11 16:05
 */
public class MessageManager {

    private Map<Fragment, ChatContainer> mContainer = new ConcurrentHashMap<>();

    public static MessageManager getInstance() {
        return MessageManagerHolder.INSTANCE;
    }

    private static class MessageManagerHolder {
        private static final MessageManager INSTANCE = new MessageManager();
    }

    private MessageManager() {}

    /**
     * 设置消息容器
     *
     * @param container
     */
    public void addContainer(Fragment fragment, ChatContainer container) {
        mContainer.put(fragment, container);
    }

    /**
     * 移除消息容器
     *
     * @param fragment
     */
    public void removeContainer(Fragment fragment) {
        if (!mContainer.isEmpty()) {
            mContainer.remove(fragment);
        }
    }

    /**
     * 处理同步消息
     *
     * @param hashMap
     */
    public void onSyncingMessage(final HashMap<String, List<MessageEntity>> hashMap) {
        if (EmptyUtil.isEmpty(hashMap)) {
            return;
        }

        ThreadUtil.request(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("MessagesSyncing");
                LinkedList<MessageEntity> messageList = new LinkedList<>();
                for (List<MessageEntity> value : hashMap.values()) {
                    messageList.addAll(value);
                }

                LogUtil.i("消息同步中--> 消息数量为：" + messageList.size());
                handleSyncMessage(messageList);
            }
        });
    }

    /**
     * 处理离线同步消息
     *
     * @param messages
     */
    private void handleSyncMessage(List<MessageEntity> messages) {
        // 检查是否有撤回消息
        List<MessageEntity> reCallList = new ArrayList<>();
        Iterator<MessageEntity> iterator = messages.iterator();
        while (iterator.hasNext()) {
            MessageEntity next = iterator.next();
            if (next.isRecalled()) {
                reCallList.add(next);
                iterator.remove();
                LogUtil.i("isRecalled --> sn: " + next.getSerialNumber());
            }
        }

        if (!reCallList.isEmpty()) {
            //处理断网时对方撤回消息的逻辑
            handleSyncRecalledMessages(reCallList);
        }

        if (!messages.isEmpty()) {
            RecentSessionManager.getInstance().addOrUpdateRecentSession(messages);
            CubeMessageRepository.getInstance().addMessage(messages, true).subscribe(new Action1<List<CubeMessage>>() {
                @Override
                public void call(List<CubeMessage> cubeMessages) {
                    EventBusUtil.post(MessageConstants.Event.EVENT_SYNCING_MESSAGE, cubeMessages);
                }
            });
        }
    }

    /**
     * 处理同步的撤回消息
     *
     * @param messageEntities
     */
    public void handleSyncRecalledMessages(final List<MessageEntity> messageEntities) {
        Observable.just(messageEntities).map(new Func1<List<MessageEntity>, List<CubeMessage>>() {
            @Override
            public List<CubeMessage> call(List<MessageEntity> messageEntities) {
                List<CubeMessage> cubeMessages = MessageMapper.convertTo(messageEntities, false);
                for (CubeMessage message : cubeMessages) {
                    buildRecallMessage(message);
                }

                return cubeMessages;
            }
        }).doOnNext(new Action1<List<CubeMessage>>() {
            @Override
            public void call(List<CubeMessage> cubeMessages) {
                CubeMessageRepository.getInstance().saveOrUpdate(cubeMessages);
                RecentSessionManager.getInstance().addOrUpdateRecentSession(messageEntities);
            }
        }).subscribeOn(Schedulers.io()).subscribe(new OnNoneSubscriber<>());
    }

    /**
     * 处理在线撤回消息
     *
     * @param messageEntity
     */
    public void onRecalled(final MessageEntity messageEntity) {
        Observable.just(messageEntity).map(new Func1<MessageEntity, CubeMessage>() {
            @Override
            public CubeMessage call(MessageEntity messageEntity) {
                CubeMessage cubeMessage = MessageMapper.convertTo(messageEntity, false);
                buildRecallMessage(cubeMessage);
                return cubeMessage;
            }
        }).doOnNext(new Action1<CubeMessage>() {
            @Override
            public void call(CubeMessage cubeMessage) {
                CubeMessageRepository.getInstance().saveOrUpdate(cubeMessage);
                RecentSessionManager.getInstance().addOrUpdateRecentSession(messageEntity);
            }
        }).compose(RxSchedulers.<CubeMessage>io_main()).subscribe(new Action1<CubeMessage>() {
            @Override
            public void call(CubeMessage cubeMessage) {
                updateLocalMessage(cubeMessage);
            }
        });
    }

    /**
     * 构建撤回消息显示的内容
     *
     * @param cubeMessage
     */
    private void buildRecallMessage(CubeMessage cubeMessage) {
        cubeMessage.setMessageType(CubeMessageType.RECALLMESSAGETIPS);
        cubeMessage.setRead(true);
        if (cubeMessage.isReceivedMessage()) {
            cubeMessage.setContent(CubeCore.getContext().getString(R.string.notice_received_recall, cubeMessage.getSenderName()));
        }
        else {
            cubeMessage.setContent(CubeCore.getContext().getString(R.string.notice_send_recall));
        }
    }

    /**
     * 发送消息 封装引擎发送消息方法
     *
     * @param messageEntity
     */
    public void sendMessage(@NonNull final MessageEntity messageEntity) {
        LogUtil.d("sendMessage --> sn: " + messageEntity.getSerialNumber());
        MessageApi.sendMessage(messageEntity).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CubeMessage>() {
            @Override
            public void call(CubeMessage cubeMessage) {
                messageSend(cubeMessage);
            }
        });
    }

    /**
     * 发送文件消息
     *
     * @param sessionType
     * @param receiver
     * @param filePath
     * @param isAnonymous
     * @param isCompress
     */
    public void sendFileMessage(final CubeSessionType sessionType, final Receiver receiver, final String filePath, final boolean isAnonymous, boolean isCompress) {
        File file = new File(filePath);
        if (TextUtils.isEmpty(filePath) || !file.exists() || file.length() == 0) {
            ToastUtil.showToast(CubeUI.getInstance().getContext(), R.string.invilid_file);
            return;
        }

        final MessageEntity messageEntity;
        int fileType = FileUtil.isImageOrVideo(FileUtil.getFileExtensionName(filePath));
        // 图片
        if (fileType == 0) {
            if (isCompress && !ImageUtil.isGif(filePath)) {
                handleCompressImage(sessionType, receiver, filePath, isAnonymous);
            }
            else {
                messageEntity = MessageBuilder.buildImageMessage(sessionType, new Sender(CubeCore.getInstance().getCubeId(), SpUtil.getUserName()), receiver, filePath, true, isAnonymous);
                sendMessage(messageEntity);
            }
        }
        // 视频
        else if (fileType == 1) {
            messageEntity = MessageBuilder.buildVideoMessage(sessionType, new Sender(CubeCore.getInstance().getCubeId(), SpUtil.getUserName()), receiver, filePath, isAnonymous);
            ((VideoClipMessage) messageEntity).setDuration(FileUtil.getVideoDuration(filePath));
            sendMessage(messageEntity);
        }
        // 其他文件
        else {
            messageEntity = MessageBuilder.buildFileMessage(sessionType, new Sender(CubeCore.getInstance().getCubeId(), SpUtil.getUserName()), receiver, filePath);
            sendMessage(messageEntity);
        }
    }

    /**
     * 处理压缩图片
     *
     * @param sessionType
     * @param receiver
     * @param filePath
     * @param isAnonymous
     */
    private void handleCompressImage(final CubeSessionType sessionType, final Receiver receiver, final String filePath, final boolean isAnonymous) {
        Observable.create(new Action1<Emitter<String>>() {
            @Override
            public void call(final Emitter<String> emitter) {
                Luban.with(CubeCore.getContext()).load(filePath)
                     // 忽略不压缩图片的大小
                     .ignoreBy(100)
                     // 设置压缩后文件存储位置
                     .setTargetDir(SpUtil.getImagePath())
                     //设置回调
                     .setCompressListener(new OnCompressListener() {
                         @Override
                         public void onStart() {}

                         @Override
                         public void onSuccess(File newFile) {
                             emitter.onNext(newFile.getPath());
                             emitter.onCompleted();
                         }

                         @Override
                         public void onError(Throwable e) {
                             emitter.onNext(filePath);
                             emitter.onCompleted();
                         }
                     })
                     //启动压缩
                     .launch();
            }
        }, Emitter.BackpressureMode.BUFFER).compose(RxSchedulers.<String>io_main()).subscribe(new Action1<String>() {
            @Override
            public void call(String path) {
                MessageEntity messageEntity = MessageBuilder.buildImageMessage(sessionType, new Sender(CubeCore.getInstance().getCubeId(), SpUtil.getUserName()), receiver, path, true, isAnonymous);
                sendMessage(messageEntity);
            }
        });
    }

    /**
     * 本地添加一条消息到数据库
     *
     * @param messageEntity
     */
    public Observable<CubeMessage> addMessageToLocal(@NonNull final MessageEntity messageEntity) {
        return CubeMessageRepository.getInstance().addMessage(messageEntity).map(new Func1<CubeMessage, CubeMessage>() {
            @Override
            public CubeMessage call(CubeMessage cubeMessage) {
                RecentSessionManager.getInstance().addOrUpdateRecentSession(messageEntity);
                return cubeMessage;
            }
        }).observeOn(AndroidSchedulers.mainThread()).map(new Func1<CubeMessage, CubeMessage>() {
            @Override
            public CubeMessage call(CubeMessage cubeMessage) {
                messagePersisted(cubeMessage);
                return cubeMessage;
            }
        });
    }

    /**
     * 正常在线发/接收消息
     *
     * @param messages
     */
    public void addMessagesToLocal(@NonNull final List<MessageEntity> messages) {
        CubeMessageRepository.getInstance().addMessage(messages, false).doOnNext(new Action1<List<CubeMessage>>() {
            @Override
            public void call(List<CubeMessage> cubeMessages) {
                // 保存最近消息，并发送刷新最近消息列表的事件通知
                RecentSessionManager.getInstance().addOrUpdateRecentSession(messages);
            }
        }).compose(RxSchedulers.<List<CubeMessage>>io_main()).subscribe(new Action1<List<CubeMessage>>() {

            @Override
            public void call(List<CubeMessage> cubeMessages) {
                if (!mContainer.isEmpty()) {
                    for (CubeMessage cubeMessage : cubeMessages) {
                        messagePersisted(cubeMessage);
                    }
                }
            }
        });
    }

    /**
     * 更新消息在数据库中的状态 （数据库里已经有此条消息）
     *
     * @param messageEntity
     */
    public Observable<CubeMessage> updateMessageToLocal(@NonNull final MessageEntity messageEntity) {
        return CubeMessageRepository.getInstance().addMessage(messageEntity).doOnNext(new Action1<CubeMessage>() {
            @Override
            public void call(CubeMessage cubeMessage) {
                RecentSessionManager.getInstance().addOrUpdateRecentSession(messageEntity);
            }
        }).observeOn(AndroidSchedulers.mainThread()).map(new Func1<CubeMessage, CubeMessage>() {
            @Override
            public CubeMessage call(CubeMessage cubeMessage) {
                updateLocalMessage(cubeMessage);
                return cubeMessage;
            }
        });
    }

    /**
     * 更新消息持久化
     *
     * @param cubeMessage
     */
    public void messagePersisted(CubeMessage cubeMessage) {
        if (!mContainer.isEmpty()) {
            for (ChatContainer chatContainer : mContainer.values()) {
                chatContainer.mPanelProxy.onMessagePersisted(cubeMessage);
            }
        }
    }

    /**
     * 消息已发
     *
     * @param cubeMessage
     */
    private void messageSend(CubeMessage cubeMessage) {
        if (mContainer != null) {
            for (ChatContainer chatContainer : mContainer.values()) {
                chatContainer.mPanelProxy.onMessageSend(cubeMessage);
            }
        }
    }

    /**
     * 更新本地消息状态
     *
     * @param cubeMessage
     */
    private void updateLocalMessage(CubeMessage cubeMessage) {
        if (!mContainer.isEmpty()) {
            for (ChatContainer chatContainer : mContainer.values()) {
                chatContainer.mPanelProxy.onMessageInLocalUpdated(cubeMessage);
            }
        }
    }
}
