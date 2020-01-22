package cube.ware.service.widget.toolbar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cube.ware.service.message.R;

/**
 * @author Wangxx
 * @date 2017/3/16
 */

public class NotifyImageView extends FrameLayout {

    private ImageView mNotifyIv;
    private ImageView mNotifyNv;

    public NotifyImageView(Context context) {
        this(context, null);
    }

    public NotifyImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotifyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.notify_view, this);
        init();
    }

    private void init() {
        mNotifyIv = (ImageView) findViewById(R.id.notify_iv);
        mNotifyNv = (ImageView) findViewById(R.id.notify_nv);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mNotifyIv.setEnabled(enabled);
    }

    public void notifyShow(boolean isShow) {
        mNotifyNv.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setIcon(int imageResource) {
        mNotifyIv.setImageDrawable(getResources().getDrawable(imageResource));
    }
}
