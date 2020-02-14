package cube.ware.service.message.recent;

import android.content.Context;
import com.common.base.BasePresenter;
import com.common.base.BaseView;
import com.common.eventbus.Event;
import cube.ware.data.model.CubeRecentViewModel;
import java.util.List;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public interface RecentContract {

    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        void onRefreshList(List<CubeRecentViewModel> cubeRecentViewModels);

        void onRefresh(CubeRecentViewModel cubeRecentViewModel);

        void onRemoveSession(String sessionId);

        void onRefreshListAvatar();
    }

    abstract class Presenter extends BasePresenter<RecentContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, RecentContract.View view) {
            super(context, view);
        }

        public abstract void refreshRecentSessions();

        public abstract <T> void handleReceiveEvent(Event<T> event);
    }
}
