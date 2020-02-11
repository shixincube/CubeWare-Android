package cube.ware.ui.appid;

import android.content.Context;
import com.common.mvp.rx.subscriber.OnActionSubscriber;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.model.dataModel.CubeIdData;
import cube.ware.data.model.dataModel.CubeTotalData;
import rx.android.schedulers.AndroidSchedulers;

public class AppIdPresenter extends AppIdContract.Presenter {
    private int count;

    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public AppIdPresenter(Context context, AppIdContract.View view) {
        super(context, view);
    }

    @Override
    void checkUsers(String appId, String appKey) {
        ApiFactory.getInstance().queryUsers(appId, appKey, 0, 20).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<CubeTotalData>() {
            @Override
            public void call(CubeTotalData totalData) {
                if (totalData.total >= 20) {
                    count = totalData.list.size();
                    mView.checkUsersSuccess();
                }
                else {
                    //创建20个账号
                    createCubeId(appId, appKey);
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
    private void createCubeId(String appId, String appKey) {
        ApiFactory.getInstance().createUser(appId, appKey).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<CubeIdData>() {
            @Override
            public void call(CubeIdData loginCubeData) {
                count++;
                if (count == 20) {
                    mView.checkUsersSuccess();
                }
                else {
                    createCubeId(appId, appKey);
                }
            }
        });
    }
}
