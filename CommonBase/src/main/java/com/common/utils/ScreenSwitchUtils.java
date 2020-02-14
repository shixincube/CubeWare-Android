package com.common.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

/**
 * 用于切换横竖屏操作
 * 白板和共享屏幕皆会使用
 * @author Wangxx
 * @date 2017/8/18
 */

public class ScreenSwitchUtils {

    private volatile static ScreenSwitchUtils mInstance;

    private final int     SENSOR_CHANGE = 888;
    // 是否是竖屏
    private       boolean isPortrait    = true;
    private       int     degree        = 90;

    private SensorManager sm;
    private OrientationSensorListener listener;
    private Sensor sensor;

    private SensorManager sm1;
    private Sensor sensor1;
    private OrientationSensorListener1 listener1;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SENSOR_CHANGE:
                    int lastDegree = degree;
                    int orientation = msg.arg1;
                    if (orientation > 45 && orientation < 135) {
                        degree = 270;
                    }
                    else if (orientation > 135 && orientation < 225) {
                        degree = 180;
                    }
                    else if (orientation > 225 && orientation < 315) {
                        if (isPortrait) {
                            isPortrait = false;
                        }
                        degree = 90;
                    }
                    else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                        if (!isPortrait) {
                            isPortrait = true;
                        }
                        degree = 0;
                    }
                    if (lastDegree != degree) {
                        if (mChangedListener != null) {
                            mChangedListener.onConfigurationChanged(degree);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /** 返回ScreenSwitchUtils单例 **/
    public static ScreenSwitchUtils init(Context context) {
        if (mInstance == null) {
            synchronized (ScreenSwitchUtils.class) {
                if (mInstance == null) {
                    mInstance = new ScreenSwitchUtils(context);
                }
            }
        }
        return mInstance;
    }

    private ScreenSwitchUtils(Context context) {
        // 注册重力感应器,监听屏幕旋转
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new OrientationSensorListener(mHandler);

        // 根据 旋转之后/点击全屏之后 两者方向一致,激活sm.
        sm1 = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor1 = sm1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener1 = new OrientationSensorListener1();
    }

    /** 开始监听 */
    public void start() {
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    /** 停止监听 */
    public void stop() {
        sm.unregisterListener(listener,sensor);
        sm1.unregisterListener(listener1,sensor1);
    }

    /**
     * 手动横竖屏切换方向
     */
    public void toggleScreen() {
        sm.unregisterListener(listener);
        sm1.registerListener(listener1, sensor1, SensorManager.SENSOR_DELAY_UI);
        if (isPortrait) {
            isPortrait = false;
            // 切换成横屏
            if (mChangedListener != null) {
                mChangedListener.onConfigurationChanged(90);
            }
        }
        else {
            isPortrait = true;
            // 切换成竖屏
            if (mChangedListener != null) {
                mChangedListener.onConfigurationChanged(0);
            }
        }
    }

    public boolean isPortrait() {
        return this.isPortrait;
    }

    /**
     * 重力感应监听者
     */
    public class OrientationSensorListener implements SensorEventListener {
        private static final int         _DATA_X    = 0;
        private static final int         _DATA_Y    = 1;
        private static final int         _DATA_Z    = 2;
        private              long        LAST_TIME  = 0;
        private SensorEvent lastEvent  = null;
        private              long        SPACE_TIME = 800;

        public static final int ORIENTATION_UNKNOWN = -1;

        private Handler rotateHandler;

        public OrientationSensorListener(Handler handler) {
            rotateHandler = handler;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {}

        public void onSensorChanged(SensorEvent event) {
            if (System.currentTimeMillis() - LAST_TIME < SPACE_TIME) {
                return;
            }
            LAST_TIME = System.currentTimeMillis();
            synchronized (event) {
                float[] values = event.values;
                int orientation = ORIENTATION_UNKNOWN;
                float X = -values[_DATA_X];
                float Y = -values[_DATA_Y];
                float Z = -values[_DATA_Z];
                float magnitude = X * X + Y * Y;
                // Don't trust the angle if the magnitude is small compared to the y
                // value
                if (magnitude * 4 >= Z * Z) {
                    // 屏幕旋转时
                    float OneEightyOverPi = 57.29577957855f;
                    float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                    orientation = 90 - (int) Math.round(angle);
                    // normalize to 0 - 359 range
                    while (orientation >= 360) {
                        orientation -= 360;
                    }
                    while (orientation < 0) {
                        orientation += 360;
                    }
                }
                if (rotateHandler != null) {
                    rotateHandler.obtainMessage(SENSOR_CHANGE, orientation, 0).sendToTarget();
                }
            }
        }
    }

    public class OrientationSensorListener1 implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        public OrientationSensorListener1() {
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (orientation > 225 && orientation < 315) {// 检测到当前实际是横屏
                if (!isPortrait) {
                    sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
                    sm1.unregisterListener(listener1);
                }
            }
            else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {// 检测到当前实际是竖屏
                if (isPortrait) {
                    sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
                    sm1.unregisterListener(listener1);
                }
            }
        }
    }

    public ScreenChangedListener mChangedListener;

    public interface ScreenChangedListener {
        void onConfigurationChanged(float rotation);
    }

    public ScreenChangedListener getChangedListener() {
        return mChangedListener;
    }

    public void setChangedListener(ScreenChangedListener changedListener) {
        mChangedListener = changedListener;
    }

    public void removeChangedListener(ScreenChangedListener changedListener) {
        if (changedListener != null && mChangedListener == changedListener) {
            mChangedListener = null;
        }
    }
}

