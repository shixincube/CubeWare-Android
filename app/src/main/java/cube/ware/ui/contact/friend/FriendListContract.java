package cube.ware.ui.contact.friend;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

import java.util.List;

import cube.ware.data.room.model.CubeUser;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public interface FriendListContract {

    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        void onResponseUserList(List<CubeUser> list);
    }


    abstract class Presenter extends BasePresenter<FriendListContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, FriendListContract.View view) {
            super(context, view);
        }

        /**
         * 获取联系人列表
         */
        public abstract void getCubeList();
    }
}
