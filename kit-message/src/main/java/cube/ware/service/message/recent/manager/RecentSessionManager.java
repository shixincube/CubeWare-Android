package cube.ware.service.message.recent.manager;

import android.text.TextUtils;
import com.common.mvp.rx.RxBus;
import com.common.utils.utils.log.LogUtil;
import cube.service.message.MessageDirection;
import cube.service.message.CustomMessage;
import cube.service.message.FileMessage;
import cube.service.message.ImageMessage;
import cube.service.message.MessageEntity;
import cube.service.message.Sender;
import cube.service.message.TextMessage;
import cube.service.message.VideoClipMessage;
import cube.service.message.VoiceClipMessage;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeMessageDirection;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.repository.CubeRecentSessionRepository;
import cube.ware.data.room.model.CubeRecentSession;
import cube.ware.eventbus.CubeEvent;
import cube.ware.MessageConstants;
import cube.ware.service.message.manager.MessageManager;
import cube.ware.service.message.manager.SystemMessageManage;
import cube.ware.utils.SpUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by dth
 * Des: 最近会话管理类
 * Date: 2018/8/30.
 */

public class RecentSessionManager {

    private static volatile RecentSessionManager mInstance = null;

    public static RecentSessionManager getInstance() {
        if (null == mInstance) {
            synchronized (RecentSessionManager.class) {
                if (null == mInstance) {
                    mInstance = new RecentSessionManager();
                }
            }
        }
        return mInstance;
    }

    private RecentSessionManager() {

    }

