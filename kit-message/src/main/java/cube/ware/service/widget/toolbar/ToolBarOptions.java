package cube.ware.service.widget.toolbar;

import android.support.design.widget.AppBarLayout;
import java.io.Serializable;

/**
 * toolbar定制化设置
 *
 * @author Wangxx
 * @date 2016/12/29
 */
public class ToolBarOptions {

    /**
     * toolbar的scrollFlags
     */
    public final static int SCROLL_FLAG_SCROLL                 = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
    public final static int SCROLL_FLAG_EXIT_UNTIL_COLLAPSED   = AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED;
    public final static int SCROLL_FLAG_ENTER_ALWAYS           = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
    public final static int SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED;
    public final static int SCROLL_FLAG_SNAP                   = AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;

    /**
     * toolbar的logo资源id
     */
    private int logoId = 0;

    /**
     * logo图标是否可见
     */
    private boolean isLogoVisible = false;

    /**
     * toolbar的progressBar资源id
     */
    private int progressId = 0;

    /**
     * progressBar图标是否可见
     */
    private boolean isProgressVisible = false;

    /**
     * toolbar的返回图标资源id
     */
    private int backIcon = 0;

    private int closeIcon = 0;

    /**
     * toolbar的返回图标内容
     */
    private String backText;

    /**
     * toolbar的返回图标文字颜色
     */
    private int backTextColor = 0;

    /**
     * toolbar的返回图标文字大小，默认14sp
     */
    private int backTextSize = 14;

    /**
     * toolbar的返回图标是否可见
     */
    private boolean isBackVisible = false;

    private boolean isCloseVisble = false;

    /**
     * toolbar的副标题
     */
    private String subtitle;

    /**
     * toolbar的副标题文字颜色
     */
    private int subtitleTextColor = 0;

    /**
     * toolbar的副标题是否可见
     */
    private boolean isSubtitleVisible = false;

    /**
     * toolbar的标题
     */
    private String title;

    /**
     * toolbar的标题文字颜色
     */
    private int titleTextColor = 0;

    /**
     * toolbar的标题文字大小，默认18sp
     */
    private int titleTextSize = 18;

    /**
     * toolbar的标题是否可见
     */
    private boolean isTitleVisible = true;

    /**
     * toolbar的Title资源id
     */
    private int leftTitleIcon = 0;

    /**
     * toolbar的Title资源id
     */
    private int rightTitleIcon = 0;

    /**
     * toolbar的右侧内容
     */
    private String rightText;

    /**
     * toolbar的右侧内容资源id
     */
    private int rightIcon = 0;

    /**
     * toolbar的右侧文字颜色
     */
    private int rightTextColor = 0;

    /**
     * toolbar的右侧文字大小，默认14sp
     */
    private int rightTextSize = 14;

    /**
     * toolbar的右侧图标是否可见
     */
    private boolean isRightVisible = false;

    /**
     * toolbar的右侧图标是否可用
     */
    private boolean isRightEnabled = true;

    /**
     * toolbar的点击监听器
     */
    private ICubeToolbar.OnTitleItemClickListener onTitleClickListener = null;

    /**
     * toolbar的滚动状态,默认不能滚动
     */
    private int scrollFlags = SCROLL_FLAG_SNAP;

    // TODO: 2017/10/28 典型的需要builder模式的代码

    public int getLogoId() {
        return logoId;
    }

    public void setLogoId(int logoId) {
        this.logoId = logoId;
    }

    public boolean isLogoVisible() {
        return isLogoVisible;
    }

    public void setLogoVisible(boolean logoVisible) {
        isLogoVisible = logoVisible;
    }

    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public boolean isProgressVisible() {
        return isProgressVisible;
    }

    public void setProgressVisible(boolean progressVisible) {
        isProgressVisible = progressVisible;
    }

    public int getBackIcon() {
        return backIcon;
    }

    public void setCloseIcon(int closeIcon) {
        this.closeIcon = closeIcon;
    }

