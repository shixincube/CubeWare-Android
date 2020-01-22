package cube.ware.service.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import cube.ware.service.message.R;

/**
 * 自定义的progressBar
 *
 * @author Wangxx
 * @date 2017/3/1
 */
public class CubeProgressBar extends View {
    private static final int STYLE_HORIZONTAL = 0;
    private static final int STYLE_ROUND      = 1;
    private static final int STYLE_SECTOR     = 2;
    /**
     * 圆形进度条边框宽度
     **/
    private              int strokeWidth      = 20;
    /**
     * 进度条中心X坐标
     **/
    private int centerX;
    /**
     * 进度条中心Y坐标
     **/
    private int centerY;
    /**
     * 进度提示文字大小
     **/
    private int percentTextSize    = 18;
    /**
     * 进度提示文字颜色
     **/
    private int percentTextColor   = 0xff009ACD;
    /**
     * 进度条背景颜色
     **/
    private int progressBarBgColor = 0xff636363;
    /**
     * 进度颜色
     **/
    private int progressColor      = 0xff00C5CD;
    /**
     * 扇形扫描进度的颜色
     */
    private int sectorColor        = 0xaaffffff;
    /**
     * 扇形扫描背景
     */
    private int unSweepColor       = 0xaa5e5e5e;
    /**
     * 进度条样式（水平/圆形）
     **/
    private int orientation        = STYLE_HORIZONTAL;
    /**
     * 圆形进度条半径
     **/
    private int radius             = 30;
    /**
     * 进度最大值
     **/
    private int max                = 100;
    /**
     * 进度值
     **/
    private int progress           = 0;
    /**
     * 水平进度条是否是空心
     **/
    private boolean isHorizonStroke;
    /**
     * 水平进度圆角值
     **/
    private int rectRound = 5;
    /** 进度文字是否显示百分号 **/
    private boolean            showPercentSign;
    private Paint              mPaint;
    /**
     * 进度回调接口
     */
    private OnProgressListener mOnProgressListener;

    public CubeProgressBar(Context context) {
        this(context, null);
    }

