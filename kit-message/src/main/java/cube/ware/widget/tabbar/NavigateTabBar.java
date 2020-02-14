package cube.ware.widget.tabbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import cube.ware.service.message.R;

/**
 * cube.ware.widget.tabbar.NavigateTabBar
 *
 * @author lzx
 * @date 2018-3-27
 */
public class NavigateTabBar extends LinearLayout implements View.OnClickListener {

    private static final String KEY_CURRENT_TAG = "cube.ware.widget.tabbar.NavigateTabBar";

    private List<ViewHolder>      mViewHolderList;
    private OnTabSelectedListener mTabSelectListener;
    private FragmentActivity      mFragmentActivity;
    private String                mCurrentTag;
    private String                mRestoreTag;

    /**
     * 主内容显示区域View的id
     */
    private int mMainContentLayoutId;

    /**
     * 选中的Tab文字颜色
     */
    private ColorStateList mSelectedTextColor;

    /**
     * 正常的Tab文字颜色
     */
    private ColorStateList mNormalTextColor;

    /**
     * Tab文字的颜色
     */
    private float mTabTextSize;

    /**
     * 默认选中的tab index
     */
    private int mDefaultSelectedTab = 0;

    /**
     * 当前选中的tab
     */
    private int mCurrentSelectedTab;

    private long lastClickTimeStamp = 0;

    public NavigateTabBar(Context context) {
        this(context, null);
    }