    public int getCloseIcon() {
        return closeIcon;
    }

    public void setBackIcon(int backIcon) {
        this.backIcon = backIcon;
    }

    public String getBackText() {
        return backText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public int getBackTextColor() {
        return backTextColor;
    }

    public void setBackTextColor(int backTextColor) {
        this.backTextColor = backTextColor;
    }

    public int getBackTextSize() {
        return backTextSize;
    }

    public void setBackTextSize(int backTextSize) {
        this.backTextSize = backTextSize;
    }

    public boolean isBackVisible() {
        return isBackVisible;
    }

    public void setBackVisible(boolean backVisible) {
        isBackVisible = backVisible;
    }

    public void setCloseVisible(boolean closeVisible) {
        isCloseVisble = closeVisible;
    }

    public boolean isCloseVisble() {
        return isCloseVisble;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getSubtitleTextColor() {
        return subtitleTextColor;
    }

    public void setSubtitleTextColor(int subtitleTextColor) {
        this.subtitleTextColor = subtitleTextColor;
    }

    public boolean isSubtitleVisible() {
        return isSubtitleVisible;
    }

    public void setSubtitleVisible(boolean subtitleVisible) {
        isSubtitleVisible = subtitleVisible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public int getTitleTextSize() {
        return titleTextSize;
    }

    public void setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
    }

    public boolean isTitleVisible() {
        return isTitleVisible;
    }

    public void setTitleVisible(boolean titleVisible) {
        isTitleVisible = titleVisible;
    }

    public int getLeftTitleIcon() {
        return leftTitleIcon;
    }

    public void setLeftTitleIcon(int leftTitleIcon) {
        this.leftTitleIcon = leftTitleIcon;
    }

    public int getRightTitleIcon() {
        return rightTitleIcon;
    }

    public void setRightTitleIcon(int rightTitleIcon) {
        this.rightTitleIcon = rightTitleIcon;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public int getRightIcon() {
        return rightIcon;
    }

    public void setRightIcon(int rightIcon) {
        this.rightIcon = rightIcon;
    }

    public int getRightTextColor() {
        return rightTextColor;
    }

    public void setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
    }

    public int getRightTextSize() {
        return rightTextSize;
    }

    public void setRightTextSize(int rightTextSize) {
        this.rightTextSize = rightTextSize;
    }

    public boolean isRightVisible() {
        return isRightVisible;
    }

    public void setRightVisible(boolean rightVisible) {
        isRightVisible = rightVisible;
    }

    public boolean isRightEnabled() {
        return isRightEnabled;
    }

    public void setRightEnabled(boolean rightEnabled) {
        isRightEnabled = rightEnabled;
    }

    public ICubeToolbar.OnTitleItemClickListener getOnTitleClickListener() {
        return onTitleClickListener;
    }

    public void setOnTitleClickListener(ICubeToolbar.OnTitleItemClickListener onTitleClickListener) {
        this.onTitleClickListener = onTitleClickListener;
    }

    public int getScrollFlags() {
        return scrollFlags;
    }

    public void setScrollFlags(int scrollFlags) {
        this.scrollFlags = scrollFlags;
    }

    private int mSubTitleLeftIcon;

    public void setSubTitleLeftIcon(int subTitleLeftIcon) {
        mSubTitleLeftIcon = subTitleLeftIcon;
    }

    public int getSubTitleLeftIcon() {
        return mSubTitleLeftIcon;
    }

    /**
     * 标题栏右侧操作按钮
     *
     * @author PengZhenjin
     * @date 2017-1-4
     */
    public abstract static class OptionMenu implements Serializable {

        /**
         * 图标资源id
         */
        public int             mIconId;
        public NotifyImageView mView;

        protected OptionMenu() {

        }

        protected OptionMenu(int iconId, NotifyImageView view) {
            this.mIconId = iconId;
            this.mView = view;
        }

        /**
         * 图标点击事件
         *
         * @param view
         */
        public abstract void onClick(NotifyImageView view);
    }
}
