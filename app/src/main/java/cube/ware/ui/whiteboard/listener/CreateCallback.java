package cube.ware.ui.whiteboard.listener;

import cube.service.common.model.CubeError;
import cube.service.conference.model.Conference;
import cube.service.whiteboard.model.Whiteboard;

/**
 * author: kun .
 * date:   On 2018/9/10
 */
public interface CreateCallback {
    void onWBFinish(Whiteboard whiteboard);
    void onWBCreate(Whiteboard whiteboard);
    void onWBError(Whiteboard whiteboard, CubeError error);
}