    public NavigateTabBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigateTabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NavigateTabBar, 0, 0);
        ColorStateList tabTextColor = typedArray.getColorStateList(R.styleable.NavigateTabBar_navigateTabTextColor);
        ColorStateList selectedTabTextColor = typedArray.getColorStateList(R.styleable.NavigateTabBar_navigateTabSelectedTextColor);

        this.mTabTextSize = typedArray.getDimensionPixelSize(R.styleable.NavigateTabBar_navigateTabTextSize, 0);
        this.mMainContentLayoutId = typedArray.getResourceId(R.styleable.NavigateTabBar_containerId, 0);
        this.mNormalTextColor = (tabTextColor != null ? tabTextColor : context.getResources().getColorStateList(R.color.navigate_tabbar_text_normal));

        if (selectedTabTextColor != null) {
            this.mSelectedTextColor = selectedTabTextColor;
        }
        else {
            ThemeUtils.checkAppCompatTheme(context);
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            this.mSelectedTextColor = context.getResources().getColorStateList(typedValue.resourceId);
        }

        this.mViewHolderList = new ArrayList<>();
    }

    public void setRedNum(int index, int num) {
        for (ViewHolder viewHolder : mViewHolderList) {
            if (viewHolder.tabIndex == index) {
                if (num <= 0) {
                    viewHolder.redNumTv.setVisibility(GONE);
                    return;
                }
                if (num > 99) {
                    viewHolder.redNumTv.setVisibility(VISIBLE);
                    viewHolder.redNumTv.setText("99+");
                }
                else {
                    viewHolder.redNumTv.setVisibility(VISIBLE);
                    viewHolder.redNumTv.setText(String.valueOf(num));
                }
            }
        }
    }

    public void setRedIMg(int index, int num) {
        for (ViewHolder viewHolder : mViewHolderList) {
            if (viewHolder.tabIndex == index) {
                if (num <= 0) {
                    viewHolder.redImag.setVisibility(GONE);
                    return;
                }
                if (num > 99) {
                    viewHolder.redImag.setVisibility(VISIBLE);
                }
                else {
                    viewHolder.redImag.setVisibility(VISIBLE);
                }
            }
        }
    }

    public void cancelRedNumt(int index) {
        for (ViewHolder viewHolder : mViewHolderList) {
            if (viewHolder.tabIndex == index) {
                viewHolder.redNumTv.setVisibility(GONE);
            }
        }
    }

    public void cancelRedIMg(int index){
        for (ViewHolder viewHolder : mViewHolderList) {
            if (viewHolder.tabIndex == index) {
                viewHolder.redImag.setVisibility(GONE);
            }
        }
    }

    /**
     * 添加tab
     *
     * @param tabParam
     */
    public void addTab(TabParam tabParam) {
        int defaultLayout = R.layout.cube_tab_view;
        View view = LayoutInflater.from(getContext()).inflate(defaultLayout, null);
        view.setFocusable(true);

        ViewHolder holder = new ViewHolder();
        holder.tabIndex = this.mViewHolderList.size();
        holder.tag = tabParam.title;
        holder.tabIndex = tabParam.index;
        holder.pageParam = tabParam;
        holder.tabIcon = (ImageView) view.findViewById(R.id.tab_icon);
        holder.tabTitle = ((TextView) view.findViewById(R.id.tab_title));
        holder.redNumTv = (TextView) view.findViewById(R.id.tab_badge_tv);
        holder.redImag = (ImageView)view.findViewById(R.id.tab_badge_red_img);

        if (TextUtils.isEmpty(tabParam.title)) {
            holder.tabTitle.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.tabIcon.getLayoutParams();
            layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            holder.tabIcon.setLayoutParams(layoutParams);
        }
        else {
            holder.tabTitle.setText(tabParam.title);
        }

        if (this.mTabTextSize != 0) {
            holder.tabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.mTabTextSize);
        }
        if(tabParam.index == 0){
            holder.tabTitle.setTextColor(this.mSelectedTextColor);
        }
        else if (this.mNormalTextColor != null) {
            holder.tabTitle.setTextColor(this.mNormalTextColor);
        }

        if (tabParam.backgroundColor > 0) {
            view.setBackgroundResource(tabParam.backgroundColor);
        }

        if (tabParam.iconResId > 0) {
            if(tabParam.index == 0){
                holder.tabIcon.setImageResource(tabParam.iconSelectedResId);
            }
            else{
                holder.tabIcon.setImageResource(tabParam.iconResId);
            }
        }
        else {
            holder.tabIcon.setVisibility(View.INVISIBLE);
        }

        if (tabParam.iconResId > 0 && tabParam.iconSelectedResId > 0) {
            view.setTag(holder);
            view.setOnClickListener(this);
            this.mViewHolderList.add(holder);
        }

        super.addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mMainContentLayoutId == 0) {
            throw new RuntimeException("mFrameLayoutId Cannot be 0");
        }
        if (this.mViewHolderList.size() == 0) {
            throw new RuntimeException("mViewHolderList.size Cannot be 0, Please call addTab()");
        }
        if (!(getContext() instanceof FragmentActivity)) {
            throw new RuntimeException("parent activity must is extends FragmentActivity");
        }
        this.mFragmentActivity = (FragmentActivity) getContext();

        ViewHolder defaultHolder = null;

        if (!TextUtils.isEmpty(this.mRestoreTag)) {
            for (ViewHolder holder : this.mViewHolderList) {
                if (TextUtils.equals(this.mRestoreTag, holder.tag)) {
                    defaultHolder = holder;
                    this.mRestoreTag = null;
                    break;
                }
            }
        }
        else {
            defaultHolder = this.mViewHolderList.get(this.mDefaultSelectedTab);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO: 2018/03/30 暂时限制一下频繁切换
        long tempTime = System.currentTimeMillis();
        if (tempTime - lastClickTimeStamp < 100) {
            return;
        }
        lastClickTimeStamp = tempTime;
        Object object = v.getTag();
        if (object != null && object instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) v.getTag();
            if (this.mTabSelectListener != null) {
                this.mTabSelectListener.onTabSelected(holder);
                if(holder.tabIndex == 2){
                    return;
                }
                this.selectIcon(holder.tabIndex);
            }
        }
    }

    private boolean isFragmentShown(FragmentTransaction transaction, String newTag) {
        if (TextUtils.equals(newTag, this.mCurrentTag)) {
            return true;
        }

        if (TextUtils.isEmpty(this.mCurrentTag)) {
            return false;
        }

        Fragment fragment = this.mFragmentActivity.getSupportFragmentManager().findFragmentByTag(this.mCurrentTag);
        if (fragment != null && !fragment.isHidden()) {
            transaction.hide(fragment);
        }

        return false;
    }

    /**
     * 设置选中的tab文字颜色
     *
     * @param color
     */
    public void setSelectedTabTextColor(int color) {
        this.mSelectedTextColor = ColorStateList.valueOf(color);
    }

    /**
     * 设置tab文字颜色
     *
     * @param color
     */
    public void setTabTextColor(int color) {
        this.mNormalTextColor = ColorStateList.valueOf(color);
    }

    /**
     * 设置fragment布局文件
     *
     * @param frameLayoutId
     */
    public void setFrameLayoutId(int frameLayoutId) {
        this.mMainContentLayoutId = frameLayoutId;
    }

    /**
     * 恢复状态
     *
     * @param savedInstanceState
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mRestoreTag = savedInstanceState.getString(KEY_CURRENT_TAG);
        }
    }

    /**
     * 保存状态
     *
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_CURRENT_TAG, this.mCurrentTag);
    }

    private void selectIcon(int position) {
        for (ViewHolder holder : this.mViewHolderList) {
            if (position != holder.tabIndex) {
                holder.tabIcon.setImageResource(holder.pageParam.iconResId);
                holder.tabTitle.setTextColor(this.mNormalTextColor);
            }
            else {
                holder.tabIcon.setImageResource(holder.pageParam.iconSelectedResId);
                holder.tabTitle.setTextColor(this.mSelectedTextColor);
            }
        }
    }

    /**
     * ViewHolder
     */
    public static class ViewHolder {
        public String    tag;
        public int       tabIndex;
        public TabParam  pageParam;
        public ImageView tabIcon;
        public TextView  tabTitle;
        public TextView  redNumTv;
        public ImageView redImag;
    }

    /**
     * tab参数类
     */
    public static class TabParam {
        public int backgroundColor = android.R.color.white;
        public int    iconResId;
        public int    iconSelectedResId;
        public int    titleStringRes;
        public int    tabViewResId;
        public int    index;
        public String title;

        /**
         * 构造方法
         *
         * @param iconResId
         * @param iconSelectedResId
         * @param title
         */
        public TabParam(int iconResId, int iconSelectedResId, String title) {
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.title = title;
        }

        /**
         * 构造方法
         *
         * @param iconResId
         * @param iconSelectedResId
         * @param title
         * @param index
         */
        public TabParam(int iconResId, int iconSelectedResId, String title, int index) {
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.title = title;
            this.index = index;
        }

        /**
         * 构造方法
         *
         * @param iconResId
         * @param iconSelectedResId
         * @param titleStringRes
         */
        public TabParam(int iconResId, int iconSelectedResId, int titleStringRes) {
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.titleStringRes = titleStringRes;
        }

        /**
         * 构造方法
         *
         * @param backgroundColor
         * @param iconResId
         * @param iconSelectedResId
         * @param titleStringRes
         */
        public TabParam(int backgroundColor, int iconResId, int iconSelectedResId, int titleStringRes) {
            this.backgroundColor = backgroundColor;
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.titleStringRes = titleStringRes;
        }

        /**
         * 构造方法
         *
         * @param backgroundColor
         * @param iconResId
         * @param iconSelectedResId
         * @param title
         */
        public TabParam(int backgroundColor, int iconResId, int iconSelectedResId, String title) {
            this.backgroundColor = backgroundColor;
            this.iconResId = iconResId;
            this.iconSelectedResId = iconSelectedResId;
            this.title = title;
        }
    }

    /**
     * tab选中监听器
     */
    public interface OnTabSelectedListener {
        void onTabSelected(ViewHolder holder);
    }

    /**
     * 设置tab选中监听器
     *
     * @param tabSelectListener
     */
    public void setTabSelectListener(OnTabSelectedListener tabSelectListener) {
        this.mTabSelectListener = tabSelectListener;
    }

    /**
     * 设置默认选中的tab
     *
     * @param index
     */
    public void setDefaultSelectedTab(int index) {
        if (index >= 0 && index < this.mViewHolderList.size()) {
            this.mDefaultSelectedTab = index;
        }
    }

    /**
     * 获取当前选中的tab
     *
     * @return
     */
    public int getCurrentSelectedTab() {
        return this.mCurrentSelectedTab;
    }
}
