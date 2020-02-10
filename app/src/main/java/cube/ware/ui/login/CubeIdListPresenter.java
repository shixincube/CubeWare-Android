package cube.ware.ui.login;

import android.content.Context;
import com.common.mvp.rx.subscriber.OnActionSubscriber;
import com.common.utils.utils.log.LogUtil;
import cube.ware.AppManager;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.api.ResultData;
import cube.ware.data.model.dataModel.LoginData;
import cube.ware.data.model.dataModel.TotalData;
import cube.ware.data.room.model.CubeUser;
import cube.ware.utils.SpUtil;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;

public class CubeIdListPresenter extends CubeIdListContract.Presenter {

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
    public void queryCubeIdList() {
        ApiFactory.getInstance().queryUsers(AppManager.getAppId(), AppManager.getAppKey(), 0, 40).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<TotalData>() {
            @Override
            public void call(TotalData totalData) {
                List<CubeUser> list = totalData.list;
                mView.getCubeIdListSuccess(list);
            }
        });
    }

    @Override
    void queryCubeToken(String cubeId) {
        ApiFactory.getInstance().queryCubeToken(AppManager.getAppId(), AppManager.getAppKey(), cubeId).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<LoginData>() {
            @Override
            public void call(LoginData loginData) {
                LogUtil.i(loginData.cubeToken);
                //保存cubeToken
                SpUtil.setCubeToken(loginData.cubeToken);
                mView.queryCubeTokenSuccess(loginData.cubeToken);
            }
        });
    }
}
