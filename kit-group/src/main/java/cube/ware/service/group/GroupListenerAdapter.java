package cube.ware.service.group;

import cube.service.CubeError;
import cube.service.group.Group;
import cube.service.group.GroupListener;
import java.util.List;

/**
 * 群监听适配器
 *
 * @author LiuFeng
 * @data 2020/2/8 13:52
 */
public class GroupListenerAdapter implements GroupListener {

    @Override
    public void onGroupFailed(CubeError error) {

    }

    @Override
    public void onGroupCreated(Group group) {

    }

    @Override
    public void onGroupDeleted(Group group) {

    }

    @Override
    public void onMemberAdded(Group group, List<String> addedMembers) {

    }

    @Override
    public void onMemberRemoved(Group group, List<String> removedMembers) {

    }

    @Override
    public void onMasterAdded(Group group, String addedMaster) {

    }

    @Override
    public void onMasterRemoved(Group group, String removedMaster) {

    }

    @Override
    public void onGroupNameChanged(Group group) {

    }
}
