package cube.ware.ui.whiteboard;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

import java.util.List;

import cube.service.group.model.Member;
import cube.service.user.model.User;
import cube.ware.ui.conference.ConferenceContract;
import rx.Observable;

public interface WhiteContract {
    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {
        abstract void getUserData(User user);
    }


    abstract class Presenter extends BasePresenter<WhiteContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, WhiteContract.View view) {
            super(context, view);
        }

        abstract boolean isCurrentGroup(String cubeId,String groupId);

        abstract boolean isSelf(String cubeId);

        abstract void getUserData(String cubeId);

        abstract Observable<List<User>> getUserDataList(List<String> cubeIdList);

        abstract Observable<List<User>> getUserDataListFromMem(List<Member> cubeIdList);
    }
}
