package cube.ware.ui.mine.avatar;

import android.content.Context;
import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

/**
 * 修改头像契约类
 *
 * @author LiuFeng
 * @data 2020/2/10 12:33
 */
public interface ModifyAvatarContract {
    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        @Override
        void showLoading();

        @Override
        void hideLoading();

        /**
         * 修改头像成功后回调
         *
         * @param url
         */
        void modifyAvatarSuccess(String url);

        @Override
        void onError(int code, String message);
    }

    /**
     * 抽象登陆Presenter方法的抽象类
     */
    abstract class Presenter extends BasePresenter<View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, View view) {
            super(context, view);
        }

        /**
         * 修改头像
         *
         * @param dataPath
         */
        abstract void modifyAvatar(String dataPath);
    }
}
