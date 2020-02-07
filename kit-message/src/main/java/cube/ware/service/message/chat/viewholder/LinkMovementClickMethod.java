package cube.ware.service.message.chat.viewholder;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 自定义LinkMovementMethod，用来判断点击和长按。
 * Created by dell on 2017/8/16.
 */

public class LinkMovementClickMethod extends LinkMovementMethod{
    private long lastClickTime;

    private static final long CLICK_DELAY = 500l;

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            /**
             * 去判断点击时间
             */
            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    if(System.currentTimeMillis() - lastClickTime < CLICK_DELAY){
                        Log.d("短按","1");
                        link[0].onClick(widget);
                    }
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                                           buffer.getSpanStart(link[0]),
                                           buffer.getSpanEnd(link[0]));
                    lastClickTime = System.currentTimeMillis();
                }

                return true;
            } else {
                Log.d("长按","2");
                Selection.removeSelection(buffer);
            }
        }
        return super.onTouchEvent(widget, buffer, event);
    }

    public static LinkMovementClickMethod getInstance(){
        if(null == sInstance){
            sInstance = new LinkMovementClickMethod();
        }
        return sInstance;
    }

    private static LinkMovementClickMethod sInstance;

}
