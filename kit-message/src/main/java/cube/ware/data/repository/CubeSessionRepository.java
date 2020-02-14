package cube.ware.data.repository;

import com.common.rx.OnSubscribeRoom;
import cube.ware.data.model.CubeRecentViewModel;
import cube.ware.data.room.CubeDBFactory;
import cube.ware.data.room.model.CubeRecentSession;
import cube.ware.data.room.model.CubeUser;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 最近会话仓库类
 *
 * @author LiuFeng
 * @data 2020/2/4 10:48
 */
public class CubeSessionRepository {

    private static volatile CubeSessionRepository mInstance;

    public static CubeSessionRepository getInstance() {
        if (null == mInstance) {
            synchronized (CubeSessionRepository.class) {
                if (null == mInstance) {
                    mInstance = new CubeSessionRepository();
                }
            }
        }
        return mInstance;
    }

    /**
     * 保存或更新一条最近会话信息到数据库
     *
     * @param cubeRecentSession
     *
     * @return
     */
    public Observable<CubeRecentSession> saveOrUpdate(final CubeRecentSession cubeRecentSession) {
        return Observable.create(new OnSubscribeRoom<CubeRecentSession>() {
            @Override
            protected CubeRecentSession get() {
                CubeDBFactory.getCubeRecentSessionDao().saveOrUpdate(cubeRecentSession);
                return cubeRecentSession;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * add或update 批量最近会话信息到数据库
     *
     * @param cubeRecentSessions
     *
     * @return
     */
    public Observable<List<CubeRecentSession>> saveOrUpdate(final List<CubeRecentSession> cubeRecentSessions) {
        return Observable.create(new OnSubscribeRoom<List<CubeRecentSession>>() {
            @Override
            protected List<CubeRecentSession> get() {
                CubeDBFactory.getCubeRecentSessionDao().saveOrUpdate(cubeRecentSessions);
                return cubeRecentSessions;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据sessionId删除会话
     *
     * @param sessionId
     *
     * @return
     */
    public Observable<String> deleteSessionById(final String sessionId) {
        return Observable.create(new OnSubscribeRoom<String>() {
            @Override
            protected String get() {
                CubeDBFactory.getCubeRecentSessionDao().deleteSessionById(sessionId);
                return sessionId;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询所有最近会话
     *
     * @return
     */
    public Observable<List<CubeRecentSession>> queryAllSessions() {
        return Observable.create(new OnSubscribeRoom<List<CubeRecentSession>>() {
            @Override
            protected List<CubeRecentSession> get() {
                return CubeDBFactory.getCubeRecentSessionDao().queryAll();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 通过会话id 查询CubeRecentSession
     *
     * @param sessionId
     *
     * @return
     */
    public Observable<CubeRecentSession> queryBySessionId(final String sessionId) {
        return Observable.create(new OnSubscribeRoom<CubeRecentSession>() {
            @Override
            protected CubeRecentSession get() {
                return CubeDBFactory.getCubeRecentSessionDao().queryBySessionId(sessionId);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询所有未读消息数量
     *
     * @return
     */
    public Observable<Integer> queryAllSessionsUnReadCount() {
        return queryAllSessions().flatMap(new Func1<List<CubeRecentSession>, Observable<List<String>>>() {
            @Override
            public Observable<List<String>> call(List<CubeRecentSession> cubeRecentSessions) {
                List<String> cubeIds = new ArrayList<>();
                for (CubeRecentSession cubeRecentSession : cubeRecentSessions) {
                    cubeIds.add(cubeRecentSession.getSessionId());
                }
                return Observable.just(cubeIds);
            }
        }).flatMap(new Func1<List<String>, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(List<String> cubeIds) {
                return CubeMessageRepository.getInstance().queryMessageUnReadCount(cubeIds);
            }
        });
    }

    /**
     * 获取所有最近列表model
     *
     * @return
     */
    public Observable<List<CubeRecentViewModel>> queryAllSessionViewModel() {
        return queryAllSessions().filter(new Func1<List<CubeRecentSession>, Boolean>() {
            @Override
            public Boolean call(List<CubeRecentSession> cubeRecentSessions) {
                return cubeRecentSessions != null && !cubeRecentSessions.isEmpty();
            }
        }).flatMap(new Func1<List<CubeRecentSession>, Observable<List<CubeRecentViewModel>>>() {
            @Override
            public Observable<List<CubeRecentViewModel>> call(List<CubeRecentSession> recentSessions) {
                return querySessionViewModel(recentSessions);
            }
        });
    }

    /**
     * 批量查询会话model
     *
     * @param recentSessions
     *
     * @return
     */
    public Observable<List<CubeRecentViewModel>> querySessionViewModel(final List<CubeRecentSession> recentSessions) {
        return Observable.from(recentSessions).filter(new Func1<CubeRecentSession, Boolean>() {
            @Override
            public Boolean call(CubeRecentSession cubeRecentSession) {
                return cubeRecentSession != null;
            }
        }).flatMap(new Func1<CubeRecentSession, Observable<CubeRecentViewModel>>() {
            @Override
            public Observable<CubeRecentViewModel> call(final CubeRecentSession cubeRecentSession) {
                return querySessionViewModel(cubeRecentSession);
            }
        }).toSortedList(new Func2<CubeRecentViewModel, CubeRecentViewModel, Integer>() {
            @Override
            public Integer call(CubeRecentViewModel cubeRecentViewModel, CubeRecentViewModel cubeRecentViewModel2) {
                //倒序排列
                long diff = cubeRecentViewModel.cubeRecentSession.getTimestamp() - cubeRecentViewModel2.cubeRecentSession.getTimestamp();
                return diff < 0 ? 1 : diff == 0 ? 0 : -1;
            }
        });
    }

    /**
     * 组装最近消息列表model
     *
     * @param recentSession
     *
     * @return
     */
    public Observable<CubeRecentViewModel> querySessionViewModel(final CubeRecentSession recentSession) {
        Observable<Integer> unReadObservable = CubeMessageRepository.getInstance().queryMessageUnReadCount(recentSession.getSessionId());
        Observable<CubeUser> cubeUserObservable = CubeUserRepository.getInstance().queryUser(recentSession.getSessionId()).map(new Func1<CubeUser, CubeUser>() {
            @Override
            public CubeUser call(CubeUser cubeUser) {
                //对于没在本地user表中的数据处理 ps：暂时 目前最近会话列表CubeRecentSession缓存了sessionName不需要CubeUser的数据了
                // CubeRecentViewModel这个包装类可以删掉 直接使用CubeRecentSession,或者需要自定义用户信息，可以组装CubeRecentViewModel这个包装类
                if (cubeUser == null) {
                    return new CubeUser(recentSession.getSessionId(), "", "");
                }

                return cubeUser;
            }
        });

        return Observable.zip(unReadObservable, cubeUserObservable, new Func2<Integer, CubeUser, CubeRecentViewModel>() {
            @Override
            public CubeRecentViewModel call(Integer unreadCount, CubeUser cubeUser) {
                CubeRecentViewModel cubeRecentViewModel = new CubeRecentViewModel();
                recentSession.setUnRead(unreadCount == null ? 0 : unreadCount);
                recentSession.setSessionName(cubeUser.getDisplayName());
                //recentSession.setFaceUrl(cubeUser.getAvatar());
                cubeRecentViewModel.cubeRecentSession = recentSession;
                return cubeRecentViewModel;
            }
        });
    }
}