    public CubeProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CubeProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CubeProgressbar);
        percentTextColor = array.getColor(R.styleable.CubeProgressbar_percent_text_color, percentTextColor);
        progressBarBgColor = array.getColor(R.styleable.CubeProgressbar_progressBarBgColor, progressBarBgColor);
        progressColor = array.getColor(R.styleable.CubeProgressbar_progressColor, progressColor);
        sectorColor = array.getColor(R.styleable.CubeProgressbar_sectorColor, sectorColor);
        unSweepColor = array.getColor(R.styleable.CubeProgressbar_unSweepColor, unSweepColor);
        percentTextSize = (int) array.getDimension(R.styleable.CubeProgressbar_percent_text_size, percentTextSize);
        strokeWidth = (int) array.getDimension(R.styleable.CubeProgressbar_stroke_width, strokeWidth);
        rectRound = (int) array.getDimension(R.styleable.CubeProgressbar_rect_round, rectRound);
        orientation = array.getInteger(R.styleable.CubeProgressbar_orientation, STYLE_HORIZONTAL);
        isHorizonStroke = array.getBoolean(R.styleable.CubeProgressbar_isHorizonStroke, false);
        showPercentSign = array.getBoolean(R.styleable.CubeProgressbar_showPercentSign, true);
        array.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        radius = centerX - strokeWidth / 2;
        if (orientation == STYLE_HORIZONTAL) {
            drawHorRectProgressBar(canvas, mPaint);
        }
        else if (orientation == STYLE_ROUND) {
            drawRoundProgressBar(canvas, mPaint);
        }
        else {
            drawSectorProgressBar(canvas, mPaint);
        }
    }

    /**
     * 绘制圆形进度条
     *
     * @param canvas
     */
    private void drawRoundProgressBar(Canvas canvas, Paint piant) {
        // 初始化画笔属性
        piant.setColor(progressBarBgColor);
        piant.setStyle(Paint.Style.STROKE);
        piant.setStrokeWidth(strokeWidth);
        // 画圆
        canvas.drawCircle(centerX, centerY, radius, piant);
        // 画圆形进度
        piant.setColor(progressColor);
        piant.setStyle(Paint.Style.STROKE);
        piant.setStrokeWidth(strokeWidth);
        RectF oval = new RectF(centerX - radius, centerY - radius, radius + centerX, radius + centerY);
        canvas.drawArc(oval, -90, 360 * progress / max, false, piant);
        // 画进度文字
        piant.setStyle(Paint.Style.FILL);
        piant.setColor(percentTextColor);
        piant.setTextSize(percentTextSize);

        String percent = progress * 100 / max + "%";
        Rect rect = new Rect();
        piant.getTextBounds(percent, 0, percent.length(), rect);
        float textWidth = rect.width();
        float textHeight = rect.height();
        if (textWidth >= radius * 2) {
            textWidth = radius * 2;
        }
        Paint.FontMetrics metrics = piant.getFontMetrics();
        float baseline = (getMeasuredHeight() - metrics.bottom + metrics.top) / 2 - metrics.top;
        canvas.drawText(percent, centerX - textWidth / 2, baseline, piant);
    }

    /**
     * 绘制水平矩形进度条
     *
     * @param canvas
     */
    private void drawHorRectProgressBar(Canvas canvas, Paint paint) {
        // 初始化画笔属性
        paint.setColor(progressBarBgColor);
        if (isHorizonStroke) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
        }
        else {
            paint.setStyle(Paint.Style.FILL);
        }
        // 画水平矩形
        canvas.drawRoundRect(new RectF(centerX - getWidth() / 2, centerY - getHeight() / 2, centerX + getWidth() / 2, centerY + getHeight() / 2), rectRound, rectRound, paint);

        // 画水平进度
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(progressColor);
        if (isHorizonStroke) {
            canvas.drawRoundRect(new RectF(centerX - getWidth() / 2, centerY - getHeight() / 2, ((progress * 100 / max) * getWidth()) / 100, centerY + getHeight() / 2), rectRound, rectRound, paint);
        }
        else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawRoundRect(new RectF(centerX - getWidth() / 2, centerY - getHeight() / 2, ((progress * 100 / max) * getWidth()) / 100, centerY + getHeight() / 2), rectRound, rectRound, paint);
            paint.setXfermode(null);
        }

        // 画进度文字
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(percentTextColor);
        paint.setTextSize(percentTextSize);
        String percent = progress * 100 / max + "%";
        Rect rect = new Rect();
        paint.getTextBounds(percent, 0, percent.length(), rect);
        float textWidth = rect.width();
        float textHeight = rect.height();
        if (textWidth >= getWidth()) {
            textWidth = getWidth();
        }
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float baseline = (getMeasuredHeight() - metrics.bottom + metrics.top) / 2 - metrics.top;
        canvas.drawText(percent, centerX - textWidth / 2, baseline, paint);
    }

    /**
     * 绘制扇形扫描式进度
     *
     * @param canvas
     * @param paint
     */
    private void drawSectorProgressBar(Canvas canvas, Paint paint) {
        // 初始化画笔属性
        paint.setColor(sectorColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        // 绘外圈
        canvas.drawCircle(centerX, centerY, radius, paint);
        // 绘内圈
        paint.setColor(unSweepColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setColor(sectorColor);
        RectF oval = new RectF(centerX - radius + 8, centerY - radius + 8, radius + centerX - 8, radius + centerY - 8);
        canvas.drawArc(oval, -90, 360 * progress / max, true, paint);
    }

    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("The progress of 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
        if (progress == max) {
            if (mOnProgressListener != null) {
                mOnProgressListener.progressToComplete();
            }
        }
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getPercentTextSize() {
        return percentTextSize;
    }

    public void setPercentTextSize(int percentTextSize) {
        this.percentTextSize = percentTextSize;
    }

    public int getPercentTextColor() {
        return percentTextColor;
    }

    public void setPercentTextColor(int percentTextColor) {
        this.percentTextColor = percentTextColor;
    }

    public int getProgressBarBgColor() {
        return progressBarBgColor;
    }

    public void setProgressBarBgColor(int progressBarBgColor) {
        this.progressBarBgColor = progressBarBgColor;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public int getSectorColor() {
        return sectorColor;
    }

    public void setSectorColor(int sectorColor) {
        this.sectorColor = sectorColor;
    }

    public int getUnSweepColor() {
        return unSweepColor;
    }

    public void setUnSweepColor(int unSweepColor) {
        this.unSweepColor = unSweepColor;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * 获取当前的最大进度值
     */
    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置最大进度值
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The max progress of 0");
        }
        this.max = max;
    }

    /**
     * 获取进度值
     */
    public synchronized int getProgress() {
        return progress;
    }

    public boolean isHorizonStroke() {
        return isHorizonStroke;
    }

    public void setHorizonStroke(boolean horizonStroke) {
        isHorizonStroke = horizonStroke;
    }

    public int getRectRound() {
        return rectRound;
    }

    public void setRectRound(int rectRound) {
        this.rectRound = rectRound;
    }

    public boolean isShowPercentSign() {
        return showPercentSign;
    }

    public void setShowPercentSign(boolean showPercentSign) {
        this.showPercentSign = showPercentSign;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    /**
     * 进度完成回调接口
     */
    public interface OnProgressListener {

        void progressToComplete();
    }

    public void setOnProgressListener(OnProgressListener mOnProgressListener) {

        this.mOnProgressListener = mOnProgressListener;
    }
}
