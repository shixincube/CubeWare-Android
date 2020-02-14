package cube.ware.service.whiteboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.common.eventbus.EventBusUtil;
import com.common.sdk.RouterUtil;
import com.common.utils.RingtoneUtil;
import com.common.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.group.model.Member;
import cube.service.user.model.User;
import cube.service.whiteboard.WhiteboardListener;
import cube.service.whiteboard.model.Whiteboard;
import cube.service.whiteboard.model.WhiteboardSlide;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.whiteboard.manager.WBCallManager;
import java.util.ArrayList;
import java.util.List;

/**
 * author: kun .
 * date:   On 2018/8/29
 */
public class WhiteBoardHandle implements WhiteboardListener {

    private static WhiteBoardHandle instance = new WhiteBoardHandle();

    private List<WhiteBoardStateListener> mWhiteBoardStateListeners = new ArrayList<>();

    private WhiteBoardHandle() {}

    /**
     * 单例
     *
     * @return
     */
    public static WhiteBoardHandle getInstance() {
        return instance;
    }

    /**
     * 启动监听
     *
     * @param
     */
    public void start() {
        CubeEngine.getInstance().getWhiteboardService().addWhiteboardListener(this);
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().getWhiteboardService().removeWhiteboardListener(this);
    }

    /**
     * 添加用户状态监听器
     *
     * @param listener
     */
    public void addWhiteBoardStateListeners(WhiteBoardStateListener listener) {
        if (listener != null && !mWhiteBoardStateListeners.contains(listener)) {
            mWhiteBoardStateListeners.add(listener);
        }
    }

    /**
     * 删除用户状态监听器
     *
     * @param listener
     */
    public void removeWhiteBoardStateListeners(WhiteBoardStateListener listener) {
        if (listener != null && mWhiteBoardStateListeners.contains(listener)) {
            mWhiteBoardStateListeners.remove(listener);
        }
    }

    /**
     * 白板创建
     *
     * @param whiteboard 白板实体
     * @param user
     */
    @Override
    public void onWhiteboardCreated(Whiteboard whiteboard, User user) {
        if (WBCallManager.getInstance().isCalling()) {
            return;
        }
        List<String> mGroupIds = new ArrayList<>();
        if (whiteboard.maxNumber != 2 && null != whiteboard.bindGroupId) {
            mGroupIds.add(whiteboard.bindGroupId);
            EventBusUtil.post(CubeConstants.Event.UpdateWhiteBoardTipView, mGroupIds);
        }
        //没有白板
        if (!WBCallManager.getInstance().isCalling()) {
            for (int i = 0; i < mWhiteBoardStateListeners.size(); i++) {
                mWhiteBoardStateListeners.get(i).onWhiteboardCreated(whiteboard, user);
            }
            if (user.cubeId.endsWith(CubeCore.getInstance().getCubeId())) {
                WBCallManager.getInstance().setCalling(true);
            }
        }
    }

    /**
     * 白板销毁
     *
     * @param whiteboard 白板实体
     * @param user
     */
    @Override
    public void onWhiteboardDestroyed(Whiteboard whiteboard, User user) {
        LogUtil.d("===回调了onWhiteboardDestroyed" + whiteboard.getMembers().size());
        RingtoneUtil.release();
        List<String> mGroupIds = new ArrayList<>();
        if (whiteboard.maxNumber != 2 && null != whiteboard.bindGroupId) {
            mGroupIds.add(whiteboard.bindGroupId);
            EventBusUtil.post(CubeConstants.Event.UpdateWhiteBoardTipView, mGroupIds);
        }
        for (int i = 0; i < mWhiteBoardStateListeners.size(); i++) {
            mWhiteBoardStateListeners.get(i).onWhiteboardDestroyed(whiteboard, user);
        }
    }

