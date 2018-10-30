package cube.ware.service.group;

import android.text.TextUtils;

import com.common.mvp.rx.RxBus;
import com.common.utils.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.group.GroupListener;
import cube.service.group.model.Group;
import cube.service.message.model.CustomMessage;
import cube.service.message.model.Receiver;
import cube.service.message.model.Sender;
import cube.service.user.model.User;
import cube.ware.data.model.dataModel.enmu.CubeCustomMessageType;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.eventbus.CubeEvent;
import cube.ware.manager.MessageManager;
import cube.ware.ui.group.adapter.GroupListenerAdapter;
import cube.ware.ui.recent.manager.RecentSessionManager;
import cube.ware.utils.SpUtil;

/**
 * 引擎群组服务处理
 *
 * @author LiuFeng
 * @date 2018-8-09
 */
public class GroupHandle implements GroupListener {

    private static GroupHandle instance = new GroupHandle();
    List<GroupListenerAdapter> mGroupListenerAdapters = new ArrayList<>();

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

    public void addGroupListener(GroupListenerAdapter groupListenerAdapter) {
        if (groupListenerAdapter != null && !mGroupListenerAdapters.contains(groupListenerAdapter)) {
            mGroupListenerAdapters.add(groupListenerAdapter);
        }
    }

    public void removeGroupListener(GroupListenerAdapter groupListenerAdapter) {
        if (groupListenerAdapter != null && mGroupListenerAdapters.contains(groupListenerAdapter)) {
            mGroupListenerAdapters.remove(groupListenerAdapter);
        }
    }

    public void clearGroupListener() {
        if (mGroupListenerAdapters != null) {
            mGroupListenerAdapters.clear();
        }
    }

    /**
     * 创建群组成功时回调
     *
     * @param group 群组实体
     * @param from  群组创建者
     */
    @Override
    public void onGroupCreated(Group group, User from) {
        LogUtil.i("onGroupCreated: ------群组创建" );
        for (GroupListenerAdapter groupListenerAdapter : mGroupListenerAdapters) {
            groupListenerAdapter.onGroupCreated(group,from);
        }
    }

    /**
     * 当群组销毁时回调
     *
     * @param group 群组实体
     * @param from  群组销毁者
     */
    @Override
    public void onGroupDestroyed(Group group, User from) {
        LogUtil.i("onGroupDestroyed: ------群组销毁" );
        RecentSessionManager.getInstance().removeRecentSession(group.groupId);
        for (GroupListenerAdapter groupListenerAdapter : mGroupListenerAdapters) {
            groupListenerAdapter.onGroupDestroyed(group,from);
        }
    }

    @Override
    public void onGroupQuited(Group group, User from) {
        LogUtil.i("onGroupQuited: ------群组退出" );
        if (TextUtils.equals(from.cubeId, SpUtil.getCubeId())) {
            //如果是自己的退出 删除最近会话
            RecentSessionManager.getInstance().removeRecentSession(group.groupId);
        }
        for (GroupListenerAdapter groupListenerAdapter : mGroupListenerAdapters) {
            groupListenerAdapter.onGroupQuited(group,from);
        }
    }

    /**
     * 群组数据更新回调(修改群昵称，头像，拥有者，开关群组，绑定群组ID信息)
     *
     * @param group 群组实体
     * @param from  群组更新者
     */
    @Override
    public void onGroupUpdated(Group group, User from) {
        LogUtil.i("onGroupUpdated: ------群组更新" );
        for (GroupListenerAdapter groupListenerAdapter : mGroupListenerAdapters) {
            groupListenerAdapter.onGroupUpdated(group,from);
        }

        //发送更改群名自定义消息
        Sender sender;
        if (from != null && !TextUtils.equals(from.cubeId, SpUtil.getCubeId())) {
            sender = new Sender(from.cubeId, from.displayName);
        } else {
            sender = new Sender(SpUtil.getCubeId(), SpUtil.getUserName());
        }
        CustomMessage customMessage = MessageManager.getInstance().buildCustomMessage(CubeSessionType.Group, sender, new Receiver(group.groupId, group.displayName), "");
        customMessage.setHeader("operate", CubeCustomMessageType.UpdateGroupName.type);
        customMessage.setReceipted(true);
        MessageManager.getInstance().addMessageInLocal(customMessage).subscribe();
        RxBus.getInstance().post(CubeEvent.EVENT_UPDATE_GROUP,group.displayName);
    }

