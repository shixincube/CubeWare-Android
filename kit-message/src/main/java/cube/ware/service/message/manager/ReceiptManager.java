package cube.ware.service.message.manager;

import com.common.mvp.eventbus.EventBusUtil;
import com.common.mvp.rx.subscriber.OnActionSubscriber;
import cube.ware.common.MessageConstants;
import cube.ware.data.repository.CubeMessageRepository;
import cube.ware.data.repository.CubeSessionRepository;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.data.room.model.CubeRecentSession;
import cube.ware.utils.SpUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

/**
 * 拦截消息处理流程中关于回执的部分
 *
 * @author LiuFeng
 * @data 2020/2/10 18:40
 */
public class ReceiptManager {

    //该缓存的作用：  因为其他终端会出现回执消息非常快的情况
    // 导致我们收到消息后入库还没有成功立马收到了回执或者先收到回执才收到消息
    private List<String> sessionList = new ArrayList<>();

    public static ReceiptManager getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final ReceiptManager INSTANCE = new ReceiptManager();
    }

    public void onReceiptedAll(final String sessionId, long timestamp) {
        //收到回执后缓存一下
        sessionList.add(sessionId + ":" + timestamp);
        // 缓存最多5个
        if (sessionList.size() > 5) {
            sessionList.remove(0);
        }

        CubeMessageRepository.getInstance().updateReceiptState(sessionId, timestamp).flatMap(new Func1<List<CubeMessage>, Observable<CubeRecentSession>>() {
            @Override
            public Observable<CubeRecentSession> call(List<CubeMessage> cubeMessages) {
                return CubeSessionRepository.getInstance().queryBySessionId(sessionId);
            }
        }).subscribe(new OnActionSubscriber<CubeRecentSession>() {
            @Override
            public void call(CubeRecentSession recentSession) {
                // 消息变化通知最近消息界面
                EventBusUtil.post(MessageConstants.Event.EVENT_REFRESH_RECENT_SESSION_LIST, Collections.singletonList(recentSession));
            }
        });
    }

    /**
     * 是否是已经回执过的消息
     */
    public boolean isAlreadyReceiptedMessage(CubeMessage cubeMessage) {
        if (cubeMessage == null || cubeMessage.getSenderId() == null || cubeMessage.getReceiverId() == null) {
            return false;
        }

        boolean isGroupMessage = cubeMessage.isGroupMessage();
        String sessionId = isGroupMessage ? cubeMessage.getGroupId() : cubeMessage.getSenderId().equals(SpUtil.getCubeId()) ? cubeMessage.getReceiverId() : cubeMessage.getSenderId();

        //反向遍历命中概率更大
        for (int i = sessionList.size() - 1; i >= 0; i--) {
            String[] split = sessionList.get(i).split(":");
            if (sessionId.equals(split[0]) && String.valueOf(cubeMessage.getTimestamp()).equals(split[1])) {
                return true;
            }
        }
        return false;
    }

    public void release() {
        if (!sessionList.isEmpty()) {
            sessionList.clear();
        }
    }
}
