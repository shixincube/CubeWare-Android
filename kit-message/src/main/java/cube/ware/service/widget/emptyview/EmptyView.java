package cube.ware.service.widget.emptyview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.common.utils.utils.log.LogUtil;
import cube.ware.service.message.R;

/**
 * 无数据View
 *
 * 参考：https://github.com/barryhappy/TEmptyView/blob/master/README.cn.md
 *
 * @author PengZhenjin
 * @date 2016/8/2
 */
public class EmptyView extends FrameLayout {

    private static EmptyViewUtil.EmptyViewBuilder mConfig = null;

    private float           mTextSize;
    private int             mTextColor;
    private String          mEmptyText;
    private int             mIconSrc;
    private OnClickListener mOnClickListener;
    private String          actionText;

    private boolean mShowIcon   = true;
    private boolean mShowText   = true;
    private boolean mShowButton = false;

    private ImageView mImageView;
    private TextView  mTextView;
    private TextView  mButton;

    public static void init(EmptyViewUtil.EmptyViewBuilder defaultConfig) {
        EmptyView.mConfig = defaultConfig;
    }

    public static boolean hasDefaultConfig() {
        return EmptyView.mConfig != null;
    }

    public static EmptyViewUtil.EmptyViewBuilder getConfig() {
        return mConfig;
    }

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.empty_view, this);

        mTextView = (TextView) findViewById(R.id.empty_tv);
        mImageView = (ImageView) findViewById(R.id.empty_iv);
        mButton = (TextView) findViewById(R.id.empty_btn);
    }

    public void setShowIcon(boolean mShowIcon) {
        this.mShowIcon = mShowIcon;
        mImageView.setVisibility(mShowIcon ? VISIBLE : GONE);
    }

    public void setShowText(boolean showText) {
        this.mShowText = showText;
        mTextView.setVisibility(showText ? VISIBLE : GONE);
    }

    public void setShowButton(boolean showButton) {
        this.mShowButton = showButton;
        mButton.setVisibility(showButton ? VISIBLE : GONE);
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        mTextView.setTextSize(mTextSize);
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        mTextView.setTextColor(mTextColor);
    }

    public String getEmptyText() {
        return mEmptyText;
    }

    public void setEmptyText(String mEmptyText) {
        this.mEmptyText = mEmptyText;
        mTextView.setText(mEmptyText);
    }

    public void setIcon(int mIconSrc) {
        this.mIconSrc = mIconSrc;
        mImageView.setImageResource(mIconSrc);
    }

    public void setIcon(Drawable drawable) {
        this.mIconSrc = 0;
        mImageView.setImageDrawable(drawable);
    }

    public void setAction(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.i("oaaaa--->");
            }
        });
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
        mButton.setText(actionText);
    }

    public void setActionColor(int textColor) {
        mButton.setTextColor(getResources().getColor(textColor));
    }

    /**
     * 设置button背景
     *
     * @param resId
     */
    public void setActionBackground(int resId) {
        mButton.setBackgroundResource(resId);
    }
}