    /**
     * 当组添加成员时回调
     *
     * @param group        群组实体
     * @param from         添加者
     * @param addedMembers 被添加列表
     */
    @Override
    public void onMemberAdded(Group group, User from, List<User> addedMembers) {
        LogUtil.i("onMemberAdded: ------添加群成员" );
        for (GroupListenerAdapter groupListenerAdapter : mGroupListenerAdapters) {
            groupListenerAdapter.onMemberAdded(group,from,addedMembers);
        }
    }

    /**
     * 当组移除成员时回调
     *
     * @param group          群组实体
     * @param from           移除者
     * @param removedMembers 被移除列表
     */
    @Override
    public void onMemberRemoved(Group group, User from, List<User> removedMembers) {
        LogUtil.i("onMemberRemoved: ------删除群成员" );
        for (GroupListenerAdapter groupListenerAdapter : mGroupListenerAdapters) {
            groupListenerAdapter.onMemberRemoved(group,from,removedMembers);
        }
    }

    /**
     * 当组添加管理员时回调
     *
     * @param group        群组实体
     * @param from         添加者
     * @param addedMasters 被添加管理员列表
     */
    @Override
    public void onMasterAdded(Group group, User from, List<User> addedMasters) {
        LogUtil.i("onMasterAdded: ------添加管理员" );
    }

    /**
     * 当组移除管理员时回调
     *
     * @param group          群组实体
     * @param from           移除者
     * @param removedMasters 被移除管理员列表
     */
    @Override
    public void onMasterRemoved(Group group, User from, List<User> removedMasters) {
        LogUtil.i("onMasterRemoved: ------删除管理员" );
    }

    /**
     * 有人申请入群时回调，仅群主和管理员（主持人）收到申请回调
     *
     * @param group   群组实体
     * @param from    发起者
     * @param applier 申请者
     */
    @Override
    public void onGroupApplied(Group group, User from, User applier) {
        LogUtil.i("onGroupApplied: ------申请入群" );
    }

    /**
     * 同意申请加入时回调
     *
     * @param group   群组实体
     * @param from    同意者
     * @param applier 申请者
     */
    @Override
    public void onGroupApplyJoined(Group group, User from, User applier) {
        LogUtil.i("onGroupApplyJoined: ------同意申请入群" );
    }

    /**
     * 拒绝申请者入群回调（仅申请者和拒绝者收到）
     *
     * @param group   群组实体
     * @param from    拒绝者
     * @param applier 申请者
     */
    @Override
    public void onGroupRejectApplied(Group group, User from, User applier) {
        LogUtil.i("onGroupRejectApplied: ------拒绝申请入群" );
    }

    /**
     * 收到邀请回调(仅邀请者自己和被邀请者收到)
     *
     * @param group   群组实体
     * @param from    邀请者
     * @param invites 被邀请列表
     */
    @Override
    public void onGroupInvited(Group group, User from, List<User> invites) {
        LogUtil.i("onGroupInvited: ------收到邀请入群" );
    }

    /**
     * 拒绝要邀请入群回调
     *
     * @param group        群组实体
     * @param from         邀请者
     * @param rejectMember 拒绝者
     */
    @Override
    public void onGroupRejectInvited(Group group, User from, User rejectMember) {
        LogUtil.i("onGroupRejectInvited: ------拒绝邀请入群" );
    }

    /**
     * 同意邀请加入时回调
     *
     * @param group        群组实体
     * @param from         邀请者
     * @param joinedMember 同意者
     */
    @Override
    public void onGroupInviteJoined(Group group, User from, User joinedMember) {
        LogUtil.i("onGroupRejectInvited: ------同意邀请入群" );
    }

    /**
     * 出现错误时回调
     *
     * @param group 群组实体
     * @param error 错误信息
     */
    @Override
    public void onGroupFailed(Group group, CubeError error) {
        LogUtil.i("onGroupFailed: ------" );
        for (GroupListenerAdapter groupListenerAdapter : mGroupListenerAdapters) {
            groupListenerAdapter.onGroupFailed(group,error);
        }
    }
}
