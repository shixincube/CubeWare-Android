package cube.ware.service.message.chat.panel.input.function;

import android.os.Bundle;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.utils.ClickUtil;
import com.common.utils.ToastUtil;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CallStatus;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.message.R;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wangxx
 * @date 2017/2/7
 */

public class VideoFunction extends BaseFunction {

    /**
     * 构造方法
     */
    public VideoFunction() {
        super(R.drawable.selector_chat_function_video_btn, R.string.video_chat);
    }

    @Override
    public void onClick() {
        if (getChatType().equals(CubeSessionType.P2P)) {//单聊
            if (CubeCore.getInstance().isCalling()) {
                ToastUtil.showToast(getActivity(), R.string.calling_please_try_again_later);
            }
            else {
                Bundle bundle = new Bundle();
                bundle.putString("call_id", getChatId());
                bundle.putSerializable("call_state", CallStatus.VIDEO_OUTGOING);
                bundle.putLong("call_time", 0l);
                ARouter.getInstance().build(CubeConstants.Router.P2PCallActivity).withBundle("call_data", bundle).navigation();
            }
        }
        else {//群聊
            if (ClickUtil.isFastClick()) {
                if (CubeCore.getInstance().isCalling()) {
                    ToastUtil.showToast(getActivity(), R.string.calling_please_try_again_later);
                }
                else {
                    isHasConference();
                }
            }
        }
    }

    private void isHasConference() {
        List<String> list = new ArrayList<>();
        list.add(getChatId());
        /*CubeEngine.getInstance().getConferenceService().queryConferencesByGroupIds(list, new CubeCallback<List<Conference>>() {
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
        });*/

        //        CubeEngine.getInstance().getConferenceService().queryConferenceDetails(getChatId(), new CubeCallback<Conference>() {
        //            @Override
        //            public void onSucceed(Conference conference) {
        //                if(conference!=null){
        //                    ToastUtil.showToast(getActivity(),"当前存在会议");
        //                }
        //            }
        //
        //            @Override
        //            public void onFailed(CubeError error) {
        //               isHasWhiteBoard();
        //            }
        //        });
    }

    private void isHasWhiteBoard() {
        List<String> groupId = new ArrayList<>();
        groupId.add(getChatId());
        /*CubeEngine.getInstance().getWhiteboardService().queryWhiteboardByGroupId(groupId, CubeCore.getInstance().getCubeId(), new CubeCallback<WhiteBoardInfo>() {
            @Override
            public void onSucceed(WhiteBoardInfo whiteBoardData) {
                if (whiteBoardData.whiteboards.size() != 0 && null != whiteBoardData.whiteboards.get(0)) {
                    ToastUtil.showToast(getActivity(), "当前存在白板");
                }
                else if (whiteBoardData.whiteboards.size() == 0) {
                    //表示当前群没有白板，跳入选择成员页面
                    Bundle bundle = new Bundle();
                    bundle.putInt("select_type", 8);//视频音频会议首次创建
                    bundle.putString("group_id", getChatId()); //mChatId 就是 groupId
                    RouterUtil.navigation(CubeConstants.Router.SelectMemberActivity, bundle);
                }
            }

            @Override
            public void onFailed(CubeError error) {
                LogUtil.d("===查询白板失败了==" + error.code + "===" + error.desc);
                //                Bundle bundle=new Bundle();
                //                bundle.putInt("select_type",8);//视频音频会议首次创建
                //                bundle.putString("group_id",getChatId()); //mChatId 就是 groupId
                //                RouterUtil.navigation(AppConstants.Router.SelectMemberActivity,bundle);
            }
        });*/
    }
}
