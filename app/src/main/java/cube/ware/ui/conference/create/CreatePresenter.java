package cube.ware.ui.conference.create;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;
import com.common.utils.utils.log.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cube.service.CubeEngine;
import cube.service.conference.model.Conference;
import cube.service.conference.model.ConferenceConfig;
import cube.service.group.GroupType;
import cube.service.group.model.Member;
import cube.service.user.model.User;
import cube.ware.R;
import cube.ware.data.repository.CubeUserRepository;
import cube.ware.data.room.model.CubeUser;
import cube.ware.service.conference.ConferenceHandle;
import cube.ware.utils.SpUtil;
import rx.Observable;
import rx.functions.Func1;

/**
 * author: kun .
 * date:   On 2018/9/3
 */
public class CreatePresenter extends CreateContract.Presenter{
    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public CreatePresenter(Context context, CreateContract.View view) {
        super(context, view);
    }


    @Override
    public void changeDate(List<CubeUser> cubeUserList) {
        List<User> userList=new ArrayList<>();
        ArrayList<String> userCubeIdList=new ArrayList<>();
        for (int i = 0; i < cubeUserList.size(); i++) {
            User user=new User();
            user.cubeId=cubeUserList.get(i).getCubeId();
            user.avatar=cubeUserList.get(i).getAvatar();
            user.displayName=cubeUserList.get(i).getDisplayName();
            userList.add(user);
            userCubeIdList.add(cubeUserList.get(i).getCubeId());
        }
        mView.getInviteDate(userList,userCubeIdList);

    }

    @Override
    public ConferenceConfig initConferenceConfig(String theme,String groupId,long startTime,long duration,boolean autoNotify,List<String> inviteList){
        List<String> list=new ArrayList<>();
        list.add(SpUtil.getCubeId());
        ConferenceConfig conferenceConfig = new ConferenceConfig(GroupType.VIDEO_CONFERENCE, theme);
        conferenceConfig.isMux = true; //是否融屏幕，传GroupType.VIDEO_CALL时使用
        conferenceConfig.force = false;  //是否强制开启
        conferenceConfig.number = "0"; //创建传0即可
        conferenceConfig.maxNumber = 9;
        if(inviteList!=null){
            conferenceConfig.invites=inviteList;  //需要邀请的人员的cubeId号，也可以后面调用initMember（）方法来实现邀请
        }
        conferenceConfig.maxMember= 9;
        conferenceConfig.startTime= startTime;
        conferenceConfig.duration= duration;
        conferenceConfig.autoNotify =autoNotify;  // true
        conferenceConfig.members = list; // 群组的id集合，创建者可以只添加自己的id号
        return conferenceConfig;
    }

//    @Override
    public Observable<List<User>> getUserDataListFromMem(List<Member>  members) {
        List<String> cubeIdList=new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            cubeIdList.add(members.get(i).cubeId);
        }
        return Observable.from(cubeIdList).flatMap(new Func1<String, Observable<User>>() {
            @Override
            public Observable<User> call(final String cubeId) {

                return CubeUserRepository.getInstance().queryUser(cubeId).map(new Func1<CubeUser, User>() {
                    @Override
                    public User call(CubeUser cubeUser) {
                        User user = new User();
                        user.cubeId = cubeUser.getCubeId();
                        user.avatar = cubeUser.getAvatar();
                        user.displayName = cubeUser.getDisplayName();
                        return user;
                    }
                });
            }
        }).toList();
    }

    @Override
    public Observable<List<User>> getUserDataList(List<String> cubeIdList) {
        return Observable.from(cubeIdList).flatMap(new Func1<String, Observable<User>>() {
            @Override
            public Observable<User> call(final String cubeId) {

                return CubeUserRepository.getInstance().queryUser(cubeId).map(new Func1<CubeUser, User>() {
                    @Override
                    public User call(CubeUser cubeUser) {
                        User user = new User();
                        user.cubeId = cubeUser.getCubeId();
                        user.avatar = cubeUser.getAvatar();
                        user.displayName = cubeUser.getDisplayName();
                        return user;
                    }
                });
            }
        }).toList();
    }

    @Override
    public TimePickerView initTimePicker(Context context) {//Dialog 模式下，在底部弹出

        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();

        startDate.set(2018, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2020, 11, 30);

        TimePickerView pvTime = new TimePickerBuilder(context, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {

                String startTime=dataToString(date,context);
                mView.onTimeSelect(date,startTime,date.getTime());
            }
        })
        .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
            @Override
            public void onTimeSelectChanged(Date date) {

            }
        })
        .setCancelColor(context.getResources().getColor(R.color.C3))
        .setRangDate(selectedDate, endDate)
        .setType(new boolean[]{true, true, true, true, true, false})
        .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
        .build();

        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);
            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
        mDialog.show();
        return pvTime;
    }

    public String dataToString(Date date, Context context){
        List<String> list= Arrays.asList(context.getResources().getStringArray(R.array.week));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat hours = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(date)+" "+list.get(date.getDay())+" "+hours.format(date);
    }


    /**
     *
     * @param cubeId
     * @return
     */
    @Override
    public boolean isCurrentGroup(String cubeId,String groupId){
        if(cubeId.equals(groupId)){
            return true;
        }else {
            return false;
        }
    }
    /**
     *
     * @param cubeId
     * @return
     */
    @Override
    public boolean isSelf(String cubeId){
        if(cubeId.equals(CubeEngine.getInstance().getSession().getUser().cubeId)){
            return true;
        }else {
            return false;
        }
    }
}
