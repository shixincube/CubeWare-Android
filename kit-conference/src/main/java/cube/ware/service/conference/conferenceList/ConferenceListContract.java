package cube.ware.service.conference.conferenceList;

import android.content.Context;

import com.common.base.BasePresenter;
import com.common.base.BaseView;

import java.util.List;

import cube.service.common.model.CubeError;
import cube.service.conference.model.Conference;
import cube.service.group.GroupType;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public interface ConferenceListContract {

    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        void getConference(List<Conference> conferenceList);

        void getConferenceFail(CubeError cubeError);
    }


    abstract class Presenter extends BasePresenter<ConferenceListContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, ConferenceListContract.View view) {
            super(context, view);
        }
        public abstract void getConferenceList(String cubeid, List<GroupType> groupTypes);


    }
}
