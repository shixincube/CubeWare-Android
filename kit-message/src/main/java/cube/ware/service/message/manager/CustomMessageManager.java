package cube.ware.service.message.manager;

import android.text.TextUtils;
import com.common.utils.FileUtil;
import com.google.gson.Gson;
import cube.service.message.CustomMessage;
import cube.service.message.Receiver;
import cube.service.message.Sender;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeCustomMessageType;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.R;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;

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
        if (type.equals(CubeCustomMessageType.AddFriend.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildAddFriendNotification(message);
        }
        else if (type.equals(CubeCustomMessageType.DOWNLOAD_COMPLETE.getType()) && !cubeMessage.isGroupMessage()) {
            if (!cubeMessage.isReceivedMessage()) {//如果是自己发的文件下载成功消息可以不显示fldy
                return null;
            }
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildDownloadCompletedNotification(message);
        }
        else if (type.equals(CubeCustomMessageType.ApplyFriend.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
        }
        else if (type.equals(CubeCustomMessageType.RefuseFriend.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
        }
        else if (type.equals(CubeCustomMessageType.AddGroupManager.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildAddGroupManagerNotification(message);
        }
        else if (type.equals(CubeCustomMessageType.DelGroupManager.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildDelGroupManagerNotification(message);
        }
        else if (type.equals(CubeCustomMessageType.DelFriend.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            String userCube = message.getHeader("fromUserCube");
            String friendUserCube = message.getHeader("friendUserCube");
            String adverse = CubeCore.getInstance().getCubeId().equals(userCube) ? friendUserCube : userCube;//对方cube
        }
        else if (type.equals(CubeCustomMessageType.AddGroupMember.getType())) {
            try {
                String memberCubeJsonArray = message.getHeader("memberCubeArray");
                JSONArray memberCubeArray = new JSONArray(memberCubeJsonArray);
                cubeMessage.setMessageType(CubeMessageType.CustomTips);
                text = buildAddGroupMemberNotification(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(CubeCustomMessageType.DelGroupMember.getType()) && !isSync) {
            try {
                cubeMessage.setMessageType(CubeMessageType.CustomTips);
                String memberCubeJsonArray = message.getHeader("memberCubeArray");
                JSONArray memberCubeArray = new JSONArray(memberCubeJsonArray);
                boolean isMyself = isMyself(memberCubeArray);
                text = buildDelGroupMemberNotification(message, groupCube, isMyself);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(CubeCustomMessageType.GroupMemberQuit.getType()) && !isSync) {
            //退出后的群组应该都是valid=false的 但是退出群组这一条服务器无法处理
            String quitCube = message.getHeader("managerCube");
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildGroupMemberQuitNotification(message, groupCube);
        }
        else if (type.equals(CubeCustomMessageType.DismissGroup.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
        }
        else if (type.equals(CubeCustomMessageType.UpdateGroupName.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildUpdateGroupNameNotification(message);
        }
        else if (type.equals(CubeCustomMessageType.UpdateGroupNotice.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildUpdateGroupNoticeNotification(message);
        }
        else if (type.equals(CubeCustomMessageType.UpdateGroupFace.getType()) && !isSync) {
        }
        else if (type.equals(CubeCustomMessageType.TransferGroupMaster.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildTransferGroupMasterNotification(message);
        }
        else if (type.equals(CubeCustomMessageType.NewGroup.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildNewGroupNotification(message);
        }
        else if (type.equals(CubeCustomMessageType.UpdateUserInfo.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
        }
        else if (type.equals(CubeCustomMessageType.UpdateGroupMemberRemark.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
        }
        else if (type.equals(CubeCustomMessageType.Logout.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
        }
        else if (type.equals(CubeCustomMessageType.Login.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            String plat = message.getHeader("plat");
            String timestamp = message.getHeader("timestamp");
        }
        else if (type.equals(CubeMessageType.UpdateUserPwd.type) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.UpdateUserPwd);
        }
        else if (type.equals(CubeCustomMessageType.VideoCall.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomCallVideo);
            text = buildCall(message);
        }
        else if (type.equals(CubeCustomMessageType.AudioCall.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomCallAudio);
            text = buildCall(message);
        }
        else if (type.equals(CubeCustomMessageType.GroupShareQr.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomShare);
            cubeMessage.setThumbUrl(message.getHeader("shareGroupQrSrc"));
            text = buildShare(message, true);
        }
        else if (type.equals(CubeCustomMessageType.UserShareQr.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomShare);
            cubeMessage.setThumbUrl(message.getHeader("shareQrSrc"));
            text = buildShare(message, false);
        }
        else if (type.equals(CubeCustomMessageType.ApplyOrAgreeToGroup.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
        }
        else if (type.equals(CubeCustomMessageType.InviteToGroup.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
        }
        else if (type.equals(CubeCustomMessageType.RefuseInviteToGroup.getType()) && !isSync) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
        }
        else if (type.equals(CubeCustomMessageType.CallAbnormal.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildCall(message);
        }
        else if (type.equals(CubeCustomMessageType.ApplyConference.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildApplyConference(message);
        }
        else if (type.equals(CubeCustomMessageType.CloseConference.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildCloseConference(message);
        }
        else if (type.equals(CubeCustomMessageType.SharerRD.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildApplyRD(message);
        }
        else if (type.equals(CubeCustomMessageType.RevokeRD.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildCloseRD(message);
        }
        else if (type.equals(CubeCustomMessageType.SecretTip.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
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
            cubeMessage.setMessageType(CubeMessageType.CustomShake);
            text = buildShake(message);
        }

        //白板相关
        else if (type.equals(CubeCustomMessageType.WhiteBoardApply.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildApplyWhiteBoard(message);
        }

        else if (type.equals(CubeCustomMessageType.WhiteBoardClose.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildCloseWhiteBoard(message);
        }
        //P2P白板相关
        else if (type.equals(CubeCustomMessageType.P2PWhiteBoardApply.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildP2PApplyWhiteBoard(message);
        }

        else if (type.equals(CubeCustomMessageType.P2PWhiteBoardClose.getType())) {
            cubeMessage.setMessageType(CubeMessageType.CustomTips);
            text = buildCloseWhiteBoard(message);
        }
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
            String s = FileUtil.formatFileSize(CubeCore.getContext(), Long.parseLong(fileSize));
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
        if (message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceVideoCall.getType())) {
            return "视频通话已结束";
        }
        else if (message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceVoiceCall.getType())) {
            return "语音通话已结束";
        }
        else if (message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceShareScreen.getType())) {
            return "屏幕分享已结束";
        }
        else {
            return "";
        }
    }

    private static String buildApplyConference(CustomMessage message) {
        String userCube = message.getHeader("userCube");
        String groupCube = message.getHeader("groupCube");
        String userDN = message.getHeader("userDN");
        if (message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceVideoCall.getType())) {
            return "【" + (TextUtils.isEmpty(userDN) ? userCube : userDN) + "】" + "发起了视频通话";
        }
        else if (message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceVoiceCall.getType())) {
            return "【" + (TextUtils.isEmpty(userDN) ? userCube : userDN) + "】" + "发起了语音通话";
        }
        else if (message.getHeader("conferenceType").equals(CubeCustomMessageType.ConferenceShareScreen.getType())) {
            return "【" + (TextUtils.isEmpty(userDN) ? userCube : userDN) + "】" + "发起了屏幕分享";
        }
        else {
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

        return "【" + (TextUtils.isEmpty(userDN) ? userCube : userDN) + "】" + "发起了白板演示";
    }

    /**
     * p2p创建白板消息构建
     *
     * @param message
     *
     * @return
     */
    private static String buildP2PApplyWhiteBoard(CustomMessage message) {
        String userCube = message.getHeader("userCube");
        String groupCube = message.getHeader("groupCube");
        String userDN = message.getHeader("userDN");

        return "【" + (TextUtils.isEmpty(userDN) ? userCube : userDN) + "】" + "发起了白板演示";
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
                if (cube.equals(CubeCore.getInstance().getCubeId())) {
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
        if (message.getHeader("managerCube").equals(CubeCore.getInstance().getCubeId())) {
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
        if (sender.getCubeId().equals(CubeCore.getInstance().getCubeId())) {
            sb.append("你修改群名为\"");
        }
        else {
            sb.append("【" + sender.getDisplayName() + "】");
            sb.append("修改群名为\"");
        }
        sb.append(receiver.getDisplayName() + "\"");
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
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (message.getHeader("managerCube").equals(CubeCore.getInstance().getCubeId())) {
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
        if (message.getHeader("managerCube").equals(CubeCore.getInstance().getCubeId())) {
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
        return CubeCore.getContext().getString(R.string.group_created_tips);
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
