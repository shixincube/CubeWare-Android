package cube.ware.widget.toolbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.TintTypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.log.LogUtil;

import cube.ware.service.message.R;

import static android.support.v7.appcompat.R.styleable;

/**
 * 自定义CubeToolbar实现类 继承LinearLayout
 */
public class CubeToolbar2 extends RelativeLayout implements View.OnClickListener, ICubeToolbar {

    protected OnTitleItemClickListener mOnTitleItemClickListener;

    private Context mContext;

    private LinearLayoutCompat mTitleLayout;
    private TextView           mTitleTextView;
    private CharSequence       mTitleText;
    private boolean            mTitleVisible;

    private LinearLayoutCompat mTitleInnerLayout;

    private TextView     mSubtitleTextView;
    private CharSequence mSubTitleText;
    private boolean      mSubTitleVisible;
    private int          mSubTitleLeftIcon;

    private ImageView mLogoView;
    private boolean   mLogoVisible;

    private ProgressBar mProgressBar;
    private boolean     mProgressVisible;

    private TextView     mBackTextView;
    private CharSequence mBackText;
    private boolean      mBackVisible;

    private TextView     mRightTextView;
    private CharSequence mRightText;
    private boolean      mRightVisible;

    private TextView     mLeftCloseTextView;
    private CharSequence mCloseText;
    private boolean      isCloseVisible;

    private static final int DEFAULT_BACK_MARGIN_RIGHT  = 8;
    private static final int DEFAULT_RIGHT_MARGIN_RIGHT = 20;
    private static final int DEFAULT_BACK_PADDING_RIGHT = 4;
    private static final int DEFAULT_BACK_PADDING       = 16;

    public CubeToolbar2(Context context) {
        this(context, null);
    }

