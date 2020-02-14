package cube.ware.ui.contact;

import android.content.Context;

import com.common.base.BasePresenter;
import com.common.base.BaseView;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public interface ContactContract {
    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {


    }


    abstract class Presenter extends BasePresenter<ContactContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, ContactContract.View view) {
            super(context, view);
        }


    }
}