    /**
     * 收到白板邀请
     *
     * @param whiteboard 白板实体
     * @param from       邀请者
     * @param list
     */
    @Override
    public void onWhiteboardInvited(Whiteboard whiteboard, User from, List<User> list) {
        LogUtil.i("Whiteboard---->> " + whiteboard.toString());
        //一对一消息
        if (whiteboard.maxNumber == 2) {
            List<Member> invite = whiteboard.invites;
            if (invite.size() >= 1) {
                //MessageManager.getInstance().sendP2PWBCreateMessage(from, invite.get(0));
            }
        }
        if (!from.cubeId.equals(CubeCore.getInstance().getCubeId())) {
            if (!WBCallManager.getInstance().isCalling() && !CubeCore.getInstance().isCalling()) { //没有正在白板
                WBCallManager.getInstance().setCalling(true);

                int CALLSTATE_INVITE = 1;
                int CALLSTATE_CREATE = 2;
                int CALLSTATE_JOIN = 3;
                String WHITEBOARD = "whiteboard";
                String INVITE_LIST = "invite_list";
                String INVITE_ID = "invite_id";
                String GROUP_ID = "group_id";
                String CHAT_TYPE = "chat_type";
                String CALLSTATA_WHITE_BOARD = "callState_white_board";

                Bundle bundle = new Bundle();
                bundle.putInt(CALLSTATA_WHITE_BOARD, CALLSTATE_INVITE);
                bundle.putSerializable(WHITEBOARD, whiteboard);
                bundle.putString(INVITE_ID, from.cubeId);
                // todo 如何区分多人白板、不依赖白板、和一对一白板
                if (whiteboard.maxNumber == 2) {//maxNumber=2 就是单聊，创建的时候传入的就是2
                    //单聊
                    bundle.putSerializable(CHAT_TYPE, CubeSessionType.P2P);
                    bundle.putString(GROUP_ID, whiteboard.bindGroupId);  //
                }
                else if (TextUtils.isEmpty(whiteboard.bindGroupId)) {   //不依赖群的多人白板（单聊也没有群概念）
                    bundle.putSerializable(CHAT_TYPE, CubeSessionType.Group);
                    bundle.putString(GROUP_ID, "");  //邀请了自己，并获得group_id
                }
                else {//群聊
                    bundle.putSerializable(CHAT_TYPE, CubeSessionType.Group);
                    bundle.putString(GROUP_ID, whiteboard.bindGroupId);  //邀请了自己，并获得group_id
                }
                RingtoneUtil.play(R.raw.ringing, CubeCore.getContext());
                RouterUtil.navigation(CubeConstants.Router.WhiteBoardActivity, bundle);
            }
            else {
                CubeEngine.getInstance().getWhiteboardService().rejectInvite(whiteboard.whiteboardId, from.cubeId);
            }
        }
        else {
            WBCallManager.getInstance().setCalling(true);
            //都应该回调
            //            if(!WBCallManager.getInstance().isCalling() && !OneOnOneCallManager.getInstance().isCalling() && !ConferenceCallManager.getInstance().isCalling()) { //没有正在白板
            for (int i = 0; i < mWhiteBoardStateListeners.size(); i++) {
                mWhiteBoardStateListeners.get(i).onWhiteboardInvited(whiteboard, from, list);
            }
            //            }
        }
    }

    /**
     * 拒绝邀请
     *
     * @param whiteboard 白板实体
     * @param from       邀请者
     * @param rejectUser
     */
    @Override
    public void onWhiteboardRejectInvited(Whiteboard whiteboard, User from, User rejectUser) {
        for (int i = 0; i < mWhiteBoardStateListeners.size(); i++) {
            mWhiteBoardStateListeners.get(i).onWhiteboardRejectInvited(whiteboard, from, rejectUser);
        }
        //一对一退出
        if (whiteboard.maxNumber == 2) {
            //MessageManager.getInstance().sendP2PWBDestoryMessage(rejectUser, from);
        }
        RingtoneUtil.release();
    }

    /**
     * 接受邀请
     *
     * @param whiteboard 白板实体
     * @param from       邀请者
     * @param inviteUser
     */
    @Override
    public void onWhiteboardAcceptInvited(Whiteboard whiteboard, User from, User inviteUser) {
        //白板中
        if (inviteUser.cubeId.equals(CubeEngine.getInstance().getSession().getUser().cubeId)) {
            WBCallManager.getInstance().setCalling(true);
        }
        for (int i = 0; i < mWhiteBoardStateListeners.size(); i++) {
            mWhiteBoardStateListeners.get(i).onWhiteboardAcceptInvited(whiteboard, from, inviteUser);
        }
        RingtoneUtil.release();
    }

