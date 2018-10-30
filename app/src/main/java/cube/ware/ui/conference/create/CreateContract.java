package cube.ware.ui.conference.create;

import android.content.Context;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cube.service.conference.model.ConferenceConfig;
import cube.service.user.model.User;
import cube.ware.data.room.model.CubeUser;
import rx.Observable;

/**
 * Created by kun
 * Des:
 * Date: 2018/9/1.
 */

public interface CreateContract {

    /**
     * Activity将实现的回调接口
     */
    interface View extends BaseView {

        void getInviteDate(List<User> userList, ArrayList<String> invitelist);

        void updateCompleteBtn();

        void onTimeSelect(Date date,String time,long timestaps);

        void onDurationSelect(String time,int index);

    }


    abstract class Presenter extends BasePresenter<CreateContract.View> {

        /**
         * 构造方法
         *
         * @param context
         * @param view
         */
        public Presenter(Context context, CreateContract.View view) {
            super(context, view);
        }

        abstract void changeDate(List<CubeUser> cubeUserList);

        //初始化 ConferenceConfig
        public abstract ConferenceConfig initConferenceConfig(String theme,String groupId,long startTime,long duration,boolean autoNotify,List<String> inviteList);

        abstract Observable<List<User>> getUserDataList(List<String> cubeIdList);

        abstract boolean isCurrentGroup(String cubeId,String groupId);

        public abstract TimePickerView initTimePicker(Context context);

        abstract boolean isSelf(String cubeId);

    }
}
