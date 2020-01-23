package cube.ware.service.message.chat.panel.input.function;

import android.widget.Toast;
import cube.ware.service.message.R;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/11.
 */

public class ShareScreenFunction extends BaseFunction {
    public ShareScreenFunction(CubeSessionType mSessionType) {
        super(R.drawable.selector_chat_function_video_btn, R.string.share_screen);
    }

    @Override
    public void onClick() {
        Toast.makeText(CubeCore.getContext(), "共享屏幕", Toast.LENGTH_SHORT).show();
    }
}
