package cube.ware.data.repository;

import com.common.mvp.rx.OnSubscribeRoom;
import com.common.utils.utils.log.LogUtil;

import java.util.LinkedList;
import java.util.List;

import cube.service.message.model.MessageEntity;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.AppDataBaseFactory;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.manager.MessageManager;
import cube.ware.utils.SpUtil;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by dth
 * Des: 消息相关仓库类，用于对外提供消息相关数据
 * Date: 2018/9/3.
 */

public class CubeMessageRepository {

    private static volatile CubeMessageRepository mInstance;

    public static CubeMessageRepository getInstance() {
        if (null == mInstance) {
            synchronized (CubeMessageRepository.class) {
                if (null == mInstance) {
                    mInstance = new CubeMessageRepository();
                }
            }
        }
        return mInstance;
    }

    /**
     * 根据消息SN更新一条语音消息已播放状态
     *
     * @param message
     * @param isPlay
     * @return
     */
    public void updateMessageIsPlay(CubeMessage message, boolean isPlay) {
        Observable.just(message)
                .observeOn(Schedulers.io())
                .subscribe(new Action1<CubeMessage>() {
                    @Override
                    public void call(CubeMessage cubeMessage) {
                        cubeMessage.setPlay(isPlay);
                        AppDataBaseFactory.getCubeMessageDao().saveOrUpdate(cubeMessage);
                    }
                });
    }

    /**
     * 根据最后一条回执消息时间戳更新多条消息已回执状态
     *
     * @param chatId
     * @param time
     * @param isReceipted
     * @return
     */
    public Observable<List<CubeMessage>> updateIsReceipted(String chatId, long time, boolean isReceipted) {
        return Observable.create(new OnSubscribeRoom<List<CubeMessage>>() {
            @Override
            protected List<CubeMessage> get() {
                List<CubeMessage> cubeMessages = AppDataBaseFactory.getCubeMessageDao().queryMessages(chatId, time, isReceipted);

                for (CubeMessage cubeMessage : cubeMessages) {
                    cubeMessage.setReceipt(true);
                    cubeMessage.setRead(true);
                }

                AppDataBaseFactory.getCubeMessageDao().saveOrUpdate(cubeMessages);
                return cubeMessages;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 添加或更新一条消息到数据库
     *
     * @param messageEntity
     * @return
     */
    public Observable<CubeMessage> addMessage(MessageEntity messageEntity) {
        CubeMessage cubeMessage = MessageManager.getInstance().convertTo(messageEntity, false);
        if (cubeMessage == null) {
            return Observable.empty();
        }


        return Observable.create(new OnSubscribeRoom<CubeMessage>() {
            @Override
            protected CubeMessage get() {
                AppDataBaseFactory.getCubeMessageDao().saveOrUpdate(cubeMessage);
                return cubeMessage;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 添加消息到数据库
     *
     * @param messageEntity
     * @param isSyncMessage 是否是同步下来的消息
     * @return
     */
    public Observable<List<CubeMessage>> addMessage(LinkedList<MessageEntity> messageEntity, boolean isSyncMessage) {
        if (messageEntity == null || messageEntity.size() == 0) {
            return Observable.create(new Observable.OnSubscribe<List<CubeMessage>>() {
                @Override
                public void call(Subscriber<? super List<CubeMessage>> subscriber) {
                    subscriber.onCompleted();
                }
            });
        }
        LogUtil.i("addMessage==> size=" + messageEntity.size() + "isSyncMessage=" + isSyncMessage);
        List<CubeMessage> cubeMessageList = MessageManager.getInstance().convertTo(messageEntity, isSyncMessage);
        return Observable.create(new OnSubscribeRoom<List<CubeMessage>>() {
            @Override
            protected List<CubeMessage> get() {
                AppDataBaseFactory.getCubeMessageDao().saveOrUpdate(cubeMessageList);
                return cubeMessageList;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     *
     * @param cubeMessages
     * @return
     */
    public Observable<List<CubeMessage>> saveOrUpdate(List<CubeMessage> cubeMessages) {
        if (cubeMessages == null || cubeMessages.size() == 0) {
            return Observable.empty();
        }
        return Observable.create(new OnSubscribeRoom<List<CubeMessage>>() {
            @Override
            protected List<CubeMessage> get() {
                AppDataBaseFactory.getCubeMessageDao().saveOrUpdate(cubeMessages);
                return cubeMessages;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<CubeMessage> saveOrUpdate(CubeMessage cubeMessage) {
        if (cubeMessage == null ) {
            return Observable.empty();
        }
        return Observable.create(new OnSubscribeRoom<CubeMessage>() {
            @Override
            protected CubeMessage get() {
                AppDataBaseFactory.getCubeMessageDao().saveOrUpdate(cubeMessage);
                return cubeMessage;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据聊天时间查询消息
     *
     * @param receiverId
     * @param sessionType
     * @param time
     * @param limit
     * @return
     */
    public Observable<List<CubeMessage>> queryMessage(String receiverId, int sessionType, long time, int limit) {
        return Observable.create(new OnSubscribeRoom<List<CubeMessage>>() {
            @Override
            protected List<CubeMessage> get() {
                return AppDataBaseFactory.getCubeMessageDao().queryMessages(receiverId, time, limit);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据消息SN删除一条消息
     *
     * @param messageSN
     */
    public Observable<Boolean> deleteMessageBySN(long messageSN) {
        return Observable.create(new OnSubscribeRoom<Boolean>() {
            @Override
            protected Boolean get() {
                CubeMessage cubeMessage = AppDataBaseFactory.getCubeMessageDao().queryMessageBySn(messageSN);
                if (cubeMessage != null) {
                    AppDataBaseFactory.getCubeMessageDao().delete(cubeMessage);
                    return true;
                }
                return false;
            }
        }).subscribeOn(Schedulers.io());
    }


    /**
     *
     * @param chatId
     * @return
     */
    public Observable<Integer> queryMessageUnRead(String chatId) {
        return Observable.create(new OnSubscribeRoom<Integer>() {
            @Override
            protected Integer get() {
                return AppDataBaseFactory.getCubeMessageDao().queryUnReadMessagesCount(chatId, SpUtil.getCubeId(),false);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询所有未读消息
     * @param chatIds
     * @return
     */
    public Observable<Integer> queryAllUnRead(List<String> chatIds) {
        return Observable.create(new OnSubscribeRoom<Integer>() {
            @Override
            protected Integer get() {
                return AppDataBaseFactory.getCubeMessageDao().queryAllUnReadMessagesCount(chatIds, SpUtil.getCubeId(),false);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据sn查一条消息
     * @param messageSN
     * @return
     */
    public Observable<CubeMessage> queryMessageBySn(long messageSN) {
        return Observable.create(new OnSubscribeRoom<CubeMessage>() {
            @Override
            protected CubeMessage get() {
                return AppDataBaseFactory.getCubeMessageDao().queryMessageBySn(messageSN);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据消息类型查消息
     * @param chatId
     * @param messageType
     * @return
     */
    public Observable<List<CubeMessage>> queryMessageListByType(String chatId, CubeMessageType messageType) {
        return Observable.create(new OnSubscribeRoom<List<CubeMessage>>() {
            @Override
            protected List<CubeMessage> get() {
                return AppDataBaseFactory.getCubeMessageDao().queryMessageListByType(chatId, messageType.getType());
            }
        }).subscribeOn(Schedulers.io());
    }
}
