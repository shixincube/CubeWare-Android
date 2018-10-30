package cube.ware.ui.contact.friend.details;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/29.
 */

public interface FriendDetailsContract {

    interface View extends BaseView {

    }


    abstract class Presenter extends BasePresenter<FriendDetailsContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, FriendDetailsContract.View view) {
            super(context, view);
        }

    }
}
