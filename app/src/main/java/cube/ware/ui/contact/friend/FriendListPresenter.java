package cube.ware.ui.contact.friend;

import android.content.Context;

import com.common.mvp.rx.subscriber.OnActionSubscriber;

import cube.ware.AppManager;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.model.dataModel.CubeTotalData;
import cube.ware.data.repository.CubeUserRepository;
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
        ApiFactory.getInstance().queryUsers(AppManager.getAppId(), AppManager.getAppKey(), 0, 20).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnActionSubscriber<CubeTotalData>() {
            @Override
            public void call(CubeTotalData totalData) {
                CubeUserRepository.getInstance().saveUser(totalData.list);
                mView.onResponseUserList(totalData.list);
            }
        });
    }
}
