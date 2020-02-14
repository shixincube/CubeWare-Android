package cube.ware.ui.selectMember;

import android.content.Context;
import cube.service.conference.ConferenceConfig;
import cube.ware.core.CubeCore;
import cube.ware.data.repository.CubeUserRepository;
import cube.ware.data.room.model.CubeUser;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * author: kun .
 * date:   On 2018/9/4
 */
public class SelectMemberPresenter extends SelectMemberContract.Presenter {
    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public SelectMemberPresenter(Context context, SelectMemberContract.View view) {
        super(context, view);
    }

    //从数据库拿到所有的成员列表数据
    @Override
    public void getMemberList() {
        CubeUserRepository.getInstance().queryAllUser().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<CubeUser>>() {
            @Override
            public void call(final List<CubeUser> cubeUsers) {
                final LinkedHashMap<String, CubeUser> mSelectedCubeMap = new LinkedHashMap<>();
                for (int i = 0; i < cubeUsers.size(); i++) {
                    mSelectedCubeMap.put(cubeUsers.get(i).getCubeId(), cubeUsers.get(i));
                }

                mView.getCubeIdListSuccess(mSelectedCubeMap, cubeUsers);
            }
        });
    }

    @Override
    public void getMemberFromGroup(String mGroupId) {
        /*CubeEngine.getInstance().getGroupService().queryGroupDetails(mGroupId, new CubeCallback<Group>() {
            @Override
            public void onSucceed(Group group) {
                if (group != null) {
                    //重新组装数据
                    List<String> members = group.getMembers();
                    List<String> masters = group.getMasters();
                    List<CubeUser> cubeUsers = new ArrayList<>();
                    //master
                    for (int i = 0; i < masters.size(); i++) {
                        CubeUser cubeUser = new CubeUser();
                        //cubeUser.setAvatar(masters.get(i).avatar);
                        cubeUser.setDisplayName(masters.get(i));
                        cubeUser.setCubeId(masters.get(i));
                        cubeUsers.add(cubeUser);
                    }
                    //member
                    for (int i = 0; i < members.size(); i++) {
                        CubeUser cubeUser = new CubeUser();
                        //cubeUser.setAvatar(members.get(i).avatar);
                        cubeUser.setDisplayName(members.get(i));
                        cubeUser.setCubeId(members.get(i));
                        cubeUsers.add(cubeUser);
                    }
                    LinkedHashMap<String, CubeUser> mSelectedCubeMap = new LinkedHashMap<>();
                    for (int i = 0; i < cubeUsers.size(); i++) {
                        mSelectedCubeMap.put(cubeUsers.get(i).getCubeId(), cubeUsers.get(i));
                    }
                    //传值
                    mView.getCubeIdListSuccess(mSelectedCubeMap, cubeUsers);
                }
            }

            @Override
            public void onFailed(CubeError cubeError) {
                LogUtil.e(cubeError.toString());
            }
        });*/
    }

    @Override
    public ConferenceConfig initConferenceConfig(String groupId) {
        List<String> master = new ArrayList<>();
        master.add(CubeCore.getInstance().getCubeId());

        ConferenceConfig conferenceConfig = new ConferenceConfig();
        conferenceConfig.groupId = groupId;
        conferenceConfig.isMux = true; //是否融屏幕，传GroupType.VIDEO_CALL时使用
        conferenceConfig.force = false;  //是否强制开启
        conferenceConfig.number = "0"; //创建传0即可,会议号
        conferenceConfig.maxMember = 9;
        conferenceConfig.cubeIds = master; // 群组的id集合，创建者可以只添加自己的id号
        return conferenceConfig;
    }
}
