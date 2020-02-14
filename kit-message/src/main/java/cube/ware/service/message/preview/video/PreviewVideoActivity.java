package cube.ware.service.message.preview.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.common.base.BaseActivity;
import com.common.base.BasePresenter;
import com.common.utils.ToastUtil;
import com.common.utils.glide.GlideUtil;
import com.common.utils.log.LogUtil;
import cube.ware.service.message.R;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.repository.CubeMessageRepository;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.preview.video.videoplay.JCVideoPlayer;
import cube.ware.service.message.preview.video.videoplay.JCVideoPlayerStandard;
import cube.ware.utils.ImageUtil;
import cube.ware.widget.CountdownChronometer;
import cube.ware.widget.SecretProgress;
import java.io.File;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/10.
 */

public class PreviewVideoActivity extends BaseActivity {

    private String          mChatId;
    private long            mMessageSn;
    private CubeMessageType mMessageType;
    private int             mChatType;

    private PhotoView             mImage;
    private JCVideoPlayerStandard mVideo;
    private ImageView             mVideoBackIv;
    private SecretProgress        mProgressTime;
    private CountdownChronometer  mTipTime;
    private ProgressBar           mProgressBar;
    private String                mFilepath;

    public static void start(Context context, String chatId, int chatType, CubeMessageType messageType, long messageSn) {
        Intent intent = new Intent(context, PreviewVideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle messageData = new Bundle();
        messageData.putString("chat_id", chatId);
        messageData.putLong("chat_sn", messageSn);
        messageData.putInt("chat_type", chatType);
        messageData.putSerializable("chat_message_type", messageType);
        intent.putExtra("message_data", messageData);
        context.startActivity(intent);
    }

    public static void start(Context context, String filePath) {
        Intent intent = new Intent(context, PreviewVideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle messageData = new Bundle();
        messageData.putString("filepath", filePath);
        intent.putExtra("message_data", messageData);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_secret_preview;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        getArguments();
        mImage = (PhotoView) findViewById(R.id.secret_image_pv);
        mVideo = (JCVideoPlayerStandard) findViewById(R.id.secret_video_pv);
        mVideoBackIv = (ImageView) findViewById(R.id.secret_video_back_iv);
        mProgressTime = (SecretProgress) findViewById(R.id.secret_progress_time);
        mTipTime = (CountdownChronometer) findViewById(R.id.secret_tip_time);
        mProgressBar = (ProgressBar) findViewById(R.id.secret_progress_bar);
        mProgressTime.setProgressColor(Color.parseColor("#FA7479"));
        mProgressTime.setProgressTotalTime(30);
    }

    @Override
    protected void initData() {

        if (!TextUtils.isEmpty(mFilepath)) {
            mImage.setVisibility(View.GONE);
            mVideo.setVisibility(View.VISIBLE);
            mVideoBackIv.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            File videoFile = new File(mFilepath);
            if (null != videoFile && videoFile.exists()) {
                loadVideo(videoFile.getAbsolutePath());
            }
        }
        else {
            CubeMessageRepository.getInstance().queryMessageBySn(mMessageSn).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CubeMessage>() {
                @Override
                public void call(CubeMessage cubeMessage) {
                    if (null != cubeMessage) {
                        if (cubeMessage.getMessageType() == CubeMessageType.Image) {
                            mImage.setVisibility(View.VISIBLE);
                            mVideo.setVisibility(View.GONE);
                            mVideoBackIv.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.VISIBLE);
                            GlideUtil.loadImage(cubeMessage.getFileUrl(), mContext, mImage, false);
                        }
                        else {
                            mImage.setVisibility(View.GONE);
                            mVideo.setVisibility(View.VISIBLE);
                            mVideoBackIv.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            ImageUtil.displayImage(mContext, R.drawable.default_image, mVideo.thumbImageView, cubeMessage.getThumbUrl());

                            if (!TextUtils.isEmpty(cubeMessage.getFileUrl())) {
                                loadVideo(cubeMessage.getFileUrl());
                            }
                            else {
                                ToastUtil.showToast(CubeCore.getContext(), 0, "视频地址为空！");
                            }
                        }
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    LogUtil.e(throwable);
                    ToastUtil.showToast(CubeCore.getContext(), 0, "未找到视频");
                }
            });
        }
    }

    @Override
    protected void initListener() {
        mImage.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });
        mTipTime.setOnTimeCompleteListener(new CountdownChronometer.OnTimeCompleteListener() {
            @Override
            public void onTimeComplete() {
                finish();
            }
        });
        mVideoBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JCVideoPlayer.backPress();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    /**
     * 获取参数
     */
    private void getArguments() {
        Bundle bundle = getIntent().getBundleExtra("message_data");
        this.mChatId = bundle.getString("chat_id");
        this.mMessageSn = bundle.getLong("chat_sn");
        this.mMessageType = (CubeMessageType) bundle.getSerializable("chat_message_type");
        this.mChatType = bundle.getInt("chat_type");

        mFilepath = bundle.getString("filepath");
        LogUtil.i("聊天人：" + this.mChatId + " ### 消息类型：" + this.mMessageType + " ### 消息sn：" + this.mMessageSn);
    }

    /**
     * 加载视频
     *
     * @param videoUrl
     */
    private void loadVideo(String videoUrl) {
        try {

            mVideo.setUp(videoUrl, JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN);
            if (this.mProgressBar.getVisibility() == View.VISIBLE) {
                this.mProgressBar.setVisibility(View.GONE);
            }

            mVideo.startButton.performClick();
        } catch (Exception e) {
            LogUtil.e("loadVideo:" + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
        JCVideoPlayer.clearSavedProgress(this, null);
    }

    @Override
    public void onBackPressed() {
        JCVideoPlayer.backPress();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            setResult(RESULT_OK);
            finish();
            return false;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JCVideoPlayer.backPress();
    }
}
