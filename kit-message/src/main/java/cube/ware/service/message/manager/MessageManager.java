package cube.ware.service.message.manager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.common.mvp.eventbus.EventBusUtil;
import com.common.mvp.rx.RxSchedulers;
import com.common.utils.utils.ThreadUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.CubeErrorCode;
import cube.service.Session;
import cube.service.call.CallAction;
import cube.service.call.CallDirection;
import cube.service.message.CardMessage;
import cube.service.message.CustomMessage;
import cube.service.message.FileMessage;
import cube.service.message.FileMessageStatus;
import cube.service.message.ImageMessage;
import cube.service.message.MessageDirection;
import cube.service.message.MessageEntity;
import cube.service.message.MessageStatus;
import cube.service.message.Receiver;
import cube.service.message.ReplyMessage;
import cube.service.message.RichContentMessage;
import cube.service.message.Sender;
import cube.service.message.TextMessage;
import cube.service.message.UnKnownMessage;
import cube.service.message.VideoClipMessage;
import cube.service.message.VoiceClipMessage;
import cube.service.message.WhiteboardClipMessage;
import cube.service.message.WhiteboardFrameMessage;
import cube.service.message.WhiteboardMessage;
import cube.ware.common.MessageConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.mapper.MessageMapper;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeCustomMessageType;
import cube.ware.data.model.dataModel.enmu.CubeFileMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageDirection;
import cube.ware.data.model.dataModel.enmu.CubeMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.repository.CubeMessageRepository;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.ChatContainer;
import cube.ware.service.message.recent.manager.RecentSessionManager;
import cube.ware.utils.FileUtil;
import cube.ware.utils.ImageUtil;
import cube.ware.utils.SpUtil;
import cube.ware.utils.StringUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by dth
 * Des: 消息管理器，用于处理引擎和本地消息的交互
 * Date: 2018/8/29.
 */

public class MessageManager {

    public static final long SHOW_TIME_PERIOD = 5 * 60 * 1000;  //消息显示时间的最小间隔时间

    private Map<Fragment, ChatContainer> mContainer = new HashMap<>();

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

    public void onDestroy(Fragment fragment) {
        if (!mContainer.isEmpty()) {
            mContainer.remove(fragment);
        }
    }

    /**
     * 是否是有效的消息
     *
     * @param messageEntity
     *
     * @return
     */
    public boolean isValidMessage(MessageEntity messageEntity) {
        return messageEntity.isValid();
    }

    public boolean isGroupMessage(MessageEntity messageEntity) {
        return messageEntity.isGroupMessage();
    }

