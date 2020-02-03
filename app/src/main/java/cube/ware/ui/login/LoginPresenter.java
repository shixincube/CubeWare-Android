package cube.ware.ui.login;

import android.content.Context;
import com.common.utils.utils.log.LogUtil;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.api.ResultData;
import cube.ware.data.model.dataModel.LoginCubeData;
import cube.ware.data.model.dataModel.TotalData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        ApiFactory.getInstance().queryUsers(appId, appKey, 0, 20, new Callback<ResultData<TotalData>>() {
            @Override
            public void onResponse(Call<ResultData<TotalData>> call, Response<ResultData<TotalData>> response) {
                if (response.isSuccessful() && response.body().state.code == 200) {
                    if (response.body().data.total >= 20) {
                        count = response.body().data.list.size();
                        mView.loginSuccess();
                    }
                    else {
                        //创建20个账号
                        createCubeId(appId,appKey);
                    }
                }
                else {
                    mView.showToast(response.body().state.desc);
                }
            }

            @Override
            public void onFailure(Call<ResultData<TotalData>> call, Throwable t) {
                mView.showToast("服务器请求错误："+t.toString());
                LogUtil.i(t.toString());
            }
        });
    }

    private void createCubeId(String appId,String appKey) {
        //创建CubeId号
        ApiFactory.getInstance().createUser(appId, appKey, new Callback<ResultData<LoginCubeData>>() {
            @Override
            public void onResponse(Call<ResultData<LoginCubeData>> call, Response<ResultData<LoginCubeData>> response) {
                if (response.isSuccessful() && response.body().state.code == 200) {
                    count++;
                    if (count == 20) {
                        mView.loginSuccess();
                    }
                    else {
                        createCubeId(appId,appKey);
                    }
                }
                else {
                    mView.showToast(response.body().state.desc);
                }
            }

            @Override
            public void onFailure(Call<ResultData<LoginCubeData>> call, Throwable t) {
                LogUtil.e(t.toString());
            }
        });
    }
}
