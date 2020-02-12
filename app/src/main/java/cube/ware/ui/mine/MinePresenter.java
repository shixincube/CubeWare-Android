package cube.ware.ui.mine;

import android.content.Context;
import com.common.utils.utils.UIHandler;
import cube.ware.data.repository.CubeUserRepository;
import cube.ware.data.room.model.CubeUser;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public class MinePresenter extends MineContract.Presenter {
    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public MinePresenter(Context context, MineContract.View view) {
        super(context, view);
    }

    @Override
    public void getUserData(String cubeId) {
        CubeUserRepository.getInstance().queryUser(cubeId).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CubeUser>() {
            @Override
            public void call(CubeUser cubeUser) {
                mView.getUserData(cubeUser);
            }
        });
    }
}
