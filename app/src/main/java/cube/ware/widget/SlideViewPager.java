package cube.ware.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 控制ViewPager是否允许滑动
 *
 * @author PengZhenjin
 * @date 2016-12-29
 */
public class SlideViewPager extends ViewPager {

    // 是否可以滑动 true:允许滑动，false:禁止滑动
    private boolean isSlide = true;

    public SlideViewPager(Context context) {
        super(context);
        this.isSlide = true;
    }

    public SlideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isSlide = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return this.isSlide && super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return this.isSlide && super.onTouchEvent(event);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 设置是否允许滑动
     *
     * @param isSlide
     */
    public void setSlide(boolean isSlide) {
        this.isSlide = isSlide;
    }

    public boolean isSlide() {
        return isSlide;
    }
}
