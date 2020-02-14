package cube.ware.service.message.chat.fragment;

import android.content.Context;
import com.common.base.BasePresenter;
import com.common.base.BaseView;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import java.util.List;

/**
 * 登录契约类，MVP规范
 *
 * @author LiuFeng
 * @date 2018-7-16
 */
public interface MessageContract {

    interface View extends BaseView {

        /**
         * 查询历史消息成功后回调
         */
        void queryMessagesSuccess(List<CubeMessageViewModel> messages);
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
         * 查询历史消息
         *
         * @param sessionType
         * @param chatId
         * @param limit
         * @param time
         * @param isSecret
         */
        public abstract void queryMessages(CubeSessionType sessionType, String chatId, int limit, long time, boolean isSecret);
    }
}
