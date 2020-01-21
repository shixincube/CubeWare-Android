package cube.ware.service.sharedesktop;

import android.content.Context;
import com.common.utils.utils.RingtoneUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.sharedesktop.ShareDesktopExtListener;
import cube.service.sharedesktop.ShareDesktopListener;
import cube.service.sharedesktop.model.ShareDesktop;
import cube.service.user.model.User;
import cube.ware.core.CubeCore;
import java.util.ArrayList;
import java.util.List;

/**
 * 引擎远程桌面服务处理
 *
 * @author LiuFeng
 * @date 2018-8-09
 */
public class ShareDesktopHandle implements ShareDesktopExtListener {

    private static ShareDesktopHandle            instance                  = new ShareDesktopHandle();
    private        List<ShareDesktopExtListener> mShareDesktopExtListeners = new ArrayList<>();
    private        Context                       mContext;

    private ShareDesktopHandle() {}

    public static ShareDesktopHandle getInstance() {
        return instance;
    }

    /**
     * 启动监听
     */
    public void start() {
        mContext = CubeCore.getContext();
        CubeEngine.getInstance().getShareDesktopService().addShareDesktopListener(this);
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().getShareDesktopService().removeShareDesktopListener(this);
    }

    /**
     * 添加一个共享屏幕监听
     *
     * @param listener
     */
    public void addShareDesktipListener(ShareDesktopExtListener listener) {
        mShareDesktopExtListeners.add(listener);
    }

    public void removeShareDesktipListener(ShareDesktopExtListener listener) {
        mShareDesktopExtListeners.remove(listener);
    }