    public CubeToolbar2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CubeToolbar2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    protected void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        initCustomView(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    protected void initCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, styleable.Toolbar, defStyleAttr, 0);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CubeToolbar);
        //GlideUtil.loadBlurImage(context, this, R.drawable.ic_blur_white);
        if (!isChild(mBackTextView)) {
            mBackTextView = new TextView(context);
            mBackTextView.setId(R.id.back);
            mBackTextView.setSingleLine();
            mBackTextView.setEllipsize(TextUtils.TruncateAt.END);
            mBackTextView.setGravity(Gravity.CENTER_VERTICAL);

            int backTextAppearance = typedArray.getResourceId(R.styleable.CubeToolbar_backTextAppearance, 0);
            if (backTextAppearance != 0) {
                mBackTextView.setTextAppearance(context, backTextAppearance);
            }

            if (typedArray.hasValue(R.styleable.CubeToolbar_backTextColor)) {
                int backTextColor = typedArray.getColor(R.styleable.CubeToolbar_backTextColor, Color.WHITE);
                mBackTextView.setTextColor(backTextColor);
            }

            setBackTextSize(typedArray.getDimensionPixelSize(R.styleable.CubeToolbar_backTextSize, 8));
            setBackIcon(typedArray.getDrawable(R.styleable.CubeToolbar_backIcon));
            setBackText(typedArray.getText(R.styleable.CubeToolbar_backText));
            setBackVisible(typedArray.getBoolean(R.styleable.CubeToolbar_backVisible, false));
            setBackPadding(dp2px(DEFAULT_BACK_PADDING));
            mBackTextView.setClickable(true);
            mBackTextView.setOnClickListener(this);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(ALIGN_PARENT_LEFT);
            layoutParams.leftMargin = typedArray.getDimensionPixelSize(R.styleable.CubeToolbar_backMarginRight, dp2px(DEFAULT_BACK_MARGIN_RIGHT));

            addView(mBackTextView, layoutParams);
        }

        if (!isChild(mLeftCloseTextView)) {
            LogUtil.d("cloudz", "mLeftCloseTextView");
            mLeftCloseTextView = new TextView(context);
            mLeftCloseTextView.setId(R.id.close);
            mLeftCloseTextView.setSingleLine();
            mLeftCloseTextView.setEllipsize(TextUtils.TruncateAt.END);
            mLeftCloseTextView.setGravity(Gravity.CENTER_VERTICAL);

            int backTextAppearance = typedArray.getResourceId(R.styleable.CubeToolbar_backTextAppearance, 0);
            if (backTextAppearance != 0) {
                mLeftCloseTextView.setTextAppearance(context, backTextAppearance);
            }

            if (typedArray.hasValue(R.styleable.CubeToolbar_backTextColor)) {
                int backTextColor = typedArray.getColor(R.styleable.CubeToolbar_backTextColor, Color.WHITE);
                mLeftCloseTextView.setTextColor(backTextColor);
            }

            mLeftCloseTextView.setClickable(true);
            mLeftCloseTextView.setOnClickListener(this);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RIGHT_OF, R.id.back);
            layoutParams.rightMargin = 2 * typedArray.getDimensionPixelSize(R.styleable.CubeToolbar_backMarginRight, dp2px(DEFAULT_BACK_MARGIN_RIGHT));

            addView(mLeftCloseTextView, layoutParams);
        }

        if (!isChild(mTitleLayout)) {
            mTitleLayout = new LinearLayoutCompat(context);
            mTitleLayout.setOrientation(LinearLayoutCompat.VERTICAL);
            mTitleLayout.setGravity(typedArray.getInt(R.styleable.CubeToolbar_title_gravity, Gravity.CENTER_VERTICAL));
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(CENTER_IN_PARENT);
            addView(mTitleLayout, layoutParams);

            if (!isChild(mTitleInnerLayout)) {
                mTitleInnerLayout = new LinearLayoutCompat(context);
                mTitleInnerLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);
                mTitleInnerLayout.setGravity(typedArray.getInt(R.styleable.CubeToolbar_title_gravity, Gravity.CENTER_VERTICAL));
                LayoutParams layoutParamsInner = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParamsInner.addRule(CENTER_IN_PARENT);
                mTitleLayout.addView(mTitleInnerLayout, layoutParamsInner);
            }
        }

        if (!isChild(mProgressBar, mTitleInnerLayout)) {
            mProgressBar = new ProgressBar(context);
            mProgressBar.setIndeterminate(typedArray.getBoolean(R.styleable.CubeToolbar_progressIndeterminate, true));

            setProgressDrawable(typedArray.getDrawable(R.styleable.CubeToolbar_progressDrawable));
            setProgressVisible(typedArray.getBoolean(R.styleable.CubeToolbar_progressVisible, false));
            int width = typedArray.getDimensionPixelSize(R.styleable.CubeToolbar_progressHeight, 70);
            int height = typedArray.getDimensionPixelSize(R.styleable.CubeToolbar_progressWidth, 70);
            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(width, height, Gravity.CENTER);
            mTitleInnerLayout.addView(mProgressBar, layoutParams);
        }

        if (!isChild(mTitleTextView, mTitleInnerLayout)) {
            mTitleTextView = new TextView(context);
            mTitleTextView.setSingleLine();
            mTitleTextView.setId(R.id.cube_title);
            mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            mTitleTextView.setGravity(Gravity.CENTER);
            mTitleTextView.setMaxWidth(ScreenUtil.dip2px(160));

            int titleTextAppearance = a.getResourceId(styleable.Toolbar_titleTextAppearance, 0);
            if (titleTextAppearance != 0) {
                mTitleTextView.setTextAppearance(context, titleTextAppearance);
            }

            if (a.hasValue(styleable.Toolbar_titleTextColor)) {
                int titleColor = a.getColor(styleable.Toolbar_titleTextColor, Color.WHITE);
                mTitleTextView.setTextColor(titleColor);
            }

            setTitleTextSize(typedArray.getDimensionPixelSize(R.styleable.CubeToolbar_titleTextSize, 10));
            setTitle("");
            setTitleIcon(typedArray.getDrawable(R.styleable.CubeToolbar_leftTitleIcon), typedArray.getDrawable(R.styleable.CubeToolbar_rightTitleIcon));
            setTitleVisible(typedArray.getBoolean(R.styleable.CubeToolbar_titleVisible, true));
            mTitleTextView.setClickable(true);
            mTitleTextView.setOnClickListener(this);

            mTitleInnerLayout.addView(mTitleTextView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }

        if (!isChild(mSubtitleTextView, mTitleLayout)) {
            mSubtitleTextView = new TextView(context);
            mSubtitleTextView.setSingleLine();
            mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            mSubtitleTextView.setGravity(Gravity.CENTER);

            int subTextAppearance = a.getResourceId(styleable.Toolbar_subtitleTextAppearance, 0);
            if (subTextAppearance != 0) {
                mSubtitleTextView.setTextAppearance(context, subTextAppearance);
            }

            if (a.hasValue(styleable.Toolbar_subtitleTextColor)) {
                int subTitleColor = a.getColor(styleable.Toolbar_subtitleTextColor, Color.WHITE);
                mSubtitleTextView.setTextColor(subTitleColor);
            }

            if (typedArray.hasValue(R.styleable.CubeToolbar_subtitleTextSize)) {
                mSubtitleTextView.setTextSize(typedArray.getDimensionPixelSize(R.styleable.CubeToolbar_subtitleTextSize, 0));
            }

            setSubtitle(a.getText(styleable.Toolbar_subtitle));
            setSubtitleVisible(typedArray.getBoolean(R.styleable.CubeToolbar_subtitleVisible, false));

            mTitleLayout.addView(mSubtitleTextView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }

        if (!isChild(mRightTextView)) {
            mRightTextView = new TextView(context);
            mRightTextView.setId(R.id.right);
            mRightTextView.setSingleLine();
            mRightTextView.setEllipsize(TextUtils.TruncateAt.END);
            mRightTextView.setGravity(Gravity.CENTER_VERTICAL);

            int rightTextAppearance = typedArray.getResourceId(R.styleable.CubeToolbar_rightTextAppearance, 0);
            if (rightTextAppearance != 0) {
                mRightTextView.setTextAppearance(context, rightTextAppearance);
            }

            if (typedArray.hasValue(R.styleable.CubeToolbar_rightTextColor)) {
                int rightTextColor = typedArray.getColor(R.styleable.CubeToolbar_rightTextColor, Color.WHITE);
                mRightTextView.setTextColor(rightTextColor);
            }

            setRightTextSize(typedArray.getDimensionPixelSize(R.styleable.CubeToolbar_rightTextSize, 8));
            setRightIcon(typedArray.getDrawable(R.styleable.CubeToolbar_rightIcon));
            setRightText(typedArray.getText(R.styleable.CubeToolbar_rightText));
            setRightVisible(typedArray.getBoolean(R.styleable.CubeToolbar_rightVisible, false));
            mRightTextView.setClickable(true);
            mRightTextView.setOnClickListener(this);

            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(ALIGN_PARENT_RIGHT);
            layoutParams.rightMargin = typedArray.getDimensionPixelSize(R.styleable.CubeToolbar_rightMarginRight, dp2px(DEFAULT_RIGHT_MARGIN_RIGHT));
            addView(mRightTextView, layoutParams);
        }

        if (!isChild(mLogoView)) {
            mLogoView = new ImageView(context);
            mLogoView.setId(R.id.logo);
            mLogoView.setScaleType(ImageView.ScaleType.CENTER);

            //setLogoBackground(typedArray.getResourceId(R.styleable.CubeToolbar_logoBackground, R.drawable.ic_search));
            setLogoVisible(typedArray.getBoolean(R.styleable.CubeToolbar_logoVisible, false));

            mLogoView.setClickable(true);
            mLogoView.setOnClickListener(this);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(ALIGN_PARENT_LEFT);
            addView(mLogoView, layoutParams);
        }

        typedArray.recycle();
        a.recycle();
    }

    public void setProgressDrawable(Drawable drawable) {
        if (mProgressBar != null) {
            mProgressBar.setIndeterminateDrawable(drawable);
        }
    }

    public void setProgressVisible(boolean visible) {
        mProgressVisible = visible;
        mProgressBar.setVisibility(visible ? VISIBLE : GONE);
    }

    public boolean isProgressVisible() {
        return mProgressVisible;
    }

    public void setTitle(CharSequence title) {
        mTitleText = title;
        if (mTitleTextView != null) {
            mTitleTextView.setText(title);
        }
    }

    public void setTitleIcon(@Nullable Drawable left, @Nullable Drawable right) {
        if (mTitleTextView != null) {
            mTitleTextView.setCompoundDrawablePadding(DEFAULT_BACK_PADDING_RIGHT);
            mTitleTextView.setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
        }
    }

    public CharSequence getTitle() {
        return mTitleText;
    }

    public void setTitleTextAppearance(Context context, int resId) {
        if (mTitleTextView != null) {
            mTitleTextView.setTextAppearance(context, resId);
        }
    }

    public void setTitleTextColor(@ColorRes int color) {
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(getResources().getColor(color));
        }
    }

    public void setTitleTextSize(int titleSize) {
        if (mTitleTextView != null) {
            mTitleTextView.setTextSize(titleSize);
        }
    }

    public void setTitleVisible(boolean visible) {
        mTitleVisible = visible;
        mTitleTextView.setVisibility(mTitleVisible ? VISIBLE : GONE);
    }

    public boolean getTitleVisible() {
        return mTitleVisible;
    }

    public void setSubtitle(CharSequence subtitle) {
        mSubTitleText = subtitle;
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setText(subtitle);
        }
    }

    public CharSequence getSubtitle() {
        return mSubTitleText;
    }

    public void setSubtitleTextAppearance(Context context, int resId) {
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setTextAppearance(context, resId);
        }
    }

    public void setSubtitleTextColor(int color) {
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setTextColor(color);
        }
    }

    public void setSubtitleVisible(boolean visible) {
        mSubTitleVisible = visible;
        mSubtitleTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public boolean getSubtitleVisible() {
        return mSubTitleVisible;
    }

    public void setLogoBackground(int resId) {
        if (mLogoView != null) {
            mLogoView.setImageResource(resId);
        }
    }

    public void setLogoVisible(boolean visible) {
        mLogoVisible = visible;
        if (mLogoView != null) {
            mLogoView.setVisibility(mLogoVisible ? VISIBLE : GONE);
        }
    }

    public boolean isLogoVisible() {
        return mLogoVisible;
    }

    public void setBackText(int resId) {
        setBackText(getContext().getText(resId));
    }

    public void setBackText(CharSequence backText) {
        mBackText = backText;
        if (mBackTextView != null) {
            //mBackTextView.setText(backText); //v2.1版本UI不想显示返回按键旁边的文字
        }
    }

    public void setClsoeText(CharSequence clsoeText) {
        mCloseText = clsoeText;
        if (mLeftCloseTextView != null) {
            mLeftCloseTextView.setText(clsoeText);
        }
    }

    public void setBackTextSize(int backTextSize) {
        if (mBackTextView != null) {
            mBackTextView.setTextSize(backTextSize);
        }
    }

    public void setBackIcon(Drawable drawable) {
        if (mBackTextView != null) {
            mBackTextView.setCompoundDrawablePadding(DEFAULT_BACK_PADDING_RIGHT);
            mBackTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
    }

    public CharSequence getBackText() {
        return mBackText;
    }

    public void setBackTextColor(@ColorRes int color) {
        if (mBackTextView != null) {
            mBackTextView.setTextColor(this.getColorStateList(color));
        }
    }

    public void setBackVisible(boolean visible) {
        mBackVisible = visible;
        mBackTextView.setVisibility(mBackVisible ? VISIBLE : GONE);
    }

    public void setCloseVisible(boolean visible) {
        isCloseVisible = visible;
        mLeftCloseTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setBackPadding(int backPadding) {
        if (mBackTextView != null) {
            mBackTextView.setPadding(0, 0, backPadding, 0);
        }
    }

    public boolean isBackVisible() {
        return mBackVisible;
    }

    public void setRightText(int resId) {
        setRightText(getContext().getText(resId));
    }

    public void setRightText(CharSequence rightText) {
        mRightText = rightText;
        if (mRightTextView != null) {
            mRightTextView.setText(rightText);
        }
    }

    public void setSubtitleTextViewIcon(Drawable drawable) {
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setCompoundDrawablePadding(8);
            mSubtitleTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
    }

    public void setSubtitleTextViewSize(float size) {
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setTextSize(size);
        }
    }

    public void setRightIcon(Drawable drawable) {
        if (mRightTextView != null) {
            mRightTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }
    }

    public void setCloseIcon(Drawable drawable) {
        if (mLeftCloseTextView != null) {
            mLeftCloseTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }
    }

    public void setRightPadding(int backPadding) {
        if (mRightTextView != null) {
            mRightTextView.setPadding(backPadding, 0, 0, 0);
        }
    }

    public CharSequence getRightText() {
        return mRightText;
    }

    public void setRightTextSize(int rightTextSize) {
        if (mRightTextView != null) {
            mRightTextView.setTextSize(rightTextSize);
        }
    }

    public void setRightTextColor(@ColorRes int color) {
        if (mRightTextView != null) {
            mRightTextView.setTextColor(this.getColorStateList(color));
        }
    }

    public void setRightVisible(boolean visible) {
        mRightVisible = visible;
        mRightTextView.setVisibility(mRightVisible ? VISIBLE : GONE);
    }

    public boolean isRightVisible() {
        return mRightVisible;
    }

    public void setRightEnabled(boolean enabled) {
        mRightTextView.setEnabled(enabled);
    }

    @Override
    public void onClick(View v) {
        if (mOnTitleItemClickListener != null) {
            mOnTitleItemClickListener.onTitleItemClick(v);
        }
    }

    /**
     * 获取ColorStateList
     *
     * @param colorId
     *
     * @return
     */
    private ColorStateList getColorStateList(@ColorRes int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColorStateList(colorId, getContext().getTheme());
        }
        else {
            return getResources().getColorStateList(colorId);
        }
    }

    public void setSubTitleLeftIcon(int subTitleLeftIcon) {
        mSubTitleLeftIcon = subTitleLeftIcon;
    }

    @Override
    public void setScrollFlags(int scrollFlags) {

    }

    @Override
    public void setOnTitleItemClickListener(ICubeToolbar.OnTitleItemClickListener listener) {
        mOnTitleItemClickListener = listener;
    }

    @Override
    public void addView(LinearLayout titleOptionLayout, ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            super.addView(titleOptionLayout, layoutParams);
        }
    }

    @Override
    public void addView(LinearLayout titleOptionLayout) {
        super.addView(titleOptionLayout);
    }

    public boolean isChild(View view) {
        return view != null && view.getParent() == this;
    }

    public boolean isChild(View view, ViewParent parent) {
        return view != null && view.getParent() == parent;
    }

    public int dp2px(float dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
