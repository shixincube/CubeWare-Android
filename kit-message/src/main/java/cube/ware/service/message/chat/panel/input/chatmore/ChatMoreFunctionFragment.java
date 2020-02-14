package cube.ware.service.message.chat.panel.input.chatmore;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.router.RouterUtil;
import com.common.utils.ToastUtil;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CallStatus;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.activity.base.BaseChatActivity;
import cube.ware.service.message.chat.ChatContainer;

/**
 * @author zzy
 * @date 2018/9/10
 * 聊天页面+展开Fragment
 */
@SuppressLint("ValidFragment")
public class ChatMoreFunctionFragment extends Fragment implements View.OnClickListener {

    private View            mRootView;
    private CubeSessionType mChatType;

    private BaseChatActivity mChatActivity;
    private ChatContainer    mContainer;

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
        mRootView = inflater.inflate(R.layout.fragment_chat_more_function, container, false);
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //语音通话
        View video_call_layout = mRootView.findViewById(R.id.video_call_layout);
        //视频通话
        View audio_call_layout = mRootView.findViewById(R.id.audio_call_layout);
        //白板演示
        View white_board_layout = mRootView.findViewById(R.id.white_board_layout);
        //共享屏幕
        View share_screen_layout = mRootView.findViewById(R.id.share_screen_layout);

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
                    //createWhiteBoard();
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
}
