package cube.ware.ui.chat.panel.input;

import android.text.Editable;

/**
 * 文本输入框监听
 *
 * @author Wangxx
 * @date 2017/1/10
 */
public interface MessageEditWatcher {

    void afterTextChanged(Editable s, int start, int before, int count);
}
