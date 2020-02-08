package cube.ware.service.message.takephoto.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;
import com.common.utils.utils.log.LogUtil;
import cube.ware.service.message.R;
import cube.ware.service.message.takephoto.lisenter.CameraListener;
import cube.ware.service.message.takephoto.lisenter.CaptureListener;
import cube.ware.service.message.takephoto.lisenter.ReturnListener;
import cube.ware.service.message.takephoto.lisenter.TypeListener;
import java.io.File;
import java.io.IOException;

/**
 * 自定义仿微信拍照
 *
 * @author Wangxx
 * @date 2017/5/25
 */
public class CameraView extends FrameLayout implements CameraInterface.CamOpenOverCallback, SurfaceHolder.Callback {
    private static final String TAG = "CameraView";

    private static final int TYPE_PICTURE = 0x001;
    private static final int TYPE_VIDEO   = 0x002;

    private CameraListener mCameraListener;

    private Context       mContext;
    private VideoView     mVideoView;
    private ImageView     mPhoto;
    private ImageView     mSwitchFlash;
    private CaptureLayout mCaptureLayout;
    private FocusView     mFocusView;

    private MediaPlayer mMediaPlayer;

    private int   layoutWidth;
    private int   focusSize;
    private float screenProp;

    private Bitmap captureBitmap;
    private String videoUrl;
    private String imageUrl;
    private int    type = -1;

    private              int CAMERA_STATE  = -1;
    private static final int STATE_IDLE    = 0x010;
    private static final int STATE_RUNNING = 0x020;
    private static final int STATE_WAIT    = 0x030;

    private boolean stopping    = false;
    private boolean isBorrow    = false;
    private boolean takePicture = false;
    private boolean onlyPause   = false;

    /**
     * switch bottom param
     */
    private int duration    = 0;
    private int flashOffSrc = 0;
    private int flashOnSrc  = 0;
    private int flashSize   = 0;
    private int flashMargin = 0;

    /**
     * constructor
     */
    public CameraView(Context context) {
        this(context, null);
    }

