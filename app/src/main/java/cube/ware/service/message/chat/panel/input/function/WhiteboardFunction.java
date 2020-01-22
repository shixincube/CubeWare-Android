package cube.ware.service.message.chat.panel.input.function;

import android.os.Bundle;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.ClickUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.CubeCallback;
import cube.service.common.model.CubeError;
import cube.service.conference.model.Conference;
import cube.service.group.GroupType;
import cube.service.whiteboard.model.WhiteBoardInfo;
import cube.service.whiteboard.model.Whiteboard;
import cube.service.whiteboard.model.WhiteboardConfig;
import cube.ware.AppConstants;
import cube.ware.CubeUI;
import cube.ware.R;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.whiteboard.WhiteBoardHandle;
import cube.ware.service.whiteboard.manager.WBCallManager;
import cube.ware.service.whiteboard.ui.listener.CreateCallback;
import cube.ware.service.whiteboard.ui.listener.WBListener;
import java.util.ArrayList;
import java.util.List;

public class WhiteboardFunction extends BaseFunction implements CreateCallback {

    private WBListener mWBListener;
    List<String> mList = new ArrayList<>();

    /**
     * 构造方法
     */
    public WhiteboardFunction(CubeSessionType mSessionType) {
        super(R.drawable.chat_whiteboard_btn_normal, R.string.whiteboard);
    }

    @Override
    public void onClick() {
        if (!ClickUtil.isFastClick(1000)) {
            return;
        }
        if (getChatType().equals(CubeSessionType.P2P)) {//单聊
            if (CubeCore.getInstance().isCalling()) {
                ToastUtil.showToast(getActivity(), R.string.calling_please_try_again_later);
            }
            else {
                mList.clear();
                mList.add(getChatId());
                mWBListener = new WBListener(getActivity(), getChatType(), mList, "");//单人白板可以不传groupId，占位而已
                mWBListener.setCreateCallback(this);
                //监听
                WhiteBoardHandle.getInstance().addWhiteBoardStateListeners(mWBListener);
                createWhiteBoard();
            }
        }
        else {//群聊
            if (CubeUI.getInstance().isCalling()) {
                ToastUtil.showToast(getActivity(), R.string.calling_please_try_again_later);
            }
            else {
                isHasConference();
            }
        }
    }

    private void isHasConference() {
        List<String> list = new ArrayList<>();
        list.add(getChatId());
        CubeEngine.getInstance().getConferenceService().queryConferencesByGroupIds(list, new CubeCallback<List<Conference>>() {
            @Override
            public void onSucceed(List<Conference> conferenceList) {
                if (conferenceList != null && conferenceList.size() > 0) {
                    ToastUtil.showToast(getActivity(), "当前存在会议");
                }
                else {
                    isHasWhiteBoard();
                }
            }

            @Override
            public void onFailed(CubeError error) {
                //当前群组没有会议
                LogUtil.d("====查询会议没有===");
                isHasWhiteBoard();
            }
        });
    }

    private void isHasWhiteBoard() {
        List<String> groupId = new ArrayList<>();
        groupId.add(getChatId());
        CubeEngine.getInstance().getWhiteboardService().queryWhiteboardByGroupId(groupId, CubeCore.getInstance().getCubeId(), new CubeCallback<WhiteBoardInfo>() {
            @Override
            public void onSucceed(WhiteBoardInfo whiteBoardData) {
                if (whiteBoardData.whiteboards.size() != 0 && null != whiteBoardData.whiteboards.get(0)) {
                    ToastUtil.showToast(getActivity(), "当前存在白板");
                }
                else if (whiteBoardData.whiteboards.size() == 0) {
                    //表示当前群没有白板，跳入选择成员页面
                    Bundle bundle = new Bundle();
                    bundle.putInt("select_type", 3);//白板首次创建
                    bundle.putString("group_id", getChatId()); //mChatId 就是 groupId
                    RouterUtil.navigation(AppConstants.Router.SelectMemberActivity, bundle);
                }
            }

            @Override
            public void onFailed(CubeError error) {
                LogUtil.d("===查询白板失败了==" + error.code + "===" + error.desc);
                //                Bundle bundle=new Bundle();
                //                bundle.putInt("select_type",3);//白板首次创建
                //                bundle.putString("group_id",getChatId()); //mChatId 就是 groupId
                //                RouterUtil.navigation(AppConstants.Router.SelectMemberActivity,bundle);
            }
        });
    }

    //开启白板
    private void createWhiteBoard() {
        List<String> master = new ArrayList<>();
        master.add(CubeCore.getInstance().getCubeId());
        WhiteboardConfig whiteboardConfig = new WhiteboardConfig(GroupType.SHARE_WB, "");
        //        whiteboardConfig.bindGroupId="";
        whiteboardConfig.maxNumber = 2; //一对一单聊
        whiteboardConfig.isOpen = true;
        whiteboardConfig.masters = master;
        CubeEngine.getInstance().getWhiteboardService().create(whiteboardConfig);
    }

    @Override
    public void onWBFinish(Whiteboard whiteboard) {
        WhiteBoardHandle.getInstance().removeWhiteBoardStateListeners(mWBListener);
    }

    @Override
    public void onWBCreate(Whiteboard whiteboard) {

    }

    @Override
    public void onWBError(Whiteboard whiteboard, CubeError error) {
        WhiteBoardHandle.getInstance().removeWhiteBoardStateListeners(mWBListener);
    }
}
