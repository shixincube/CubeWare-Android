package cube.ware.ui.splash;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public interface SplashContract {
    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        void enterMain();

        void enterLogin();

        void timeCountDown(int count);
    }


    abstract class Presenter extends BasePresenter<SplashContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, SplashContract.View view) {
            super(context, view);
        }

        abstract void autoLogin(boolean isAuto);

    }
}
