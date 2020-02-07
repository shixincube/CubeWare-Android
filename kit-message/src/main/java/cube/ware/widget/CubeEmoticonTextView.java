package cube.ware.widget;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import cube.ware.service.message.R;
import cube.ware.service.message.chat.helper.AtHelper;
import cube.ware.service.message.chat.panel.input.emoticon.EmoticonUtil;

/**
 * 表情TextView
 *
 * @author PengZhenjin
 * @date 2016/7/18
 */
public class CubeEmoticonTextView extends TextView {
    private static final String TAG = CubeEmoticonTextView.class.getSimpleName();

    public CubeEmoticonTextView(Context context) {
        super(context);
    }

    public CubeEmoticonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CubeEmoticonTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setText(CharSequence text, BufferType type, boolean isMy) {
        if (!TextUtils.isEmpty(text)) {
            SpannableStringBuilder spannableStringBuilder = EmoticonUtil.gifEmoticons(this.getContext(), this, text.toString(), EmoticonUtil.DEF_SCALE, ImageSpan.ALIGN_BOTTOM);
            if (isMy) {
                AtHelper.replaceAtTag(this.getContext(), spannableStringBuilder, R.color.white);
            }
            else {
                AtHelper.replaceAtTag(this.getContext(), spannableStringBuilder, R.color.cube_primary);
            }
            text = spannableStringBuilder;
        }
        super.setText(text, type);
    }
}
