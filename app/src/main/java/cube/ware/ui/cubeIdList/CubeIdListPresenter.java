package cube.ware.ui.cubeIdList;

import android.content.Context;
import android.support.annotation.NonNull;
import com.common.mvp.rx.subscriber.OnActionSubscriber;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.Session;
import cube.service.account.AccountListener;
import cube.ware.AppManager;
import cube.ware.api.CubeUI;
import cube.ware.core.CubeCore;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.model.dataModel.CubeTokenData;
import cube.ware.data.model.dataModel.CubeTotalData;
import cube.ware.data.repository.CubeUserRepository;
import cube.ware.data.room.model.CubeUser;
import cube.ware.utils.SpUtil;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;

public class CubeIdListPresenter extends CubeIdListContract.Presenter implements AccountListener {

    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public CubeIdListPresenter(Context context, CubeIdListContract.View view) {
        super(context, view);
        //添加监听
        CubeEngine.getInstance().getAccountService().addAccountListener(this);
    }

    @Override
    public void queryCubeIdList() {
        ApiFactory.getInstance().queryUsers(AppManager.getAppId(), AppManager.getAppKey(), 0, 40).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<CubeTotalData>() {
            @Override
            public void call(CubeTotalData totalData) {
                List<CubeUser> list = totalData.list;
                mView.queryCubeIdListSuccess(list);
            }
        });
    }

    @Override
    void queryCubeToken(String cubeId) {
        ApiFactory.getInstance().queryCubeToken(AppManager.getAppId(), AppManager.getAppKey(), cubeId).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<CubeTokenData>() {
            @Override
            public void call(CubeTokenData loginData) {
                LogUtil.i(loginData.cubeToken);
                //保存cubeToken
                SpUtil.setCubeToken(loginData.cubeToken);
                mView.queryCubeTokenSuccess(loginData.cubeToken);
            }
        });
    }

    @Override
    void login(@NonNull String cubeId, @NonNull String cubeToken, String displayName) {
        CubeUI.getInstance().login(cubeId, cubeToken, displayName);
    }

    @Override
    void saveUsers(List<CubeUser> users) {
        Session session  = CubeEngine.getInstance().getSession();
        SpUtil.setCubeId(session.getCubeId());
        SpUtil.setUserName(session.getDisplayName());
        SpUtil.setUserAvator(AppManager.getAvatarUrl() + session.getCubeId());
        CubeCore.getInstance().setCubeId(session.getCubeId());
        CubeUserRepository.getInstance().saveUser(users).subscribe();
    }

    @Override
    public void onLogin(Session session) {
        mView.loginSuccess();
    }

    @Override
    public void onLogout(Session session) {

    }

    @Override
    public void onAccountFailed(CubeError error) {
        mView.loginFailed(error.desc);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CubeEngine.getInstance().getAccountService().removeAccountListener(this);
    }
}
