package cube.ware.ui.login;

import android.content.Context;
import com.common.mvp.rx.subscriber.OnActionSubscriber;
import com.common.utils.utils.log.LogUtil;
import cube.ware.AppManager;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.model.dataModel.CubeTokenData;
import cube.ware.data.model.dataModel.CubeTotalData;
import cube.ware.data.room.model.CubeUser;
import cube.ware.utils.SpUtil;
import java.util.List;
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
        ApiFactory.getInstance().queryUsers(AppManager.getAppId(), AppManager.getAppKey(), 0, 40).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<CubeTotalData>() {
            @Override
            public void call(CubeTotalData totalData) {
                List<CubeUser> list = totalData.list;
                mView.getCubeIdListSuccess(list);
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
}
