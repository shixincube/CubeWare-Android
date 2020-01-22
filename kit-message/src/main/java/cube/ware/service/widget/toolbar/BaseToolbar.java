package cube.ware.service.widget.toolbar;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import static android.support.v7.appcompat.R.attr;

/**
 * 自定义CubeToolbar基类
 *
 * @author Wangxx
 * @date 2017/1/19
 */
public abstract class BaseToolbar extends Toolbar {

    protected ICubeToolbar.OnTitleItemClickListener mOnTitleItemClickListener;

    public BaseToolbar(Context context) {
        this(context, null);
    }

    public BaseToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, attr.toolbarStyle);
    }

    public BaseToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    protected void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        initCustomView(context, attrs, defStyleAttr);
    }

    protected abstract void initCustomView(Context context, AttributeSet attrs, int defStyleAttr);

    public boolean isChild(View view) {
        return view != null && view.getParent() == this;
    }

    public boolean isChild(View view, ViewParent parent) {
        return view != null && view.getParent() == parent;
    }

    public void setOnTitleItemClickListener(ICubeToolbar.OnTitleItemClickListener listener) {
        mOnTitleItemClickListener = listener;
    }

    public int dp2px(float dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