    /**
     * constructor
     */
    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * constructor
     */
    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //get AttributeSet
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CameraView, defStyleAttr, 0);
        flashSize = a.getDimensionPixelSize(R.styleable.CameraView_flashSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 35, getResources().getDisplayMetrics()));
        flashMargin = a.getDimensionPixelSize(R.styleable.CameraView_flashMargin, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        flashOffSrc = a.getResourceId(R.styleable.CameraView_flashOffSrc, R.drawable.selector_camera_flash_off);
        flashOnSrc = a.getResourceId(R.styleable.CameraView_flashOnSrc, R.drawable.selector_camera_flash_open);
        duration = a.getInteger(R.styleable.CameraView_duration_max, 10 * 1000);
        a.recycle();
        initData();
        initView();
    }

    private void initData() {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        layoutWidth = outMetrics.widthPixels;
        focusSize = layoutWidth / 4;
        CAMERA_STATE = STATE_IDLE;
    }

    private void initView() {
        setWillNotDraw(false);
        this.setBackgroundColor(0xff000000);
        //VideoView
        mVideoView = new VideoView(mContext);
        LayoutParams videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mVideoView.setLayoutParams(videoViewParam);

        //mPhoto
        mPhoto = new ImageView(mContext);
        LayoutParams photoParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPhoto.setLayoutParams(photoParam);
        mPhoto.setBackgroundColor(0xff000000);
        mPhoto.setVisibility(GONE);

        //switchFlash
        mSwitchFlash = new ImageView(mContext);
        LayoutParams imageFlashParam = new LayoutParams(flashSize, flashSize);
        imageFlashParam.gravity = Gravity.LEFT;
        imageFlashParam.setMargins(flashMargin, flashMargin, 0, 0);
        mSwitchFlash.setLayoutParams(imageFlashParam);
        initFlashModel();
        setFlashModelShow();
        mSwitchFlash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFalsh();
            }
        });

        //CaptureLayout
        mCaptureLayout = new CaptureLayout(mContext);
        LayoutParams layout_param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout_param.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        layout_param.setMargins(0, 0, 0, 40);
        mCaptureLayout.setLayoutParams(layout_param);
        mCaptureLayout.setDuration(duration);

        //mFocusView

        mFocusView = new FocusView(mContext, focusSize);
        LayoutParams focus_param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mFocusView.setLayoutParams(focus_param);
        mFocusView.setVisibility(GONE);

        //add view to ParentLayout
        this.addView(mVideoView);
        this.addView(mPhoto);
        this.addView(mSwitchFlash);
        this.addView(mCaptureLayout);
        this.addView(mFocusView);
        //START >>>>>>> captureLayout lisenter callback
        mCaptureLayout.setCaptureListener(new CaptureListener() {
            @Override
            public void takePictures() {
                if (CAMERA_STATE != STATE_IDLE || takePicture || !CameraInterface.getInstance().isPreviewing()) {
                    return;
                }
                CAMERA_STATE = STATE_RUNNING;
                takePicture = true;
                mFocusView.setVisibility(GONE);
                CameraInterface.getInstance().takePicture(new CameraInterface.TakePictureCallback() {
                    @Override
                    public void captureResult(Bitmap bitmap, String imagePath, boolean isVertical) {
                        captureBitmap = bitmap;
                        imageUrl = imagePath;
                        CameraInterface.getInstance().doStopCamera();
                        type = TYPE_PICTURE;
                        isBorrow = true;
                        CAMERA_STATE = STATE_WAIT;
                        if (isVertical) {
                            mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        else {
                            mPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        }
                        mPhoto.setImageBitmap(bitmap);
                        mPhoto.setVisibility(VISIBLE);
                        mCaptureLayout.startAlphaAnimation();
                        mCaptureLayout.startTypeBtnAnimator();
                        takePicture = false;
                        mSwitchFlash.setVisibility(GONE);
                        if (mCameraListener != null && imagePath != null) {
                            mCameraListener.success(new File(imagePath));
                        }
                        mCaptureLayout.isRecord(false);
                    }
                });
            }

            @Override
            public void recordShort(long time) {
                if (CAMERA_STATE != STATE_RUNNING && stopping) {
                    return;
                }
                stopping = true;
                mCaptureLayout.setTextWithAnimation();
                if (mCaptureLayout != null) {
                    mCaptureLayout.onResume();
                }

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CameraInterface.getInstance().stopRecord(true, new CameraInterface.StopRecordCallback() {
                            @Override
                            public void recordResult(String url) {
                                Log.i(TAG, "stopping ...");
                                mCaptureLayout.isRecord(false);
                                CAMERA_STATE = STATE_IDLE;
                                stopping = false;
                                isBorrow = false;
                            }
                        });
                    }
                }, 1500 - time);
            }

            @Override
            public void recordStart() {
                if (CAMERA_STATE != STATE_IDLE && stopping) {
                    return;
                }
                mCaptureLayout.isRecord(true);
                isBorrow = true;
                CAMERA_STATE = STATE_RUNNING;
                mFocusView.setVisibility(GONE);
                CameraInterface.getInstance().startRecord(mVideoView.getHolder().getSurface(), new CameraInterface.ErrorCallback() {
                    @Override
                    public void onError() {
                        mCaptureLayout.isRecord(false);
                        CAMERA_STATE = STATE_WAIT;
                        stopping = false;
                        isBorrow = false;
                    }
                });
            }

            @Override
            public void recordEnd(long time) {
                CameraInterface.getInstance().stopRecord(false, new CameraInterface.StopRecordCallback() {
                    @Override
                    public void recordResult(final String url) {
                        CAMERA_STATE = STATE_WAIT;
                        videoUrl = url;
                        type = TYPE_VIDEO;
                        previewVideo();
                    }
                });
            }

            @Override
            public void recordZoom(float zoom) {
                CameraInterface.getInstance().setZoom(zoom, CameraInterface.TYPE_RECORDER);
            }
        });
        mCaptureLayout.setTypeListener(new TypeListener() {
            @Override
            public void cancel() {
                if (CAMERA_STATE == STATE_WAIT) {
                    release();
                    handlerPictureOrVideo(type, false);
                    CameraInterface.getInstance().doOpenCamera(CameraView.this);
                }
            }

            @Override
            public void confirm() {
                if (CAMERA_STATE == STATE_WAIT) {
                    release();
                    handlerPictureOrVideo(type, true);
                }
            }
        });
        mCaptureLayout.setReturnListener(new ReturnListener() {
            @Override
            public void onReturn() {
                if (mCameraListener != null && !takePicture) {
                    mCameraListener.quit();
                }
            }

            @Override
            public void onSwitch(boolean isPost) {
                mSwitchFlash.setVisibility(isPost ? VISIBLE : GONE);
            }
        });
        //END >>>>>>> captureLayout lisenter callback
        mVideoView.getHolder().addCallback(this);
    }

    private void switchFalsh() {
        if (isBorrow) {
            return;
        }
        new Thread() {
            /**
             * switch camera
             */
            @Override
            public void run() {
                CameraInterface.getInstance().switchFlashMode();
                mSwitchFlash.post(new Runnable() {
                    @Override
                    public void run() {
                        //更新UI
                        setFlashModel();
                    }
                });
            }
        }.start();
    }

    public void release() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void setFlashModelShow() {
        if (CameraInterface.getInstance().isCameraPost()) {
            mSwitchFlash.setVisibility(VISIBLE);
        }
        else {
            mSwitchFlash.setVisibility(GONE);
        }
    }

    private void initFlashModel() {
        mSwitchFlash.setImageResource(flashOffSrc);
    }

    private void setFlashModel() {
        if (CameraInterface.getInstance().isFlashOpen()) {
            mSwitchFlash.setImageResource(flashOnSrc);
        }
        else {
            mSwitchFlash.setImageResource(flashOffSrc);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float widthSize = MeasureSpec.getSize(widthMeasureSpec);
        float heightSize = MeasureSpec.getSize(heightMeasureSpec);
        screenProp = heightSize / widthSize;
    }

    @Override
    public synchronized void cameraHasOpened() {
        LogUtil.i(TAG, "cameraHasOpened");
        if (onlyPause) {
            return;
        }
        if (mSwitchFlash != null) {
            setFlashModelShow();
        }
        CameraInterface.getInstance().doStartPreview(mVideoView.getHolder(), screenProp);
        post(new Runnable() {
            @Override
            public void run() {
                if (getWidth() > 0 && getHeight() > 0) {
                    setFocusViewWidthAnimation(getWidth() / 2, getHeight() / 2);
                }
            }
        });
    }

    @Override
    public void cameraSwitchSuccess() {
    }

    /**
     * start preview
     */
    public void onResume() {
        CameraInterface.getInstance().registerSensorManager(mContext);
        if (mCaptureLayout != null) {
            mCaptureLayout.onResume();
        }
        if (onlyPause && CAMERA_STATE == STATE_IDLE) {
            new Thread() {
                @Override
                public void run() {
                    CameraInterface.getInstance().doOpenCamera(CameraView.this);
                }
            }.start();
        }
        mFocusView.setVisibility(GONE);
        setFlashModel();
    }

    /**
     * stop preview
     */
    public void onPause() {
        onlyPause = true;
        CameraInterface.getInstance().unregisterSensorManager(mContext);
        if (mCaptureLayout != null) {
            mCaptureLayout.onPause();
        }
        CameraInterface.getInstance().doStopCamera();
    }

    private boolean firstTouch       = true;
    private float   firstTouchLength = 0;
    private int     zoomScale        = 0;

    /**
     * handler touch focus
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1) {
                    //显示对焦指示器
                    setFocusViewWidthAnimation(event.getX(), event.getY());
                }
                if (event.getPointerCount() == 2) {
                    Log.i(TAG, "ACTION_DOWN = " + 2);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    firstTouch = true;
                }
                if (event.getPointerCount() == 2) {
                    //第一个点
                    float point_1_X = event.getX(0);
                    float point_1_Y = event.getY(0);
                    //第二个点
                    float point_2_X = event.getX(1);
                    float point_2_Y = event.getY(1);

                    float result = (float) Math.sqrt(Math.pow(point_1_X - point_2_X, 2) + Math.pow(point_1_Y - point_2_Y, 2));

                    if (firstTouch) {
                        firstTouchLength = result;
                        firstTouch = false;
                    }

                    if ((int) (result - firstTouchLength) / 20 != 0) {
                        firstTouch = true;
                        CameraInterface.getInstance().setZoom(result - firstTouchLength, CameraInterface.TYPE_CAPTURE);
                    }

                    Log.i(TAG, "result = " + (result - firstTouchLength));
                }
                break;
            case MotionEvent.ACTION_UP:
                firstTouch = true;
                break;
        }
        //        return super.onTouchEvent(event);
        return true;
    }

    /**
     * focusview animation
     */
    private void setFocusViewWidthAnimation(float x, float y) {
        if (isBorrow) {
            return;
        }
        if (y > mCaptureLayout.getTop()) {
            return;
        }
        mFocusView.setVisibility(VISIBLE);

        int mFocusViewX = (mFocusView.getWidth() > 0 ? mFocusView.getWidth() : focusSize) / 2;
        int mFocusViewY = (mFocusView.getHeight() > 0 ? mFocusView.getHeight() : focusSize) / 2;

        if (x < mFocusViewX) {
            x = mFocusViewX;
        }
        if (y < mFocusViewY) {
            y = mFocusViewY;
        }

        if (x > layoutWidth - mFocusViewX) {
            x = layoutWidth - mFocusViewX;
        }
        if (y > mCaptureLayout.getTop() - mFocusViewY) {
            y = mCaptureLayout.getTop() - mFocusViewY;
        }

        mFocusView.setX(x - mFocusViewX);
        mFocusView.setY(y - mFocusViewY);

        CameraInterface.getInstance().handleFocus(x, y, new CameraInterface.FocusCallback() {
            @Override
            public void focusSuccess() {
                mFocusView.setVisibility(GONE);
            }
        });

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFocusView, "scaleX", 1, 0.6f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFocusView, "scaleY", 1, 0.6f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFocusView, "alpha", 1f, 0.3f, 1f, 0.3f, 1f, 0.3f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY).before(alpha);
        animSet.setDuration(400);
        animSet.start();
    }

    public void setCameraListener(CameraListener jCameraListener) {
        this.mCameraListener = jCameraListener;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void handlerPictureOrVideo(int type, boolean confirm) {
        if (mCameraListener == null || type == -1) {
            return;
        }
        switch (type) {
            case TYPE_PICTURE:
                if (confirm && !TextUtils.isEmpty(imageUrl)) {
                    mCameraListener.captureSuccess(imageUrl);
                }
                else {
                    mPhoto.setVisibility(GONE);
                    //删除图片
                    File file = new File(imageUrl);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (captureBitmap != null) {
                        captureBitmap.recycle();
                    }
                    captureBitmap = null;
                    mCameraListener.cancel();
                }
                break;
            case TYPE_VIDEO:
                if (confirm) {
                    //回调录像成功后的URL
                    mCameraListener.recordSuccess(videoUrl);
                }
                else {
                    //删除视频
                    File file = new File(videoUrl);
                    if (file.exists()) {
                        file.delete();
                    }
                    //                    mCameraListener.cancel();
                }
                mCaptureLayout.isRecord(false);
                LayoutParams videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                //videoViewParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                mVideoView.setLayoutParams(videoViewParam);
                if (mCaptureLayout != null) {
                    mCaptureLayout.onResume();
                }
                break;
        }
        isBorrow = false;
        CAMERA_STATE = STATE_IDLE;
    }

    public void setSaveVideoPath(String path) {
        CameraInterface.getInstance().setSavePath(path);
    }

    /**
     * TextureView resize
     */
    public void updateVideoViewSize(float videoWidth, float videoHeight) {
        if (videoWidth > videoHeight) {
            LayoutParams videoViewParam;
            int height = (int) ((videoHeight / videoWidth) * getWidth());
            videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, height);
            videoViewParam.gravity = Gravity.CENTER;
            mVideoView.setLayoutParams(videoViewParam);
        }
    }

    /**
     * forbidden audio
     */
    public void enableShutterSound(boolean enable) {
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (CAMERA_STATE == STATE_IDLE) {
            //更新UI
            CameraInterface.getInstance().doOpenCamera(CameraView.this, false);
        }
        else if (CAMERA_STATE == STATE_WAIT && !TextUtils.isEmpty(videoUrl)) {
            previewVideo();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        onlyPause = false;
        CameraInterface.getInstance().doDestroyCamera();
    }

    //启动Camera错误回调
    public void setErrorListener(CameraInterface.ErrorListener errorListener) {
        CameraInterface.getInstance().setErrorListener(errorListener);
    }

    private void previewVideo() {
        if (!TextUtils.isEmpty(videoUrl)) {
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(videoUrl)) {
                        try {
                            if (mMediaPlayer == null) {
                                mMediaPlayer = new MediaPlayer();
                            }
                            else {
                                mMediaPlayer.reset();
                            }
                            Log.i(TAG, "URL = " + videoUrl);
                            mMediaPlayer.setDataSource(videoUrl);
                            mMediaPlayer.setSurface(mVideoView.getHolder().getSurface());
                            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                @Override
                                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                    updateVideoViewSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
                                }
                            });
                            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mMediaPlayer.start();
                                }
                            });
                            mMediaPlayer.setLooping(true);
                            mMediaPlayer.prepare();
                        } catch (IOException e) {
                            LogUtil.i("" + e.getMessage());
                        }
                    }
                }
            }).start();
        }
    }
}
