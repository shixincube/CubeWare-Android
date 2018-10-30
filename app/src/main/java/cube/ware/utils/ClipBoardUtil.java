package cube.ware.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * 剪贴板工具类
 *
 * @author PengZhenjin
 * @date 2017-1-10
 */
public class ClipBoardUtil {

    /**
     * 复制文本文件
     *
     * @param context
     * @param text
     */
    public static void copyText(Context context, String text) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("text", text);
        cmb.setPrimaryClip(cd);
    }

    /**
     * 粘贴文本文件
     *
     * @param context
     *
     * @return
     */
    public static String pasteText(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd = cmb.getPrimaryClip();
        ClipData.Item item = cd.getItemAt(0);
        return item.getText().toString();
    }
}
