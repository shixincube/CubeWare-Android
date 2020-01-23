package cube.ware.ui.selectMember;

import android.content.Context;
import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;
import cube.service.conference.model.ConferenceConfig;
import cube.service.group.GroupType;
import cube.service.whiteboard.model.WhiteboardConfig;
import cube.ware.data.room.model.CubeUser;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * author: kun .
 * date:   On 2018/9/4
 */
public interface SelectMemberContract {
    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        void getArgment();

        void getCubeIdListSuccess(LinkedHashMap<String, CubeUser> mSelectedCubeMap, List<CubeUser> mSelectedCubeList);
    }

    abstract class Presenter extends BasePresenter<SelectMemberContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, SelectMemberContract.View view) {
            super(context, view);
        }

        public abstract void getMemberList();

        public abstract void getMemberFromGroup(String cubeId);

        public abstract ConferenceConfig initConferenceConfig(GroupType groupType, String groupId);

        public abstract WhiteboardConfig initWhiteboardConfig(GroupType groupType, String mGroupId);
    }
}