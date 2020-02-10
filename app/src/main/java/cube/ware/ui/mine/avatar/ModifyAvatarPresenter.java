package cube.ware.ui.mine.avatar;

import android.content.Context;
import com.common.mvp.eventbus.EventBusUtil;
import com.common.mvp.rx.subscriber.OnTwiceSubscriber;
import com.common.utils.utils.log.LogUtil;
import cube.ware.common.MessageConstants;
import cube.ware.data.api.ApiException;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.model.dataModel.CubeAvatar;
import cube.ware.data.room.model.CubeUser;
import cube.ware.utils.SpUtil;
import java.io.File;
import rx.android.schedulers.AndroidSchedulers;

public class ModifyAvatarPresenter extends ModifyAvatarContract.Presenter {

    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public ModifyAvatarPresenter(Context context, ModifyAvatarContract.View view) {
        super(context, view);
    }

    @Override
    void modifyAvatar(String dataPath) {
        mView.showLoading();
        ApiFactory.getInstance().uploadAvatar(SpUtil.getCubeToken(), new File(dataPath)).observeOn(AndroidSchedulers.mainThread()).subscribe(new OnTwiceSubscriber<CubeAvatar>() {
            @Override
            public void onNext(CubeAvatar cubeAvatar) {
                LogUtil.i("修改头像 --> url:" + cubeAvatar.getUrl());
                CubeUser user = new CubeUser();
                user.setCubeId(SpUtil.getCubeId());
                user.setDisplayName(SpUtil.getUserName());
                user.setAvatar(cubeAvatar.getUrl());
                SpUtil.setUserAvator(cubeAvatar.getUrl());
                EventBusUtil.post(MessageConstants.Event.EVENT_REFRESH_CUBE_USER, user);

                mView.hideLoading();
                mView.modifyAvatarSuccess(cubeAvatar.getUrl());
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i("uploadAvatar --> onError:" + e);
                mView.hideLoading();
                ApiException apiException = (ApiException) e;
                mView.onError(apiException.getCode(), apiException.getDesc());
            }
        });
    }
}
