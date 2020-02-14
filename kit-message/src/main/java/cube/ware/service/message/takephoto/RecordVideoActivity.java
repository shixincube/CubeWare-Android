package cube.ware.service.message.takephoto;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.utils.ScreenUtil;
import com.common.utils.ToastUtil;
import com.common.utils.log.LogUtil;
import com.lzy.imagepicker.view.SuperCheckBox;
import cube.ware.core.CubeConstants;
import cube.ware.service.message.R;
import cube.ware.service.message.file.FileActivity;
import cube.ware.service.message.takephoto.lisenter.CameraListener;
import cube.ware.service.message.takephoto.view.CameraInterface;
import cube.ware.service.message.takephoto.view.CameraView;
import cube.ware.service.message.takephoto.view.CaptureButton;
import cube.ware.utils.FileUtil;
import cube.ware.utils.SpUtil;
import java.io.File;

/**
 * 照相摄像主界面
 *
 * @author Wangxx
 * @date 2017/2/9
 */
@Route(path = CubeConstants.Router.RecordVideoActivity)
public class RecordVideoActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    public static final int    TAKE_VIDEO_CODE = 1000;
    public static final int    TAKE_PHOTO_CODE = 1001;
    public static final String TAKE_VIDEO_PATH = "TAKE_VIDEO_PATH";
    public static final String TAKE_PHOTO_PATH = "TAKE_PHOTO_PATH";
    public static final String PHOTO_IS_ORIGIN = "PHOTO_IS_ORIGIN";

    private CameraView    mCameraView;
    private SuperCheckBox mCbOrigin;
    private String        mFileSize;
    private boolean       mIsOrigin;
    private long          mAttachmentSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtil.setFullScreen(this);
        setContentView(R.layout.activity_record_video);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        initData();
        //NotificationUtil.getInstance(this).setAllowNotifySound(false);
    }

    private void initData() {
        this.mAttachmentSize = getIntent().getLongExtra("attachment_size", 0);
        this.mCbOrigin.setText(getString(R.string.origin));
        this.mCbOrigin.setOnCheckedChangeListener(this);
        this.mCbOrigin.setChecked(false);

        //设置视频保存路径
        this.mCameraView.setSaveVideoPath(SpUtil.getResourcePath());

        //JCameraView监听
        this.mCameraView.setCameraListener(new CameraListener() {
            @Override
            public void captureSuccess(final String url) {
                //获取图片bitmap
                returnPhotoPath(url, mIsOrigin);
            }

            @Override
            public void recordSuccess(String url) {
                //获取视频路径
                LogUtil.i("videoUrl: " + url);
                returnVideoPath(url);
            }

            @Override
            public void quit() {
                //退出按钮
                onBackPressed();
            }

            @Override
            public void success(File file) {
                mFileSize = FileUtil.formatFileSize(RecordVideoActivity.this, file.length());
                mCbOrigin.setText(getString(R.string.origin));
                mCbOrigin.setChecked(false);
                mCbOrigin.setVisibility(View.VISIBLE);
            }

            @Override
            public void cancel() {
                mCbOrigin.setVisibility(View.GONE);
            }
        });

        this.mCameraView.setErrorListener(new CameraInterface.ErrorListener() {
            @Override
            public void onError() {
                ToastUtil.showToast(getApplicationContext(), 0, getString(R.string.request_camera_permission));
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraView != null) {
            mCameraView.setErrorListener(null);
        }
        //NotificationUtil.getInstance(this).setAllowNotifySound(true);
    }

    private void initView() {
        this.mCameraView = (CameraView) findViewById(R.id.camera_view);
        this.mCbOrigin = (SuperCheckBox) findViewById(R.id.cb_origin_image);
        //        mCameraView.onResume();
    }

    /**
     * 返回视频消息
     *
     * @param videoPath
     */
    public void returnVideoPath(String videoPath) {
        Intent data = new Intent();
        data.putExtra(TAKE_VIDEO_PATH, videoPath);
        if (getParent() == null) {
            setResult(TAKE_VIDEO_CODE, data);
        }
        else {
            getParent().setResult(TAKE_VIDEO_CODE, data);
        }
        onBackPressed();
    }

    /**
     * 返回图片压缩路径
     *
     * @param photoPath
     * @param isOrigin
     */
    public void returnPhotoPath(String photoPath, boolean isOrigin) {
        long size = new File(photoPath).length();
        if ((mAttachmentSize + size) > FileActivity.FILE_MAX_SIZE) {
            ToastUtil.showToast(this, 0, this.getString(R.string.select_max_size));
            return;
        }
        Intent data = new Intent();
        data.putExtra(TAKE_PHOTO_PATH, photoPath);
        data.putExtra(PHOTO_IS_ORIGIN, isOrigin);
        if (getParent() == null) {
            setResult(TAKE_PHOTO_CODE, data);
        }
        else {
            getParent().setResult(TAKE_PHOTO_CODE, data);
        }
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (CaptureButton.isPause) {
            return;
        }
        mCameraView.onPause();
        ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).abandonAudioFocus(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //        mCameraView.onPause();
        //        mCameraView.release();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_origin_image) { // 原图
            if (isChecked) {
                this.mIsOrigin = true;
                this.mCbOrigin.setText(getString(R.string.origin_size, mFileSize));
            }
            else {
                this.mIsOrigin = false;
                this.mCbOrigin.setText(getString(R.string.origin));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 回收CameraView内存，存在bug
        mCameraView.release();
        this.overridePendingTransition(0, R.anim.bottom_out);
    }
}