    /**
     * 当桌面创建时回调
     *
     * @param sd       共享消息实体
     * @param fromUser 创建者user实体
     */
    @Override
    public void onShareDesktopCreated(ShareDesktop sd, User fromUser) {
        LogUtil.i("共享屏幕创建回调，当绑定群组===> onShareDesktopCreated" + sd.bindGroupId);
        // 播放来电铃声
        RingtoneUtil.play(R.raw.ringing, mContext);
        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
            for (ShareDesktopListener listener : this.mShareDesktopExtListeners) {
                if (listener != null) {
                    listener.onShareDesktopCreated(sd, fromUser);
                }
            }
        }
    }

    /**
     * 当桌面共享销毁时回调
     * 这个为什么会有一个邀请者实体
     *
     * @param sd       共享消息实体
     * @param fromUser 邀请者user实体
     */
    @Override
    public void onShareDesktopDestroyed(ShareDesktop sd, User fromUser) {
        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
            for (ShareDesktopListener listener : this.mShareDesktopExtListeners) {
                if (listener != null) {
                    listener.onShareDesktopDestroyed(sd, fromUser);
                }
            }
        }
    }

    /**
     * 申请加入时回调(仅管理员或群组收到)
     *
     * @param sd            共享消息实体
     * @param appliedMember 申请者user实体
     */
    @Override
    public void onShareDesktopApplied(ShareDesktop sd, User appliedMember) {

    }

    /**
     * 同意申请加入时回调
     * 应该是差一个同意申请的指令通知
     *
     * @param sd            共享消息实体
     * @param fromUser      同意申请者user实体
     * @param appliedMember 申请者user实体
     */
    @Override
    public void onShareDesktopAcceptApplied(ShareDesktop sd, User fromUser, User appliedMember) {

    }

    /**
     * 拒绝申请加入时回调
     *
     * @param sd            共享消息实体
     * @param fromUser      拒绝申请者user实体
     * @param appliedMember 申请者user实体
     */
    @Override
    public void onShareDesktopRejectApplied(ShareDesktop sd, User fromUser, User appliedMember) {

    }

    /**
     * 同意申请加入时回调
     * A邀请B，C同意B加入了，A也要收到通知
     *
     * @param sd       共享消息实体
     * @param fromUser 邀请者user实体
     */
    @Override
    public void onShareDesktopApplyJoined(ShareDesktop sd, User fromUser, User joinedMember) {

    }

    /**
     * 当连接上桌面共享时回调
     *
     * @param sd
     * @param srcCubeID    共享者CubeID
     * @param targetCubeID {String} 成功连接的目标用户CubeID
     */
    @Override
    public void onShareDesktopConnectShared(ShareDesktop sd, String srcCubeID, String targetCubeID) {
        LogUtil.d("====onShareDesktopConnectShared: ---- 桌面分享连接成功" + sd.desktopId);
        //桌面共享连接成功
        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
            for (ShareDesktopListener listener : this.mShareDesktopExtListeners) {
                if (listener != null) {
                    listener.onShareDesktopConnectShared(sd, srcCubeID, targetCubeID);
                }
            }
        }
    }

    /**
     * 当离开桌面共享时回调
     *
     * @param sd       共享桌面消息实体
     * @param fromUser 离开者user实体
     */
    @Override
    public void onShareDesktopQuited(ShareDesktop sd, User fromUser) {
        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
            for (ShareDesktopListener listener : this.mShareDesktopExtListeners) {
                if (listener != null) {
                    listener.onShareDesktopQuited(sd, fromUser);
                }
            }
        }
    }

    /**
     * 收到邀请回调(仅邀请者自己和被邀请者回调)
     *
     * @param sd       共享桌面对象实例
     * @param fromUser 邀请者user实体
     */
    public void onShareDesktopInvited(ShareDesktop sd, User fromUser) {

        //        LogUtil.d("====收到邀请 --- " + sd.desktopId);
        //        //需要验证此处通知的对象是所有群成员还是别邀请的群成员
        //        ShareDesketopManager.getInstance().saveShareDesktop(sd);
        //        Activity activity = ActivityManager.getInstance().findActivity(ShareScreenActivity.class);
        //        LogUtil.d("===onShareDesktopInvited: --- activity --- " + activity);
        //        for (Member invite : sd.invites) {
        //            if (TextUtils.equals(invite.cubeId, CubeEngine.getInstance().getSession().user.cubeId) && activity == null) {
        //                //收到远程桌面邀请回调 并且自己在被邀请者集合里面 并且桌面activity没被启动过
        //                Bundle bundle = new Bundle();
        //                bundle.putSerializable("shaerdesketop",sd);
        //                bundle.putString("inviteId",fromUser.cubeId);
        //                bundle.putSerializable("statues", CallStatus.REMOTE_DESKTOP_INCOMING);
        //                ARouter.getInstance().build(AppConstants.Router.P2PCallActivity).withBundle("desketop_data",bundle).navigation();
        //                // 播放来电铃声
        //                RingtoneUtil.play(R.raw.ringing, mContext);
        //                break;
        //            }
        //        }
        //
        //        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
        //            for (ShareDesktopListener listener : this.mShareDesktopExtListeners) {
        //                if (listener != null) {
        //                    listener.onShareDesktopInvited(sd, fromUser);
        //                }
        //            }
        //        }

    }

    /**
     * 同意邀请加入时回调
     *
     * @param sd            共享桌面对象实例
     * @param fromUser      邀请者user实体
     * @param joinedMembers 加入者user实体
     */
    public void onShareDesktopInviteJoined(ShareDesktop sd, User fromUser, User joinedMembers) {
        LogUtil.d("onShareDesktopInviteJoined:=== 同意邀请回调 --- ");
        //同意邀请 桌面共享
        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
            for (ShareDesktopListener listener : this.mShareDesktopExtListeners) {
                if (listener != null) {
                    listener.onShareDesktopInviteJoined(sd, fromUser, joinedMembers);
                }
            }
        }
    }

    /**
     * 当拒绝共享桌面时回调
     *
     * @param sd           共享桌面对象实例
     * @param fromUser     邀请者user实体
     * @param rejectMember 拒绝者user实体
     */
    public void onShareDesktopRejectInvited(ShareDesktop sd, User fromUser, User rejectMember) {
        LogUtil.d("===onShareDesktopRejectInvited: --- 拒绝邀请回调");
        //拒绝邀请 桌面共享
        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
            for (ShareDesktopListener listener : this.mShareDesktopExtListeners) {
                if (listener != null) {
                    listener.onShareDesktopRejectInvited(sd, fromUser, rejectMember);
                }
            }
        }
    }

    /**
     * 共享桌面出错回调
     *
     * @param error 错误信息
     */
    @Override
    public void onShareDesktopFailed(CubeError error) {
        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
            for (ShareDesktopListener listener : this.mShareDesktopExtListeners) {
                if (listener != null) {
                    listener.onShareDesktopFailed(error);
                }
            }
        }
    }

    /**
     * 分享桌面回调
     *
     * @param rd
     */
    @Deprecated
    @Override
    public void onRemoteDesktopShared(ShareDesktop rd) {

    }

    /**
     * 撤销分享桌面回调
     *
     * @param rd
     */
    @Deprecated
    @Override
    public void onRemoteDesktopRevoked(ShareDesktop rd) {

    }

    /**
     * 数据加载中
     */
    @Deprecated
    @Override
    public void onRemoteDesktopLoading() {

    }

    /**
     * 画面播放
     */
    @Deprecated
    @Override
    public void onRemoteDesktopPlay() {

    }

    @Override
    public void onShareDesktopCreatedOther(ShareDesktop shareDesktop, User user) {

    }

    @Override
    public void onShareDesktopDestroyedOther(ShareDesktop shareDesktop, User user) {

    }

    @Override
    public void onShareDesktopAcceptAppliedOther(ShareDesktop shareDesktop, User user, User user1) {

    }

    @Override
    public void onShareDesktopRejectAppliedOther(ShareDesktop shareDesktop, User user, User user1) {

    }

    @Override
    public void onShareDesktopApplyJoinedOther(ShareDesktop shareDesktop, User user, User user1) {

    }

    /**
     * 其他终端退出同步
     *
     * @param shareDesktop
     * @param fromUser
     */
    @Override
    public void onShareDesktopQuitedOther(ShareDesktop shareDesktop, User fromUser) {
        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
            for (ShareDesktopExtListener listener : this.mShareDesktopExtListeners) {
                if (listener != null) {
                    listener.onShareDesktopQuitedOther(shareDesktop, fromUser);
                }
            }
        }
    }

    /**
     * 其他 终端收到邀请同步
     *
     * @param shareDesktop
     * @param fromUser
     */
    @Override
    public void onShareDesktopInvitedOther(ShareDesktop shareDesktop, User fromUser) {
        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
            for (ShareDesktopExtListener listener : this.mShareDesktopExtListeners) {
                if (listener != null) {
                    listener.onShareDesktopInvitedOther(shareDesktop, fromUser);
                }
            }
        }
    }

    /**
     * 其他终端同意加入同步
     *
     * @param shareDesktop
     * @param fromUser
     * @param joinedMembers
     */
    @Override
    public void onShareDesktopInviteJoinedOther(ShareDesktop shareDesktop, User fromUser, User joinedMembers) {
        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
            for (ShareDesktopExtListener listener : this.mShareDesktopExtListeners) {
                if (listener != null) {
                    listener.onShareDesktopInviteJoinedOther(shareDesktop, fromUser, joinedMembers);
                }
            }
        }
    }

    /**
     * 其他终端拒绝邀请同步
     *
     * @param shareDesktop
     * @param fromUser
     * @param rejectMember
     */
    @Override
    public void onShareDesktopRejectInvitedOther(ShareDesktop shareDesktop, User fromUser, User rejectMember) {
        if (this.mShareDesktopExtListeners != null && !this.mShareDesktopExtListeners.isEmpty()) {
            for (ShareDesktopExtListener listener : this.mShareDesktopExtListeners) {
                if (listener != null) {
                    listener.onShareDesktopRejectInvitedOther(shareDesktop, fromUser, rejectMember);
                }
            }
        }
    }
}
