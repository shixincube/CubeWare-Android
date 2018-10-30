package cube.ware.widget.indexbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.common.utils.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cube.ware.R;
import cube.ware.widget.indexbar.bean.BaseIndexPinyinBean;
import cube.ware.widget.indexbar.helper.IIndexBarDataHelper;
import cube.ware.widget.indexbar.helper.IndexBarDataHelperImpl;

/**
 * 自定义索引右侧边栏
 *
 * @author Wangxx
 * @date 2017/4/11
 */
public class CubeIndexBar extends View {

    // 在最后面（默认的数据源）
    public static String[] INDEX_STRING = {
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
    };

    // 是否需要根据实际的数据来生成索引数据源（例如 只有 A B C 三种tag，那么索引栏就 A B C 三项）
    private boolean isNeedRealIndex;

    // 索引数据源
    private List<String> mIndexDataList;

    // View的宽高
    private int mWidth, mHeight;

    // 每个index区域的高度
    private int mGapHeight;

    private Paint mPaint  = new Paint();
    //选中的圆
    private Paint mCircle = new Paint();

    // 手指按下时的背景色
    private int mPressedBackground;

    // 以下是帮助类
    // 汉语->拼音，拼音->tag
    private IIndexBarDataHelper mDataHelper;

    // 以下边变量是外部set进来的
    private TextView                            mPressedShowTextView;   // 用于特写显示正在被触摸的index值
    private boolean                             isSourceDataAlreadySorted;  // 源数据是否已经有序
    private List<? extends BaseIndexPinyinBean> mSourceData;    // Adapter的数据源
    private LinearLayoutManager                 mLayoutManager;
    private NestedScrollView                    scrollView;
    private int mHeaderViewCount = 0;
    private int baseline;   //字母的高度
    private int mPressI = -1;//当前选中位置

    private static int CENTER = 0;//0为默认处理方式，控件居中显示的
    private static int BOTTOM = 1;//1为控件底部显示的

    private int mType = 0;//判断控件显示类型
    private int   mTextSize;
    private float mPosX;
    private float mPosY;

    public CubeIndexBar(Context context) {
        this(context, null);
    }

    public CubeIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CubeIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public int getHeaderViewCount() {
        return mHeaderViewCount;
    }

    /**
     * 设置Headerview的Count
     *
     * @param headerViewCount
     *
     * @return
     */
    public CubeIndexBar setHeaderViewCount(int headerViewCount) {
        mHeaderViewCount = headerViewCount;
        return this;
    }

    public boolean isSourceDataAlreadySorted() {
        return isSourceDataAlreadySorted;
    }

    /**
     * 源数据 是否已经有序
     *
     * @param sourceDataAlreadySorted
     *
     * @return
     */
    public CubeIndexBar setSourceDataAlreadySorted(boolean sourceDataAlreadySorted) {
        this.isSourceDataAlreadySorted = sourceDataAlreadySorted;
        return this;
    }

    public IIndexBarDataHelper getDataHelper() {
        return mDataHelper;
    }

