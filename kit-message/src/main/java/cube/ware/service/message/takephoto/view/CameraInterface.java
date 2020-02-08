package cube.ware.service.message.takephoto.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.ImageView;
import com.common.utils.utils.DeviceUtil;
import com.common.utils.utils.UIHandler;
import com.common.utils.utils.log.LogUtil;
import cube.ware.service.message.takephoto.util.AngleUtil;
import cube.ware.service.message.takephoto.util.CameraParamUtil;
import cube.ware.service.message.takephoto.util.CheckPermission;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;

/**
 * camera操作单例
 *
 * @author Wangxx
 * @date 2017/5/25
 */
@SuppressWarnings("deprecation")
public class CameraInterface {
    public static final int MEDIA_QUALITY_HIGH                   = 5 * 1024 * 1024;
    public static final int MEDIA_QUALITY_MIDDLE                 = 16 * 100000;
    public static final int MEDIA_QUALITY_LOW                    = 12 * 100000;
    public static final int MEDIA_QUALITY_POOR                   = 8 * 100000;
    public static final int MEDIA_QUALITY_FUNNY                  = 4 * 100000;
    public static final int MEDIA_QUALITY_DESPAIR                = 2 * 100000;
    public static final int MEDIA_QUALITY_SORRY                  = 80000;
    public static final int MEDIA_QUALITY_SORRY_YOU_ARE_GOOD_MAN = 10000;

    private static final String TAG = "CameraInterface";

    private Camera  mCamera;
    private boolean isPreviewing = false;

    public boolean isPreviewing() {
        return isPreviewing;
    }

    private static CameraInterface mCameraInterface;

    private int SELECTED_CAMERA       = -1;
    private int CAMERA_POST_POSITION  = -1;
    private int CAMERA_FRONT_POSITION = -1;

    private final String FLASH_MODE_OFF = Camera.Parameters.FLASH_MODE_OFF;
    private final String FLASH_MODE_ON  = Camera.Parameters.FLASH_MODE_TORCH;
    private       String SELECTED_FLASH = FLASH_MODE_OFF;

    private SurfaceHolder mHolder    = null;
    private float         screenProp = -1.0f;

    private boolean           isRecorder = false;
    private MediaRecorder     mediaRecorder;
    private String            videoFileName;
    private String            savePath;
    private String            videoFileAbsPath;
    private Camera.Parameters backParams;
    private Camera.Parameters beforeParams;

    private ImageView mSwitchView;

    public void setSwitchView(ImageView mSwitchView) {
        this.mSwitchView = mSwitchView;
        if (mSwitchView != null) {
            cameraAngle = CameraParamUtil.getInstance().getCameraDisplayOrientation(mSwitchView.getContext(), SELECTED_CAMERA);
        }
    }

    private int preview_width;
    private int preview_height;