    /**
     * 加入成功回调，邀请者和被邀请者都会收到
     *
     * @param whiteboard 白板实体
     * @param user
     */
    @Override
    public void onWhiteboardJoined(Whiteboard whiteboard, User user) {
        List<String> mGroupIds = new ArrayList<>();
        if (whiteboard.maxNumber != 2 && null != whiteboard.bindGroupId) {
            mGroupIds.add(whiteboard.bindGroupId);
            EventBusUtil.post(CubeConstants.Event.UpdateWhiteBoardTipView, mGroupIds);
        }
        LogUtil.i("===Whiteboard join---->>" + whiteboard.getMembers());
        for (int i = 0; i < mWhiteBoardStateListeners.size(); i++) {
            mWhiteBoardStateListeners.get(i).onWhiteboardJoined(whiteboard, user);
        }
    }

    /**
     * 退出白板，邀请者和别邀请都会收到
     *
     * @param whiteboard 白板实体
     * @param user
     */
    @Override
    public void onWhiteboardQuited(Whiteboard whiteboard, User user) {
        LogUtil.d("===回调了onWhiteboardQuited" + whiteboard.getMembers().size());
        RingtoneUtil.release();
        List<String> mGroupIds = new ArrayList<>();
        if (whiteboard.maxNumber != 2 && null != whiteboard.bindGroupId) {
            mGroupIds.add(whiteboard.bindGroupId);
            EventBusUtil.post(CubeConstants.Event.UpdateWhiteBoardTipView, mGroupIds);
        }
        //一对一退出
        if (whiteboard.maxNumber == 2) {
            //获取到唯一的白板里的对象
            Member member = getMember(whiteboard);
            if (member != null) {
                if (user.cubeId.equals(CubeCore.getInstance().getCubeId())) { //user是自己，发给别人
                    //MessageManager.getInstance().sendP2PWBDestoryMessage(user, member);
                }
                else { //自己发给别人user是别人
                    //User userSelf = GsonUtil.toBean(SpUtil.getUserJson(), User.class);
                    //MessageManager.getInstance().sendP2PWBDestoryMessage(userSelf, user);
                }
            }
        }
        if (user.cubeId.equals(CubeEngine.getInstance().getSession().getUser().cubeId)) {
            WBCallManager.getInstance().restCalling();
        }
        for (int i = 0; i < mWhiteBoardStateListeners.size(); i++) {
            mWhiteBoardStateListeners.get(i).onWhiteboardQuited(whiteboard, user);
        }
    }

    @Nullable
    private Member getMember(Whiteboard whiteboard) {
        List<Member> members = whiteboard.members;
        List<Member> invites = whiteboard.invites;
        Member member = null;
        if (members != null && members.size() > 0) {
            member = members.get(0);
        }
        else if (invites != null && invites.size() > 0) {
            member = invites.get(0);
        }
        return member;
    }

    @Override
    public void onSlideUploading(Whiteboard whiteboard, WhiteboardSlide whiteboardSlide, long l, long l1) {
        for (int i = 0; i < mWhiteBoardStateListeners.size(); i++) {
            mWhiteBoardStateListeners.get(i).onSlideUploading(whiteboard, whiteboardSlide, l, l1);
        }
    }

    @Override
    public void onSlideUploadCompleted(Whiteboard whiteboard, WhiteboardSlide whiteboardSlide) {
        for (int i = 0; i < mWhiteBoardStateListeners.size(); i++) {
            mWhiteBoardStateListeners.get(i).onSlideUploadCompleted(whiteboard, whiteboardSlide);
        }
    }

    @Override
    public void onSlideUpdated(Whiteboard whiteboard, WhiteboardSlide whiteboardSlide) {
        for (int i = 0; i < mWhiteBoardStateListeners.size(); i++) {
            mWhiteBoardStateListeners.get(i).onSlideUpdated(whiteboard, whiteboardSlide);
        }
    }

    @Override
    public void onWhiteboardFailed(Whiteboard whiteboard, CubeError cubeError) {
        for (int i = 0; i < mWhiteBoardStateListeners.size(); i++) {
            mWhiteBoardStateListeners.get(i).onWhiteboardFailed(whiteboard, cubeError);
        }
    }
}
