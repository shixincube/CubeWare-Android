package cube.ware.ui.setting;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

/**
 * Created by dth
 * Des: 设置契约类
 * Date: 2018/9/17.
 */

public interface SettingContract {

    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

    }


    abstract class Presenter extends BasePresenter<SettingContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, SettingContract.View view) {
            super(context, view);
        }

    }
}