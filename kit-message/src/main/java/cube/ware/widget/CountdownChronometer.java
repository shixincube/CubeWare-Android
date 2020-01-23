package cube.ware.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Chronometer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义倒计时器
 *
 * @author Wangxx
 * @date 2017/7/24
 */
@SuppressLint({ "ViewConstructor", "SimpleDateFormat" })
public class CountdownChronometer extends Chronometer {
    private long                   mTime;
    private long                   mNextTime;
    private OnTimeCompleteListener mListener;
    private SimpleDateFormat       mTimeFormat;

    public CountdownChronometer(Context context) {
        this(context, null);
    }

    public CountdownChronometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownChronometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO 自动生成的构造函数存根
        this.setOnChronometerTickListener(listener);
    }

    /**
     * 重新启动计时
     */
    public void reStart(long _time_s) {
        if (_time_s == -1) {
            mNextTime = mTime;
        }
        else {
            mTime = mNextTime = _time_s;
        }
        this.start();
    }

    public void reStart() {
        reStart(-1);
    }

    /**
     * 继续计时
     */
    public void onResume() {
        this.start();
    }

    /**
     * 暂停计时
     */
    public void onPause() {
        this.stop();
    }

    /**
     * 设置时间格式
     *
     * @param pattern 计时格式
     */
    public void setTimeFormat(String pattern) {
        mTimeFormat = new SimpleDateFormat(pattern);
    }

    public void setOnTimeCompleteListener(OnTimeCompleteListener l) {
        mListener = l;
    }

    OnChronometerTickListener listener = new OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            if (mNextTime <= 0) {
                if (mNextTime == 0) {
                    CountdownChronometer.this.stop();
                    if (null != mListener) {
                        mListener.onTimeComplete();
                    }
                }
                mNextTime = 0;
                updateTimeText();
                return;
            }

            mNextTime--;
            updateTimeText();
        }
    };

    /**
     * 初始化时间
     *
     * @param _time_s
     */
    public void initTime(long _time_s) {
        mTime = mNextTime = _time_s;
        if (_time_s > 0) {
            this.start();
        }
        else {
            mTime = mNextTime = 0;
        }
        //updateTimeText();
    }

    private void updateTimeText() {
        if (mTimeFormat != null) {
            this.setText(mTimeFormat.format(new Date(mNextTime * 1000)));
        }
        else {
            this.setText(String.valueOf(mNextTime + 1));
        }
    }

    public interface OnTimeCompleteListener {
        void onTimeComplete();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (true) {
            return;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == VISIBLE) {
            super.onWindowVisibilityChanged(visibility);
        }
    }
}
