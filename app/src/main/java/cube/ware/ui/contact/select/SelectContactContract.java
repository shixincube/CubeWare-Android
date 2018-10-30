package cube.ware.ui.contact.select;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

import java.util.List;

import cube.ware.data.room.model.CubeUser;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/28.
 */

public interface SelectContactContract {

    interface View extends BaseView {

        void onResponseUserList(List<CubeUser> list);
    }


    abstract class Presenter extends BasePresenter<SelectContactContract.View> {

        public Presenter(Context context, SelectContactContract.View view) {
            super(context, view);
        }

        /**
         * 获取联系人列表
         */
        public abstract void getCubeList();
    }
}
