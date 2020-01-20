package cube.ware.service.whiteboard.ui;

import android.content.Context;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.group.model.Member;
import cube.service.user.model.User;
import cube.ware.data.repository.CubeUserRepository;
import cube.ware.data.room.model.CubeUser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WhitePresenter extends WhiteContract.Presenter {
    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public WhitePresenter(Context context, WhiteContract.View view) {
        super(context, view);
    }

    /**
     * @param cubeId
     *
     * @return
     */
    @Override
    public boolean isCurrentGroup(String cubeId, String groupId) {
        if (cubeId.equals(groupId)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @param cubeId
     *
     * @return
     */
    @Override
    public boolean isSelf(String cubeId) {
        if (cubeId.equals(CubeEngine.getInstance().getSession().getUser().cubeId)) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void getUserData(String cubeId) {
        if (cubeId != null) {
            CubeUserRepository.getInstance().queryUser(cubeId).observeOn(Schedulers.io()).subscribe(new Action1<CubeUser>() {
                @Override
                public void call(CubeUser cubeUser) {
                    LogUtil.d("==-查询到的cubeuser是够为空===" + cubeUser);
                    if (null != cubeUser) {
                        User user = new User();
                        user.cubeId = cubeUser.getCubeId();
                        user.avatar = cubeUser.getAvatar();
                        user.displayName = cubeUser.getDisplayName();
                        mView.getUserData(user);
                    }
                }
            });
        }
    }

    /**
     * 去重复
     *
     * @param members
     * @param invites
     *
     * @return
     */
    public List<Member> deleteRepeat(List<Member> members, List<Member> invites) {
        if (members != null && invites != null) {
            for (int i = 0; i < members.size(); i++) {
                Member member = members.get(i);
                Iterator<Member> it = invites.iterator();
                while (it.hasNext()) {
                    if (member.cubeId.equals(it.next().cubeId)) {
                        it.remove();
                    }
                }
            }
        }
        return invites;
    }

    @Override
    public Observable<List<User>> getUserDataList(List<String> cubeIdList) {
        return Observable.from(cubeIdList).flatMap(new Func1<String, Observable<User>>() {
            @Override
            public Observable<User> call(final String cubeId) {
                return CubeUserRepository.getInstance().queryUser(cubeId).map(new Func1<CubeUser, User>() {
                    @Override
                    public User call(CubeUser cubeUser) {
                        User user = new User();
                        user.cubeId = cubeUser.getCubeId();
                        user.avatar = cubeUser.getAvatar();
                        user.displayName = cubeUser.getDisplayName();
                        return user;
                    }
                });
            }
        }).toList();
    }

    @Override
    public Observable<List<User>> getUserDataListFromMem(List<Member> members) {
        List<String> cubeIdList = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            cubeIdList.add(members.get(i).cubeId);
        }
        return Observable.from(cubeIdList).flatMap(new Func1<String, Observable<User>>() {
            @Override
            public Observable<User> call(final String cubeId) {
                return CubeUserRepository.getInstance().queryUser(cubeId).map(new Func1<CubeUser, User>() {
                    @Override
                    public User call(CubeUser cubeUser) {
                        User user = new User();
                        user.cubeId = cubeUser.getCubeId();
                        user.avatar = cubeUser.getAvatar();
                        user.displayName = cubeUser.getDisplayName();
                        return user;
                    }
                });
            }
        }).toList();
    }
}
