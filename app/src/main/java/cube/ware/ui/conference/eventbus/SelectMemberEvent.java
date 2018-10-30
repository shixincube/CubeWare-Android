package cube.ware.ui.conference.eventbus;

import java.util.List;

import cube.ware.data.room.model.CubeUser;

/**
 * author: kun .
 * des：选择会议成员的event
 * date:   On 2018/9/5
 */
public class SelectMemberEvent {
    List<CubeUser> mCubeUserList;

    public SelectMemberEvent(List<CubeUser> cubeUserList) {
        mCubeUserList = cubeUserList;
    }

    public List<CubeUser> getCubeUserList() {
        return mCubeUserList;
    }

    public void setCubeUserList(List<CubeUser> cubeUserList) {
        mCubeUserList = cubeUserList;
    }
}