    /**
     * 判断是否是有人@我
     *
     * @param messageContent
     *
     * @return
     */
    public boolean isAtMe(String messageContent) {
        if (TextUtils.isEmpty(messageContent)) {
            return false;
        }
        Pattern pattern = Pattern.compile(MessageConstants.REGEX.REGEX_AT_MEMBER);
        Matcher matcher = pattern.matcher(messageContent);
        while (matcher.find()) {
            String oldContent = matcher.group();
            if (oldContent.contains(CubeCore.getInstance().getCubeId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是@全体成员
     *
     * @param messageContent
     *
     * @return
     */
    public boolean isAtAll(String messageContent) {
        if (TextUtils.isEmpty(messageContent)) {
            return false;
        }
        Pattern pattern = Pattern.compile(MessageConstants.REGEX.REGEX_AT_ALL);
        Matcher matcher = pattern.matcher(messageContent);
        return matcher.find();
    }

    public void onSyncingMessage(final Context context, final HashMap<String, List<MessageEntity>> hashMap) {
        if (hashMap == null) {
            return;
        }
        LogUtil.i("onMessagesSyncing size" + hashMap.size());
        ThreadUtil.request(new Runnable() {
            @Override
            public void run() {
                Collection<List<MessageEntity>> values = hashMap.values();
                for (List<MessageEntity> value : values) {
                    LinkedList<MessageEntity> list = new LinkedList<>(value);
                    MessageManager.getInstance().onSyncingMessage(context, list);
                }
            }
        });
    }

    /**
     * 拉取离线消息 sync
     *
     * @param context
     * @param messages onMessagesSyncing
     */
    public void onSyncingMessage(final Context context, final LinkedList<MessageEntity> messages) {
        if (null == messages) {
            throw new IllegalArgumentException("MessageEntity can't be null!");
        }
        LogUtil.i("onSyncingMessage size=" + messages.size());

        // 检查是否有撤回消息
        Iterator<MessageEntity> iterator = messages.iterator();
        List<MessageEntity> reCallList = new ArrayList<>();
        while (iterator.hasNext()) {
            MessageEntity next = iterator.next();
            if (next.isRecalled()) {
                LogUtil.i("onSyncingMessage_sn - > " + next.getSerialNumber());
                //                reCallMessage(context, next);  //处理断网时对方撤回消息的逻辑
                reCallList.add(next);
                iterator.remove();
            }
        }
        if (!reCallList.isEmpty()) {
            reCallMessageList(reCallList);//处理断网时对方撤回消息的逻辑
        }

        if (!messages.isEmpty()) {
            RecentSessionManager.getInstance().addOrUpdateRecentSession(messages);
            CubeMessageRepository.getInstance().addMessage(messages, true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<CubeMessage>>() {
                @Override
                public void call(List<CubeMessage> cubeMessages) {
                    EventBusUtil.post(MessageConstants.Event.EVENT_SYNCING_MESSAGE, cubeMessages);
                }
            });
        }
    }

    /**
     * 撤回消息
     *
     * @param messageEntity
     */
    public void reCallMessage(final MessageEntity messageEntity) {
        if (null == messageEntity) {
            throw new IllegalArgumentException("MessageEntity can't be null!");
        }

        //是否是已经撤回过的消息
        boolean recalled = messageEntity.isRecalled();
        final CubeMessage cubeMessage = MessageMapper.convertTo(messageEntity, false);
        Observable<CubeMessage> observable = null;
        Observable<CubeMessage> recallCubeMessage = Observable.create(new Observable.OnSubscribe<CubeMessage>() {
            @Override
            public void call(Subscriber<? super CubeMessage> subscriber) {
                subscriber.onNext(cubeMessage);
            }
        });

        observable = recalled ? recallCubeMessage : CubeMessageRepository.getInstance().queryMessageBySn(messageEntity.getSerialNumber());
        observable.flatMap(new Func1<CubeMessage, Observable<CubeMessage>>() {
            @Override
            public Observable<CubeMessage> call(final CubeMessage cubeMessage) {
                return buildCubeMessageForReCall(cubeMessage);
            }
        }).flatMap(new Func1<CubeMessage, Observable<CubeMessage>>() {
            @Override
            public Observable<CubeMessage> call(final CubeMessage cubeMessage) {
                return CubeMessageRepository.getInstance().saveOrUpdate(cubeMessage);
            }
        }).filter(new Func1<CubeMessage, Boolean>() {
            @Override
            public Boolean call(CubeMessage cubeMessage) {
                return cubeMessage != null;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CubeMessage>() {
            @Override
            public void call(CubeMessage cubeMessage) {
                if (!mContainer.isEmpty()) {
                    for (ChatContainer chatContainer : mContainer.values()) {
                        chatContainer.mPanelProxy.onMessageInLocalUpdated(cubeMessage);
                    }
                }
                // 保存最近消息，并发送刷新最近消息列表的事件通知
                RecentSessionManager.getInstance().addOrUpdateRecentSession(messageEntity);
            }
        });
    }

    /**
     * 处理断网时撤回消息逻辑
     *
     * @param messageEntities
     */
    public void reCallMessageList(final List<MessageEntity> messageEntities) {
        Observable.just(messageEntities).subscribeOn(Schedulers.io()).flatMap(new Func1<List<MessageEntity>, Observable<List<CubeMessage>>>() {
            @Override
            public Observable<List<CubeMessage>> call(List<MessageEntity> messageEntities) {
                LogUtil.i("reCallMessageList: " + messageEntities.size());
                List<CubeMessage> cubeMessages = MessageMapper.convertTo(messageEntities, false);
                if (cubeMessages == null) {
                    return Observable.empty();
                }
                return Observable.from(cubeMessages).flatMap(new Func1<CubeMessage, Observable<CubeMessage>>() {
                    @Override
                    public Observable<CubeMessage> call(CubeMessage cubeMessage) {
                        return buildCubeMessageForReCall(cubeMessage);
                    }
                }).toList();
            }
        }).flatMap(new Func1<List<CubeMessage>, Observable<List<CubeMessage>>>() {
            @Override
            public Observable<List<CubeMessage>> call(List<CubeMessage> cubeMessages) {
                return CubeMessageRepository.getInstance().saveOrUpdate(cubeMessages);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<CubeMessage>>() {
            @Override
            public void call(List<CubeMessage> cubeMessages) {
                LogUtil.i("reCallMessageList ---- cubeMessages : " + cubeMessages.size());
                // 保存最近消息，并发送刷新最近消息列表的事件通知
                RecentSessionManager.getInstance().addOrUpdateRecentSession(messageEntities);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(throwable);
            }
        });
    }

    /**
     * 构建撤回消息显示的 CubeMessage
     *
     * @param cubeMessage
     *
     * @return
     */
    public Observable<CubeMessage> buildCubeMessageForReCall(CubeMessage cubeMessage) {
        if (cubeMessage == null) {
            return Observable.empty();
        }
        if (cubeMessage.isReceivedMessage()) {
            cubeMessage.setMessageType(CubeMessageType.RECALLMESSAGETIPS);
            cubeMessage.setRead(true);
            cubeMessage.setContent(CubeCore.getContext().getString(R.string.notice_received_recall, cubeMessage.getSenderName()));
            return Observable.just(cubeMessage);
            //            if (cubeMessage.isGroupMessage()) {
            //                // TODO: 2018/9/25 目前群组成员方案待定，先使用查询user
            //                return CubeUserRepository.getInstance().queryUser(cubeMessage.getSenderId())
            //                            .map(new Func1<CubeUser, CubeMessage>() {
            //                                @Override
            //                                public CubeMessage call(CubeUser cubeUser) {
            //                                    cubeMessage.setMessageType(CubeMessageType.RECALLMESSAGETIPS.getType());
            //                                    cubeMessage.setRead(true);
            //                                    String name = TextUtils.isEmpty(cubeUser.getDisplayName()) ? cubeUser.getCubeId() : cubeUser.getDisplayName();
            //                                    cubeMessage.setContent(CubeCore.getContext().getString(R.string.notice_received_recall, name));
            //                                    return cubeMessage;
            //                                }
            //                            });
            //            }
            //            else {
            //                return CubeUserRepository.getInstance().queryUser(cubeMessage.getSenderId())
            //                        .map(new Func1<CubeUser, CubeMessage>() {
            //                            @Override
            //                            public CubeMessage call(CubeUser cubeUser) {
            //                                cubeMessage.setMessageType(CubeMessageType.RECALLMESSAGETIPS.getType());
            //                                cubeMessage.setRead(true);
            //                                String name = TextUtils.isEmpty(cubeUser.getDisplayName()) ? cubeUser.getCubeId() : cubeUser.getDisplayName();
            //                                cubeMessage.setContent(CubeCore.getContext().getString(R.string.notice_received_recall, name));
            //                                return cubeMessage;
            //                            }
            //                        });
            //            }
        }
        else {
            cubeMessage.setMessageType(CubeMessageType.RECALLMESSAGETIPS);
            cubeMessage.setRead(true);
            cubeMessage.setContent(CubeCore.getContext().getString(R.string.notice_send_recall));
            return Observable.just(cubeMessage);
        }
    }

    /**
     * 构建自定义消息
     *
     * @param sessionType
     * @param senderId
     * @param receiverId
     * @param content
     *
     * @return
     */
    public CustomMessage buildCustomMessage(CubeSessionType sessionType, String senderId, String receiverId, String content) {
        CustomMessage message = new CustomMessage(content);
        message.setSender(senderId);
        message.setReceiver(receiverId);
        message.setStatus(MessageStatus.Succeed);
        message.setTimestamp(System.currentTimeMillis());
        if (sessionType == CubeSessionType.Group) {  // 群消息
            message.setGroupId(receiverId);
        }
        return message;
    }

    /**
     * 构建自定义消息
     *
     * @param sessionType
     * @param sender
     * @param receiver
     * @param content
     *
     * @return
     */
    public CustomMessage buildCustomMessage(CubeSessionType sessionType, Sender sender, Receiver receiver, String content) {
        CustomMessage message = new CustomMessage(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setStatus(MessageStatus.Succeed);
        message.setTimestamp(System.currentTimeMillis());
        if (sessionType == CubeSessionType.Group) {  // 群消息
            message.setGroupId(receiver.getCubeId());
        }
        return message;
    }

    /**
     * 构建自定义消息model
     *
     * @param cubeMessage
     *
     * @return
     */
    public Observable<CubeMessageViewModel> buildCustom(final CubeMessage cubeMessage) {
        CubeMessageViewModel viewModel = new CubeMessageViewModel();
        viewModel.mMessage = cubeMessage;
        return Observable.just(viewModel);
    }

    /**
     * 构建用户信息model
     *
     * @param cubeMessage
     *
     * @return
     */
    public Observable<CubeMessageViewModel> buildUserInfo(final CubeMessage cubeMessage) {

        CubeMessageViewModel viewModel = new CubeMessageViewModel();
        viewModel.userNme = cubeMessage.getSenderName();
        viewModel.userFace = CubeCore.getInstance().getAvatarUrl() + cubeMessage.getSenderId();
        viewModel.remark = cubeMessage.getSenderName();
        viewModel.mMessage = cubeMessage;
        return Observable.just(viewModel);
        //        return CubeUserRepository.getInstance().queryUser(cubeMessage.getSenderId())
        //                .map(new Func1<CubeUser, CubeMessageViewModel>() {
        //
        //                    @Override
        //                    public CubeMessageViewModel call(CubeUser cubeUser) {
        //                        if (cubeUser == null) {
        //                            cubeUser = new CubeUser(cubeMessage.getSenderId(), "", "");
        //                        }
        //                        CubeMessageViewModel viewModel = new CubeMessageViewModel();
        //                        viewModel.userNme = TextUtils.isEmpty(cubeUser.getDisplayName()) ? cubeUser.getCubeId() : cubeUser.getDisplayName();
        //                        viewModel.userFace = cubeUser.getAvatar();
        //                        viewModel.remark = cubeUser.getDisplayName();
        //                        viewModel.mMessage = cubeMessage;
        //                        return viewModel;
        //                    }
        //                });

    }

    /**
     * 构建密聊提示消息
     *
     * @param chatId
     *
     * @return
     */
    private Observable<List<CubeMessageViewModel>> getSecretTipMessage(String chatId) {
        CustomMessage customMessage = MessageManager.getInstance().buildCustomMessage(CubeSessionType.Secret, CubeCore.getInstance().getCubeId(), chatId, CubeCore.getContext().getResources().getString(R.string.secret_message_tip));
        customMessage.setReceived(true);
        customMessage.setReceipted(true);
        customMessage.setAnonymous(true);
        customMessage.setHeader("operate", "secret_tip");
        customMessage.setTimestamp(0);
        customMessage.setSendTimestamp(0);
        return CubeMessageRepository.getInstance().addMessage(customMessage).flatMap(new Func1<CubeMessage, Observable<CubeMessageViewModel>>() {
            @Override
            public Observable<CubeMessageViewModel> call(CubeMessage cubeMessage) {
                return buildCustom(cubeMessage);
            }
        }).toList();
    }

    /**
     * 构建文本消息
     *
     * @param sessionType
     * @param senderId
     * @param receiverId
     * @param content
     * @param isSecret
     *
     * @return
     */
    public TextMessage buildTextMessage(CubeSessionType sessionType, String senderId, String receiverId, String receiverName, String content, boolean isSecret) {
        TextMessage message = new TextMessage(content);
        message.setSender(new Sender(senderId, CubeCore.getInstance().getUserName()));
        message.setReceiver(new Receiver(receiverId, receiverName));
        //        message.setSender(senderId);
        //        message.setReceiver(receiverId);
        message.setDirection(MessageDirection.Sent);
        message.setStatus(MessageStatus.Sending);
        message.setAnonymous(isSecret);
        if (sessionType == CubeSessionType.Group) {  // 群消息
            message.setGroupId(receiverId);
        }
        return message;
    }

    /**
     * 构建文件消息
     *
     * @param sessionType
     * @param sender
     * @param receiver
     * @param filePath
     *
     * @return
     */
    private FileMessage buildFileMessage(CubeSessionType sessionType, Sender sender, Receiver receiver, String filePath) {
        FileMessage message = new FileMessage(new File(filePath));
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setDirection(MessageDirection.Sent);
        message.setStatus(MessageStatus.Sending);
        if (sessionType == CubeSessionType.Group) {  // 群消息
            message.setGroupId(receiver.getCubeId());
        }
        return message;
    }

    /**
     * 构建图片消息
     *
     * @param context
     * @param sessionType
     * @param sender
     * @param receiver
     * @param imagePath
     * @param isOrigin    是否是原图
     * @param isAnonymous
     *
     * @return
     */
    public ImageMessage buildImageMessage(Context context, CubeSessionType sessionType, Sender sender, Receiver receiver, String imagePath, boolean isOrigin, boolean isAnonymous) {
        try {
            if (!ImageUtil.isGif(imagePath) && !isOrigin) { // 如果不是gif图，也不是原图
                String fileName = imagePath.substring(imagePath.lastIndexOf("/"));
                String thumbImagePath = MessageConstants.Sp.PATH_APP + File.separator + "image" + fileName;    // 生成缩略图
                imagePath = ImageUtil.createThumbnailImage(context, imagePath, thumbImagePath, 1920, 20);
            }
            ImageMessage message = new ImageMessage(new File(imagePath));
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setDirection(MessageDirection.Sent);
            message.setStatus(MessageStatus.Sending);
            message.setAnonymous(isAnonymous);
            message.computeWidthHeight();
            if (sessionType == CubeSessionType.Group) {  // 群消息
                message.setGroupId(receiver.getCubeId());
            }
            return message;
        } catch (Exception e) {
            LogUtil.e("构建图片消息出错" + e.getMessage());
            return null;
        }
    }

    /**
     * 构建视频消息
     *
     * @param context
     * @param sessionType
     * @param sender
     * @param receiver
     * @param isAnonymous
     *
     * @return
     */
    public VideoClipMessage buildVideoMessage(Context context, CubeSessionType sessionType, Sender sender, Receiver receiver, String videoPath, boolean isAnonymous) {
        VideoClipMessage message = new VideoClipMessage(new File(videoPath));
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setDirection(MessageDirection.Sent);
        message.setStatus(MessageStatus.Sending);
        message.setAnonymous(isAnonymous);
        message.computeWidthHeight();
        if (sessionType == CubeSessionType.Group) {  // 群消息
            message.setGroupId(receiver.getCubeId());
        }
        return message;
    }

    /**
     * 构建语音消息
     *
     * @param context
     * @param sessionType
     * @param senderId
     * @param receiverId
     * @param voiceMessage
     * @param isSecret
     *
     * @return
     */
    public VoiceClipMessage buildVoiceMessage(Context context, CubeSessionType sessionType, String senderId, String receiverId, VoiceClipMessage voiceMessage, boolean isSecret) {
        voiceMessage.setSender(senderId);
        voiceMessage.setReceiver(receiverId);
        voiceMessage.setDirection(MessageDirection.Sent);
        voiceMessage.setStatus(MessageStatus.Sending);
        voiceMessage.setAnonymous(isSecret);
        if (sessionType == CubeSessionType.Group) {  // 群消息
            voiceMessage.setGroupId(receiverId);
        }
        return voiceMessage;
    }

    /**
     * 构建视频消息
     *
     * @param context
     * @param sessionType
     * @param senderId
     * @param receiverId
     * @param videoMessage
     *
     * @return
     */
    public VideoClipMessage buildVideoMessage(Context context, CubeSessionType sessionType, String senderId, String receiverId, VideoClipMessage videoMessage) {
        videoMessage.setSender(senderId);
        videoMessage.setReceiver(receiverId);
        videoMessage.setDirection(MessageDirection.Sent);
        videoMessage.setStatus(MessageStatus.Sending);
        videoMessage.computeWidthHeight();
        if (sessionType == CubeSessionType.Group) {  // 群消息
            videoMessage.setGroupId(receiverId);
        }
        return videoMessage;
    }

    /**
     * 构建白板消息
     *
     * @param context
     * @param sessionType
     * @param senderId
     * @param receiverId
     * @param whiteboardMessage
     *
     * @return
     */
    public WhiteboardFrameMessage buildWhiteboardMessage(Context context, CubeSessionType sessionType, String senderId, String receiverId, WhiteboardFrameMessage whiteboardMessage) {
        whiteboardMessage.setSender(senderId);
        whiteboardMessage.setReceiver(receiverId);
        whiteboardMessage.setDirection(MessageDirection.Sent);
        whiteboardMessage.setStatus(MessageStatus.Sending);
        if (sessionType == CubeSessionType.Group) {  // 群消息
            whiteboardMessage.setGroupId(receiverId);
        }
        return whiteboardMessage;
    }

    public boolean replyMessage(CubeMessage cubeMessage, MessageEntity reply) {
        try {
            JSONObject jsonObject = new JSONObject(cubeMessage.getReplyContentJson());
            MessageEntity source = CubeEngine.getInstance().getMessageService().parseMessage(jsonObject.getString("source"));
            // 构建回复消息
            ReplyMessage message = new ReplyMessage(source, reply, reply.getReceiver(), reply.getSender(), reply.getSerialNumber());
            CubeEngine.getInstance().getMessageService().sendMessage(message);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 分页查询历史消息列表
     *
     * @param chatId      会话id
     * @param sessionType 类型
     * @param limit       每页条数
     * @param time        开始查询时间
     *
     * @return
     */
    public Observable<List<CubeMessageViewModel>> queryHistoryMessage(final String chatId, final int sessionType, final int limit, long time, final boolean isSecret) {
        return CubeMessageRepository.getInstance().queryMessage(chatId, time, limit).flatMap(new Func1<List<CubeMessage>, Observable<List<CubeMessageViewModel>>>() {
            @Override
            public Observable<List<CubeMessageViewModel>> call(List<CubeMessage> cubeMessages) {
                if (cubeMessages == null || cubeMessages.isEmpty()) {
                    if (isSecret) {
                        return getSecretTipMessage(chatId);
                    }
                    return Observable.empty();
                }

                if (cubeMessages.size() > 0) {
                    CubeMessage message = cubeMessages.get(0);
                    //发送消息回执
                    if (!message.isReceipt && message.isReceivedMessage()) {
                        //直接更新本地数据库消息回执状态，不等服务器的回执消息反馈
                        ReceiptManager.getInstance().onReceiptedAll(chatId, message.getTimestamp());
                        //发送回执消息
                        String sessionId = message.isGroupMessage() ? message.getGroupId() : message.getSenderId();
                        CubeEngine.getInstance().getMessageService().receiptMessages(sessionId, message.getTimestamp());
                    }
                }

                return Observable.from(cubeMessages).flatMap(new Func1<CubeMessage, Observable<CubeMessageViewModel>>() {
                    @Override
                    public Observable<CubeMessageViewModel> call(final CubeMessage cubeMessage) {
                        //自定义消息特殊处理
                        if (cubeMessage.getMessageType() == CubeMessageType.CustomTips) {
                            return buildCustom(cubeMessage);
                        }

                        if (sessionType == CubeSessionType.Group.getType()) {
                            return buildUserInfo(cubeMessage);
                        }
                        else {
                            return buildUserInfo(cubeMessage);
                        }
                    }
                }).toList();
            }
        });
    }

    /**
     * 发送消息 封装引擎发送消息方法
     *
     * @param context
     * @param messageEntity
     */
    public Observable<Boolean> sendMessage(final Context context, final MessageEntity messageEntity) {
        if (null == messageEntity) {
            ToastUtil.showToast(context, "消息发送失败!");
            throw new IllegalArgumentException("MessageEntity can't be null!");
        }
        if (messageEntity instanceof FileMessage) {
            ((FileMessage) messageEntity).setFileStatus(FileMessageStatus.Uploading);
        }
        LogUtil.d("sendMessage==>" + "message sn=" + messageEntity.getSerialNumber());
        return CubeMessageRepository.getInstance().addMessage(messageEntity).filter(new Func1<CubeMessage, Boolean>() {
            @Override
            public Boolean call(CubeMessage cubeMessage) {
                LogUtil.i("发送--->添加一条消息到数据库: " + cubeMessage);
                if (cubeMessage != null) {
                    RecentSessionManager.getInstance().addOrUpdateRecentSession(messageEntity);
                }
                return cubeMessage != null;
            }
        }).map(new Func1<CubeMessage, Boolean>() {
            @Override
            public Boolean call(CubeMessage cubeMessage) {
                try {
                    CubeEngine.getInstance().getMessageService().sendMessage(messageEntity);
                } catch (Exception e) {
                    LogUtil.i("添加一条消息到数据库 - > " + e.getMessage());
                }
                if (mContainer != null) {
                    for (ChatContainer chatContainer : mContainer.values()) {
                        chatContainer.mPanelProxy.onMessageSend(cubeMessage);
                    }
                }
                return true;
            }
        });
    }

    public void sendFileMessage(final Context context, final CubeSessionType sessionType, final Receiver receiver, final String filePath, final boolean isAnonymous, boolean isCompress) {
        LogUtil.i("sendFileMessage==>isCompress=" + isCompress);
        File file = new File(filePath);
        if (TextUtils.isEmpty(filePath) || !file.exists() || file.length() == 0) {
            ToastUtil.showToast(context, R.string.invilid_file);
            return;
        }
        final MessageEntity messageEntity;
        int fileType = FileUtil.isImageOrVideo(FileUtil.getFileExtensionName(filePath));
        if (fileType == 0) {
            if (isCompress && !ImageUtil.isGif(filePath)) {
                Luban.with(CubeCore.getContext()).load(filePath)                                   // 传人要压缩的图片列表
                     .ignoreBy(100)                                  // 忽略不压缩图片的大小
                     .setTargetDir(SpUtil.getImagePath())                        // 设置压缩后文件存储位置
                     .setCompressListener(new OnCompressListener() { //设置回调
                         @Override
                         public void onStart() {
                             // TODO 压缩开始前调用，可以在方法内启动 loading UI
                         }

                         @Override
                         public void onSuccess(File newfile) {
                             // TODO 压缩成功后调用，返回压缩后的图片文件
                             LogUtil.i("Luban ==>onSuccess file=" + newfile.getPath());
                             MessageEntity innermessageEntity = MessageManager.getInstance().buildImageMessage(context, sessionType, new Sender(CubeCore.getInstance().getCubeId(), SpUtil.getUserName()), receiver, newfile.getPath(), true, isAnonymous);
                             sendMessage(context, innermessageEntity).subscribe();
                         }

                         @Override
                         public void onError(Throwable e) {
                             // TODO 当压缩过程出现问题时调用
                             MessageEntity innermessageEntity = MessageManager.getInstance().buildImageMessage(context, sessionType, new Sender(CubeCore.getInstance().getCubeId(), SpUtil.getUserName()), receiver, filePath, true, isAnonymous);
                             sendMessage(context, innermessageEntity).subscribe();
                         }
                     }).launch();    //启动压缩
            }
            else {
                messageEntity = MessageManager.getInstance().buildImageMessage(context, sessionType, new Sender(CubeCore.getInstance().getCubeId(), SpUtil.getUserName()), receiver, filePath, true, isAnonymous);
                sendMessage(context, messageEntity).subscribe();
            }
        }
        else if (fileType == 1) {
            messageEntity = MessageManager.getInstance().buildVideoMessage(context, sessionType, new Sender(CubeCore.getInstance().getCubeId(), SpUtil.getUserName()), receiver, filePath, isAnonymous);
            ((VideoClipMessage) messageEntity).setDuration(FileUtil.getVideoDuration(filePath));
            sendMessage(context, messageEntity).subscribe();
        }
        else {
            messageEntity = MessageManager.getInstance().buildFileMessage(sessionType, new Sender(CubeCore.getInstance().getCubeId(), SpUtil.getUserName()), receiver, filePath);
            sendMessage(context, messageEntity).subscribe();
        }
    }

    /**
     * 正常在线发/接收消息
     *
     * @param messages
     */
    public void addMessagesToLocal(final LinkedList<MessageEntity> messages) {
        if (null == messages) {
            throw new IllegalArgumentException("MessageEntity can't be null!");
        }
        LogUtil.d("addMessagesToLocal size=" + messages.size());
        CubeMessageRepository.getInstance().addMessage(messages, false).compose(RxSchedulers.<List<CubeMessage>>io_main()).subscribe(new Subscriber<List<CubeMessage>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                // 保存最近消息，并发送刷新最近消息列表的事件通知
                RecentSessionManager.getInstance().addOrUpdateRecentSession(messages);
            }

            @Override
            public void onNext(List<CubeMessage> cubeMessages) {
                LogUtil.i("接收--->添加" + cubeMessages.size() + "条消息到数据库:" + cubeMessages.toString());
                if (!mContainer.isEmpty()) {
                    for (CubeMessage cubeMessage : cubeMessages) {
                        for (ChatContainer chatContainer : mContainer.values()) {
                            chatContainer.mPanelProxy.onMessagePersisted(cubeMessage);
                        }
                    }
                }
                // 保存最近消息，并发送刷新最近消息列表的事件通知
                RecentSessionManager.getInstance().addOrUpdateRecentSession(messages);
            }
        });
    }

    /**
     * 更新消息在数据库中的状态 （数据库里已经有此条消息）
     *
     * @param messageEntity
     */
    public Observable<CubeMessage> updateMessageInLocal(final MessageEntity messageEntity) {
        LogUtil.i("updateMessageInLocal==> sn" + messageEntity.getSerialNumber());
        return CubeMessageRepository.getInstance().addMessage(messageEntity).observeOn(AndroidSchedulers.mainThread()).filter(new Func1<CubeMessage, Boolean>() {
            @Override
            public Boolean call(CubeMessage cubeMessage) {
                LogUtil.i("updateMessageInLocal==>sn filter");
                // 更新消息列表和最近会话
                if (cubeMessage != null) {
                    if (!mContainer.isEmpty()) {
                        for (ChatContainer chatContainer : mContainer.values()) {
                            chatContainer.mPanelProxy.onMessageInLocalUpdated(cubeMessage);
                        }
                    }
                    RecentSessionManager.getInstance().addOrUpdateRecentSession(messageEntity);
                }
                return cubeMessage != null;
            }
        });
    }

    /**
     * 本地添加一条消息到数据库
     *
     * @param messageEntity
     */
    public Observable<CubeMessage> addMessageInLocal(final MessageEntity messageEntity) {
        LogUtil.i("addMessageInLocal==> sn" + messageEntity.getSerialNumber());
        return CubeMessageRepository.getInstance().addMessage(messageEntity).observeOn(AndroidSchedulers.mainThread()).filter(new Func1<CubeMessage, Boolean>() {
            @Override
            public Boolean call(CubeMessage cubeMessage) {

                // 更新消息列表和最近会话
                if (cubeMessage != null) {
                    if (!mContainer.isEmpty()) {
                        for (ChatContainer chatContainer : mContainer.values()) {
                            chatContainer.mPanelProxy.onMessagePersisted(cubeMessage);
                        }
                    }
                    RecentSessionManager.getInstance().addOrUpdateRecentSession(messageEntity);
                }
                return cubeMessage != null;
            }
        });
    }

    public void resumeMessage(long messageSN) {
        CubeEngine.getInstance().getMessageService().resumeMessage(messageSN);
    }

    public void updateMessageLite(CubeMessage cubeMessage) {
        if (cubeMessage != null) {
            if (!mContainer.isEmpty()) {
                for (ChatContainer chatContainer : mContainer.values()) {
                    chatContainer.mPanelProxy.onMessagePersisted(cubeMessage);
                }
            }
        }
    }

    /**
     * 重发消息
     *
     * @param messageSn
     */
    public void resendMessage(long messageSn) {
        CubeEngine.getInstance().getMessageService().reSendMessage(messageSn);
    }

    /**
     * 删除数据库中的一条消息
     *
     * @param message
     */
    public Observable<Boolean> deleteMessage(CubeMessage message) {
        LogUtil.i("deleteMessage message sn" + message.getMessageSN() + "，message id" + message.getChatId());
        CubeEngine.getInstance().getMessageService().pauseMessage(message.getMessageSN());
        return CubeMessageRepository.getInstance().deleteMessageBySN(message.getMessageSN());
    }

    /**
     * P2P音视频结束消息封装，更新界面显示
     *
     * @param context
     * @param session
     * @param callAction
     */
    public void onCallEnd(Context context, Session session, CallAction callAction) {
        onCallEnd(context, session, callAction, null);
    }

    public void onCallEnd(Context context, Session session, CallAction callAction, CubeError error) {
        boolean isCall = false;
        if (session.getCallPeer() != null) {
            String content;
            Sender sender;
            Receiver receiver;
            LogUtil.d("=====结束:" + callAction + " " + session.getCallDirection());
            if (session.getCallDirection() == CallDirection.Outgoing && CallAction.CANCEL.equals(callAction)) {
                content = context.getString(R.string.peer_has_refused);
                isCall = true;
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.CANCEL.equals(callAction)) {
                content = context.getString(R.string.call_not_accept);
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.ANSWER_BY_OTHER.equals(callAction) || session.getCallDirection() == CallDirection.Outgoing && CallAction.ANSWER_BY_OTHER.equals(callAction)) {
                content = context.getString(R.string.other_terminal_has_answered);
                isCall = true;
            }
            else if (session.getCallDirection() == CallDirection.Outgoing && CallAction.CANCEL_ACK.equals(callAction)) {
                content = context.getString(R.string.cancelled);
            }
            else if (session.getCallDirection() == CallDirection.Outgoing && CallAction.CANCEL_BY_OTHER.equals(callAction)) {
                content = context.getString(R.string.call_voice_not_answer);
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.CANCEL_BY_OTHER.equals(callAction)) {
                content = context.getString(R.string.other_terminal_has_cancle);
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.CANCEL_ACK.equals(callAction)) {
                content = context.getString(R.string.refused);
                isCall = true;
            }
            else if (session.getCallDirection() == CallDirection.Outgoing && CallAction.BYE.equals(callAction) || session.getCallDirection() == CallDirection.Outgoing && CallAction.BYE_ACK.equals(callAction)) {
                if (session.getCallTime() != 0l) {
                    content = context.getString(R.string.call_completed);
                    isCall = true;
                }
                else {
                    content = context.getString(R.string.call_user_busy);
                }
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.BYE.equals(callAction) || session.getCallDirection() == CallDirection.Incoming && CallAction.BYE_ACK.equals(callAction)) {
                if (session.getCallTime() != 0l) {
                    content = context.getString(R.string.call_completed);
                    isCall = true;
                }
                else {
                    content = context.getString(R.string.call_user_busy);
                }
            }
            else {
                content = context.getString(R.string.call_completed);
            }

            // FIXME: 2017/9/5 暂时避免引擎同时回调callFailed和callEnd的错误
            if (TextUtils.isEmpty(session.getCallPeer().getCubeId())) {
                return;
            }
            if (session.getCallDirection() == CallDirection.Outgoing) {
                sender = new Sender(session.getCubeId(), session.getDisplayName());
                receiver = new Receiver(session.getCallPeer().getCubeId(), session.getCallPeer().getDisplayName());
            }
            else {
                sender = new Sender(session.getCallPeer().getCubeId(), session.getCallPeer().getDisplayName());
                receiver = new Receiver(session.getCubeId(), session.getDisplayName());
            }
            LogUtil.d("===本条消息所封装的sender--->" + sender + "===receiver--->" + receiver);

            if (error != null) {
                if (error.code == CubeErrorCode.RequestTerminated.code || error.code == CubeErrorCode.DoNotDisturb.code) {
                    content = context.getString(R.string.call_voice_not_answer);
                    //String tips = "通话未接听，点击回拨";
                    //tips = context.getString(R.string.call_not_accept);
                    ////告诉对方，有未接来电
                    //CustomMessage message = MessageManager.getInstance().buildCustomMessage(context, CubeSessionType.P2P, sender, receiver, tips);
                    //if (session.getVideoEnabled()) {
                    //    message.setHeader("operate", CubeCustomMessageType.VideoCall.type);
                    //}
                    //else {
                    //    message.setHeader("operate", CubeCustomMessageType.AudioCall.type);
                    //}
                    //message.setStatus(MessageStatus.None);
                    //CubeEngine.getInstance().getMessageService().sendMessage(message);
                }
            }
            CustomMessage message = MessageManager.getInstance().buildCustomMessage(CubeSessionType.P2P, sender, receiver, content);
            if (session.getVideoEnabled()) {
                message.setHeader("operate", CubeCustomMessageType.VideoCall.type);
            }
            else {
                message.setHeader("operate", CubeCustomMessageType.AudioCall.type);
            }
            message.setReceived(isCall);
            //存储消息
            MessageManager.getInstance().addMessageInLocal(message).subscribe();
        }
    }

    /**
     * P2P处理音视频错误回调消息，如对方正在通话中
     *
     * @param context
     * @param session
     * @param cubeError
     */
    public void onCallFailed(Context context, Session session, CubeError cubeError) {
        boolean isCall = false;
        if (session != null && session.getCallPeer() != null) {
            String content = null;
            Sender sender;
            Receiver receiver;
            if (cubeError.code == CubeErrorCode.DoNotDisturb.code || cubeError.code == CubeErrorCode.BusyHere.code || cubeError.code == CubeErrorCode.RequestTerminated.code) {
                content = context.getString(R.string.call_user_busy);
            }
            // FIXME: 2017/9/5 暂时避免引擎同时回调callFailed和callEnd的错误
            if (TextUtils.isEmpty(session.getCallPeer().getCubeId())) {
                return;
            }
            if (session.getCallDirection() == CallDirection.Outgoing) {
                sender = new Sender(session.getCubeId(), session.getDisplayName());
                receiver = new Receiver(session.getCallPeer().getCubeId(), session.getCallPeer().getDisplayName());
            }
            else {
                sender = new Sender(session.getCallPeer().getCubeId(), session.getCallPeer().getDisplayName());
                receiver = new Receiver(session.getCubeId(), session.getDisplayName());
            }

            if (!TextUtils.isEmpty(content)) {
                CustomMessage message = MessageManager.getInstance().buildCustomMessage(CubeSessionType.P2P, sender, receiver, content);
                if (session.getVideoEnabled()) {
                    message.setHeader("operate", CubeCustomMessageType.VideoCall.type);
                }
                else {
                    message.setHeader("operate", CubeCustomMessageType.AudioCall.type);
                }
                message.setReceived(isCall);
                //存储消息
                MessageManager.getInstance().addMessageInLocal(message).subscribe();
            }
        }
    }
}
