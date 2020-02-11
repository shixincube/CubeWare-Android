package cube.ware.ui.cubeIdList;

import android.content.Context;
import android.support.annotation.NonNull;
import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;
import cube.service.Session;
import cube.ware.data.room.model.CubeUser;
import java.util.List;

public interface CubeIdListContract {

    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        @Override
        void showMessage(String message);

        /**
         * 登录成功后回调
         */
        void getCubeIdListSuccess(List<CubeUser> userList);

        /**
         * 获取token
         *
         * @param cubeToken
         */
        void queryCubeTokenSuccess(String cubeToken);

        void loginSuccess();

        void loginFailed(String desc);
    }

    /**
     * 抽象登陆Presenter方法的抽象类
     */
    abstract class Presenter extends BasePresenter<View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, View view) {
            super(context, view);
        }

        /**
         * @param
         */
        abstract void queryCubeIdList();

        /**
         * 获取token
         *
         * @param cubeId
         */
        abstract void queryCubeToken(String cubeId);

        /**
         * 登录引擎
         *
         * @param cubeId
         * @param cubeToken
         * @param displayName
         */
        abstract void login(@NonNull String cubeId, @NonNull String cubeToken, String displayName);

        abstract void saveUsers(List<CubeUser> cubeUsers);
    }
}
