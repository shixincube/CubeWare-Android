package cube.ware.service.group.groupDetails;

import android.content.Context;
import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/28.
 */

public interface GroupDetailsContract {
    interface View extends BaseView {

    }

    abstract class Presenter extends BasePresenter<View> {

        public Presenter(Context context, GroupDetailsContract.View view) {
            super(context, view);
        }
    }
}
