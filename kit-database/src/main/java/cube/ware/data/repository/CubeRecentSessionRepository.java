package cube.ware.data.repository;

import com.common.mvp.rx.OnSubscribeRoom;
import cube.ware.data.model.dataModel.CubeRecentViewModel;
import cube.ware.data.room.AppDataBaseFactory;
import cube.ware.data.room.model.CubeRecentSession;
import cube.ware.data.room.model.CubeUser;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/3.
 */

public class CubeRecentSessionRepository {

    private static volatile CubeRecentSessionRepository mInstance;

    public static CubeRecentSessionRepository getInstance() {
        if (null == mInstance) {
            synchronized (CubeRecentSessionRepository.class) {
                if (null == mInstance) {
                    mInstance = new CubeRecentSessionRepository();
                }
            }
        }
        return mInstance;
    }

    /**
     * add或update一条最近会话信息到数据库
     *
     * @param cubeRecentSession
     *
     * @return
     */
    public Observable<CubeRecentSession> addOrUpdateRecentSession(final CubeRecentSession cubeRecentSession) {
        return Observable.create(new OnSubscribeRoom<CubeRecentSession>() {
            @Override
            protected CubeRecentSession get() {
                AppDataBaseFactory.getCubeRecentSessionDao().saveOrUpdate(cubeRecentSession);
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
    public Observable<List<CubeRecentSession>> addOrUpdateRecentSession(final List<CubeRecentSession> cubeRecentSessions) {
        return Observable.create(new OnSubscribeRoom<List<CubeRecentSession>>() {
            @Override
            protected List<CubeRecentSession> get() {
                AppDataBaseFactory.getCubeRecentSessionDao().saveOrUpdate(cubeRecentSessions);
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
    public Observable<CubeRecentSession> removeRecentSession(final String sessionId) {
        return Observable.create(new OnSubscribeRoom<CubeRecentSession>() {
            @Override
            protected CubeRecentSession get() {
                CubeRecentSession cubeRecentSession = AppDataBaseFactory.getCubeRecentSessionDao().queryBySessionId(sessionId);
                if (cubeRecentSession != null) {
                    AppDataBaseFactory.getCubeRecentSessionDao().delete(cubeRecentSession);
                }
                return cubeRecentSession;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询所有最近会话
     *
     * @return
     */
    public Observable<List<CubeRecentSession>> queryAll() {
        return Observable.create(new OnSubscribeRoom<List<CubeRecentSession>>() {
            @Override
            protected List<CubeRecentSession> get() {
                return AppDataBaseFactory.getCubeRecentSessionDao().queryAll();
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
                return AppDataBaseFactory.getCubeRecentSessionDao().queryBySessionId(sessionId);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询所有未读消息数量
     *
     * @return
     */
    public Observable<Integer> queryUnAllReadCount() {

        return queryAll().doOnNext(new Action1<List<CubeRecentSession>>() {
            @Override
            public void call(List<CubeRecentSession> cubeRecentSessions) {
                if (cubeRecentSessions == null) {
                    return;
                }
            }
        }).flatMap(new Func1<List<CubeRecentSession>, Observable<List<String>>>() {
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
            public Observable<Integer> call(List<String> strings) {
                return CubeMessageRepository.getInstance().queryAllUnRead(strings);
            }
        });
    }

    /**
     * 组装最近消息列表model
     *
     * @param sessionId
     *
     * @return
     */
    public Observable<CubeRecentViewModel> queryUnReadCubeRecentSession(final String sessionId) {
        Observable<CubeRecentSession> sessionObservable = queryBySessionId(sessionId);
        Observable<Integer> unReadObservable = CubeMessageRepository.getInstance().queryMessageUnRead(sessionId);
        Observable<CubeUser> cubeUserObservable = CubeUserRepository.getInstance().queryUser(sessionId).map(new Func1<CubeUser, CubeUser>() {
            @Override
            public CubeUser call(CubeUser cubeUser) {
                //对于没在本地user表中的数据处理 ps：暂时 目前最近会话列表CubeRecentSession缓存了sessionName不需要CubeUser的数据了
                // CubeRecentViewModel这个包装类可以删掉 直接使用CubeRecentSession,或者需要自定义用户信息，可以组装CubeRecentViewModel这个包装类
                if (cubeUser == null) {
                    return new CubeUser(sessionId, "", "");
                }

                return cubeUser;
            }
        });

        return Observable.zip(sessionObservable, unReadObservable, cubeUserObservable, new Func3<CubeRecentSession, Integer, CubeUser, CubeRecentViewModel>() {
            @Override
            public CubeRecentViewModel call(CubeRecentSession cubeRecentSession, Integer integer, CubeUser cubeUser) {
                CubeRecentViewModel cubeRecentViewModel = new CubeRecentViewModel();
                cubeRecentSession.setUnRead(integer == null ? 0 : integer);
                cubeRecentViewModel.cubeRecentSession = cubeRecentSession;
                cubeRecentViewModel.userName = cubeUser.getDisplayName();
                cubeRecentViewModel.userFace = cubeUser.getAvatar();
                return cubeRecentViewModel;
            }
        });
    }

    /**
     * 获取所有最近列表model
     *
     * @return
     */
    public Observable<List<CubeRecentViewModel>> queryAllUnReadCubeRecentSession() {
        return queryAll().doOnNext(new Action1<List<CubeRecentSession>>() {
            @Override
            public void call(List<CubeRecentSession> cubeRecentSessions) {
                if (cubeRecentSessions == null) {
                    return;
                }
            }
        }).flatMap(new Func1<List<CubeRecentSession>, Observable<List<CubeRecentViewModel>>>() {
            @Override
            public Observable<List<CubeRecentViewModel>> call(List<CubeRecentSession> cubeRecentSessions) {
                if (cubeRecentSessions == null || cubeRecentSessions.isEmpty()) {
                    return Observable.just(null);
                }
                return Observable.from(cubeRecentSessions).filter(new Func1<CubeRecentSession, Boolean>() {
                    @Override
                    public Boolean call(CubeRecentSession cubeRecentSession) {
                        return cubeRecentSession != null;
                    }
                }).flatMap(new Func1<CubeRecentSession, Observable<CubeRecentViewModel>>() {
                    @Override
                    public Observable<CubeRecentViewModel> call(final CubeRecentSession cubeRecentSession) {
                        return queryUnReadCubeRecentSession(cubeRecentSession.getSessionId());
                    }
                }).toSortedList(new Func2<CubeRecentViewModel, CubeRecentViewModel, Integer>() {
                    @Override
                    public Integer call(CubeRecentViewModel cubeRecentViewModel, CubeRecentViewModel cubeRecentViewModel2) {
                        //倒序排列
                        return cubeRecentViewModel.cubeRecentSession.getTimestamp() - cubeRecentViewModel2.cubeRecentSession.getTimestamp() > 0 ? -1 : 1;
                    }
                });
            }
        });
    }
}
