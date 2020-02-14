package cube.ware.common;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import com.common.base.BaseActivity;
import com.common.base.BasePresenter;
import cube.ware.service.message.R;
import cube.ware.widget.toolbar.CubeToolbar;
import cube.ware.widget.toolbar.ICubeToolbar;
import cube.ware.widget.toolbar.ToolBarOptions;

/**
 * Created by dth
 * Des: 定制了toolbar的activity 如果需要使用自定义toolbar，继承它
 * Date: 2018/9/10.
 */

public abstract class BaseToolBarActivity<T extends BasePresenter> extends BaseActivity<T> {

    protected ICubeToolbar   mToolbar;
    protected ToolBarOptions mToolbarOptions = null;

    /**
     * 设置toolbar
     *
     * @param options
     */
    public void setToolBar(final ToolBarOptions options) {
        mToolbar = (ICubeToolbar) findViewById(R.id.toolbar);
        mToolbarOptions = options;
        if (mToolbar != null) {
            innerSetupToolbar(options);
            if (mToolbar instanceof CubeToolbar) {
                super.setSupportActionBar((Toolbar) mToolbar);
            }
        }
    }

    protected void innerSetupToolbar(ToolBarOptions options) {
        mToolbar.setLogoVisible(options.isLogoVisible());
        mToolbar.setTitleVisible(options.isTitleVisible());
        mToolbar.setBackVisible(options.isBackVisible());
        mToolbar.setCloseVisible(options.isCloseVisble());
        mToolbar.setRightVisible(options.isRightVisible());
        mToolbar.setRightEnabled(options.isRightEnabled());
        mToolbar.setSubtitleVisible(options.isSubtitleVisible());
        mToolbar.setProgressVisible(options.isProgressVisible());

        if (options.isCloseVisble()) {
            if (options.getCloseIcon() != 0) {
                mToolbar.setCloseIcon(getResources().getDrawable(options.getCloseIcon()));
            }
        }

        if (options.isLogoVisible()) {
            if (options.getLogoId() != 0) {
                mToolbar.setLogoBackground(options.getLogoId());
            }
        }

        if (options.isProgressVisible()) {
            if (options.getProgressId() != 0) {
                mToolbar.setProgressDrawable(getResources().getDrawable(options.getProgressId()));
            }
        }

        if (options.isTitleVisible()) {
            if (options.getRightTitleIcon() != 0) {
                mToolbar.setTitleIcon(null, getResources().getDrawable(options.getRightTitleIcon()));
            }
            else if (options.getLeftTitleIcon() != 0) {
                mToolbar.setTitleIcon(getResources().getDrawable(options.getLeftTitleIcon()), null);
            }
            else {
                mToolbar.setTitleIcon(null, null);
            }
            if (!TextUtils.isEmpty(options.getTitle())) {
                mToolbar.setTitle(options.getTitle());
            }
            if (options.getTitleTextColor() != 0) {
                mToolbar.setTitleTextColor(options.getTitleTextColor());
            }
            if (options.getTitleTextSize() != 0) {
                mToolbar.setTitleTextSize(options.getTitleTextSize());
            }
        }

        if (options.isSubtitleVisible()) {
            if (!TextUtils.isEmpty(options.getSubtitle())) {
                mToolbar.setSubtitle(options.getSubtitle());
            }
            if (options.getSubtitleTextColor() != 0) {
                mToolbar.setSubtitleTextColor(options.getSubtitleTextColor());
            }
        }

        if (options.isBackVisible()) {
            if (options.getBackIcon() != 0) {
                mToolbar.setBackIcon(getResources().getDrawable(options.getBackIcon()));
            }
            else {
                mToolbar.setBackIcon(null);
            }
            if (!TextUtils.isEmpty(options.getBackText())) {
                mToolbar.setBackText(options.getBackText());
            }
            if (options.getBackTextColor() != 0) {
                mToolbar.setBackTextColor(options.getBackTextColor());
            }
            if (options.getBackTextSize() != 0) {
                mToolbar.setBackTextSize(options.getBackTextSize());
            }
        }

        if (!TextUtils.isEmpty(options.getRightText())) {
            mToolbar.setRightText(options.getRightText());
        }
        if (options.getRightIcon() != 0) {
            mToolbar.setRightIcon(getResources().getDrawable(options.getRightIcon()));
        }
        if (options.getRightTextColor() != 0) {
            mToolbar.setRightTextColor(options.getRightTextColor());
        }
        if (options.getRightTextSize() != 0) {
            mToolbar.setRightTextSize(options.getRightTextSize());
        }

        if (options.getSubTitleLeftIcon() != 0) {
            mToolbar.setSubTitleLeftIcon(options.getSubTitleLeftIcon());
        }

        if (options.getScrollFlags() != 0) {
            mToolbar.setScrollFlags(options.getScrollFlags());
        }

        if (options.getOnTitleClickListener() != null) {
            mToolbar.setOnTitleItemClickListener(options.getOnTitleClickListener());
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (this.mToolbar != null) {
            this.mToolbar.setTitle(title);
        }
    }

    /**
     * 获取toolbar
     *
     * @return
     */
    public ICubeToolbar getToolBar() {
        return this.mToolbar;
    }

    /**
     * 获取ToolBarOptions
     *
     * @return
     */
    public ToolBarOptions getToolBarOptions() {
        return this.mToolbarOptions;
    }

    /**
     * 获取toolbar的高度
     *
     * @return
     */
    public int getToolBarHeight() {
        if (this.mToolbar != null) {
            return this.mToolbar.getHeight();
        }
        return 0;
    }
}
