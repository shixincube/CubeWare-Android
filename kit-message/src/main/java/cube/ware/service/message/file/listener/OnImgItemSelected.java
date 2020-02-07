package cube.ware.service.message.file.listener;


import cube.ware.service.message.file.entity.LocalMedia;
import java.util.List;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/10.
 */

public interface OnImgItemSelected {
    void onImgSelected(List<LocalMedia> imgMap);
}
