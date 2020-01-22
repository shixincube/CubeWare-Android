package cube.ware.service.widget.toolbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author CloudZhang
 * @date 2017/11/22 14:01
 * 无论是原生形式的toolbar还是自定义view实现的toolbar 统一向外部提供ICubeToolbar接口
 */

public interface ICubeToolbar {

    public void setProgressDrawable(Drawable drawable);

    public void setProgressVisible(boolean visible);

    public boolean isProgressVisible();

    public void setTitle(CharSequence title);

    public void setTitleIcon(@Nullable Drawable left, @Nullable Drawable right);

    public CharSequence getTitle();

    public void setTitleTextAppearance(Context context, int resId);

    public void setTitleTextColor(@ColorRes int color);

    public void setTitleTextSize(int titleSize);

    public void setTitleVisible(boolean visible);

    public boolean getTitleVisible();

    public void setSubtitle(CharSequence subtitle);

    public CharSequence getSubtitle();

    public void setSubtitleTextAppearance(Context context, int resId);

    public void setSubtitleTextColor(int color);

    public void setSubtitleVisible(boolean visible);

    public boolean getSubtitleVisible();

    public void setLogoBackground(int resId);

    public void setLogoVisible(boolean visible);

    public boolean isLogoVisible();

    public void setBackText(int resId);

    public void setBackText(CharSequence backText);

    public void setClsoeText(CharSequence clsoeText);

    public void setBackTextSize(int backTextSize);

    public void setBackIcon(Drawable drawable);

    public CharSequence getBackText();

    public void setBackTextColor(@ColorRes int color);

    public void setBackVisible(boolean visible);

    public void setCloseVisible(boolean visible);

    public void setBackPadding(int backPadding);

    public boolean isBackVisible();

    public void setRightText(int resId);

    public void setRightText(CharSequence rightText);

    public void setSubtitleTextViewIcon(Drawable drawable);

    public void setSubtitleTextViewSize(float size);

    public void setRightIcon(Drawable drawable);

    public void setCloseIcon(Drawable drawable);

    public void setRightPadding(int backPadding);

    public CharSequence getRightText();

    public void setRightTextSize(int rightTextSize);

    public void setRightTextColor(@ColorRes int color);

    public void setRightVisible(boolean visible);

    public boolean isRightVisible();

    public void setRightEnabled(boolean enabled);

    public void onClick(View v);

    public void setSubTitleLeftIcon(int subTitleLeftIcon);

    public void setScrollFlags(int scrollFlags);

    void setOnTitleItemClickListener(OnTitleItemClickListener onTitleClickListener);

    public int getHeight();

    void addView(LinearLayout titleOptionLayout, ViewGroup.LayoutParams layoutParams);

    void addView(LinearLayout titleOptionLayout);

    void setVisibility(int gone);

    void setBackgroundResource(int black);

    interface OnTitleItemClickListener {
        void onTitleItemClick(View v);
    }
}
