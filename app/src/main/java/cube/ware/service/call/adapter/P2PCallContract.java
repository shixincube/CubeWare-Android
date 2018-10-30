package cube.ware.service.call.adapter;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

import cube.service.user.model.User;

/**
 * Created by zzy on 2018/8/29.
 */

public interface P2PCallContract {
    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        /**
         * 获取用户数据后回调
         */
        void getCallUserSuccess(User user);
    }

    /**
     * 抽象登陆Presenter方法的抽象类
     */
    abstract class Presenter extends BasePresenter<P2PCallContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, P2PCallContract.View view) {
            super(context, view);
        }

        /**
         * 获取通话用户信息
         *
         * @param
         */
        abstract void getCallUser(String callId);
    }

}
