package cube.ware.service.group.groupList;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public interface GroupListContract {

    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {


    }


    abstract class Presenter extends BasePresenter<GroupListContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, GroupListContract.View view) {
            super(context, view);
        }
    }
}
