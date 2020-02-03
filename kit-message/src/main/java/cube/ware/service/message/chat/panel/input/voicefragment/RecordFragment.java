package cube.ware.service.message.chat.panel.input.voicefragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.common.utils.utils.log.LogUtil;

import cube.service.message.VoiceClipMessage;
import cube.ware.service.message.R;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.message.manager.MessageManager;
import cube.ware.service.message.chat.BaseChatActivity;
import cube.ware.service.message.chat.ChatContainer;
import cube.ware.widget.voice.AudioRecordLayout;

/**
 * @author Wangxx
 * @date 2017/2/8
 */
@SuppressLint("ValidFragment")
public class RecordFragment extends Fragment {
    private AudioRecordLayout mAudioRecordLayout;
    private View              mRootView;
    private ChatContainer     mChatContainer;
    private BaseChatActivity  mChatActivity;

    /**
     * Fragment必须要有空的构造函数，否则直接crash。因为Fragment源码中用到反射构造了对象，是无参数的构造函数
     * @SuppressLint("ValidFragment")按理说忽略警告是有用的，但是在这好像没用
     */
    public RecordFragment() {

    }

    public RecordFragment(ChatContainer chatContainer, BaseChatActivity chatActivity) {
        this.mChatContainer = chatContainer;
        this.mChatActivity = chatActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_chat_record, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAudioRecordLayout = (AudioRecordLayout) mRootView.findViewById(R.id.record_layout);
        mAudioRecordLayout.setChatActivity(mChatActivity);

        mAudioRecordLayout.setOnRecordStatusListener(new AudioRecordLayout.onRecordStatusListener() {

            @Override
            public void onRecordStart() {
                Log.v("test", "开始");
            }

            @Override
            public void onRecordComplete(VoiceClipMessage vcm) {
                Log.v("test", "完成");
                boolean isSecret = mChatContainer.mSessionType == CubeSessionType.Secret;
                VoiceClipMessage voiceClipMessage = MessageManager.getInstance().buildVoiceMessage(mChatContainer.mChatActivity, CubeSessionType.P2P, CubeCore.getInstance().getCubeId(), mChatContainer.mChatId, vcm, isSecret);
                MessageManager.getInstance().sendMessage(mChatContainer.mChatActivity, voiceClipMessage).subscribe();
            }

            @Override
            public void onAuditionStart(VoiceClipMessage vcm) {
                LogUtil.e("试听" + vcm.getDuration());
                //将当前fragment加入到返回栈中
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.voice_view, new PlayFragment(mChatContainer, vcm)).commit();
            }

            @Override
            public void onRecordCancel() {
                Log.v("test", "取消");
            }
        });
    }
}
