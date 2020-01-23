package cube.ware.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 密聊倒计时进度条
 *
 * @author Wangxx
 * @date 2017/7/27
 */
public class SecretProgress extends View {

    private Paint   mPaint   = new Paint();
    private boolean mIsStart = false;
    private int  mProgressTime;
    private int  mProgressColor;
    private long mStartTime;
    private int  mProgressTotalTime;

    public SecretProgress(Context context) {
        this(context, null);
    }

    public SecretProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecretProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mProgressColor);
        stop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long currTime = System.currentTimeMillis();
        if (mProgressTotalTime == 0) {
            mProgressTotalTime = mProgressTime;
        }

        if (mIsStart) {
            float measureWidth = getMeasuredWidth() * ((float) mProgressTime / mProgressTotalTime);
            float mSpeed = measureWidth / 1.0f / mProgressTime;// 速度   = 一边距离 ／ 总时间
            float durTime = (currTime - mStartTime);//时间间隔
            float dist = mSpeed * durTime;//一边在durTime行走的距离
            if (dist < measureWidth) {//判断是否到达终点
                canvas.drawRect(dist + getMeasuredWidth() - measureWidth, 0.0f, getMeasuredWidth(), getMeasuredHeight(), mPaint);//绘制进度条
                invalidate();
                return;
            }
            else {
                stop();
            }
        }
        canvas.drawRect(0.0f, 0.0f, 0.0f, getMeasuredHeight(), mPaint);
    }

    public void start() {
        mIsStart = true;
        mStartTime = System.currentTimeMillis();
        invalidate();
        setVisibility(VISIBLE);
    }

    public void stop() {
        mIsStart = false;
        setVisibility(GONE);
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        mPaint.setColor(mProgressColor);
    }

    public int getProgressTime() {
        return mProgressTime;
    }

    public void setProgressTime(int progressTime) {
        mProgressTime = progressTime * 1000;
    }

    public int getProgressTotalTime() {
        return mProgressTotalTime;
    }

    public void setProgressTotalTime(int progressTotalTime) {
        mProgressTotalTime = progressTotalTime * 1000;
    }
}