    /**
     * 设置数据源帮助类
     *
     * @param dataHelper
     *
     * @return
     */
    public CubeIndexBar setDataHelper(IIndexBarDataHelper dataHelper) {
        mDataHelper = dataHelper;
        return this;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()); // 默认的TextSize
        mPressedBackground = Color.BLACK;   // 默认按下是纯黑色
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CubeIndexBar, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.CubeIndexBar_indexBarTextSize) {
                mTextSize = typedArray.getDimensionPixelSize(attr, mTextSize);
            }
            else if (attr == R.styleable.CubeIndexBar_indexBarPressBackground) {
                mPressedBackground = typedArray.getColor(attr, mPressedBackground);
            }
        }
        typedArray.recycle();

        // 初始化索引数据
        initIndexDatas();

      /*  mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(getResources().getColor(R.color.contacts_indexbar_color));
*/
        // 设置index触摸监听器
        setOnIndexPressedListener(new onIndexPressedListener() {
            @Override
            public void onIndexPressed(int index, String text) {
                if (mPressedShowTextView != null) { // 显示hintTexView
                    mPressedShowTextView.setVisibility(View.VISIBLE);
                    mPressedShowTextView.setText(text);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mPressedShowTextView.getLayoutParams();
                    if (mType == CENTER) {
                        if (index == 0) {
                            params.setMargins(0, index * (baseline), 28, 0);
                        }
                        else {
                            DisplayMetrics dm = getResources().getDisplayMetrics();
                            if (dm.densityDpi >= 480) {
                                params.setMargins(0, index * (baseline + 12), 28, 0);
                            }
                            else {
                                if (index < mIndexDataList.size() / 2) {
                                    params.setMargins(0, index * (baseline + 10), 28, 0);
                                }
                                else {
                                    params.setMargins(0, index * (baseline + 8), 28, 0);
                                }
                            }
                        }
                    }
                    else if (mType == BOTTOM) {
                        DisplayMetrics dm = getResources().getDisplayMetrics();
                        int dip = ScreenUtil.px2dip(dm.densityDpi);//类似小米MAX之类的大屏低DPI手机，得加字母的高度
                        if (dm.densityDpi >= 480) {
                            dip = 0;
                        }
                        params.setMargins(0, (mGapHeight * index) + (mGapHeight * (index - 1)) + (dip), 150, 0);
                    }
                    mPressedShowTextView.setLayoutParams(params);
                }
                // 滑动Rv
                if (mLayoutManager != null) {
                    int position = getPosByTag(text);
                    if (position != -1) {
                        mLayoutManager.scrollToPositionWithOffset(position, 0);
                    }
                }
                if (scrollView != null) {
                    int position = getPosByTag(text);
                    if (position != -1) {
                        scrollView.scrollTo(0, position * 300);
                    }
                }
            }

            @Override
            public void onMotionEventEnd() {
                // 隐藏hintTextView
                if (mPressedShowTextView != null) {
                    mPressedShowTextView.setVisibility(View.GONE);
                }
            }
        });

        mDataHelper = new IndexBarDataHelperImpl();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 取出宽高的MeasureSpec  Mode 和Size
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        mPosX = mWidth - 1.6f * mTextSize;
        int measureWidth = 0, measureHeight = 0;    // 最终测量出来的宽高

        // 得到合适宽度：
        Rect indexBounds = new Rect();  // 存放每个绘制的index的Rect区域
        String index;   // 每个要绘制的index内容
        if (mIndexDataList != null && !mIndexDataList.isEmpty()) {
            for (int i = 0; i < mIndexDataList.size(); i++) {
                index = mIndexDataList.get(i);
                mPaint.getTextBounds(index, 0, index.length(), indexBounds);    // 测量计算文字所在矩形，可以得到宽高
                measureWidth = Math.max(indexBounds.width(), measureWidth); // 循环结束后，得到index的最大宽度
                measureHeight = Math.max(indexBounds.height(), measureHeight);  // 循环结束后，得到index的最大高度，然后*size
            }
            measureHeight *= mIndexDataList.size();
        }
        switch (wMode) {
            case MeasureSpec.EXACTLY:
                measureWidth = wSize;
                break;
            case MeasureSpec.AT_MOST:
                measureWidth = Math.min(measureWidth, wSize);   // wSize此时是父控件能给子View分配的最大空间
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        // 得到合适的高度：
        switch (hMode) {
            case MeasureSpec.EXACTLY:
                measureHeight = hSize;
                break;
            case MeasureSpec.AT_MOST:
                measureHeight = Math.min(measureHeight, hSize); // wSize此时是父控件能给子View分配的最大空间
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
     /*   int t = getPaddingTop();    // top的基准点(支持padding)
        String index;   // 每个要绘制的index内容
        for (int i = 0; i < mIndexDataList.size(); i++) {
            index = mIndexDataList.get(i);

            canvas.drawText(index, mWidth / 2 - mPaint.measureText(index) / 2, t + mGapHeight * i + baseline, mPaint);  // 调用drawText，居中显示绘制index
        }*/
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();    // 获得画笔的FontMetrics，用来计算baseLine。因为drawText的y坐标，代表的是绘制的文字的baseLine的位置
        baseline = (int) ((mGapHeight - fontMetrics.bottom - fontMetrics.top) / 2); // 计算出在每格index区域，竖直居中的baseLine值
        drawLetters(canvas);
    }

    private void drawLetters(Canvas canvas) {
        int t = getPaddingTop();    // t
        RectF rectF = new RectF();
        rectF.left = mPosX - mTextSize;
        rectF.right = mPosX + mTextSize;
        rectF.top = mTextSize / 2;
        rectF.bottom = mHeight - mTextSize / 2;
        mPaint.reset();
        mCircle.reset();
        String index;
        for (int i = 0; i < mIndexDataList.size(); i++) {
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);
            mPaint.setTextAlign(Paint.Align.CENTER);
            index = mIndexDataList.get(i);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float posY = ((mGapHeight - fontMetrics.bottom - fontMetrics.top) / 2); // 计算出在每格index区域，竖直居中的baseLine值
            float x = mWidth / 2 - mPaint.measureText(index) / 2;
            if (i != mPressI) {
                mPaint.setColor(getResources().getColor(R.color.contacts_indexbar_color));
                canvas.drawText(index, x, t + mGapHeight * i + posY, mPaint);
            }
            else {
                mCircle.setAntiAlias(true);
                mCircle.setColor(getResources().getColor(R.color.primary));
                mCircle.setStyle(Paint.Style.FILL);
                canvas.drawCircle(x, (t + mGapHeight * i + posY) - 15, x, mCircle);
                mPaint.setColor(getResources().getColor(R.color.white));
                canvas.drawText(index, x, t + mGapHeight * i + posY, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //  setBackgroundColor(mPressedBackground); // 手指按下时背景变色
                // 注意这里没有break，因为down时，也要计算落点 回调监听器

            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                // 通过计算判断落点在哪个区域：
                mPressI = (int) ((y - getPaddingTop()) / mGapHeight);

                // 边界处理（在手指move时，有可能已经移出边界，防止越界）
                if (mPressI < 0) {
                    mPressI = 0;
                }
                else if (mPressI >= mIndexDataList.size()) {
                    mPressI = mIndexDataList.size() - 1;
                }
                // 回调监听器
                if (null != mOnIndexPressedListener && mPressI > -1 && mPressI < mIndexDataList.size()) {
                    mOnIndexPressedListener.onIndexPressed(mPressI, mIndexDataList.get(mPressI));
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                mPressI = -1;
                //setBackgroundResource(android.R.color.transparent); // 手指抬起时背景恢复透明
                // 回调监听器
                if (null != mOnIndexPressedListener) {
                    mOnIndexPressedListener.onMotionEventEnd();
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        // 解决源数据为空或者size为0的情况,
        if (null == mIndexDataList || mIndexDataList.isEmpty()) {
            return;
        }
        computeGapHeight();
    }

    /**
     * 当前被按下的index的监听器
     */
    public interface onIndexPressedListener {
        void onIndexPressed(int index, String text);    // 当某个Index被按下

        void onMotionEventEnd();    // 当触摸事件结束（UP CANCEL）
    }

    private onIndexPressedListener mOnIndexPressedListener;

    public onIndexPressedListener getOnIndexPressedListener() {
        return mOnIndexPressedListener;
    }

    public void setOnIndexPressedListener(onIndexPressedListener onIndexPressedListener) {
        this.mOnIndexPressedListener = onIndexPressedListener;
    }

    /**
     * 显示当前被按下的index的TextView
     *
     * @return
     */

    public CubeIndexBar setPressedShowTextView(TextView pressedShowTextView) {
        this.mPressedShowTextView = pressedShowTextView;
        return this;
    }

    public CubeIndexBar setLayoutManager(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        return this;
    }

    public CubeIndexBar setNestedScrollView(NestedScrollView scrollView) {
        this.scrollView = scrollView;
        return this;
    }

    public CubeIndexBar setType(int type) {
        this.mType = type;
        return this;
    }

    /**
     * 一定要在设置数据源{@link #setSourceData(List)}之前调用
     *
     * @param needRealIndex
     *
     * @return
     */
    public CubeIndexBar setNeedRealIndex(boolean needRealIndex) {
        isNeedRealIndex = needRealIndex;
        initIndexDatas();
        return this;
    }

    /**
     * 初始化索引数据
     */
    private void initIndexDatas() {
        if (isNeedRealIndex) {
            mIndexDataList = new ArrayList<>();
        }
        else {
            mIndexDataList = Arrays.asList(INDEX_STRING);
        }
    }

    /**
     * 设置数据
     *
     * @param sourceData
     *
     * @return
     */
    public CubeIndexBar setSourceData(List<? extends BaseIndexPinyinBean> sourceData) {
        this.mSourceData = sourceData;
        initSourceDatas();  // 对数据源进行初始化
        return this;
    }

    /**
     * 初始化原始数据源，并取出索引数据源
     *
     * @return
     */
    private void initSourceDatas() {
        // 解决源数据为空 或者size为0的情况,
        if (null == mSourceData || mSourceData.isEmpty()) {
            mIndexDataList.clear();
            return;
        }
        if (!isSourceDataAlreadySorted) {
            // 排序sourceData
            mDataHelper.sortSourceData(mSourceData);
        }
        else {
            // 汉语->拼音
            mDataHelper.convert(mSourceData);
            // 拼音->tag
            mDataHelper.fillIndexTag(mSourceData);
        }
        if (isNeedRealIndex) {
            mIndexDataList.clear();
            mDataHelper.getSortedIndexData(mSourceData, mIndexDataList);
            computeGapHeight();
        }
        invalidate();
    }

    /**
     * 以下情况调用：
     * 1 在数据源改变
     * 2 控件size改变时
     * 计算gapHeight
     */
    private void computeGapHeight() {
        if (mIndexDataList.size() > 20) {//根据数据长度来将index高度重新计算
            mGapHeight = (mHeight - getPaddingTop() - getPaddingBottom()) / mIndexDataList.size();
        }
        else {
            mGapHeight = (mHeight - getPaddingTop() - getPaddingBottom()) / 20;
        }
    }

    /**
     * 对数据源排序
     */
    public void sortData(List<? extends BaseIndexPinyinBean> sourceData) {
        // 排序sourceData
        mDataHelper.sortSourceData(sourceData);
    }

    /**
     * 根据传入的pos返回tag
     *
     * @param tag
     *
     * @return
     */
    private int getPosByTag(String tag) {
        // 解决源数据为空 或者size为0的情况,
        if (null == mSourceData || mSourceData.isEmpty()) {
            return -1;
        }
        if (TextUtils.isEmpty(tag)) {
            return -1;
        }
        for (int i = 0; i < mSourceData.size(); i++) {
            if (tag.equals(mSourceData.get(i).getBaseIndexTag())) {
                return i + getHeaderViewCount();
            }
        }
        return -1;
    }
}
