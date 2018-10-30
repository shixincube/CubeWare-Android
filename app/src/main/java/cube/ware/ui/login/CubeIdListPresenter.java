package cube.ware.ui.login;

import android.content.Context;

import com.common.utils.utils.log.LogUtil;

import java.util.List;

import cube.ware.AppManager;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.api.ResultData;
import cube.ware.data.model.dataModel.LoginData;
import cube.ware.data.model.dataModel.TotalData;
import cube.ware.data.room.model.CubeUser;
import cube.ware.utils.SpUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CubeIdListPresenter extends CubeIdListContract.Presenter{

    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public CubeIdListPresenter(Context context, CubeIdListContract.View view) {
        super(context, view);
    }

    @Override
    public void getCubeIdList() {
        ApiFactory.getInstance().find(AppManager.getAppId(), 0, 40, new Callback<ResultData<TotalData>>() {
            @Override
            public void onResponse(Call<ResultData<TotalData>> call, Response<ResultData<TotalData>> response) {
                if (response.isSuccessful()) {
                    List<CubeUser> list = response.body().data.list;
                    mView.getCubeIdListSuccess(list);
                }
            }

            @Override
            public void onFailure(Call<ResultData<TotalData>> call, Throwable t) {
                mView.showToast(t.getStackTrace().toString());
                LogUtil.i(t.toString());
            }
        });
    }

    @Override
    void getCubetoken(String cubeId) {
        ApiFactory.getInstance().getCubeToken(AppManager.getAppId(), AppManager.getAppKey(), cubeId, new Callback<ResultData<LoginData>>() {
            @Override
            public void onResponse(Call<ResultData<LoginData>> call, Response<ResultData<LoginData>> response) {
                if(response.isSuccessful()){
                    if(response.body().data.cubeToken!=null){
                        SpUtil.setCubeToken(response.body().data.cubeToken);  //保存cubeToken
                        LogUtil.i(response.body().data.cubeToken);
                        mView.getCubeToken(response.body().data.cubeToken);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultData<LoginData>> call, Throwable t) {
                LogUtil.i(t.getStackTrace().toString());
            }
        });
    }
}
