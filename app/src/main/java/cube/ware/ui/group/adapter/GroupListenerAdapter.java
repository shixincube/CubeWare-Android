package cube.ware.ui.group.adapter;

import java.util.List;

import cube.service.common.model.CubeError;
import cube.service.group.GroupListener;
import cube.service.group.model.Group;
import cube.service.user.model.User;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/28.
 */

public  class GroupListenerAdapter implements GroupListener{
    @Override
    public void onGroupCreated(Group group, User user) {

    }

    @Override
    public void onGroupDestroyed(Group group, User user) {

    }

    @Override
    public void onGroupQuited(Group group, User from) {

    }

    @Override
    public void onGroupUpdated(Group group, User user) {

    }

    @Override
    public void onMemberAdded(Group group, User user, List<User> list) {

    }

    @Override
    public void onMemberRemoved(Group group, User user, List<User> list) {

    }

    @Override
    public void onMasterAdded(Group group, User user, List<User> list) {

    }

    @Override
    public void onMasterRemoved(Group group, User user, List<User> list) {

    }

    @Override
    public void onGroupApplied(Group group, User user, User user1) {

    }

    @Override
    public void onGroupApplyJoined(Group group, User user, User user1) {

    }

    @Override
    public void onGroupRejectApplied(Group group, User user, User user1) {

    }

    @Override
    public void onGroupInvited(Group group, User user, List<User> list) {

    }

    @Override
    public void onGroupRejectInvited(Group group, User user, User user1) {

    }

    @Override
    public void onGroupInviteJoined(Group group, User user, User user1) {

    }

    @Override
    public void onGroupFailed(Group group, CubeError cubeError) {

    }
}
