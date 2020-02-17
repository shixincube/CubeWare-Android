package cube.ware.service.message.chat.listener;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.ImageView;

import com.common.utils.ToastUtil;

import java.io.File;

import cube.service.CubeEngine;
import cube.ware.service.message.R;
import cube.ware.data.repository.CubeMessageRepository;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.manager.PlayerManager;
import cube.ware.service.message.chat.viewholder.MsgViewHolderAudio;

/**
 * 语音消息播放监听器
 *
 * @author PengZhenjin
 * @date 2016-11-17
 */
public class VoicePlayClickListener implements View.OnClickListener, SensorEventListener {
    private Context     context;
    private CubeMessage mMessage;
    private ImageView   voiceView;
    private boolean     isReceiver;
    private AnimationDrawable  voiceAnimation = null;
    private MsgViewHolderAudio mAudio         = null;

    public VoicePlayClickListener(Context context, MsgViewHolderAudio msgViewHolderAudio, CubeMessage message, ImageView voiceView, boolean isReceiver) {
        this.context = context;
        this.mAudio = msgViewHolderAudio;
        this.mMessage = message;
        this.voiceView = voiceView;
        this.isReceiver = isReceiver;
    }

    /**
     * 开始播放动画
     */
    private void startPlayAnimation() {
        if (isReceiver) {
            voiceView.setImageResource(R.drawable.cube_audio_animation_list_left);
        }
        else {
            voiceView.setImageResource(R.drawable.cube_audio_animation_list_right);
        }
        voiceAnimation = (AnimationDrawable) voiceView.getDrawable();
        voiceAnimation.start();
    }

    /**
     * 停止播放动画
     */
    public void stopPlayAnimation() {
        if (null != voiceAnimation) {
            voiceAnimation.stop();
        }
        if (isReceiver) {
            voiceView.setImageResource(R.drawable.ic_audio_animation_list_left_3);
        }
        else {
            voiceView.setImageResource(R.drawable.ic_audio_animation_list_right_3);
        }
    }

    /**
     * 开始播放
     *
     * @param voiceFilePath
     */
    public void startPlay(String voiceFilePath) {
        if (!mMessage.isPlay()) {
            mMessage.setPlay(true);
            //更新数据库语音消息播放状态
            CubeMessageRepository.getInstance().updateMessage(mMessage);
            mAudio.mIndicator.setVisibility(View.GONE);
        }

        if (mAudio.isShowSecretMessage()) {
            mAudio.startSecretTime(30 + mMessage.getDuration());
            mAudio.receipt();
        }

        PlayerManager.getInstance().play(voiceFilePath, new PlayerManager.PlayCallback() {
            @Override
            public void onPrepared() {
                startPlayAnimation();
            }

            @Override
            public void onComplete() {
                stopPlayAnimation();
            }

            @Override
            public void stop() {
                stopPlayAnimation();
            }
        });
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        PlayerManager.getInstance().stop();
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    private boolean isPlaying() {
        return PlayerManager.getInstance().isPlaying();
    }

    private boolean isSame() {
        return mMessage.getFilePath().equals(PlayerManager.getInstance().getFilePath());
    }

    @Override
    public void onClick(final View v) {
        if (CubeEngine.getInstance().getSession().isCalling()) {
            ToastUtil.showToast( context.getString(R.string.in_calling_tips));
            return;
        }

        if (isSame()) {
            if (isPlaying()) {
                this.stopPlay();
            }
            else {
                File file = new File(mMessage.getFilePath());
                this.startPlay(file.getAbsolutePath());
            }
        }
        else {
            this.stopPlay();
            File file = new File(mMessage.getFilePath());
            this.startPlay(file.getAbsolutePath());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}