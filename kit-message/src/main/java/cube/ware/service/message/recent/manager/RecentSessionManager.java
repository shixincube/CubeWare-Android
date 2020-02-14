package cube.ware.service.message.recent.manager;

import android.text.TextUtils;
import com.common.eventbus.EventBusUtil;
import com.common.utils.log.LogUtil;
import cube.service.message.MessageDirection;
import cube.service.message.MessageEntity;
import cube.service.message.TextMessage;
import cube.ware.common.MessageConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.mapper.SessionMapper;
import cube.ware.data.repository.CubeSessionRepository;
import cube.ware.data.room.model.CubeRecentSession;
import cube.ware.service.message.chat.helper.AtHelper;
import cube.ware.utils.SpUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

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
        CubeRecentSession cubeRecentSession = SessionMapper.convertTo(messageEntity);
        CubeSessionRepository.getInstance().saveOrUpdate(cubeRecentSession).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CubeRecentSession>() {
            @Override
            public void call(CubeRecentSession cubeRecentSession) {
                // 刷新最近会话列表
                EventBusUtil.post(MessageConstants.Event.EVENT_REFRESH_RECENT_SESSION_LIST, Collections.singletonList(cubeRecentSession));
            }
        });
    }

    /**
     * 批量添加或更新最近会话到数据库
     *
     * @param messageList
     */
    public void addOrUpdateRecentSession(List<MessageEntity> messageList) {
        List<MessageEntity> sortMessageList = sortMessage(messageList);
        List<CubeRecentSession> cubeRecentSessions = SessionMapper.convertTo(sortMessageList);
        Observable.from(cubeRecentSessions).flatMap(new Func1<CubeRecentSession, Observable<CubeRecentSession>>() {
            @Override
            public Observable<CubeRecentSession> call(final CubeRecentSession cubeRecentSession) {
                return CubeSessionRepository.getInstance().queryBySessionId(cubeRecentSession.getSessionId()).map(new Func1<CubeRecentSession, CubeRecentSession>() {
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
                return CubeSessionRepository.getInstance().saveOrUpdate(cubeRecentSessions);
            }
        }).subscribe(new Action1<List<CubeRecentSession>>() {
            @Override
            public void call(List<CubeRecentSession> cubeRecentSessions) {
                // 刷新最近会话列表
                EventBusUtil.post(MessageConstants.Event.EVENT_REFRESH_RECENT_SESSION_LIST, cubeRecentSessions);
            }
        });
    }

    /**
     * 删除一个最近会话
     *
     * @param sessionId
     */
    public void deleteSessionById(String sessionId) {
        CubeSessionRepository.getInstance().deleteSessionById(sessionId).subscribe(new Action1<String>() {
            @Override
            public void call(String sessionId) {
                EventBusUtil.post(MessageConstants.Event.EVENT_REMOVE_RECENT_SESSION_SINGLE, sessionId);
            }
        });
    }

    /**
     * 对数据进行排序过滤
     *
     * @param messageList
     */
    private List<MessageEntity> sortMessage(List<MessageEntity> messageList) {
        // 消息仓库最大存储量
        HashMap<String, MessageEntity> messageCache = new HashMap<>();
        HashMap<String, MessageEntity> messageSecretCache = new HashMap<>();
        for (int i = 0; i < messageList.size(); i++) {
            MessageEntity message = messageList.get(i);
            if (TextUtils.equals(message.getSender().getCubeId(), MessageConstants.SystemSession.SYSTEM)) {
                continue;
            }

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
                if (AtHelper.isAtMe(textMessage.getContent())) {
                    // 若消息中包含@自己的信息，则显示有人@我
                    SpUtil.setReceiveAt(MessageConstants.Sp.SP_CUBE_AT + SpUtil.getCubeId() + sessionId, true);
                    LogUtil.d("有人@我: " + sessionId);
                }
                else if (AtHelper.isAtAll(textMessage.getContent()) && message.isGroupMessage()) {
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

        return new ArrayList<>(messageCache.values());
    }
}
