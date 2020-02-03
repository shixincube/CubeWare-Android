package cube.ware.ui.contact.select;

import android.content.Context;

import com.common.utils.utils.log.LogUtil;

import cube.ware.AppManager;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.api.ResultData;
import cube.ware.data.model.dataModel.TotalData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/28.
 */

public class SelectContactPresenter extends SelectContactContract.Presenter{

    public SelectContactPresenter(Context context, SelectContactContract.View view) {
        super(context, view);
    }

    @Override
    public void getCubeList() {
        ApiFactory.getInstance().queryUsers(AppManager.getAppId(), AppManager.getAppKey(), 0, 20, new Callback<ResultData<TotalData>>() {
            @Override
            public void onResponse(Call<ResultData<TotalData>> call, Response<ResultData<TotalData>> response) {
                if (response.isSuccessful()) {
                    ResultData<TotalData> body = response.body();
                    if (body != null&&body.data!=null) {
                        TotalData data = body.data;
                        mView.onResponseUserList(data.list);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultData<TotalData>> call, Throwable t) {

                LogUtil.e(t);
            }
        });
    }
}
