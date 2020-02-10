package cube.ware.ui.contact.friend;

import android.content.Context;

import com.common.mvp.rx.subscriber.OnActionSubscriber;
import com.common.utils.utils.log.LogUtil;

import cube.ware.AppManager;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.api.ResultData;
import cube.ware.data.model.dataModel.TotalData;
import cube.ware.data.repository.CubeUserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;

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
        ApiFactory.getInstance().queryUsers(AppManager.getAppId(), AppManager.getAppKey(), 0, 20).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<TotalData>() {
            @Override
            public void call(TotalData totalData) {
                CubeUserRepository.getInstance().saveUser(totalData.list);
                mView.onResponseUserList(totalData.list);
            }
        });
    }
}
