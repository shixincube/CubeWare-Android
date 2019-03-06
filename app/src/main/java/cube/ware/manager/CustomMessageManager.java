package cube.ware.manager;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import cube.service.message.model.CustomMessage;
import cube.service.message.model.Receiver;
import cube.service.message.model.Sender;
import cube.ware.CubeUI;
import cube.ware.R;
import cube.ware.data.model.dataModel.enmu.CubeCustomMessageType;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.utils.FileUtil;
import cube.ware.utils.SpUtil;


/**
 * 系统消息描述文本构造器。主要是将各个系统消息转换为显示的文本内容。
 *
 * @author Wangxx
 * @date 2017/3/9
 */
public class CustomMessageManager {

    public static boolean getCustomText(CubeMessage cubeMessage, CustomMessage message, String type, boolean isSync) {
        String text = buildText(cubeMessage, message, type, isSync);
        if (!TextUtils.isEmpty(text)) {
            cubeMessage.setContent(text);
            return true;
        }
        else {
            return false;
        }
    }

    private static String buildText(CubeMessage cubeMessage, CustomMessage message, String type, boolean isSync) {
        String text = null;
        String groupCube = getGroupCube(message);
        String token = message.getHeader("token");
        if (type.equals(CubeCustomMessageType.AddFriend.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildAddFriendNotification(message);
//            if (CubeUI.getInstance().getFriendOperationListener() != null && !isSync) {
//                String adverse = CubeSpUtil.getCubeUser().getCubeId().equals(message.getHeader("acceptUserCube")) ? message.getHeader("applyUserCube") : message.getHeader("acceptUserCube");//对方cube
//                CubeUI.getInstance().getFriendOperationListener().agree(adverse, token);
//                RxBus.getInstance().post(CubeEvent.EVENT_ADD_OR_DELETE_FRIEND, new EventAddOrDeleteFriendEventModel(adverse, true));
//            }
        }
        else if (type.equals(CubeCustomMessageType.DOWNLOAD_COMPLETE.getType()) && !cubeMessage.isGroupMessage()) {
            if (!cubeMessage.isReceivedMessage()) {//如果是自己发的文件下载成功消息可以不显示fldy
                return null;
            }
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildDownloadCompletedNotification(message);
        }
        else if (type.equals(CubeCustomMessageType.ApplyFriend.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
//            if (CubeUI.getInstance().getFriendOperationListener() != null) {
//                CubeUI.getInstance().getFriendOperationListener().apply(message.getHeader("applyUserCube"));
//            }
        }
        else if (type.equals(CubeCustomMessageType.RefuseFriend.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
//            if (CubeUI.getInstance().getFriendOperationListener() != null) {
//                CubeUI.getInstance().getFriendOperationListener().refuse();
//            }
        }
        else if (type.equals(CubeCustomMessageType.AddGroupManager.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildAddGroupManagerNotification(message);
//            if (CubeUI.getInstance().getGroupOperationListener() != null && !isSync) {
//                CubeUI.getInstance().getGroupOperationListener().addGroupManager(groupCube, message.getHeader("managerCube"), token);
//            }
        }
        else if (type.equals(CubeCustomMessageType.DelGroupManager.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildDelGroupManagerNotification(message);
//            if (CubeUI.getInstance().getGroupOperationListener() != null && !isSync) {
//                CubeUI.getInstance().getGroupOperationListener().delGroupManager(groupCube, message.getHeader("managerCube"), token);
//            }
        }
        else if (type.equals(CubeCustomMessageType.DelFriend.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            String userCube = message.getHeader("fromUserCube");
            String friendUserCube = message.getHeader("friendUserCube");
            String adverse = SpUtil.getCubeId().equals(userCube) ? friendUserCube : userCube;//对方cube
//            MessageManager.getInstance().deleteMessagesBySessionId(adverse, CubeSessionType.P2P, false);
//
//            if (CubeUI.getInstance().getFriendOperationListener() != null) {
//                CubeUI.getInstance().getFriendOperationListener().delete(adverse, token);
//                RxBus.getInstance().post(CubeEvent.EVENT_ADD_OR_DELETE_FRIEND, new EventAddOrDeleteFriendEventModel(adverse, false));
//            }
        }
        else if (type.equals(CubeCustomMessageType.AddGroupMember.getType())) {
            try {
                String memberCubeJsonArray = message.getHeader("memberCubeArray");
                JSONArray memberCubeArray = new JSONArray(memberCubeJsonArray);
                cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
                text = buildAddGroupMemberNotification(message);
//                if (CubeUI.getInstance().getGroupOperationListener() != null && !isSync) {
//                    CubeUI.getInstance().getGroupOperationListener().addMember(groupCube, memberCubeArray, token);
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(CubeCustomMessageType.DelGroupMember.getType()) && !isSync) {
            try {
                cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
                String memberCubeJsonArray = message.getHeader("memberCubeArray");
                JSONArray memberCubeArray = new JSONArray(memberCubeJsonArray);
                boolean isMyself = isMyself(memberCubeArray);
                text = buildDelGroupMemberNotification(message, groupCube, isMyself);
//                if (CubeUI.getInstance().getGroupOperationListener() != null && !isSync) {
//                    CubeUI.getInstance().getGroupOperationListener().delMember(groupCube, memberCubeArray, isMyself, token);
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(CubeCustomMessageType.GroupMemberQuit.getType()) && !isSync) {
            //退出后的群组应该都是valid=false的 但是退出群组这一条服务器无法处理
            String quitCube = message.getHeader("managerCube");
            //if (quitCube.equals(SpUtil.getCubeUser().getCubeId()) && isSync) {
            //    return "";
            //}
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildGroupMemberQuitNotification(message, groupCube);
//            if (CubeUI.getInstance().getGroupOperationListener() != null && !isSync) {
//                CubeUI.getInstance().getGroupOperationListener().memberQuit(groupCube, quitCube, token, quitCube.equals(CubeSpUtil.getCubeUser().getCubeId()));
//            }
        }
        else if (type.equals(CubeCustomMessageType.DismissGroup.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
//            MessageManager.getInstance().deleteMessagesBySessionId(groupCube, CubeSessionType.Group, false);
//            if (CubeUI.getInstance().getGroupOperationListener() != null && !isSync) {
//                CubeUI.getInstance().getGroupOperationListener().dismiss(groupCube, token);
//            }
        }
        else if (type.equals(CubeCustomMessageType.UpdateGroupName.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildUpdateGroupNameNotification(message);
//            if (CubeUI.getInstance().getGroupOperationListener() != null && !isSync) {
//                CubeUI.getInstance().getGroupOperationListener().updateGroupName(groupCube, message.getHeader("groupName"), token);
//            }
        }
        else if (type.equals(CubeCustomMessageType.UpdateGroupNotice.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildUpdateGroupNoticeNotification(message);
//            if (CubeUI.getInstance().getGroupOperationListener() != null && !isSync) {
//                CubeUI.getInstance().getGroupOperationListener().updateGroupNotice(groupCube, message.getHeader("noticeContent"), token);
//            }
        }
        else if (type.equals(CubeCustomMessageType.UpdateGroupFace.getType()) && !isSync) {
//            if (CubeUI.getInstance().getGroupOperationListener() != null) {
//                CubeUI.getInstance().getGroupOperationListener().updateGroupFace(groupCube, message.getHeader("groupFaceSrc"), token);
//            }
        }
        else if (type.equals(CubeCustomMessageType.TransferGroupMaster.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildTransferGroupMasterNotification(message);
//            if (CubeUI.getInstance().getGroupOperationListener() != null && !isSync) {
//                CubeUI.getInstance().getGroupOperationListener().transferGroupMaster(groupCube, message.getHeader("newMasterCube"), token);
//            }
        }
        else if (type.equals(CubeCustomMessageType.NewGroup.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildNewGroupNotification(message);
//            if (CubeUI.getInstance().getGroupOperationListener() != null && !isSync) {
//                CubeUI.getInstance().getGroupOperationListener().newGroup(groupCube, token);
//            }
        }
        else if (type.equals(CubeCustomMessageType.UpdateUserInfo.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
//            if (CubeUI.getInstance().getFriendOperationListener() != null) {
//                CubeUI.getInstance().getFriendOperationListener().update(message.getHeader("cube"));
//            }
        }
        else if (type.equals(CubeCustomMessageType.UpdateGroupMemberRemark.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
//            if (CubeUI.getInstance().getGroupOperationListener() != null) {
//                CubeUI.getInstance().getGroupOperationListener().updateMemberRemark(groupCube, message.getHeader("memberCube"), message.getHeader("memberRemark"), token);
//            }
        }
        else if (type.equals(CubeCustomMessageType.Logout.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
//            if (CubeUI.getInstance().getLoginListener() != null) {
//                String plat = message.getHeader("plat");
//                CubeUI.getInstance().getLoginListener().loginOut(plat);
//            }
        }
        else if (type.equals(CubeCustomMessageType.Login.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            String plat = message.getHeader("plat");
            String timestamp = message.getHeader("timestamp");
//            if (CubeUI.getInstance().getLoginListener() != null) {
//                CubeUI.getInstance().getLoginListener().loginIn(plat, Long.parseLong(timestamp), token);
//            }
        }
        else if (type.equals(CubeCustomMessageType.UpdateUserPwd.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeCustomMessageType.UpdateUserPwd.getType());
//            if (CubeUI.getInstance().getLoginListener() != null) {
//                CubeUI.getInstance().getLoginListener().updateUserPwd();
//            }
        }
        else if (type.equals(CubeCustomMessageType.VideoCall.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomCallVideo.getType());
            text = buildCall(message);
        }
        else if (type.equals(CubeCustomMessageType.AudioCall.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomCallAudio.getType());
            text = buildCall(message);
        }
        else if (type.equals(CubeCustomMessageType.GroupShareQr.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomShare.getType());
            cubeMessage.setThumbUrl(message.getHeader("shareGroupQrSrc"));
            text = buildShare(message, true);
        }
        else if (type.equals(CubeCustomMessageType.UserShareQr.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomShare.getType());
            cubeMessage.setThumbUrl(message.getHeader("shareQrSrc"));
            text = buildShare(message, false);
        }
        else if (type.equals(CubeCustomMessageType.ApplyOrAgreeToGroup.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
//            CubeUI.getInstance().getGroupOperationListener().applyOrAgreeToGroup(message.getHeader("groupCube"), message.getHeader("groupName"), message.getHeader("userCube"), message.getHeader("userDisplayName"));
        }
        else if (type.equals(CubeCustomMessageType.InviteToGroup.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
//            CubeUI.getInstance().getGroupOperationListener().inviteToGroup(message.getHeader("groupCube"), message.getHeader("groupName"), message.getHeader("userCube"), message.getHeader("userDisplayName"));
        }
        else if (type.equals(CubeCustomMessageType.RefuseInviteToGroup.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
//            CubeUI.getInstance().getGroupOperationListener().refuseInviteToGroup(message.getHeader("groupCube"), message.getHeader("groupName"), message.getHeader("userCube"), message.getHeader("userDisplayName"));
        }
        else if (type.equals(CubeCustomMessageType.CallAbnormal.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildCall(message);
        }
        else if (type.equals(CubeCustomMessageType.ApplyConference.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildApplyConference(message);
        }
        else if (type.equals(CubeCustomMessageType.CloseConference.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildCloseConference(message);
        }
        else if (type.equals(CubeCustomMessageType.SharerRD.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildApplyRD(message);
        }
        else if (type.equals(CubeCustomMessageType.RevokeRD.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildCloseRD(message);
        }
        else if (type.equals(CubeCustomMessageType.SecretTip.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = message.getBody();
        }
        else if (CubeCustomMessageType.WriteEvent.type.equals(type) && !isSync) {
//            if (CubeUI.getInstance().getWriteEventListeners().size() > 0) {
//                for (int i = 0; i < CubeUI.getInstance().getWriteEventListeners().size(); i++) {
//                    CubeUI.getInstance().getWriteEventListeners().get(i).onWriting(message);
//                }
//            }
        }
        else if (type.equals(CubeCustomMessageType.ShakeEvent.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomShake.getType());
            text = buildShake(message);
        }

        //白板相关
        else if (type.equals(CubeCustomMessageType.WhiteBoardApply.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildApplyWhiteBoard(message);
        }

        else if (type.equals(CubeCustomMessageType.WhiteBoardClose.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildCloseWhiteBoard(message);
        }
        //P2P白板相关
        else if (type.equals(CubeCustomMessageType.P2PWhiteBoardApply.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildP2PApplyWhiteBoard(message);
        }

        else if (type.equals(CubeCustomMessageType.P2PWhiteBoardClose.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
            text = buildCloseWhiteBoard(message);
        }
//        else if (type.equals(CubeCustomMessageType.CompletedTates.getType())) {
//            cubeMessage.setMessageType(CubeMessageType.CustomTips.getType());
//            CubeUI.getInstance().getGroupOperationListener().compledeGroupTask(message.getHeader("taskId"), Long.parseLong(message.getHeader("groupId")));
//            text = buildTaskStatus(message);
//        }
//        else if (type.equals(CubeCustomMessageType.GroupFileAdd.getType())) {
//            CubeUI.getInstance().getGroupOperationListener().groupFileAdd(Long.parseLong(message.getHeader("userId")), message.getHeader("groupId"), Boolean.parseBoolean(message.getHeader("isFolder")), message.getHeader("fileName"), message.getHeader("fileId"), Long.parseLong(message.getHeader("timestamp")));
//        }
//        else if (type.equals(CubeCustomMessageType.GroupFileDelete.getType())) {
//            CubeUI.getInstance().getGroupOperationListener().groupFileDelete(Long.parseLong(message.getHeader("userId")), message.getHeader("groupId"), message.getHeader("fileNameArray"), message.getHeader("fileIdArray"), Long.parseLong(message.getHeader("timestamp")));
//        }
        else {
            text = null;
        }
        return text;
    }

    private static String buildDownloadCompletedNotification(CustomMessage message) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("对方已成功接收了您发送的文件");
            stringBuilder.append("“");
            stringBuilder.append(message.getHeader("fileName"));
            stringBuilder.append("”");
            stringBuilder.append("(");
            String fileSize = message.getHeader("fileSize");
            String s = FileUtil.formatFileSize(Long.parseLong(fileSize));
            stringBuilder.append(s);
            stringBuilder.append(")");
            return stringBuilder.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static String buildCloseRD(CustomMessage message) {
        return "屏幕分享已结束";
    }

    private static String buildApplyRD(CustomMessage message) {
        return "【" + message.getHeader("userName") + "】" + "发起了屏幕分享";
    }

    private static String buildCloseConference(CustomMessage message) {
        if(message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceVideoCall.getType())){
            return "视频通话已结束";
        }else  if(message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceVoiceCall.getType())){
            return "语音通话已结束";
        }else if(message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceShareScreen.getType())){
            return "屏幕分享已结束";
        }else {
            return "";
        }
    }

    private static String buildApplyConference(CustomMessage message) {
        String userCube = message.getHeader("userCube");
        String groupCube = message.getHeader("groupCube");
        String userDN = message.getHeader("userDN");
        if(message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceVideoCall.getType())){
            return "【" +  (TextUtils.isEmpty(userDN) ? userCube : userDN) + "】" + "发起了视频通话";
        }else if(message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceVoiceCall.getType())){
            return "【" +  (TextUtils.isEmpty(userDN) ? userCube : userDN) + "】" + "发起了语音通话";
        }else if(message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceShareScreen.getType())){
            return "【" +  (TextUtils.isEmpty(userDN) ? userCube : userDN) + "】" + "发起了屏幕分享";
        }else {
            return "";
        }
    }

    private static String buildCloseWhiteBoard(CustomMessage message) {
        return "白板演示已结束";
    }

    private static String buildApplyWhiteBoard(CustomMessage message) {
        String userCube = message.getHeader("userCube");
        String groupCube = message.getHeader("groupCube");
        String userDN = message.getHeader("userDN");

        return "【" +  (TextUtils.isEmpty(userDN) ? userCube : userDN) + "】" + "发起了白板演示";
    }

    /**
     * p2p创建白板消息构建
     * @param message
     * @return
     */
    private static String buildP2PApplyWhiteBoard(CustomMessage message) {
        String userCube = message.getHeader("userCube");
        String groupCube = message.getHeader("groupCube");
        String userDN = message.getHeader("userDN");

        return "【" +  (TextUtils.isEmpty(userDN) ? userCube : userDN) + "】" + "发起了白板演示";

    }

    private static String buildShare(CustomMessage message, boolean isGroup) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (isGroup) {
            map.put("cube", message.getHeader("groupCube"));
            map.put("name", message.getHeader("groupName"));
            map.put("face", message.getHeader("groupFace"));
        }
        else {
            map.put("cube", message.getHeader("shareUserCube"));
            map.put("name", message.getHeader("shareUserDisplayName"));
            map.put("face", message.getHeader("shareUserFace"));
        }
        map.put("typ", message.getHeader("operate"));
        return new Gson().toJson(map);
    }

    /**
     * 判断是否是自己
     *
     * @param memberCubeArray
     *
     * @return
     */
    private static boolean isMyself(JSONArray memberCubeArray) throws JSONException {
        if (memberCubeArray != null && memberCubeArray.length() > 0) {
            for (int i = 0; i < memberCubeArray.length(); i++) {
                String cube = memberCubeArray.getString(i);
                if (cube.equals(SpUtil.getCubeId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取群组cube号
     *
     * @param message
     *
     * @return
     */
    private static String getGroupCube(CustomMessage message) {
        return message.getHeader("groupCube");
    }

    /**
     * 构建自定义通话消息
     *
     * @param message
     *
     * @return
     */
    private static String buildCall(CustomMessage message) {
        return message.getBody();
    }

    public static String buildShake(CustomMessage message) {
        return (message.isSendMessage() ? "抖了一下" : "抖了你一下");
    }

    public static String buildShake(CubeMessage message) {
        return (message.isReceivedMessage() ? "抖了你一下" : "抖了一下");
    }

    /**
     * 构建添加好友成功消息通知
     *
     * @param message
     *
     * @return
     */
    private static String buildAddFriendNotification(CustomMessage message) {
        StringBuilder sb = new StringBuilder();
        sb.append("你们已成为好友，现在可以聊聊了");
        //if (message.getHeader("applyUserCube").equals(SpUtil.getCubeUser().getCubeId())) {
        //    sb.append(message.getHeader("acceptUserDisplayName"));
        //}
        //else {
        //    sb.append(message.getHeader("applyUserDisplayName"));
        //}
        //sb.append("】,现在可以开始聊天了");
        return sb.toString();
    }

    /**
     * 构建转移群主消息通知
     *
     * @param message
     *
     * @return
     */
    private static String buildTransferGroupMasterNotification(CustomMessage message) {
        String sb = "【" + message.getHeader("newMasterDisplayName") + "】已成为群主";
        return sb;
    }

    /**
     * 构建更新群公告消息通知
     *
     * @param message
     *
     * @return
     */
    private static String buildUpdateGroupNoticeNotification(CustomMessage message) {
        StringBuilder sb = new StringBuilder();
        if (message.getHeader("managerCube").equals(SpUtil.getCubeId())) {
            sb.append("你修改群公告为\"");
        }
        else {
            sb.append("【" + message.getHeader("managerDisplayName") + "】");
            sb.append("修改群公告为\"");
        }
        sb.append(message.getHeader("noticeContent") + "\"");
        return sb.toString();
    }

    /**
     * 构建修改群名称消息通知
     *
     * @param message
     *
     * @return
     */
    private static String buildUpdateGroupNameNotification(CustomMessage message) {
        Sender sender = message.getSender();
        Receiver receiver = message.getReceiver();
        StringBuilder sb = new StringBuilder();
        if (sender.getCubeId().equals(SpUtil.getCubeId())) {
            sb.append("你修改群名为\"");
        }
        else {
            sb.append("【" +sender.getDisplayName() + "】");
            sb.append("修改群名为\"");
        }
        sb.append(receiver.getDisplayName() + "\"");
        return sb.toString();
    }

    private static String buildTaskStatus(CustomMessage message) {
        StringBuilder sb = new StringBuilder();
        if (message.getHeader("actionCube").equals(SpUtil.getCubeId())) {
            sb.append("你把任务" + " ");
        }
        else {
            sb.append(message.getHeader("actionDisName") + "把任务" + " ");
        }
        String taskName = message.getHeader("taskName");
        //sb.append("<font color='#8da5ff'>" + taskName + "</font>" + " ");
        sb.append(taskName + " ");
        if (message.getHeader("actionType").equals("1")) {
            sb.append("标记完成");
        }
        else {
            sb.append("取消完成");
        }
        return sb.toString();
    }

    /**
     * 构建移除群管理员消息通知
     *
     * @param message
     *
     * @return
     */
    private static String buildDelGroupManagerNotification(CustomMessage message) {
        String sb = "【" + message.getHeader("managerDisplayName") + "】被取消了群管理员";
        return sb;
    }

    /**
     * 构建添加群管理员消息通知
     *
     * @param message
     *
     * @return
     */
    private static String buildAddGroupManagerNotification(CustomMessage message) {
        String sb = "【" + message.getHeader("managerDisplayName") + "】已提升为群管理员";
        return sb;
    }

    /**
     * 构建删除群成员消息通知
     *
     * @param message
     * @param groupCube
     * @param isMyself
     *
     * @return
     */
    private static String buildDelGroupMemberNotification(CustomMessage message, String groupCube, boolean isMyself) {
        if (isMyself) {
//            MessageManager.getInstance().deleteMessagesBySessionId(groupCube, CubeSessionType.Group, false);
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (message.getHeader("managerCube").equals(SpUtil.getCubeId())) {
            sb.append("您将【");
        }
        else {
            sb.append("管理员将【");
        }
        String nameArray = message.getHeader("memberDisplayNameArray");
        toArray(sb, nameArray);
        sb.append("】移出了群组");
        return sb.toString();
    }

    /**
     * 构建退出群消息通知
     *
     * @param message
     * @param groupCube
     *
     * @return
     */
    private static String buildGroupMemberQuitNotification(CustomMessage message, String groupCube) {
        if (message.getHeader("managerCube").equals(SpUtil.getCubeId())) {
//            MessageManager.getInstance().deleteMessagesBySessionId(groupCube, CubeSessionType.Group, false);
            return null;
        }
        String sb = "【" + message.getHeader("managerDisplayName") + "】" + "已退出群组";
        return sb;
    }

    /**
     * 构建创建群消息通知
     *
     * @param message
     *
     * @return
     */
    private static String buildNewGroupNotification(CustomMessage message) {
        return CubeUI.getInstance().getContext().getString(R.string.group_created_tips);
    }

    /**
     * 构建添加群成员消息通知
     *
     * @param message
     *
     * @return
     */
    private static String buildAddGroupMemberNotification(CustomMessage message) {
        StringBuilder sb = new StringBuilder();
        sb.append("【");
        String memberCubeArray = message.getHeader("memberCubeArray");
        String nameArray = message.getHeader("memberDisplayNameArray");
        try {
            JSONArray jsonArray = new JSONArray(memberCubeArray);
            JSONArray nameJsonArray = new JSONArray(nameArray);
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                String o = (String) jsonArray.get(i);
//                CubeUser cubeUserSync = CubeUI.getInstance().getCubeDataProvider().getCubeUserSync(o);
//                sb.append(cubeUserSync == null ? nameJsonArray.get(i) : cubeUserSync.getUserRemarkName());
                if (i != length - 1) {
                    sb.append("，");
                }
            }
            sb.append("】 加入群组");
            return sb.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void toArray(StringBuilder sb, String nameArray) {
        try {
            JSONArray jsonArray = new JSONArray(nameArray);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String name = jsonArray.getString(i);
                    if (i == jsonArray.length() - 1) {
                        sb.append(name);
                    }
                    else {
                        sb.append(name);
                        sb.append("、");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
