package cube.ware.service.message.chat.activity.file.listener;

import java.io.File;
import java.util.Map;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/10.
 */

public interface OnFileItemSelected {
    void onFileSelected(Map<Integer, File> mSelectedFileMap);
}
