package cube.ware.ui.mine;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

import cube.service.user.model.User;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public interface MineContract {
    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        void getUserData(User user);
    }


    abstract class Presenter extends BasePresenter<MineContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, MineContract.View view) {
            super(context, view);
        }

        abstract void getUserData(String cubeId);
    }
}
