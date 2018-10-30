package cube.ware.data.repository;

import com.common.mvp.rx.OnSubscribeRoom;

import java.util.List;

import cube.ware.data.room.AppDataBaseFactory;
import cube.ware.data.room.model.CubeUser;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/3.
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
    public Observable<CubeUser> queryUser(String cubeId) {
        return Observable.create(new OnSubscribeRoom<CubeUser>() {
            @Override
            protected CubeUser get() {
                return AppDataBaseFactory.getCubeUserDao().queryUser(cubeId);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 保存用户列表
     * @param cubeUsers
     * @return
     */
    public Observable<List<CubeUser>> saveUser(List<CubeUser> cubeUsers) {
        return Observable.create(new OnSubscribeRoom<List<CubeUser>>() {
            @Override
            protected List<CubeUser> get() {
                AppDataBaseFactory.getCubeUserDao().saveOrUpdate(cubeUsers);
                return cubeUsers;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 保存用户
     * @param cubeUser
     * @return
     */
    public Observable<CubeUser> saveUser(CubeUser cubeUser) {
        return Observable.create(new OnSubscribeRoom<CubeUser>() {
            @Override
            protected CubeUser get() {
                AppDataBaseFactory.getCubeUserDao().saveOrUpdate(cubeUser);
                return cubeUser;
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询所有用户数据
     * @return
     */
    public Observable<List<CubeUser>> queryAllUser(){
        return Observable.create(new OnSubscribeRoom<List<CubeUser>>() {
            @Override
            protected List<CubeUser> get() {
                List<CubeUser> cubeUsers = AppDataBaseFactory.getCubeUserDao().queryAll();
                return cubeUsers;
            }
        });
    }
}
