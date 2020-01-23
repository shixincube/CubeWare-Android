package cube.ware.service.message.chat.activity.file.listener;


import java.util.List;

import cube.ware.service.message.chat.activity.file.entity.LocalMedia;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/10.
 */

public interface OnImgItemSelected {
    void onImgSelected(List<LocalMedia> imgMap);
}