    private int                 angle               = 0;
    private int                 rotation            = 0;
    private int                 cameraAngle         = 90;//摄像头角度   默认为90度
    private SensorManager       sm                  = null;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
                return;
            }
            float[] values = event.values;
            angle = AngleUtil.getSensorAngle(values[0], values[1]);
            rotationAnimation();
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    //切换摄像头icon跟随手机角度进行旋转
    private void rotationAnimation() {
        if (mSwitchView == null) {
            return;
        }
        if (rotation != angle) {
            int start_rotaion = 0;
            int end_rotation = 0;
            switch (rotation) {
                case 0:
                    start_rotaion = 0;
                    switch (angle) {
                        case 90:
                            end_rotation = -90;
                            break;
                        case 270:
                            end_rotation = 90;
                            break;
                    }
                    break;
                case 90:
                    start_rotaion = -90;
                    switch (angle) {
                        case 0:
                            end_rotation = 0;
                            break;
                        case 180:
                            end_rotation = -180;
                            break;
                    }
                    break;
                case 180:
                    start_rotaion = 180;
                    switch (angle) {
                        case 90:
                            end_rotation = 270;
                            break;
                        case 270:
                            end_rotation = 90;
                            break;
                    }
                    break;
                case 270:
                    start_rotaion = 90;
                    switch (angle) {
                        case 0:
                            end_rotation = 0;
                            break;
                        case 180:
                            end_rotation = 180;
                            break;
                    }
                    break;
            }
            ObjectAnimator anim = ObjectAnimator.ofFloat(mSwitchView, "rotation", start_rotaion, end_rotation);
            anim.setDuration(500);
            anim.start();
            rotation = angle;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void setSavePath(String savePath) {
        this.savePath = savePath;
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static final int TYPE_RECORDER  = 0x090;
    public static final int TYPE_CAPTURE   = 0x091;
    private             int nowScaleRate   = 0;
    private             int recordScleRate = 0;

    public void setZoom(float zoom, int type) {
        if (mCamera == null) {
            return;
        }

        backParams = mCamera.getParameters();
        if (!backParams.isZoomSupported() && !backParams.isSmoothZoomSupported()) {
            return;
        }
        switch (type) {
            case TYPE_RECORDER:
                //如果不是录制视频中，上滑不会缩放
                if (!isRecorder) {
                    return;
                }
                if (zoom >= 0) {
                    //每移动50个像素缩放一个级别
                    int scaleRate = (int) (zoom / 20);
                    if (scaleRate <= backParams.getMaxZoom() && scaleRate >= nowScaleRate && recordScleRate != scaleRate) {
                        backParams.setZoom(scaleRate);
                        mCamera.setParameters(backParams);
                        recordScleRate = scaleRate;
                    }
                }
                break;
            case TYPE_CAPTURE:
                //每移动50个像素缩放一个级别
                int scaleRate = (int) (zoom / 20);
                if (scaleRate < backParams.getMaxZoom()) {
                    nowScaleRate += scaleRate;
                    if (nowScaleRate < 0) {
                        nowScaleRate = 0;
                    }
                    else if (nowScaleRate > backParams.getMaxZoom()) {
                        nowScaleRate = backParams.getMaxZoom();
                    }
                    backParams.setZoom(nowScaleRate);
                    mCamera.setParameters(backParams);
                }
                Log.i(TAG, "nowScaleRate = " + nowScaleRate);
                break;
        }
    }

    interface CamOpenOverCallback {
        void cameraHasOpened();

        void cameraSwitchSuccess();
    }

    private CameraInterface() {
        findAvailableCameras();
        SELECTED_CAMERA = CAMERA_POST_POSITION;
        savePath = "";
    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    public static void destroyCameraInterface() {
        if (mCameraInterface != null) {
            mCameraInterface = null;
        }
    }

    /**
     * open Camera
     */
    void doOpenCamera(CamOpenOverCallback callback) {
        if (!CheckPermission.isCameraUseable(SELECTED_CAMERA) && this.errorListener != null) {
            UIHandler.run(new Runnable() {
                @Override
                public void run() {
                    errorListener.onError();
                }
            });
            return;
        }
        if (mCamera == null) {
            openCamera(SELECTED_CAMERA);
        }
        callback.cameraHasOpened();
    }

    /**
     * open Camera
     */
    void doOpenCamera(CamOpenOverCallback callback, boolean isFront) {
        if (!CheckPermission.isCameraUseable(SELECTED_CAMERA) && this.errorListener != null) {
            UIHandler.run(new Runnable() {
                @Override
                public void run() {
                    errorListener.onError();
                }
            });
            return;
        }
        if (mCamera == null) {
            if (isFront) {
                SELECTED_CAMERA = CAMERA_FRONT_POSITION;
                openCamera(CAMERA_FRONT_POSITION);
            }
            else {
                SELECTED_CAMERA = CAMERA_POST_POSITION;
                openCamera(CAMERA_POST_POSITION);
            }
        }
        callback.cameraHasOpened();
    }

    private void openCamera(int id) {
        try {
            releaseCameraAndPreview();
            this.mCamera = Camera.open(id);
            if (backParams != null) {
                mCamera.setParameters(backParams);
            }
        } catch (Exception var3) {
            if (this.errorListener != null) {
                this.errorListener.onError();
            }
        }

        if (Build.VERSION.SDK_INT > 17 && this.mCamera != null) {
            try {
                this.mCamera.enableShutterSound(false);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "enable shutter sound faild");
            }
        }
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public synchronized void switchCamera(CamOpenOverCallback callback) {
        if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
            SELECTED_CAMERA = CAMERA_FRONT_POSITION;
            beforeParams = backParams;
        }
        else {
            SELECTED_CAMERA = CAMERA_POST_POSITION;
        }
        doStopCamera();
        mCamera = Camera.open(SELECTED_CAMERA);
        if (beforeParams != null && SELECTED_CAMERA == CAMERA_POST_POSITION) {
            mCamera.setParameters(beforeParams);
        }
        doStartPreview(mHolder, screenProp);
        callback.cameraSwitchSuccess();
    }

    public boolean isCameraPost() {
        return SELECTED_CAMERA == CAMERA_POST_POSITION;
    }

    /**
     * 设置闪光灯模式
     */
    public synchronized void switchFlashMode() {
        if (SELECTED_FLASH.equals(FLASH_MODE_OFF)) {
            SELECTED_FLASH = FLASH_MODE_TORCH;
        }
        else {
            SELECTED_FLASH = FLASH_MODE_OFF;
        }
        if (mCamera == null) {
            return;
        }
        backParams = mCamera.getParameters();
        backParams.setFlashMode(SELECTED_FLASH);
        mCamera.setParameters(backParams);
        Log.i(TAG, "=== Switch FlashMode ===" + SELECTED_FLASH);
    }

    public boolean isFlashOpen() {
        return SELECTED_FLASH.equals(FLASH_MODE_ON) || SELECTED_FLASH.equals(FLASH_MODE_TORCH);
    }

    /**
     * doStartPreview
     */
    void doStartPreview(SurfaceHolder holder, float screenProp) {
        LogUtil.d(TAG, "doStartPreview");
        if (this.screenProp < 0) {
            this.screenProp = screenProp;
        }
        if (holder == null) {
            return;
        }
        this.mHolder = holder;
        if (mCamera != null) {
            try {
                backParams = mCamera.getParameters();
                Camera.Size previewSize = CameraParamUtil.getInstance().getPreviewSize(backParams.getSupportedPreviewSizes(), 1500, screenProp);
                Camera.Size pictureSize = CameraParamUtil.getInstance().getPictureSize(backParams.getSupportedPictureSizes(), 1500, screenProp);

                backParams.setPreviewSize(previewSize.width, previewSize.height);

                preview_width = previewSize.width;
                preview_height = previewSize.height;

                backParams.setPictureSize(pictureSize.width, pictureSize.height);

                if (CameraParamUtil.getInstance().isSupportedFocusMode(backParams.getSupportedFocusModes(), Camera.Parameters.FOCUS_MODE_AUTO)) {
                    backParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                if (CameraParamUtil.getInstance().isSupportedPictureFormats(backParams.getSupportedPictureFormats(), ImageFormat.JPEG)) {
                    backParams.setPictureFormat(ImageFormat.JPEG);
                    backParams.setJpegQuality(100);
                }
                mCamera.setParameters(backParams);

                /**
                 * SurfaceView
                 */
                mCamera.setPreviewDisplay(holder);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
                isPreviewing = true;
            } catch (Exception e) {
                LogUtil.e(TAG, "=== Start Preview comes Exception e" + e.getMessage());
                e.printStackTrace();
            }
        }
        Log.i(TAG, "=== Start Preview ===");
    }

    /**
     * 停止预览，释放Camera
     */
    void doStopCamera() {
        if (null != mCamera) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewDisplay(null);
                isPreviewing = false;
                mCamera.release();
                mCamera = null;
                Log.i(TAG, "=== Stop Camera ===");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void doDestroyCamera() {
        if (null != mCamera) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewDisplay(null);
                mHolder = null;
                isPreviewing = false;
                mCamera.release();
                mCamera = null;
                destroyCameraInterface();
                Log.i(TAG, "=== Stop Camera ===");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拍照
     */
    private int nowAngle;

    void takePicture(final TakePictureCallback callback) {
        if (mCamera == null) {
            return;
        }
        switch (cameraAngle) {
            case 90:
                nowAngle = Math.abs(angle + cameraAngle) % 360;
                break;
            case 270:
                nowAngle = Math.abs(cameraAngle - angle);
                break;
        }

        if (!isPreviewing) {
            return;
        }

        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                    matrix.setRotate(nowAngle);
                }
                else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                    matrix.setRotate(360 - nowAngle);
                    matrix.postScale(-1, 1);
                }

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (callback != null) {
                    if (nowAngle == 90 || nowAngle == 270) {
                        callback.captureResult(bitmap, saveImage(bitmap), true);
                    }
                    else {
                        callback.captureResult(bitmap, saveImage(bitmap), false);
                    }
                }
            }
        });
    }

    private String saveImage(Bitmap bmp) {
        if (savePath.equals("")) {
            savePath = Environment.getExternalStorageDirectory().getPath();
        }
        File appDir = new File(savePath, "image");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void startRecord(Surface surface, ErrorCallback callback) {

        if (isRecorder) {
            return;
        }
        int nowAngle = (angle + 90) % 360;
        if (mCamera == null) {
            openCamera(SELECTED_CAMERA);
        }
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        backParams = mCamera.getParameters();
        List<String> focusModes = backParams.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            backParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(backParams);
        mCamera.unlock();
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        Camera.Size videoSize;
        if (backParams.getSupportedVideoSizes() == null) {
            videoSize = CameraParamUtil.getInstance().getPictureSize(backParams.getSupportedPreviewSizes(), 1000, screenProp);
        }
        else {
            videoSize = CameraParamUtil.getInstance().getPictureSize(backParams.getSupportedVideoSizes(), 1000, screenProp);
        }

        //        Log.i(TAG, "setVideoSize    width = " + videoSize.width + "height = " + videoSize.height);
        if (videoSize.width == videoSize.height) {
            mediaRecorder.setVideoSize(preview_width, preview_height);
        }
        else {
            mediaRecorder.setVideoSize(videoSize.width, videoSize.height);
        }

        if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
            //手机预览倒立的处理
            if (cameraAngle == 270) {
                //横屏
                if (nowAngle == 0) {
                    mediaRecorder.setOrientationHint(180);
                }
                else if (nowAngle == 270) {
                    mediaRecorder.setOrientationHint(270);
                }
                else {
                    mediaRecorder.setOrientationHint(90);
                }
            }
            else {
                if (nowAngle == 90) {
                    mediaRecorder.setOrientationHint(270);
                }
                else if (nowAngle == 270) {
                    mediaRecorder.setOrientationHint(90);
                }
                else {
                    mediaRecorder.setOrientationHint(nowAngle);
                }
            }
        }
        else {
            mediaRecorder.setOrientationHint(nowAngle);
        }

        if (DeviceUtil.isHuaWeiRongyao()) {
            mediaRecorder.setVideoEncodingBitRate(MEDIA_QUALITY_FUNNY);
        }
        else {
            mediaRecorder.setVideoEncodingBitRate(MEDIA_QUALITY_MIDDLE);
        }
        mediaRecorder.setPreviewDisplay(surface);

        videoFileName = "video_" + System.currentTimeMillis() + ".mp4";
        if (savePath.equals("")) {
            savePath = Environment.getExternalStorageDirectory().getPath();
        }
        File appDir = new File(savePath, "video");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, videoFileName);
        videoFileAbsPath = file.getAbsolutePath();
        mediaRecorder.setOutputFile(videoFileAbsPath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecorder = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.i(TAG, "startRecord IllegalStateException");
            if (this.errorListener != null) {
                this.errorListener.onError();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "startRecord IOException");
            if (this.errorListener != null) {
                this.errorListener.onError();
            }
        } catch (RuntimeException e) {
            Log.i(TAG, "startRecord RuntimeException");
        }
    }

    void stopRecord(boolean isShort, StopRecordCallback callback) {
        if (!isRecorder) {
            return;
        }
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setOnInfoListener(null);
            mediaRecorder.setPreviewDisplay(null);
            try {
                mediaRecorder.stop();
            } catch (RuntimeException e) {
                e.printStackTrace();
                mediaRecorder = null;
                mediaRecorder = new MediaRecorder();
                Log.i(TAG, "stop RuntimeException");
            } catch (Exception e) {
                e.printStackTrace();
                mediaRecorder = null;
                mediaRecorder = new MediaRecorder();
                Log.i(TAG, "stop Exception");
            } finally {
                if (mediaRecorder != null) {
                    mediaRecorder.release();
                }
                mediaRecorder = null;
                isRecorder = false;
            }
            if (isShort) {
                //delete video file
                boolean result = true;
                File file = new File(videoFileAbsPath);
                if (file.exists()) {
                    result = file.delete();
                }
                if (result) {
                    callback.recordResult(null);
                }
                return;
            }
            doStopCamera();
            callback.recordResult(videoFileAbsPath);
        }
    }

    private void findAvailableCameras() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, info);
            switch (info.facing) {
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    CAMERA_FRONT_POSITION = info.facing;
                    break;
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    CAMERA_POST_POSITION = info.facing;
                    break;
            }
        }
    }

    public void handleFocus(final float x, final float y, final FocusCallback callback) {
        if (mCamera == null) {
            return;
        }
        try {
            backParams = mCamera.getParameters();
            Camera.Size previewSize = backParams.getPreviewSize();
            Rect focusRect = calculateTapArea(x, y, 1f, previewSize);
            mCamera.cancelAutoFocus();
            if (backParams.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<>();
                focusAreas.add(new Camera.Area(focusRect, 800));
                backParams.setFocusAreas(focusAreas);
            }
            else {
                Log.i(TAG, "focus areas not supported");
                callback.focusSuccess();
                return;
            }
            final String currentFocusMode = backParams.getFocusMode();
            backParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            Log.i(TAG, "width = " + backParams.getPreviewSize().width + "height = " + backParams.getPreviewSize().height);

            mCamera.setParameters(backParams);

            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        backParams = camera.getParameters();
                        backParams.setFocusMode(currentFocusMode);
                        camera.setParameters(backParams);
                        callback.focusSuccess();
                    }
                    else {
                        handleFocus(x, y, callback);
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    private static Rect calculateTapArea(float x, float y, float coefficient, Camera.Size previewSize) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / previewSize.width - 1000);
        int centerY = (int) (y / previewSize.height - 1000);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    interface StopRecordCallback {
        void recordResult(String url);
    }

    interface TakePictureCallback {
        void captureResult(Bitmap bitmap, String imagePath, boolean isVertical);
    }

    interface FocusCallback {
        void focusSuccess();
    }

    interface ErrorCallback {
        void onError();
    }

    private ErrorListener errorListener;

    public interface ErrorListener {
        void onError();
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void registerSensorManager(Context context) {
        if (sm == null) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        sm.registerListener(sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterSensorManager(Context context) {
        if (sm == null) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        sm.unregisterListener(sensorEventListener);
    }
}