    /**
     * 添加或更新最近会话到数据库
     *
     * @param messageEntity
     */
    public void addOrUpdateRecentSession(final MessageEntity messageEntity) {
        if (null == messageEntity) {
            throw new IllegalArgumentException("MessageEntity can't be null!");
        }
        LogUtil.i("addOrUpdateRecentSession MessageEntity sn=" + messageEntity.getSerialNumber());

        final CubeRecentSession cubeRecentSession = this.convertTo(messageEntity);
        if (cubeRecentSession == null) {
            return;
        }

        CubeRecentSessionRepository.getInstance().addOrUpdateRecentSession(cubeRecentSession).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CubeRecentSession>() {
            @Override
            public void call(CubeRecentSession cubeRecentSession) {
                // 刷新最近会话列表

                RxBus.getInstance().post(CubeEvent.EVENT_REFRESH_RECENT_SESSION_SINGLE, cubeRecentSession.getSessionId());
            }
        });
    }

    /**
     * 批量添加或更新最近会话到数据库
     *
     * @param messageList
     */
    public void addOrUpdateRecentSession(final List<MessageEntity> messageList) {
        if (null == messageList) {
            throw new IllegalArgumentException("MessageEntity can't be null!");
        }
        LogUtil.i("addOrUpdateRecentSession messageList size=" + messageList.size());
        ArrayList<MessageEntity> sortMessageList = sortMessage(messageList);
        final List<CubeRecentSession> cubeRecentSessions = this.convertTo(sortMessageList);
        LogUtil.i("addOrUpdateRecentSession cubeRecentSessions size=" + cubeRecentSessions.size());
        if (cubeRecentSessions == null || cubeRecentSessions.isEmpty()) {
            LogUtil.e("convertTo comes some wrong");
            return;
        }

        Observable.from(cubeRecentSessions).subscribeOn(Schedulers.io()).flatMap(new Func1<CubeRecentSession, Observable<CubeRecentSession>>() {
            @Override
            public Observable<CubeRecentSession> call(final CubeRecentSession cubeRecentSession) {
                return CubeRecentSessionRepository.getInstance().queryBySessionId(cubeRecentSession.getSessionId()).map(new Func1<CubeRecentSession, CubeRecentSession>() {
                    @Override
                    public CubeRecentSession call(CubeRecentSession cubeRecentSessionDB) {
                        if (cubeRecentSessionDB == null) {
                            return cubeRecentSession;
                        }
                        return cubeRecentSession.getTimestamp() > cubeRecentSessionDB.getTimestamp() ? cubeRecentSession : cubeRecentSessionDB;
                    }
                });
            }
        }).toList().flatMap(new Func1<List<CubeRecentSession>, Observable<List<CubeRecentSession>>>() {
            @Override
            public Observable<List<CubeRecentSession>> call(List<CubeRecentSession> cubeRecentSessions) {
                return CubeRecentSessionRepository.getInstance().addOrUpdateRecentSession(cubeRecentSessions);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<CubeRecentSession>>() {
            @Override
            public void call(List<CubeRecentSession> cubeRecentSessions) {
                // 刷新最近会话列表

                if (cubeRecentSessions.size() == 1) {
                    RxBus.getInstance().post(CubeEvent.EVENT_REFRESH_RECENT_SESSION_SINGLE, cubeRecentSessions.get(0).getSessionId());
                }
                else {
                    RxBus.getInstance().post(CubeEvent.EVENT_REFRESH_RECENT_SESSION_LIST, cubeRecentSessions);
                }
            }
        });
    }

    /**
     * 删除一个最近会话
     *
     * @param sessionId
     */
    public void removeRecentSession(String sessionId) {
        CubeRecentSessionRepository.getInstance().removeRecentSession(sessionId).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CubeRecentSession>() {
            @Override
            public void call(CubeRecentSession cubeRecentSession) {
                if (cubeRecentSession == null) {
                    return;
                }
                RxBus.getInstance().post(CubeEvent.EVENT_REMOVE_RECENT_SESSION_SINGLE, cubeRecentSession.getSessionId());
            }
        });
    }

    /**
     * 对数据进行排序过滤
     *
     * @param messageList
     */
    private ArrayList<MessageEntity> sortMessage(List<MessageEntity> messageList) {
        // 消息仓库最大存储量
        HashMap<String, MessageEntity> messageCache = new HashMap<>();
        HashMap<String, MessageEntity> messageSecretCache = new HashMap<>();
        for (int i = 0; i < messageList.size(); i++) {
            MessageEntity message = messageList.get(i);
       /*     if (message.getSender().getCubeId().equals("10000")) {
                continue;
            }*/
            String sessionId;
            if (message.isAnonymous()) {
                sessionId = "secret_chat";

                String senderId = message.getSender().getCubeId();
                String receiverId = message.getReceiver().getCubeId();
                boolean isSelf = senderId.equals(CubeCore.getInstance().getCubeId());
                String chatId = isSelf ? receiverId : senderId;
                if (messageSecretCache.containsKey(chatId)) {
                    if (messageSecretCache.get(chatId).getTimestamp() <= message.getTimestamp()) {
                        messageSecretCache.put(chatId, message);
                    }
                }
                else {
                    messageSecretCache.put(chatId, message);
                }
            }
            else {
                if (message.isGroupMessage()) {
                    sessionId = message.getGroupId();
                }
                else {
                    String senderId = message.getSender().getCubeId();
                    String receiverId = message.getReceiver().getCubeId();
                    boolean isSelf = senderId.equals(CubeCore.getInstance().getCubeId());
                    sessionId = isSelf ? receiverId : senderId;
                }
            }

            if (message instanceof TextMessage && message.getDirection() != MessageDirection.Sent && !message.isReceipted()) {
                TextMessage textMessage = (TextMessage) message;
                if (MessageManager.getInstance().isAtMe(textMessage.getContent())) {
                    // 若消息中包含@自己的信息，则显示有人@我
                    SpUtil.setReceiveAt(MessageConstants.Sp.SP_CUBE_AT + SpUtil.getCubeId() + sessionId, true);
                    LogUtil.d("有人@我: " + sessionId);
                }
                else if (MessageManager.getInstance().isAtAll(textMessage.getContent()) && message.isGroupMessage()) {
                    SpUtil.setReceiveAtAll(MessageConstants.Sp.SP_CUBE_RECEIVE_ATALL + SpUtil.getCubeId() + sessionId, true);
                    LogUtil.d("有@全体: " + sessionId);
                }
            }

            if (messageCache.containsKey(sessionId)) {
                if (messageCache.get(sessionId).getTimestamp() <= message.getTimestamp()) {
                    messageCache.put(sessionId, message);
                }
            }
            else {
                messageCache.put(sessionId, message);
            }
        }

        //addOrUpdateRecentSecretSession(context, new ArrayList<>(messageSecretCache.values()));
        return new ArrayList<>(messageCache.values());
    }

    /**
     * 批量将MessageEntity转化为CubeRecentSession
     *
     * @param messages
     *
     * @return
     */
    public List<CubeRecentSession> convertTo(List<MessageEntity> messages) {
        List<CubeRecentSession> list = new ArrayList<>();
        for (MessageEntity messageEntity : messages) {
            try {
                CubeRecentSession cubeRecentSession = convertTo(messageEntity);
                if (cubeRecentSession != null) {
                    list.add(cubeRecentSession);
                }
            } catch (Exception e) {
                LogUtil.e("将MessageEntity转换为CubeRecentSession出错");
                e.printStackTrace();
                return list;
            }
        }
        return list;
    }

    /**
     * 将MessageEntity转化为CubeRecentSession
     *
     * @param messageEntity
     *
     * @return
     */
    public CubeRecentSession convertTo(MessageEntity messageEntity) {
        if (null == messageEntity) {
            throw new IllegalArgumentException("MessageEntity can't be null");
        }
        String senderId;
        String receiverId = messageEntity.getReceiver().getCubeId();
        if (SystemMessageManage.getInstance().isAddFriend(messageEntity)) {
            CustomMessage customMessage = (CustomMessage) messageEntity;
            String operate = customMessage.getHeader("operate");
            String applyCube = customMessage.getHeader("applyUserCube");
            String acceptCube = customMessage.getHeader("acceptUserCube");
            if (TextUtils.equals(applyCube, CubeCore.getInstance().getCubeId())) {
                senderId = acceptCube;
            }
            else {
                senderId = applyCube;
            }
        }
        else {
            senderId = messageEntity.getSender().getCubeId();
        }
        if ((senderId.equals(receiverId) && senderId.equals(CubeCore.getInstance().getCubeId()))) {
            //业务层不显示自己发给自己的消息
            return null;
        }
        try {
            CubeRecentSession cubeRecentSession = new CubeRecentSession();
            if (MessageManager.getInstance().isGroupMessage(messageEntity)) {
                buildGroupRecentSession(messageEntity, cubeRecentSession);
            }
            else {
                if (TextUtils.isEmpty(CubeCore.getInstance().getCubeId())) {
                    LogUtil.e("将MessageEntity转换为CubeRecentSession出错 for myCubeId is null");
                    return null;
                }
                boolean isSelf = senderId.equals(CubeCore.getInstance().getCubeId());
                String sessionId;
                if (messageEntity.isAnonymous()) {
                    sessionId = "secret_chat";
                }
                else {
                    sessionId = isSelf ? receiverId : senderId;
                }
                cubeRecentSession.setSessionId(sessionId);
                String sessionName;
                String receiverName = messageEntity.getReceiver().getDisplayName();
                String senderName = messageEntity.getSender().getDisplayName();
                sessionName = isSelf ? receiverName : senderName;
                LogUtil.i("sessionName -------> " + sessionName);
                cubeRecentSession.setSessionName(TextUtils.isEmpty(sessionName) ? sessionId : sessionName);
                if (sessionId.equals(CubeCore.getInstance().getCubeId()) || SystemMessageManage.getInstance().isSystemSessionId(senderId)) {
                    //业务层不显示自己发给自己的消息   一对一不显示自定义消息
                    return null;
                }
                buildP2PRecentSession(messageEntity, cubeRecentSession);
            }

            cubeRecentSession.setContent(getContent(messageEntity));
            cubeRecentSession.setMessageDirection(getDirection(messageEntity));
            cubeRecentSession.setTimestamp(messageEntity.getTimestamp());
            cubeRecentSession.setTop(false);
            return cubeRecentSession;
        } catch (Exception e) {
            LogUtil.e("将MessageEntity转换为CubeRecentSession出错");
            e.printStackTrace();
            return null;
        }
    }

    private String getContent(MessageEntity messageEntity) {
        Sender sender = messageEntity.getSender();
        String content = "";
        if (messageEntity.isGroupMessage() && !TextUtils.equals(sender.getCubeId(), CubeCore.getInstance().getCubeId())) {
            String displayName = TextUtils.isEmpty(sender.getDisplayName()) ? sender.getCubeId() : sender.getDisplayName();
            content = displayName + ": ";
        }
        if (messageEntity.isRecalled()) {
            content += "[撤回消息]";
        }
        else if (messageEntity instanceof TextMessage) {
            content += ((TextMessage) messageEntity).getContent();
        }
        else if (messageEntity instanceof ImageMessage) {
            content += "[图片消息]";
        }
        else if (messageEntity instanceof VoiceClipMessage) {
            content += "[语音消息]";
        }
        else if (messageEntity instanceof VideoClipMessage) {
            content += "[视频消息]";
        }
        else if (messageEntity instanceof CustomMessage) {
            content = "[系统消息]";
        }
        else if (messageEntity instanceof FileMessage) {
            content += "[文件消息]";
        }
        else {
            content += "[未知消息]";
        }

        return content;
    }

    /**
     * 构建群组RecentSession
     *
     * @param messageEntity
     * @param cubeRecentSession
     */
    private void buildGroupRecentSession(MessageEntity messageEntity, CubeRecentSession cubeRecentSession) {
        cubeRecentSession.setSessionId(messageEntity.getGroupId());
        String displayName = messageEntity.getGroup() == null ? messageEntity.getReceiver().getDisplayName() : messageEntity.getGroup().getDisplayName();
        cubeRecentSession.setSessionName(TextUtils.isEmpty(displayName) ? messageEntity.getGroupId() : displayName);
        cubeRecentSession.setSessionType(CubeSessionType.Group.getType());
    }

    /**
     * 构建P2PRecentSession
     *
     * @param messageEntity
     * @param cubeRecentSession
     */
    private void buildP2PRecentSession(MessageEntity messageEntity, CubeRecentSession cubeRecentSession) {
        if (messageEntity.getSender().getCubeId().equals(SystemMessageManage.SystemSession.VERIFY.getSessionId()) && SystemMessageManage.getInstance().isFromVerify(messageEntity)) {
            cubeRecentSession.setSessionId(SystemMessageManage.SystemSession.VERIFY.getSessionId());
        }
        if (messageEntity.isAnonymous()) {
            cubeRecentSession.setSessionType(CubeSessionType.Secret.getType());
        }
        else {
            cubeRecentSession.setSessionType(CubeSessionType.P2P.getType());
        }
    }

    /**
     * 获取消息方向
     *
     * @param entity
     *
     * @return
     */
    public int getDirection(MessageEntity entity) {
        if (entity.getDirection() == MessageDirection.Sent) {
            return CubeMessageDirection.Sent.getDirection();
        }
        else if (entity.getDirection() == MessageDirection.Received) {
            return CubeMessageDirection.Received.getDirection();
        }
        else {
            return CubeMessageDirection.None.getDirection();
        }
    }
}
