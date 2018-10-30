package cube.ware.utils;

import android.view.View;

import java.util.Calendar;

/**
 * 自定义防止抖动点击
 *
 * @author Wangxx
 * @date 2017/2/14
 */

public abstract class NoDoubleClickListener implements View.OnClickListener {
    public static final int  MIN_CLICK_DELAY_TIME = 500;
    private             long lastClickTime        = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    protected abstract void onNoDoubleClick(View v);
}
