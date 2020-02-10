package cube.ware.ui.login;

import android.content.Context;
import com.common.mvp.rx.subscriber.OnActionSubscriber;
import com.common.utils.utils.log.LogUtil;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.api.ResultData;
import cube.ware.data.model.dataModel.LoginCubeData;
import cube.ware.data.model.dataModel.TotalData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
        ApiFactory.getInstance().queryUsers(appId, appKey, 0, 20).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<TotalData>() {
            @Override
            public void call(TotalData totalData) {
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
        ApiFactory.getInstance().createUser(appId, appKey).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<LoginCubeData>() {
            @Override
            public void call(LoginCubeData loginCubeData) {
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
