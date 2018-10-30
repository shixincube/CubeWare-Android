package cube.ware.ui.chat.panel.input.function;

import android.widget.Toast;

import cube.ware.App;
import cube.ware.R;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/11.
 */

public class ShareScreenFunction extends BaseFunction{
    public ShareScreenFunction(CubeSessionType mSessionType) {
        super(R.drawable.selector_chat_function_video_btn, R.string.share_screen);
    }

    @Override
    public void onClick() {
        Toast.makeText(App.getContext(), "共享屏幕", Toast.LENGTH_SHORT).show();
    }
}
