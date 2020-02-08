package cube.ware.service.group;

import com.common.mvp.eventbus.EventBusUtil;
import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.group.Group;
import cube.service.group.GroupListener;
import cube.ware.core.CubeConstants;
import java.util.List;

/**
 * 引擎群组服务处理
 *
 * @author LiuFeng
 * @date 2018-8-09
 */
public class GroupHandle implements GroupListener {

    private static GroupHandle instance = new GroupHandle();

    private GroupHandle() {}

    /**
     * 单例
     *
     * @return
     */
    public static GroupHandle getInstance() {
        return instance;
    }

    /**
     * 启动监听
     */
    public void start() {
        CubeEngine.getInstance().getGroupService().addGroupListener(this);
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().getGroupService().removeGroupListener(this);
    }

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
        //发送更改群名自定义消息
        EventBusUtil.post(CubeConstants.Event.UPDATE_GROUP, group.getDisplayName());
    }
}
