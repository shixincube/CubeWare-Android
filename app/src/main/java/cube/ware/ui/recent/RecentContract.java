package cube.ware.ui.recent;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

import java.util.List;

import cube.ware.data.model.dataModel.CubeRecentViewModel;

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


        public abstract void getRecentSessionList();

        public abstract void subscribeChange();

    }
}
