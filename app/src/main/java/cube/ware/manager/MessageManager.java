package cube.ware.manager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.common.mvp.rx.RxBus;
import com.common.mvp.rx.RxSchedulers;
import com.common.utils.utils.ThreadUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import cube.service.CubeEngine;
import cube.service.call.CallAction;
import cube.service.call.CallDirection;
import cube.service.call.model.CallSession;
import cube.service.common.model.CubeError;
import cube.service.common.model.CubeErrorCode;
import cube.service.message.FileMessageStatus;
import cube.service.message.MessageDirection;
import cube.service.message.MessageStatus;
import cube.service.message.model.CardMessage;
import cube.service.message.model.CustomMessage;
import cube.service.message.model.FileMessage;
import cube.service.message.model.ImageMessage;
import cube.service.message.model.MessageEntity;
import cube.service.message.model.ReceiptMessage;
import cube.service.message.model.Receiver;
import cube.service.message.model.ReplyMessage;
import cube.service.message.model.RichContentMessage;
import cube.service.message.model.Sender;
import cube.service.message.model.TextMessage;
import cube.service.message.model.UnKnownMessage;
import cube.service.message.model.VideoClipMessage;
import cube.service.message.model.VoiceClipMessage;
import cube.service.message.model.WhiteboardClipMessage;
import cube.service.message.model.WhiteboardFrameMessage;
import cube.service.message.model.WhiteboardMessage;
import cube.service.user.model.User;
import cube.ware.AppConstants;
import cube.ware.CubeUI;
import cube.ware.R;
import cube.ware.data.model.dataModel.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeCustomMessageType;
import cube.ware.data.model.dataModel.enmu.CubeFileMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageDirection;
import cube.ware.data.model.dataModel.enmu.CubeMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.repository.CubeMessageRepository;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.eventbus.Event;
import cube.ware.ui.chat.ChatContainer;
import cube.ware.ui.recent.manager.RecentSessionManager;
import cube.ware.utils.FileUtil;
import cube.ware.utils.ImageUtil;
import cube.ware.utils.SpUtil;
import cube.ware.utils.StringUtil;
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

    public static final long                         SHOW_TIME_PERIOD = 5 * 60 * 1000;  //消息显示时间的最小间隔时间
    private             Map<Fragment, ChatContainer> mContainer       = new HashMap<>();

    public static MessageManager getInstance() {
        return MessageManagerHolder.INSTANCE;
    }

    private static class MessageManagerHolder {
        private static final MessageManager INSTANCE = new MessageManager();
    }

    private MessageManager() {

    }

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
     * @return
     */
    public boolean isAtMe(String messageContent) {
        if (TextUtils.isEmpty(messageContent)) {
            return false;
        }
        Pattern pattern = Pattern.compile(AppConstants.REGEX.REGEX_AT_MEMBER);
        Matcher matcher = pattern.matcher(messageContent);
        while (matcher.find()) {
            String oldContent = matcher.group();
            if (oldContent.contains(SpUtil.getCubeId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是@全体成员
     *
     * @param messageContent
     * @return
     */
    public boolean isAtAll(String messageContent) {
        if (TextUtils.isEmpty(messageContent)) {
            return false;
        }
        Pattern pattern = Pattern.compile(AppConstants.REGEX.REGEX_AT_ALL);
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
            CubeMessageRepository.getInstance().addMessage(messages, true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<CubeMessage>>() {
                        @Override
                        public void call(List<CubeMessage> cubeMessages) {
                            RxBus.getInstance().post(Event.EVENT_SYNCING_MESSAGE, cubeMessages);
//                            MessageBufferPool.getInstance().setNewMessageViewModelList(cubeMessages);
                        }
                    });
        }
    }

    /**
     * 撤回消息
     *
     * @param context
     * @param messageEntity
     */
    public void reCallMessage(final Context context, final MessageEntity messageEntity) {
        LogUtil.d("message sn=" + messageEntity.getSerialNumber());
        if (null == messageEntity) {
            throw new IllegalArgumentException("MessageEntity can't be null!");
        }
        boolean recalled = messageEntity.isRecalled(); //是否是已经撤回过的消息
        final CubeMessage cubeMessage = convertTo(messageEntity, false);
        Observable<CubeMessage> observable = null;
        Observable<CubeMessage> recallCubeMessage = Observable.create(new Observable.OnSubscribe<CubeMessage>() {
            @Override
            public void call(Subscriber<? super CubeMessage> subscriber) {
                subscriber.onNext(cubeMessage);
            }
        });

        observable = recalled ? recallCubeMessage : CubeMessageRepository.getInstance().queryMessageBySn(messageEntity.getSerialNumber());;
        observable.flatMap(new Func1<CubeMessage, Observable<CubeMessage>>() {
            @Override
            public Observable<CubeMessage> call(final CubeMessage cubeMessage) {
                return buildCubeMessageForReCall(cubeMessage);
            }
        }).flatMap(new Func1<CubeMessage, Observable<CubeMessage>>() {
            @Override
            public Observable<CubeMessage> call(final CubeMessage cubeMessage) {
                return  CubeMessageRepository.getInstance().saveOrUpdate(cubeMessage);
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
     * @param messageEntities
     */
    public void reCallMessageList(List<MessageEntity> messageEntities) {
        Observable.just(messageEntities)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<List<MessageEntity>, Observable<List<CubeMessage>>>() {
                    @Override
                    public Observable<List<CubeMessage>> call(List<MessageEntity> messageEntities) {
                        LogUtil.i("reCallMessageList: " + messageEntities.size());
                        List<CubeMessage> cubeMessages = convertTo(messageEntities, false);
                        if (cubeMessages == null) {
                            return Observable.empty();
                        }
                        return Observable.from(cubeMessages)
                                .flatMap(new Func1<CubeMessage, Observable<CubeMessage>>() {
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
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CubeMessage>>() {
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
     * @param cubeMessage
     * @return
     */
    public Observable<CubeMessage> buildCubeMessageForReCall(CubeMessage cubeMessage) {
        if (cubeMessage == null) {
            return Observable.empty();
        }
        if (cubeMessage.isReceivedMessage()) {
            cubeMessage.setMessageType(CubeMessageType.RECALLMESSAGETIPS.getType());
            cubeMessage.setRead(true);
            cubeMessage.setContent(CubeUI.getInstance().getContext().getString(R.string.notice_received_recall, cubeMessage.getSenderName()));
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
//                                    cubeMessage.setContent(CubeUI.getInstance().getContext().getString(R.string.notice_received_recall, name));
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
//                                cubeMessage.setContent(CubeUI.getInstance().getContext().getString(R.string.notice_received_recall, name));
//                                return cubeMessage;
//                            }
//                        });
//            }
        }
        else {
            cubeMessage.setMessageType(CubeMessageType.RECALLMESSAGETIPS.getType());
            cubeMessage.setRead(true);
            cubeMessage.setContent(CubeUI.getInstance().getContext().getString(R.string.notice_send_recall));
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
     * @return
     */
    public Observable<CubeMessageViewModel> buildUserInfo(final CubeMessage cubeMessage) {

        CubeMessageViewModel viewModel = new CubeMessageViewModel();
        viewModel.userNme = cubeMessage.getSenderName();
        viewModel.userFace = AppConstants.AVATAR_URL+cubeMessage.getSenderId();
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
     * @return
     */
    private Observable<List<CubeMessageViewModel>> getSecretTipMessage(String chatId) {
        CustomMessage customMessage = MessageManager.getInstance().buildCustomMessage(CubeSessionType.Secret, SpUtil.getCubeId(), chatId, CubeUI.getInstance().getContext().getResources().getString(R.string.secret_message_tip));
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
     * @return
     */
    public TextMessage buildTextMessage(CubeSessionType sessionType, String senderId, String receiverId,String receiverName, String content, boolean isSecret) {
        TextMessage message = new TextMessage(content);
        message.setSender(new Sender(senderId,SpUtil.getUserName()));
        message.setReceiver(new Receiver(receiverId,receiverName));
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
     * @return
     */
    public ImageMessage buildImageMessage(Context context, CubeSessionType sessionType, Sender sender, Receiver receiver, String imagePath, boolean isOrigin, boolean isAnonymous) {
        try {
            if (!ImageUtil.isGif(imagePath) && !isOrigin) { // 如果不是gif图，也不是原图
                String fileName = imagePath.substring(imagePath.lastIndexOf("/"));
                String thumbImagePath = AppConstants.Sp.PATH_APP + File.separator + "image" + fileName;    // 生成缩略图
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
     * @return
     */
    public Observable<List<CubeMessageViewModel>> queryHistoryMessage(final String chatId, final int sessionType, final int limit, long time, final boolean isSecret) {
        return CubeMessageRepository.getInstance().queryMessage(chatId, sessionType, time, limit).flatMap(new Func1<List<CubeMessage>, Observable<List<CubeMessageViewModel>>>() {
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
                        ReceiptManager.getInstance().updateIsReceipted(chatId,message.getTimestamp(),false);
                        //发送回执消息
                        ReceiptMessage receiptMessage = new ReceiptMessage(message.isGroupMessage() ? message.getGroupId() : message.getSenderId(), SpUtil.getCubeId());
                        receiptMessage.setTraceless(true);
                        CubeEngine.getInstance().getMessageService().sendMessage(receiptMessage);
                    }
                }

                return Observable.from(cubeMessages).flatMap(new Func1<CubeMessage, Observable<CubeMessageViewModel>>() {
                    @Override
                    public Observable<CubeMessageViewModel> call(final CubeMessage cubeMessage) {
                        //自定义消息特殊处理
                        if (cubeMessage.getMessageType().equals(CubeMessageType.CustomTips.getType())) {
                            return buildCustom(cubeMessage);
                        }

                        if (sessionType == CubeSessionType.Group.getType()) {
                            return buildUserInfo(cubeMessage);
                        } else {
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
                LogUtil.i("发送--->添加一条消息到数据库: " + cubeMessage.getCustomHeaderMap().toString());
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
                Luban.with(CubeUI.getInstance().getContext()).load(filePath)                                   // 传人要压缩的图片列表
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
                                MessageEntity innermessageEntity = MessageManager.getInstance().buildImageMessage(context, sessionType, new Sender(SpUtil.getCubeId(),SpUtil.getUserName()), receiver, newfile.getPath(), true, isAnonymous);
                                sendMessage(context, innermessageEntity).subscribe();
                            }

                            @Override
                            public void onError(Throwable e) {
                                // TODO 当压缩过程出现问题时调用
                                MessageEntity innermessageEntity = MessageManager.getInstance().buildImageMessage(context, sessionType, new Sender(SpUtil.getCubeId(),SpUtil.getUserName()), receiver, filePath, true, isAnonymous);
                                sendMessage(context, innermessageEntity).subscribe();
                            }
                        }).launch();    //启动压缩
            } else {
                messageEntity = MessageManager.getInstance().buildImageMessage(context, sessionType, new Sender(SpUtil.getCubeId(),SpUtil.getUserName()), receiver, filePath, true, isAnonymous);
                sendMessage(context, messageEntity).subscribe();
            }
        } else if (fileType == 1) {
            messageEntity = MessageManager.getInstance().buildVideoMessage(context, sessionType, new Sender(SpUtil.getCubeId(),SpUtil.getUserName()), receiver, filePath, isAnonymous);
            ((VideoClipMessage) messageEntity).setDuration(FileUtil.getVideoDuration(filePath));
            sendMessage(context, messageEntity).subscribe();
        } else {
            messageEntity = MessageManager.getInstance().buildFileMessage(sessionType, new Sender(SpUtil.getCubeId(),SpUtil.getUserName()), receiver, filePath);
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
        return CubeMessageRepository.getInstance().addMessage(messageEntity)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<CubeMessage, Boolean>() {
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
        return CubeMessageRepository.getInstance().addMessage(messageEntity)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<CubeMessage, Boolean>() {
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
     * 批量转换 将引擎的MessageEntity转换为CubeMessage
     *
     * @param messages
     * @param isSync   是否为同步下来的消息
     * @return
     */
    public List<CubeMessage> convertTo(List<MessageEntity> messages, boolean isSync) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        List<CubeMessage> cubeMessageList = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            MessageEntity messageEntity = messages.get(i);
            try {
                CubeMessage cubeMessage = convertTo(messageEntity, isSync);
                if (cubeMessage != null) {
                    cubeMessageList.add(cubeMessage);
                }
            } catch (Exception e) {
                LogUtil.e("将MessageEntity转换为CubeMessage出错!" + e.getMessage());
                return cubeMessageList;
            }
        }
        return cubeMessageList;
    }

    /**
     * 将引擎的MessageEntity转换为CubeMessage
     *
     * @param messageEntity
     * @param isSync
     * @return
     */
    public CubeMessage convertTo(MessageEntity messageEntity, boolean isSync) {
        if (null == messageEntity) {
            throw new IllegalArgumentException("MessageEntity can't be null!");
        }
        try {
            CubeMessage cubeMessage = new CubeMessage();
            if (messageEntity instanceof UnKnownMessage) {
                cubeMessage.setMessageType(CubeMessageType.Unknown.getType());
                cubeMessage.setContent(CubeUI.getInstance().getContext().getString(R.string.unknown_message_type));
            }
            if (messageEntity instanceof ReceiptMessage) {
                LogUtil.i("引擎回执消息转化处理====>");
                return null;
            } else if (messageEntity instanceof TextMessage) {         // 文本消息
                TextMessage textMessage = (TextMessage) messageEntity;
                String textContent = textMessage.getContent();
                cubeMessage.setContent(textContent);
                if (StringUtil.isEmoji(textContent)) {
                    cubeMessage.setMessageType(CubeMessageType.Emoji.getType());
                    cubeMessage.setContent("[图片]");
                    cubeMessage.setEmojiContent(textContent);
                } else {
                    cubeMessage.setMessageType(CubeMessageType.Text.getType());
                    cubeMessage.setContent(textContent);
                }
            } else if (messageEntity instanceof CardMessage) {    //卡片消息
                CardMessage cardMessage = (CardMessage) messageEntity;
                cubeMessage.setMessageType(CubeMessageType.CARD.getType());
                cubeMessage.setContent(cardMessage.getContent());
                cubeMessage.setCardTitle(cardMessage.getTitle());
                cubeMessage.setCardIcon(cardMessage.getIcon());
                JSONObject json = cardMessage.toJSON();
                cubeMessage.setCardContentJson(json.getJSONArray("cardContents").toString());
            } else if (messageEntity instanceof FileMessage) {    // 文件消息
                FileMessage fileMessage = (FileMessage) messageEntity;
                File file = fileMessage.getFile();
                if (null != file && file.exists()) {
                    cubeMessage.setFilePath(file.getAbsolutePath());
                }
                cubeMessage.setFileUrl(fileMessage.getUrl());
                cubeMessage.setFileName(fileMessage.getFileName());
                cubeMessage.setProcessedSize(fileMessage.getProcessed());
                cubeMessage.setFileSize(fileMessage.getFileSize());
                cubeMessage.setLastModified(fileMessage.getFileLastModified());
                cubeMessage.setFileMessageStatus(this.getFileMessageStatus(fileMessage).getStatus());
                cubeMessage.setMessageType(CubeMessageType.File.getType());
                cubeMessage.setContent(CubeUI.getInstance().getContext().getString(R.string.file_message));

                if (fileMessage instanceof ImageMessage) {  // 图片
                    ImageMessage imageMessage = (ImageMessage) fileMessage;
                    File thumbFile = imageMessage.getThumbFile();
                    if (null != thumbFile && thumbFile.exists()) {
                        cubeMessage.setThumbPath(thumbFile.getAbsolutePath());
                    }
                    cubeMessage.setThumbUrl(imageMessage.getThumbUrl());
                    cubeMessage.setImgWidth(imageMessage.getWidth());
                    cubeMessage.setImgHeight(imageMessage.getHeight());
                    cubeMessage.setMessageType(CubeMessageType.Image.getType());
                    cubeMessage.setContent(CubeUI.getInstance().getContext().getString(R.string.image_message));
                } else if (fileMessage instanceof VoiceClipMessage) { // 语音
                    VoiceClipMessage voiceClipMessage = (VoiceClipMessage) fileMessage;
                    cubeMessage.setDuration(voiceClipMessage.getDuration());
                    cubeMessage.setMessageType(CubeMessageType.Voice.getType());
                    cubeMessage.setContent(CubeUI.getInstance().getContext().getString(R.string.voice_message));
//                    cubeMessage.setPlay(messageEntity.getDirection() == MessageDirection.Sent || messageEntity.isReceipted());
                } else if (fileMessage instanceof VideoClipMessage) { // 视频
                    VideoClipMessage videoClipMessage = (VideoClipMessage) fileMessage;
                    File thumbFile = videoClipMessage.getThumbFile();
                    if (null != thumbFile && thumbFile.exists()) {
                        cubeMessage.setThumbPath(thumbFile.getAbsolutePath());
                    }
                    cubeMessage.setThumbUrl(videoClipMessage.getThumbUrl());
                    cubeMessage.setDuration(videoClipMessage.getDuration());
                    cubeMessage.setImgWidth(videoClipMessage.getWidth());
                    cubeMessage.setImgHeight(videoClipMessage.getHeight());
                    cubeMessage.setMessageType(CubeMessageType.Video.getType());
                    cubeMessage.setContent(CubeUI.getInstance().getContext().getString(R.string.video_message));
                } else if (fileMessage instanceof WhiteboardMessage) {    // 白板
                    WhiteboardMessage whiteboardMessage = (WhiteboardMessage) fileMessage;
                    File thumbFile = whiteboardMessage.getThumbFile();
                    if (null != thumbFile && thumbFile.exists()) {
                        cubeMessage.setThumbPath(thumbFile.getAbsolutePath());
                    }
                    cubeMessage.setThumbUrl(whiteboardMessage.getThumbUrl());
                    cubeMessage.setMessageType(CubeMessageType.Whiteboard.getType());
                    cubeMessage.setContent(CubeUI.getInstance().getContext().getString(R.string.whiteboard_message));

                    if (whiteboardMessage instanceof WhiteboardClipMessage) {    // 白板剪辑
                        WhiteboardClipMessage whiteboardClipMessage = (WhiteboardClipMessage) whiteboardMessage;
                    }
                }
            } else if (messageEntity instanceof CustomMessage) {      // 自定义消息
                //如果是验证消息 通知刷新最近列表
                if (SystemMessageManage.getInstance().isFromVerify(messageEntity) && !isSync) {
                    LogUtil.i("EVENT_REFRESH_SYSTEM_MESSAGE");
                    RxBus.getInstance().post(Event.EVENT_REFRESH_SYSTEM_MESSAGE, true);
                }
                CustomMessage customMessage = (CustomMessage) messageEntity;
                String operate = customMessage.getHeader("operate");
                cubeMessage.setOperate(operate);
                cubeMessage.setMessageDirection(this.getMessageDirection(messageEntity).getDirection());
                cubeMessage.setCustomHeaders(getHeaders(messageEntity));
                if (!messageEntity.getOnlyReceivers().isEmpty() && !messageEntity.getOnlyReceivers().contains(messageEntity.getReceiver().getCubeId())) {
                    LogUtil.i("不是发给自己的自定义消息不做处理====>" + customMessage.toString());
                    return null;
                }
                if (!CustomMessageManager.getCustomText(cubeMessage, customMessage, operate, isSync)) {
                    LogUtil.i("不做处理的自定义消息====>" + customMessage.toString());
                    return null;
                }
            } else if (messageEntity instanceof RichContentMessage) {
                cubeMessage.setMessageType(CubeMessageType.RICHTEXT.getType());
                StringBuilder stringBuilder = new StringBuilder();
                RichContentMessage richContentMessage = (RichContentMessage) messageEntity;
                //获取富文本消息中的消息
                List<MessageEntity> messageEntities = richContentMessage.getMessages();
                for (MessageEntity entity : messageEntities) {
                    if (entity instanceof TextMessage) {
                        String content = ((TextMessage) entity).getContent();
                        if (StringUtil.isEmoji(content)) {
                            stringBuilder.append("[图片]");
                        } else {
                            stringBuilder.append(content);
                        }
                    } else if (entity instanceof ImageMessage) {
                        stringBuilder.append("[图片]");
                    } else if (entity instanceof FileMessage) {
                        stringBuilder.append("[文件]");
                    }
                }
                cubeMessage.setContent(stringBuilder.toString());  //给最近聊天列表显示的内容
            } else if (messageEntity instanceof ReplyMessage) {
                cubeMessage.setMessageType(CubeMessageType.REPLYMESSAGE.getType());
                ReplyMessage messageEntity1 = (ReplyMessage) messageEntity;
                cubeMessage.setReplyContentJson(messageEntity1.toString());
                MessageEntity reply = messageEntity1.getReply();
                if (!(reply instanceof ReplyMessage)) {
                    cubeMessage.setContent(getMessageContent(reply));
                } else {
                    //回复消息在最近列表中显示reply消息的内容 如果reply消息仍然 intanceof ReplyMessage则说明出错了 概率很低 简单容错处理一下
                    cubeMessage.setContent("回复消息");
                }
            } else {
                cubeMessage.setMessageType(CubeMessageType.Unknown.getType());
                cubeMessage.setContent(CubeUI.getInstance().getContext().getString(R.string.unknown_message_type));
            }
            cubeMessage.setMessageSN(messageEntity.getSerialNumber());
            cubeMessage.setMessageDirection(this.getMessageDirection(messageEntity).getDirection());
            cubeMessage.setMessageStatus(this.getMessageStatus(messageEntity).getStatus());
            cubeMessage.setSenderId(messageEntity.getSender().getCubeId());
            cubeMessage.setSenderName(TextUtils.isEmpty(messageEntity.getSender().getDisplayName()) ? messageEntity.getSender().getCubeId() : messageEntity.getSender().getDisplayName());
            cubeMessage.setSendTimestamp(messageEntity.getSendTimestamp());
            cubeMessage.setReceiverId(messageEntity.getReceiver().getCubeId());
            cubeMessage.setReceiveTimestamp(messageEntity.getReceiveTimestamp());
            cubeMessage.setGroupId(messageEntity.getGroupId() == null ? null : messageEntity.getGroupId());
            cubeMessage.setTimestamp(messageEntity.getTimestamp());
            cubeMessage.setRead(messageEntity.getDirection() == MessageDirection.Sent || cubeMessage.getMessageType().equals(CubeMessageType.CustomTips.getType()) || messageEntity.isReceipted());
            cubeMessage.setReceipt(messageEntity.isReceipted());
            cubeMessage.setAnonymous(messageEntity.isAnonymous());
            boolean alreadyReceiptedMessage = ReceiptManager.getInstance().isAlreadyReceiptedMessage(cubeMessage.getTimestamp());
            if (alreadyReceiptedMessage) {
                cubeMessage.setRead(true);
                cubeMessage.setReceipt(true);
            }
            if (cubeMessage.getCustomHeaders() == null || cubeMessage.getCustomHeaders().isEmpty()) {

                cubeMessage.setCustomHeaders(getHeaders(messageEntity));
            }
            return cubeMessage;
        } catch (Exception e) {
            LogUtil.e("将MessageEntity转换为CubeMessage出错!：" + messageEntity.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取CubeWare的文件消息状态
     *
     * @param fileMessage
     * @return
     */
    public CubeFileMessageStatus getFileMessageStatus(FileMessage fileMessage) {
        if (fileMessage.getFileStatus() == FileMessageStatus.Uploading) {
            return CubeFileMessageStatus.Uploading;
        } else if (fileMessage.getFileStatus() == FileMessageStatus.Downloading) {
            return CubeFileMessageStatus.Downloading;
        } else if (fileMessage.getFileStatus() == FileMessageStatus.Succeed) {
            return CubeFileMessageStatus.Succeed;
        } else if (fileMessage.getFileStatus() == FileMessageStatus.Failed) {
            return CubeFileMessageStatus.Failed;
        } else {
            return CubeFileMessageStatus.Unknown;
        }
    }

    /**
     * 获取CubeWare的消息接收方向
     *
     * @param messageEntity
     * @return
     */
    private CubeMessageDirection getMessageDirection(MessageEntity messageEntity) {
        if (messageEntity.getDirection() == MessageDirection.Sent) {
            return CubeMessageDirection.Sent;
        } else if (messageEntity.getDirection() == MessageDirection.Received) {
            return CubeMessageDirection.Received;
        } else {
            return CubeMessageDirection.None;
        }
    }

    public String getHeaders(MessageEntity messageEntity) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<String, Object> entry : messageEntity.getHeaders().entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", entry.getKey());
            if (entry.getValue() instanceof Map) {
                jsonObject.put("value", entry.getValue().toString());
            } else {
                jsonObject.put("value", entry.getValue());
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    /**
     * note:如果convertTo中setContent发生变化 这里也要发生变化 建议不要将convertto中setContent都交给该方法
     * 因为只有回复消息真正需要这个方法 其他消息走这个方法增加消耗
     *
     * @param messageEntity
     * @return
     */
    public String getMessageContent(MessageEntity messageEntity) {
        Context context = CubeUI.getInstance().getContext();
        if (messageEntity == null || messageEntity instanceof UnKnownMessage) {
            return context.getString(R.string.unknown_message_type);
        } else if (messageEntity instanceof TextMessage) {
            String textContent = ((TextMessage) messageEntity).getContent();
            if (StringUtil.isEmoji(textContent)) {
                return "[图片]";
            } else {
                return textContent;
            }
        } else if (messageEntity instanceof CardMessage) {
            CardMessage cardMessage = (CardMessage) messageEntity;
            return cardMessage.getContent();
        } else if (messageEntity instanceof FileMessage) {
            if (messageEntity instanceof ImageMessage) {  // 图片
                return context.getString(R.string.image_message);
            } else if (messageEntity instanceof VoiceClipMessage) { // 语音
                return context.getString(R.string.voice_message);
            } else if (messageEntity instanceof VideoClipMessage) { // 视频
                return context.getString(R.string.video_message);
            } else if (messageEntity instanceof WhiteboardMessage) {    // 白板
                return context.getString(R.string.whiteboard_message);
            }
        } else if (messageEntity instanceof RichContentMessage) {
            StringBuilder stringBuilder = new StringBuilder();
            RichContentMessage richContentMessage = (RichContentMessage) messageEntity;
            //获取富文本消息中的消息
            List<MessageEntity> messageEntities = richContentMessage.getMessages();
            for (MessageEntity entity : messageEntities) {
                if (entity instanceof TextMessage) {
                    String content = ((TextMessage) entity).getContent();
                    if (StringUtil.isEmoji(content)) {
                        stringBuilder.append("[图片]");
                    } else {
                        stringBuilder.append(content);
                    }
                } else if (entity instanceof ImageMessage) {
                    stringBuilder.append("[图片]");
                } else if (entity instanceof FileMessage) {
                    stringBuilder.append("[文件]");
                }
            }
            return stringBuilder.toString();  //给最近聊天列表显示的内容
        } else if (messageEntity instanceof ReplyMessage) {
            ReplyMessage replyMessage = (ReplyMessage) messageEntity;
            MessageEntity reply = replyMessage.getReply();
            if (reply instanceof ReplyMessage) {
                return "回复消息";
            } else {
                return getMessageContent(reply);
            }
        } else if (messageEntity instanceof CustomMessage) {
            //CustomMessage customMessage = (CustomMessage) messageEntity;
            //String type = customMessage.getHeader("operate");
            //if (type.equals(CubeCustomMessageType.GroupShareQr.getType())) {
            //    return "[分享]";
            //}
            //else if (type.equals(CubeCustomMessageType.UserShareQr.getType())) {
            //    return "[分享]";
            //}
            //目前能回复的自定义消息只有二维码分享消息
            return "[二维码]";
        }
        return context.getString(R.string.unknown_message_type);
    }

    /**
     * 获取CubeWare的消息状态
     *
     * @param messageEntity
     * @return
     */
    public CubeMessageStatus getMessageStatus(MessageEntity messageEntity) {
        if (messageEntity.getStatus() == MessageStatus.Sending) {
            return CubeMessageStatus.Sending;
        } else if (messageEntity.getStatus() == MessageStatus.Receiving) {
            return CubeMessageStatus.Receiving;
        } else if (messageEntity.getStatus() == MessageStatus.Succeed) {
            return CubeMessageStatus.Succeed;
        } else if (messageEntity.getStatus() == MessageStatus.Failed) {
            return CubeMessageStatus.Failed;
        } else {
            return CubeMessageStatus.None;
        }
    }

    /**
     * P2P音视频结束消息封装，更新界面显示
     *
     * @param context
     * @param session
     * @param callAction
     */
    public void onCallEnd(Context context, CallSession session, CallAction callAction) {
        onCallEnd(context, session, callAction, null);
    }

    public void onCallEnd(Context context, CallSession session, CallAction callAction, CubeError error) {
        boolean isCall = false;
        if (session.getCaller() != null) {
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
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.ANSWER_BY_OTHER.equals(callAction)
                    || session.getCallDirection() == CallDirection.Outgoing && CallAction.ANSWER_BY_OTHER.equals(callAction)
                    ) {
                content = context.getString(R.string.other_terminal_has_answered);
                isCall = true;
            }
            else if (session.getCallDirection() == CallDirection.Outgoing && CallAction.CANCEL_ACK.equals(callAction)) {
                content = context.getString(R.string.cancelled);
            }
            else if (session.getCallDirection() == CallDirection.Outgoing && CallAction.CANCEL_BY_OTHER.equals(callAction)) {
                content = context.getString(R.string.call_voice_not_answer);
            }
            else if(session.getCallDirection() == CallDirection.Incoming && CallAction.CANCEL_BY_OTHER.equals(callAction)){
                content = context.getString(R.string.other_terminal_has_cancle);
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.CANCEL_ACK.equals(callAction)) {
                content = context.getString(R.string.refused);
                isCall = true;
            }
            else if (session.getCallDirection() == CallDirection.Outgoing && CallAction.BYE.equals(callAction) || session.getCallDirection() == CallDirection.Outgoing && CallAction.BYE_ACK.equals(callAction)) {
                if (session.getStartTime() != 0l) {
                    content = context.getString(R.string.call_completed);
                    isCall = true;


                }
                else {
                    content = context.getString(R.string.call_user_busy);
                }
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.BYE.equals(callAction) || session.getCallDirection() == CallDirection.Incoming && CallAction.BYE_ACK.equals(callAction)) {
                if (session.getStartTime() != 0l) {
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
            if (TextUtils.isEmpty(session.getCaller().cubeId)) {
                return;
            }
            if (session.getCallDirection() == CallDirection.Outgoing) {
                sender = new Sender(session.getCaller().cubeId,session.getCaller().displayName);
                receiver = new Receiver(session.getCallee().cubeId,session.getCallee().displayName);
            }
            else {
                sender = new Sender(session.getCallee().cubeId,session.getCallee().displayName);
                receiver = new Receiver(session.getCaller().cubeId,session.getCaller().displayName);
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
            if (session.isVideoEnabled()) {
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
     * @param context
     * @param session
     * @param cubeError
     */
    public void onCallFailed(Context context, CallSession session, CubeError cubeError) {
        boolean isCall = false;
        if (session != null && session.getCaller() != null) {
            String content = null;
            Sender sender;
            Receiver receiver;
            if (cubeError.code == CubeErrorCode.DoNotDisturb.code || cubeError.code == CubeErrorCode.BusyHere.code || cubeError.code == CubeErrorCode.RequestTerminated.code) {
                content = context.getString(R.string.call_user_busy);
            }
            // FIXME: 2017/9/5 暂时避免引擎同时回调callFailed和callEnd的错误
            if (TextUtils.isEmpty(session.getCaller().cubeId)) {
                return;
            }
            if (session.getCallDirection() == CallDirection.Outgoing) {
                sender = new Sender(session.getCaller().cubeId,session.getCaller().displayName);
                receiver = new Receiver(session.getCallee().cubeId,session.getCallee().displayName);
            }
            else {
                sender = new Sender(session.getCallee().cubeId,session.getCallee().displayName);
                receiver = new Receiver(session.getCaller().cubeId,session.getCaller().displayName);
            }

            if (!TextUtils.isEmpty(content)) {
                CustomMessage message = MessageManager.getInstance().buildCustomMessage(CubeSessionType.P2P, sender, receiver, content);
                if (session.isVideoEnabled()) {
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

    /**
     * p2p白板发送创建消息，更新本地
     * @param from
     * @param to
     */
    public void sendP2PWBCreateMessage(User from,User to){
        Sender sender = new Sender(from.cubeId,from.displayName);//发起者
        Receiver receiver = new Receiver(to.cubeId,to.displayName);//接受者
        CustomMessage customMessage = MessageManager.getInstance().buildCustomMessage(CubeSessionType.P2P, sender, receiver, "");
        customMessage.setHeader("operate", CubeCustomMessageType.P2PWhiteBoardApply.type);
        customMessage.setHeader("userCube", from.cubeId);
        customMessage.setHeader("userDN", from.displayName);
        MessageManager.getInstance().updateMessageInLocal(customMessage).subscribe();
    }

    /**
     * p2p白板发送创建消息，更新本地
     * @param from
     * @param to
     */
    public void sendP2PWBDestoryMessage(User from,User to){
        Sender sender = new Sender(from.cubeId,from.displayName);//发起者
        Receiver receiver = new Receiver(to.cubeId,to.displayName);//接受者
        CustomMessage customMessage = MessageManager.getInstance().buildCustomMessage(CubeSessionType.P2P, sender, receiver, "");
        customMessage.setHeader("operate", CubeCustomMessageType.P2PWhiteBoardClose.type);
        MessageManager.getInstance().addMessageInLocal(customMessage).subscribe();
    }
}
