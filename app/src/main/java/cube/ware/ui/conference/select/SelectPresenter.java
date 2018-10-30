package cube.ware.ui.conference.select;

import android.content.Context;
import android.text.TextUtils;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;
import com.common.utils.utils.ThreadUtil;
import com.common.utils.utils.UIHandler;
import com.common.utils.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cube.service.CubeEngine;
import cube.service.common.CubeCallback;
import cube.service.common.model.CubeError;
import cube.service.conference.model.ConferenceConfig;
import cube.service.group.GroupType;
import cube.service.group.model.Group;
import cube.service.group.model.Member;
import cube.service.user.model.User;
import cube.service.whiteboard.model.WhiteboardConfig;
import cube.ware.AppManager;
import cube.ware.data.api.ApiFactory;
import cube.ware.data.api.ResultData;
import cube.ware.data.model.dataModel.TotalData;
import cube.ware.data.repository.CubeUserRepository;
import cube.ware.data.room.AppDataBaseFactory;
import cube.ware.data.room.model.CubeUser;
import cube.ware.utils.SpUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author: kun .
 * date:   On 2018/9/4
 */
public class SelectPresenter  extends SelectContract.Presenter {
    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public SelectPresenter(Context context, SelectContract.View view) {
        super(context, view);
    }

    //从数据库拿到所有的成员列表数据
    @Override
    public void getMemberList(){
        ThreadUtil.request(new Runnable() {
            @Override
            public void run() {
                CubeUserRepository.getInstance().queryAllUser().subscribe(new Action1<List<CubeUser>>() {
                    @Override
                    public void call(List<CubeUser> cubeUsers) {

                        LinkedHashMap<String, CubeUser> mSelectedCubeMap = new LinkedHashMap<>();
                        for (int i = 0; i < cubeUsers.size(); i++) {
                            mSelectedCubeMap.put(cubeUsers.get(i).getCubeId(),cubeUsers.get(i));
                        }
                        UIHandler.run(new Runnable() {
                            @Override
                            public void run() {
                                mView.getCubeIdListSuccess(mSelectedCubeMap,cubeUsers);
                            }
                        });
                    }
                });
            }
        });
    }


    @Override
    public void getMemberFromGroup(String mGroupId){
        CubeEngine.getInstance().getGroupService().queryGroupDetails(mGroupId, new CubeCallback<Group>() {
            @Override
            public void onSucceed(Group group) {
                if(group!=null){
                    //重新组装数据
                    List<Member> members = group.getMembers();
                    List<Member> masters = group.masters;
                    List<CubeUser> cubeUsers=new ArrayList<>();
                    //master
                    for (int i = 0; i < masters.size(); i++) {
                        CubeUser cubeUser=new CubeUser();
                        cubeUser.setAvatar(masters.get(i).avatar);
                        cubeUser.setDisplayName(masters.get(i).displayName);
                        cubeUser.setCubeId(masters.get(i).cubeId);
                        cubeUsers.add(cubeUser);
                    }
                    //member
                    for (int i = 0; i < members.size(); i++) {
                        CubeUser cubeUser=new CubeUser();
                        cubeUser.setAvatar(members.get(i).avatar);
                        cubeUser.setDisplayName(members.get(i).displayName);
                        cubeUser.setCubeId(members.get(i).cubeId);
                        cubeUsers.add(cubeUser);
                    }
                    LinkedHashMap<String, CubeUser> mSelectedCubeMap = new LinkedHashMap<>();
                    for (int i = 0; i < cubeUsers.size(); i++) {
                        mSelectedCubeMap.put(cubeUsers.get(i).getCubeId(),cubeUsers.get(i));
                    }
                    //传值
                    mView.getCubeIdListSuccess(mSelectedCubeMap,cubeUsers);
                }
            }

            @Override
            public void onFailed(CubeError cubeError) {
                LogUtil.e(cubeError.toString());
            }
        });
    }

    @Override
    public ConferenceConfig initConferenceConfig(GroupType groupType, String groupId){
        List<String> master=new ArrayList<>();
        master.add(SpUtil.getCubeId());

        ConferenceConfig conferenceConfig = new ConferenceConfig(groupType, groupId);
        conferenceConfig.isMux = true; //是否融屏幕，传GroupType.VIDEO_CALL时使用
        conferenceConfig.force = false;  //是否强制开启
        conferenceConfig.number = "0"; //创建传0即可,会议号
        if(!TextUtils.isEmpty(groupId)){
            conferenceConfig.bindGroupId = groupId;
        }
        conferenceConfig.maxMember= 9;
        conferenceConfig.startTime= 0;
        conferenceConfig.duration= 0;
        conferenceConfig.autoNotify =false;
        conferenceConfig.maxNumber = 9;
        conferenceConfig.members = master; // 群组的id集合，创建者可以只添加自己的id号
        return conferenceConfig;
    }

    @Override
    public WhiteboardConfig initWhiteboardConfig(GroupType groupType,String mGroupId){
        List<String> master=new ArrayList<>();
        master.add(SpUtil.getCubeId());
        WhiteboardConfig whiteboardConfig=new WhiteboardConfig(groupType,mGroupId);
        whiteboardConfig.maxNumber=9;
        if(!TextUtils.isEmpty(mGroupId)){
            whiteboardConfig.bindGroupId=mGroupId;
        }
        whiteboardConfig.isOpen=true;
        whiteboardConfig.masters=master;
        return whiteboardConfig;
    }

}
