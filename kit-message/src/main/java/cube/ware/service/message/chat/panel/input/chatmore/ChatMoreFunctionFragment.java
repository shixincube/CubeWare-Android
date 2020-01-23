package cube.ware.service.message.chat.panel.input.chatmore;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.ToastUtil;
import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.group.GroupType;
import cube.service.whiteboard.model.Whiteboard;
import cube.service.whiteboard.model.WhiteboardConfig;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CallStatus;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.BaseChatActivity;
import cube.ware.service.message.chat.ChatContainer;
import cube.ware.service.whiteboard.WhiteBoardHandle;
import cube.ware.service.whiteboard.ui.listener.CreateCallback;
import cube.ware.service.whiteboard.ui.listener.WBListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zzy
 * @date 2018/9/10
 * 聊天页面+展开Fragment
 */
@SuppressLint("ValidFragment")
public class ChatMoreFunctionFragment extends Fragment implements View.OnClickListener, CreateCallback {

    private View            mRootView;
    private View            video_call_layout;//语音通话
    private View            audio_call_layout;//视频通话
    private View            white_board_layout;//白板演示
    private View            share_screen_layout;//共享屏幕
    private CubeSessionType mChatType;

    private BaseChatActivity  mChatActivity;
    private ChatContainer     mContainer;
    private WBListener        mWBListener;
    private ArrayList<String> mList = new ArrayList<>();

    public ChatMoreFunctionFragment() {
    }

    public ChatMoreFunctionFragment(BaseChatActivity activity, CubeSessionType mChatType, ChatContainer container) {
        this.mChatActivity = activity;
        this.mChatType = mChatType;
        this.mContainer = container;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mList.add(mContainer.mChatId);
        mRootView = inflater.inflate(R.layout.fragment_chat_more_function, container, false);
        mWBListener = new WBListener(mChatActivity, mChatType, mList, "p2p");//单人白板可以不传groupId，占位而已
        mWBListener.setCreateCallback(this);
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WhiteBoardHandle.getInstance().removeWhiteBoardStateListeners(mWBListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        video_call_layout = mRootView.findViewById(R.id.video_call_layout);
        audio_call_layout = mRootView.findViewById(R.id.audio_call_layout);
        white_board_layout = mRootView.findViewById(R.id.white_board_layout);
        share_screen_layout = mRootView.findViewById(R.id.share_screen_layout);

        video_call_layout.setOnClickListener(this);
        audio_call_layout.setOnClickListener(this);
        white_board_layout.setOnClickListener(this);
        share_screen_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.video_call_layout) {
            if (mChatType.equals(CubeSessionType.P2P)) { //单聊
                if (CubeCore.getInstance().isCalling()) {
                    ToastUtil.showToast(mChatActivity, R.string.calling_please_try_again_later);
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("call_id", mContainer.mChatId);
                    bundle.putSerializable("call_state", CallStatus.VIDEO_OUTGOING);
                    bundle.putLong("call_time", 0l);
                    ARouter.getInstance().build(CubeConstants.Router.P2PCallActivity).withBundle("call_data", bundle).navigation();
                }
            }
            else { //群聊
                if (CubeCore.getInstance().isCalling()) {
                    ToastUtil.showToast(mChatActivity, R.string.calling_please_try_again_later);
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("select_type", 8);//视频会议首次创建
                    bundle.putString("group_id", mContainer.mChatId); //mChatId 就是 groupId
                    RouterUtil.navigation(CubeConstants.Router.SelectMemberActivity, bundle);
                }
            }
        }
        else if (id == R.id.audio_call_layout) {
            if (mChatType.equals(CubeSessionType.P2P)) {//单聊
                if (CubeCore.getInstance().isCalling()) {
                    ToastUtil.showToast(mChatActivity, R.string.calling_please_try_again_later);
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("call_id", mContainer.mChatId);
                    bundle.putSerializable("call_state", CallStatus.AUDIO_OUTGOING);
                    bundle.putLong("call_time", 0l);
                    ARouter.getInstance().build(CubeConstants.Router.P2PCallActivity).withBundle("call_data", bundle).navigation();
                }
            }
            else {//群聊
                if (CubeCore.getInstance().isCalling()) {
                    ToastUtil.showToast(mChatActivity, R.string.calling_please_try_again_later);
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("select_type", 2);//视频音频会议首次创建
                    bundle.putString("group_id", mContainer.mChatId); //mChatId 就是 groupId
                    RouterUtil.navigation(CubeConstants.Router.SelectMemberActivity, bundle);
                }
            }
        }
        else if (id == R.id.white_board_layout) { //白板
            if (mChatType.equals(CubeSessionType.P2P)) {//单聊
                if (CubeCore.getInstance().isCalling()) {
                    ToastUtil.showToast(mChatActivity, R.string.calling_please_try_again_later);
                }
                else {
                    //监听
                    WhiteBoardHandle.getInstance().addWhiteBoardStateListeners(mWBListener);
                    createWhiteBoard();
                }
            }
            else {//群聊
                Bundle bundle = new Bundle();
                bundle.putInt("select_type", 3);//白板首次创建
                bundle.putString("group_id", mContainer.mChatId); //mChatId 就是 groupId
                RouterUtil.navigation(CubeConstants.Router.SelectMemberActivity, bundle);
            }
        }
        else if (id == R.id.share_screen_layout) {
        }
    }

    //开启白板
    private void createWhiteBoard() {
        List<String> master = new ArrayList<>();
        master.add(CubeCore.getInstance().getCubeId());
        WhiteboardConfig whiteboardConfig = new WhiteboardConfig(GroupType.SHARE_WB, "p2p");
        whiteboardConfig.bindGroupId = "p2p";
        whiteboardConfig.maxNumber = 9;
        whiteboardConfig.isOpen = true;
        whiteboardConfig.masters = master;
        CubeEngine.getInstance().getWhiteboardService().create(whiteboardConfig);
    }

    //白板创建回调
    @Override
    public void onWBFinish(Whiteboard whiteboard) {

    }

    @Override
    public void onWBCreate(Whiteboard whiteboard) {

    }

    @Override
    public void onWBError(Whiteboard whiteboard, CubeError error) {

    }
}
