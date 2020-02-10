package cube.ware.ui.login;

import android.content.Context;
import com.common.mvp.rx.subscriber.OnActionSubscriber;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.model.dataModel.CubeIdData;
import cube.ware.data.model.dataModel.CubeTotalData;
import rx.android.schedulers.AndroidSchedulers;

public class LoginPresenter extends LoginContract.Presenter {
    private int count;

    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public LoginPresenter(Context context, LoginContract.View view) {
        super(context, view);
    }

    @Override
    void login(String appId,String appKey) {
        getCubeList(appId,appKey);
    }

    private void getCubeList(String appId, String appKey) {
        ApiFactory.getInstance().queryUsers(appId, appKey, 0, 20).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<CubeTotalData>() {
            @Override
            public void call(CubeTotalData totalData) {
                if (totalData.total >= 20) {
                    count = totalData.list.size();
                    mView.loginSuccess();
                }
                else {
                    //创建20个账号
                    createCubeId(appId,appKey);
                }
            }
        });
    }

    /**
     * 创建CubeId号
     *
     * @param appId
     * @param appKey
     */
    private void createCubeId(String appId,String appKey) {
        ApiFactory.getInstance().createUser(appId, appKey).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<CubeIdData>() {
            @Override
            public void call(CubeIdData loginCubeData) {
                count++;
                if (count == 20) {
                    mView.loginSuccess();
                }
                else {
                    createCubeId(appId,appKey);
                }
            }
        });
    }
}
