package cube.ware.ui.contact.friend;

import android.content.Context;

import com.common.utils.utils.log.LogUtil;

import cube.ware.AppManager;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.api.ResultData;
import cube.ware.data.model.dataModel.TotalData;
import cube.ware.data.repository.CubeUserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public class FriendListPresenter extends FriendListContract.Presenter{
    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public FriendListPresenter(Context context, FriendListContract.View view) {
        super(context, view);
    }

    @Override
    public void getCubeList() {
        ApiFactory.getInstance().find(AppManager.getAppId(), 0, 20, new Callback<ResultData<TotalData>>() {
            @Override
            public void onResponse(Call<ResultData<TotalData>> call, Response<ResultData<TotalData>> response) {
                if (response.isSuccessful()) {
                    ResultData<TotalData> body = response.body();
                    if (body != null) {
                        TotalData data = body.data;
                        CubeUserRepository.getInstance().saveUser(data.list);
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
