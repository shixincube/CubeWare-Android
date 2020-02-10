package cube.ware.ui.contact.select;

import android.content.Context;

import com.common.mvp.rx.subscriber.OnActionSubscriber;
import com.common.utils.utils.log.LogUtil;

import cube.ware.AppManager;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.api.ResultData;
import cube.ware.data.model.dataModel.TotalData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;

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
        ApiFactory.getInstance().queryUsers(AppManager.getAppId(), AppManager.getAppKey(), 0, 20).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<TotalData>() {
            @Override
            public void call(TotalData totalData) {
                mView.onResponseUserList(totalData.list);
            }
        });
    }
}
