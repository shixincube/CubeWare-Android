package cube.ware.data.repository;

import com.common.mvp.rx.OnSubscribeRoom;
import cube.ware.data.room.CubeDBFactory;
import cube.ware.data.room.model.CubeUser;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * 用户仓库类，用于对外提供用户信息数据
 *
 * @author LiuFeng
 * @data 2020/2/3 18:28
 */
public class CubeUserRepository {

    private static volatile CubeUserRepository mInstance;

    public static CubeUserRepository getInstance() {
        if (null == mInstance) {
            synchronized (CubeUserRepository.class) {
                if (null == mInstance) {
                    mInstance = new CubeUserRepository();
                }
            }
        }
        return mInstance;
    }

    /**
     * 查询用户信息，根据用户cube号
     *
     * @param cubeId
     *
     * @return
     */
    public Observable<CubeUser> queryUser(final String cubeId) {
        return Observable.create(new OnSubscribeRoom<CubeUser>() {
            @Override
            protected CubeUser get() {
                return CubeDBFactory.getCubeUserDao().queryUser(cubeId);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 保存用户列表
     *
     * @param cubeUsers
     *
     * @return
     */
    public Observable<List<CubeUser>> saveUser(final List<CubeUser> cubeUsers) {
        return Observable.create(new OnSubscribeRoom<List<CubeUser>>() {
            @Override
            protected List<CubeUser> get() {
                CubeDBFactory.getCubeUserDao().saveOrUpdate(cubeUsers);
                return cubeUsers;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 保存用户
     *
     * @param cubeUser
     *
     * @return
     */
    public Observable<CubeUser> saveUser(final CubeUser cubeUser) {
        return Observable.create(new OnSubscribeRoom<CubeUser>() {
            @Override
            protected CubeUser get() {
                CubeDBFactory.getCubeUserDao().saveOrUpdate(cubeUser);
                return cubeUser;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询所有用户数据
     *
     * @return
     */
    public Observable<List<CubeUser>> queryAllUser() {
        return Observable.create(new OnSubscribeRoom<List<CubeUser>>() {
            @Override
            protected List<CubeUser> get() {
                return CubeDBFactory.getCubeUserDao().queryAll();
            }
        }).subscribeOn(Schedulers.io());
    }
}
