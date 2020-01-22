package cube.ware.service.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.List;

/**
 * This utility class can add a horizontal popup-menu easily
 * 该工具类可以很方便的为View、ListView/GridView绑定长按弹出横向气泡菜单
 */

/**
 * 弹出横向气泡菜单，支持View、ListView/GridView长按绑定事件
 *
 * @author PengZhenjin
 * @date 2016-10-17
 */
public class PopupHorizontalMenu implements KeyboardWatcher.OnKeyboardToggleListener {

    private static final int   DEFAULT_NORMAL_TEXT_COLOR         = Color.WHITE;
    private static final int   DEFAULT_PRESSED_TEXT_COLOR        = Color.WHITE;
    private static final float DEFAULT_TEXT_SIZE_PIXEL           = 12;
    private static final float DEFAULT_TEXT_PADDING_LEFT_PIXEL   = 10;
    private static final float DEFAULT_TEXT_PADDING_TOP_PIXEL    = 8;
    private static final float DEFAULT_TEXT_PADDING_RIGHT_PIXEL  = 10;
    private static final float DEFAULT_TEXT_PADDING_BOTTOM_PIXEL = 8;
    private static final int   DEFAULT_NORMAL_BACKGROUND_COLOR   = Color.parseColor("#aa000000");
    private static final int   DEFAULT_PRESSED_BACKGROUND_COLOR  = Color.parseColor("#22000000");
    private static final int   DEFAULT_BACKGROUND_RADIUS_PIXEL   = 8;
    private static final int   DEFAULT_DIVIDER_COLOR             = Color.parseColor("#00000000");
    private static final int   DEFAULT_DIVIDER_WIDTH_PIXEL       = 1;
    private static final int   DEFAULT_DIVIDER_HEIGHT_PIXEL      = 15;

    private Context                  mContext;
    private PopupWindow              mPopupWindow;
    private View                     mBindView;
    private View                     mContextView;
    private View                     mIndicatorView;
    private List<String>             mPopupItemList;
    private OnPopupListClickListener mOnPopupListClickListener;
    private int                      mContextPosition;
    private float                    mRawX;
    private float                    mRawY;
    private StateListDrawable        mLeftItemBackground;
    private StateListDrawable        mRightItemBackground;
    private StateListDrawable        mCornerItemBackground;
    private ColorStateList           mTextColorStateList;
    private int                      mIndicatorWidth;
    private int                      mIndicatorHeight;
    private int                      mPopupWindowWidth;
    private int                      mPopupWindowHeight;
    private int                      mScreenWidth;
    private int                      mScreenHeight;
    private int                      mNormalTextColor;
    private int                      mPressedTextColor;
    private float                    mTextSizePixel;
    private float                    mTextPaddingLeftPixel;
    private float                    mTextPaddingTopPixel;
    private float                    mTextPaddingRightPixel;
    private float                    mTextPaddingBottomPixel;
    private int                      mNormalBackgroundColor;
    private int                      mPressedBackgroundColor;
    private int                      mBackgroundCornerRadiusPixel;
    private int                      mDividerColor;
    private int                      mDividerWidthPixel;
    private int                      mDividerHeightPixel;
    private KeyboardWatcher          keyboardWatcher;

