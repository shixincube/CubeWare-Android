package cube.ware.data.repository;

import com.common.mvp.rx.OnSubscribeRoom;
import cube.service.message.MessageEntity;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.CubeDataBaseFactory;
import cube.ware.data.room.mapper.MessageMapper;
import cube.ware.data.room.model.CubeMessage;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 消息仓库类，用于对外提供消息相关数据
 *
 * @author LiuFeng
 * @data 2020/2/3 18:25
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
     * 保存更新消息
     *
     * @param message
     *
     * @return
     */
    public void updateMessage(CubeMessage message) {
        Observable.just(message).observeOn(Schedulers.io()).subscribe(new Action1<CubeMessage>() {
            @Override
            public void call(CubeMessage cubeMessage) {
                CubeDataBaseFactory.getCubeMessageDao().saveOrUpdate(cubeMessage);
            }
        });
    }

    /**
     * 更新会话消息的回执状态
     *
     * @param chatId
     * @param time
     *
     * @return
     */
    public Observable<List<CubeMessage>> updateReceiptState(final String chatId, final long time) {
        return Observable.create(new OnSubscribeRoom<List<CubeMessage>>() {
            @Override
            protected List<CubeMessage> get() {
                List<CubeMessage> cubeMessages = CubeDataBaseFactory.getCubeMessageDao().queryMessages(chatId, time, false);
                for (CubeMessage cubeMessage : cubeMessages) {
                    cubeMessage.setReceipt(true);
                    cubeMessage.setRead(true);
                }

                CubeDataBaseFactory.getCubeMessageDao().saveOrUpdate(cubeMessages);
                return cubeMessages;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 添加或更新一条消息到数据库
     *
     * @param messageEntity
     *
     * @return
     */
    public Observable<CubeMessage> addMessage(final MessageEntity messageEntity) {
        if (messageEntity == null) {
            return Observable.empty();
        }

        return Observable.create(new OnSubscribeRoom<CubeMessage>() {
            @Override
            protected CubeMessage get() {
                CubeMessage message = MessageMapper.convertTo(messageEntity, false);
                CubeDataBaseFactory.getCubeMessageDao().saveOrUpdate(message);
                return message;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 添加消息到数据库
     *
     * @param messageEntity
     * @param isSyncMessage 是否是同步下来的消息
     *
     * @return
     */
    public Observable<List<CubeMessage>> addMessage(final List<MessageEntity> messageEntity, final boolean isSyncMessage) {
        if (messageEntity == null || messageEntity.size() == 0) {
            return Observable.empty();
        }

        return Observable.create(new OnSubscribeRoom<List<CubeMessage>>() {
            @Override
            protected List<CubeMessage> get() {
                List<CubeMessage> cubeMessageList = MessageMapper.convertTo(messageEntity, isSyncMessage);
                CubeDataBaseFactory.getCubeMessageDao().saveOrUpdate(cubeMessageList);
                return cubeMessageList;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 保存或更新消息
     *
     * @param cubeMessages
     *
     * @return
     */
    public Observable<List<CubeMessage>> saveOrUpdate(final List<CubeMessage> cubeMessages) {
        if (cubeMessages == null || cubeMessages.size() == 0) {
            return Observable.empty();
        }

        return Observable.create(new OnSubscribeRoom<List<CubeMessage>>() {
            @Override
            protected List<CubeMessage> get() {
                CubeDataBaseFactory.getCubeMessageDao().saveOrUpdate(cubeMessages);
                return cubeMessages;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 保存或更新消息
     *
     * @param cubeMessage
     *
     * @return
     */
    public Observable<CubeMessage> saveOrUpdate(final CubeMessage cubeMessage) {
        if (cubeMessage == null) {
            return Observable.empty();
        }

        return Observable.create(new OnSubscribeRoom<CubeMessage>() {
            @Override
            protected CubeMessage get() {
                CubeDataBaseFactory.getCubeMessageDao().saveOrUpdate(cubeMessage);
                return cubeMessage;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据聊天时间查询消息
     *
     * @param chatId
     * @param time
     * @param limit
     *
     * @return
     */
    public Observable<List<CubeMessage>> queryMessage(final String chatId, final long time, final int limit) {
        return Observable.create(new OnSubscribeRoom<List<CubeMessage>>() {
            @Override
            protected List<CubeMessage> get() {
                return CubeDataBaseFactory.getCubeMessageDao().queryMessages(chatId, time, limit);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 通过SN删除消息
     *
     * @param messageSN
     */
    public Observable<Boolean> deleteMessageBySN(final long messageSN) {
        return Observable.create(new OnSubscribeRoom<Boolean>() {
            @Override
            protected Boolean get() {
                CubeMessage cubeMessage = CubeDataBaseFactory.getCubeMessageDao().queryMessageBySn(messageSN);
                if (cubeMessage != null) {
                    CubeDataBaseFactory.getCubeMessageDao().delete(cubeMessage);
                    return true;
                }
                return false;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询会话消息未读数
     *
     * @param chatId
     *
     * @return
     */
    public Observable<Integer> queryMessageUnReadCount(final String chatId) {
        return Observable.create(new OnSubscribeRoom<Integer>() {
            @Override
            protected Integer get() {
                return CubeDataBaseFactory.getCubeMessageDao().queryUnReadMessagesCount(chatId, CubeCore.getInstance().getCubeId(), false);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询会话列表的消息的未读总数
     *
     * @param chatIds
     *
     * @return
     */
    public Observable<Integer> queryMessageUnReadCount(final List<String> chatIds) {
        return Observable.create(new OnSubscribeRoom<Integer>() {
            @Override
            protected Integer get() {
                return CubeDataBaseFactory.getCubeMessageDao().queryAllUnReadMessagesCount(chatIds, CubeCore.getInstance().getCubeId(), false);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据sn查一条消息
     *
     * @param messageSN
     *
     * @return
     */
    public Observable<CubeMessage> queryMessageBySn(final long messageSN) {
        return Observable.create(new OnSubscribeRoom<CubeMessage>() {
            @Override
            protected CubeMessage get() {
                return CubeDataBaseFactory.getCubeMessageDao().queryMessageBySn(messageSN);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据消息类型查消息
     *
     * @param chatId
     * @param messageType
     *
     * @return
     */
    public Observable<List<CubeMessage>> queryMessageListByType(final String chatId, final CubeMessageType messageType) {
        return Observable.create(new OnSubscribeRoom<List<CubeMessage>>() {
            @Override
            protected List<CubeMessage> get() {
                return CubeDataBaseFactory.getCubeMessageDao().queryMessageListByType(chatId, messageType.type);
            }
        }).subscribeOn(Schedulers.io());
    }
}
