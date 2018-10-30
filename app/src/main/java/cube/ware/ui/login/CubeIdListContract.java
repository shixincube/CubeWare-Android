package cube.ware.ui.login;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

import java.util.List;

import cube.service.user.model.User;
import cube.ware.data.room.model.CubeUser;

public interface CubeIdListContract  {

    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        /**
         * 登录成功后回调
         */
        void getCubeIdListSuccess(List<CubeUser> userList);

        /**
         * show toast
         * @param msg
         */
        void showToast(String msg);

        /**
         * 获取token
         * @param cubeToken
         */
        void getCubeToken(String cubeToken);
        /**
         *
         * @param user
         */
        void loginCubeEngineSuccess(User user);
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
         *
         *
         * @param
         */
        abstract void getCubeIdList();

        /**
         * 获取token
         * @param cubeId
         */
        abstract void getCubetoken(String cubeId);
    }
}