    /**
     * 初始化
     *
     * @param context
     * @param bindView                 绑定的view
     * @param popupItemList            弹出条目列表
     * @param onPopupListClickListener 弹出条目点击事件监听器
     */
    public void init(Context context, View bindView, List<String> popupItemList, OnPopupListClickListener onPopupListClickListener) {
        this.mNormalTextColor = DEFAULT_NORMAL_TEXT_COLOR;
        this.mPressedTextColor = DEFAULT_PRESSED_TEXT_COLOR;
        this.mTextSizePixel = dp2px(DEFAULT_TEXT_SIZE_PIXEL);
        this.mTextPaddingLeftPixel = dp2px(DEFAULT_TEXT_PADDING_LEFT_PIXEL);
        this.mTextPaddingTopPixel = dp2px(DEFAULT_TEXT_PADDING_TOP_PIXEL);
        this.mTextPaddingRightPixel = dp2px(DEFAULT_TEXT_PADDING_RIGHT_PIXEL);
        this.mTextPaddingBottomPixel = dp2px(DEFAULT_TEXT_PADDING_BOTTOM_PIXEL);
        this.mNormalBackgroundColor = DEFAULT_NORMAL_BACKGROUND_COLOR;
        this.mPressedBackgroundColor = DEFAULT_PRESSED_BACKGROUND_COLOR;
        this.mBackgroundCornerRadiusPixel = dp2px(DEFAULT_BACKGROUND_RADIUS_PIXEL);
        this.mDividerColor = DEFAULT_DIVIDER_COLOR;
        this.mDividerWidthPixel = dp2px(DEFAULT_DIVIDER_WIDTH_PIXEL);
        this.mDividerHeightPixel = dp2px(DEFAULT_DIVIDER_HEIGHT_PIXEL);
        this.mContext = context;
        this.mBindView = bindView;
        this.mPopupItemList = popupItemList;
        this.mOnPopupListClickListener = onPopupListClickListener;
        this.mPopupWindow = null;
        keyboardWatcher = new KeyboardWatcher((Activity) this.mContext);
        keyboardWatcher.setListener(this);
        this.mBindView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRawX = event.getRawX();
                mRawY = event.getRawY();
                return false;
            }
        });
        if (this.mBindView instanceof AbsListView) {
            ((AbsListView) this.mBindView).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    mContextView = view;
                    mContextPosition = position;
                    showPopupListWindow();
                    return true;
                }
            });
        }
        else {
            this.mBindView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mContextView = v;
                    showPopupListWindow();
                    return false;
                }
            });
        }
        if (this.mScreenWidth == 0) {
            this.mScreenWidth = getScreenWidth();
        }
        if (this.mScreenHeight == 0) {
            this.mScreenHeight = getScreenHeight();
        }
        this.refreshBackgroundOrRadiusStateList();
        this.refreshTextColorStateList(this.mPressedTextColor, this.mNormalTextColor);
    }

    /**
     * 显示弹出菜单
     */
    private void showPopupListWindow() {
        if (this.mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
            return;
        }
        if (this.mPopupWindow == null) {
            LinearLayout contentView = new LinearLayout(this.mContext);
            contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentView.setOrientation(LinearLayout.VERTICAL);
            LinearLayout popupListContainer = new LinearLayout(this.mContext);
            popupListContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            popupListContainer.setOrientation(LinearLayout.HORIZONTAL);
            popupListContainer.setBackgroundDrawable(this.mCornerItemBackground);
            contentView.addView(popupListContainer);
            if (this.mIndicatorView != null) {
                LinearLayout.LayoutParams layoutParams;
                if (this.mIndicatorView.getLayoutParams() == null) {
                    layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
                else {
                    layoutParams = (LinearLayout.LayoutParams) this.mIndicatorView.getLayoutParams();
                }
                layoutParams.gravity = Gravity.CENTER;
                this.mIndicatorView.setLayoutParams(layoutParams);
                contentView.addView(this.mIndicatorView);
            }
            for (int i = 0; i < this.mPopupItemList.size(); i++) {
                TextView textView = new TextView(this.mContext);
                textView.setTextColor(this.mTextColorStateList);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.mTextSizePixel);
                textView.setPadding(dp2px(12), dp2px(8), dp2px(12), dp2px(8));
                textView.setClickable(true);
                final int finalI = i;
                final String type = mPopupItemList.get(i);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnPopupListClickListener != null) {
                            mOnPopupListClickListener.onPopupListClick(mContextView, mContextPosition, type, finalI);
                            hidePopupListWindow();
                        }
                    }
                });
                textView.setText(this.mPopupItemList.get(i));
                if (this.mPopupItemList.size() > 1 && i == 0) {
                    textView.setBackgroundDrawable(this.mLeftItemBackground);
                }
                else if (this.mPopupItemList.size() > 1 && i == this.mPopupItemList.size() - 1) {
                    textView.setBackgroundDrawable(this.mRightItemBackground);
                }
                else if (this.mPopupItemList.size() == 1) {
                    textView.setBackgroundDrawable(this.mCornerItemBackground);
                }
                else {
                    textView.setBackgroundDrawable(getCenterItemBackground());
                }
                popupListContainer.addView(textView);
                if (this.mPopupItemList.size() > 1 && i != this.mPopupItemList.size() - 1) {
                    View divider = new View(this.mContext);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mDividerWidthPixel, mDividerHeightPixel);
                    layoutParams.gravity = Gravity.CENTER;
                    divider.setLayoutParams(layoutParams);
                    divider.setBackgroundColor(mDividerColor);
                    popupListContainer.addView(divider);
                }
            }
            if (this.mPopupWindowWidth == 0) {
                this.mPopupWindowWidth = getViewWidth(popupListContainer);
            }
            if (this.mIndicatorView != null && this.mIndicatorWidth == 0) {
                if (this.mIndicatorView.getLayoutParams().width > 0) {
                    this.mIndicatorWidth = this.mIndicatorView.getLayoutParams().width;
                }
                else {
                    this.mIndicatorWidth = getViewWidth(this.mIndicatorView);
                }
            }
            if (this.mIndicatorView != null && this.mIndicatorHeight == 0) {
                if (this.mIndicatorView.getLayoutParams().height > 0) {
                    this.mIndicatorHeight = this.mIndicatorView.getLayoutParams().height;
                }
                else {
                    this.mIndicatorHeight = getViewHeight(this.mIndicatorView);
                }
            }
            if (this.mPopupWindowHeight == 0) {
                this.mPopupWindowHeight = getViewHeight(popupListContainer) + this.mIndicatorHeight;
            }
            this.mPopupWindow = new PopupWindow(contentView, this.mPopupWindowWidth, this.mPopupWindowHeight, true);
            this.mPopupWindow.setTouchable(true);
            this.mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        if (this.mIndicatorView != null) {
            float marginLeftScreenEdge = this.mRawX;
            float marginRightScreenEdge = this.mScreenWidth - this.mRawX;
            if (marginLeftScreenEdge < this.mPopupWindowWidth / 2f) {
                // in case of the draw of indicator out of Screen's bounds
                if (marginLeftScreenEdge < this.mIndicatorWidth / 2f + this.mBackgroundCornerRadiusPixel) {
                    this.mIndicatorView.setTranslationX(this.mIndicatorWidth / 2f + this.mBackgroundCornerRadiusPixel - this.mPopupWindowWidth / 2f);
                }
                else {
                    this.mIndicatorView.setTranslationX(marginLeftScreenEdge - this.mPopupWindowWidth / 2f);
                }
            }
            else if (marginRightScreenEdge < this.mPopupWindowWidth / 2f) {
                if (marginRightScreenEdge < this.mIndicatorWidth / 2f + this.mBackgroundCornerRadiusPixel) {
                    this.mIndicatorView.setTranslationX(this.mPopupWindowWidth / 2f - this.mIndicatorWidth / 2f - this.mBackgroundCornerRadiusPixel);
                }
                else {
                    this.mIndicatorView.setTranslationX(this.mPopupWindowWidth / 2f - marginRightScreenEdge);
                }
            }
            else {
                this.mIndicatorView.setTranslationX(0);
            }
        }
        int[] location = new int[2];
        mBindView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        this.mPopupWindow.showAtLocation(this.mBindView, Gravity.TOP, -mScreenWidth / 2 + x + mBindView.getWidth() / 2, y - this.mPopupWindowHeight);
    }

    private void refreshBackgroundOrRadiusStateList() {
        // left
        GradientDrawable leftItemPressedDrawable = new GradientDrawable();
        leftItemPressedDrawable.setColor(this.mPressedBackgroundColor);
        leftItemPressedDrawable.setCornerRadii(new float[] {
            this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, 0, 0, 0, 0, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel
        });
        GradientDrawable leftItemNormalDrawable = new GradientDrawable();
        leftItemNormalDrawable.setColor(this.mNormalBackgroundColor);
        leftItemNormalDrawable.setCornerRadii(new float[] {
            this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, 0, 0, 0, 0, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel
        });
        this.mLeftItemBackground = new StateListDrawable();
        this.mLeftItemBackground.addState(new int[] { android.R.attr.state_pressed }, leftItemPressedDrawable);
        this.mLeftItemBackground.addState(new int[] {}, leftItemNormalDrawable);
        // right
        GradientDrawable rightItemPressedDrawable = new GradientDrawable();
        rightItemPressedDrawable.setColor(this.mPressedBackgroundColor);
        rightItemPressedDrawable.setCornerRadii(new float[] {
            0, 0, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, 0, 0
        });
        GradientDrawable rightItemNormalDrawable = new GradientDrawable();
        rightItemNormalDrawable.setColor(this.mNormalBackgroundColor);
        rightItemNormalDrawable.setCornerRadii(new float[] {
            0, 0, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, 0, 0
        });
        this.mRightItemBackground = new StateListDrawable();
        this.mRightItemBackground.addState(new int[] { android.R.attr.state_pressed }, rightItemPressedDrawable);
        this.mRightItemBackground.addState(new int[] {}, rightItemNormalDrawable);
        // corner
        GradientDrawable cornerItemPressedDrawable = new GradientDrawable();
        cornerItemPressedDrawable.setColor(this.mPressedBackgroundColor);
        cornerItemPressedDrawable.setCornerRadii(new float[] {
            this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel
        });
        GradientDrawable cornerItemNormalDrawable = new GradientDrawable();
        cornerItemNormalDrawable.setColor(this.mNormalBackgroundColor);
        cornerItemNormalDrawable.setCornerRadii(new float[] {
            this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel, this.mBackgroundCornerRadiusPixel
        });
        this.mCornerItemBackground = new StateListDrawable();
        this.mCornerItemBackground.addState(new int[] { android.R.attr.state_pressed }, cornerItemPressedDrawable);
        this.mCornerItemBackground.addState(new int[] {}, cornerItemNormalDrawable);
    }

    private StateListDrawable getCenterItemBackground() {
        StateListDrawable centerItemBackground = new StateListDrawable();
        GradientDrawable centerItemPressedDrawable = new GradientDrawable();
        centerItemPressedDrawable.setColor(this.mPressedBackgroundColor);
        GradientDrawable centerItemNormalDrawable = new GradientDrawable();
        centerItemNormalDrawable.setColor(this.mNormalBackgroundColor);
        centerItemBackground.addState(new int[] { android.R.attr.state_pressed }, centerItemPressedDrawable);
        centerItemBackground.addState(new int[] {}, centerItemNormalDrawable);
        return centerItemBackground;
    }

    private void refreshTextColorStateList(int pressedTextColor, int normalTextColor) {
        int[][] states = new int[2][];
        states[0] = new int[] { android.R.attr.state_pressed };
        states[1] = new int[] {};
        int[] colors = new int[] { pressedTextColor, normalTextColor };
        mTextColorStateList = new ColorStateList(states, colors);
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private int getScreenHeight() {
        WindowManager wm = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    private int getViewWidth(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredWidth();
    }

    private int getViewHeight(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    public void hidePopupListWindow() {
        if (this.mContext instanceof Activity && ((Activity) this.mContext).isFinishing()) {
            return;
        }
        if (this.mPopupWindow != null && this.mPopupWindow.isShowing()) {
            this.mPopupWindow.dismiss();
        }
    }

    public View getIndicatorView() {
        return this.mIndicatorView;
    }

    public View getDefaultIndicatorView(final float widthPixel, final float heightPixel, final int color) {
        ImageView indicator = new ImageView(this.mContext);
        Drawable drawable = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                Path path = new Path();
                Paint paint = new Paint();
                paint.setColor(color);
                paint.setStyle(Paint.Style.FILL);
                path.moveTo(0f, 0f);
                path.lineTo(widthPixel, 0f);
                path.lineTo(widthPixel / 2, heightPixel);
                path.close();
                canvas.drawPath(path, paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }

            @Override
            public int getIntrinsicWidth() {
                return (int) widthPixel;
            }

            @Override
            public int getIntrinsicHeight() {
                return (int) heightPixel;
            }
        };
        indicator.setImageDrawable(drawable);
        return indicator;
    }

    public void setIndicatorView(View indicatorView) {
        this.mIndicatorView = indicatorView;
    }

    public void setIndicatorSize(int widthPixel, int heightPixel) {
        this.mIndicatorWidth = widthPixel;
        this.mIndicatorHeight = heightPixel;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(this.mIndicatorWidth, this.mIndicatorHeight);
        layoutParams.gravity = Gravity.CENTER;
        if (this.mIndicatorView != null) {
            this.mIndicatorView.setLayoutParams(layoutParams);
        }
    }

    public int getNormalTextColor() {
        return this.mNormalTextColor;
    }

    public void setNormalTextColor(int normalTextColor) {
        this.mNormalTextColor = normalTextColor;
        refreshTextColorStateList(this.mPressedTextColor, this.mNormalTextColor);
    }

    public int getPressedTextColor() {
        return this.mPressedTextColor;
    }

    public void setPressedTextColor(int pressedTextColor) {
        this.mPressedTextColor = pressedTextColor;
        refreshTextColorStateList(this.mPressedTextColor, this.mNormalTextColor);
    }

    public float getTextSizePixel() {
        return this.mTextSizePixel;
    }

    public void setTextSizePixel(float textSizePixel) {
        this.mTextSizePixel = textSizePixel;
    }

    public float getTextPaddingLeftPixel() {
        return this.mTextPaddingLeftPixel;
    }

    public void setTextPaddingLeftPixel(float textPaddingLeftPixel) {
        this.mTextPaddingLeftPixel = textPaddingLeftPixel;
    }

    public float getTextPaddingTopPixel() {
        return this.mTextPaddingTopPixel;
    }

    public void setTextPaddingTopPixel(float textPaddingTopPixel) {
        this.mTextPaddingTopPixel = textPaddingTopPixel;
    }

    public float getTextPaddingRightPixel() {
        return this.mTextPaddingRightPixel;
    }

    public void setTextPaddingRightPixel(float textPaddingRightPixel) {
        this.mTextPaddingRightPixel = textPaddingRightPixel;
    }

    public float getTextPaddingBottomPixel() {
        return this.mTextPaddingBottomPixel;
    }

    public void setTextPaddingBottomPixel(float textPaddingBottomPixel) {
        this.mTextPaddingBottomPixel = textPaddingBottomPixel;
    }

    public int getNormalBackgroundColor() {
        return mNormalBackgroundColor;
    }

    public void setNormalBackgroundColor(int normalBackgroundColor) {
        this.mNormalBackgroundColor = normalBackgroundColor;
        this.refreshBackgroundOrRadiusStateList();
    }

    public int getPressedBackgroundColor() {
        return this.mPressedBackgroundColor;
    }

    public void setPressedBackgroundColor(int pressedBackgroundColor) {
        this.mPressedBackgroundColor = pressedBackgroundColor;
        refreshBackgroundOrRadiusStateList();
    }

    public int getBackgroundCornerRadiusPixel() {
        return this.mBackgroundCornerRadiusPixel;
    }

    public void setBackgroundCornerRadiusPixel(int backgroundCornerRadiusPixel) {
        this.mBackgroundCornerRadiusPixel = backgroundCornerRadiusPixel;
        this.refreshBackgroundOrRadiusStateList();
    }

    public int getDividerColor() {
        return this.mDividerColor;
    }

    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
    }

    public int getDividerWidthPixel() {
        return this.mDividerWidthPixel;
    }

    public void setDividerWidthPixel(int dividerWidthPixel) {
        this.mDividerWidthPixel = dividerWidthPixel;
    }

    public int getDividerHeightPixel() {
        return this.mDividerHeightPixel;
    }

    public void setDividerHeightPixel(int dividerHeightPixel) {
        this.mDividerHeightPixel = dividerHeightPixel;
    }

    @Override
    public void onKeyboardShown(int keyboardSize) {
    }

    /**
     * 当键盘显示点击条目弹出pop，关闭键盘让pop消失
     */
    @Override
    public void onKeyboardClosed() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            hidePopupListWindow();
        }
    }

    public interface OnPopupListClickListener {
        void onPopupListClick(View contextView, int contextPosition, String type, int position);
    }

    public int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public Resources getResources() {
        if (mContext == null) {
            return Resources.getSystem();
        }
        else {
            return mContext.getResources();
        }
    }
}
