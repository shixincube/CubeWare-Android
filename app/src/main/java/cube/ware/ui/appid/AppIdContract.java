package cube.ware.ui.appid;

import android.content.Context;
import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

/**
 * 登录契约类，MVP规范
 *
 * @author LiuFeng
 * @date 2018-7-16
 */
public interface AppIdContract {

    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        /**
         * 检查用户成功后回调
         */
        void checkUsersSuccess();

        /**
         * show toast
         *
         * @param msg
         */
        void showToast(String msg);
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
         * checkUsers
         */
        abstract void checkUsers(String appId, String appKey);
    }
}
