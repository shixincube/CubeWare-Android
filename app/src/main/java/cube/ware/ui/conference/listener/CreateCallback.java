package cube.ware.ui.conference.listener;

import cube.service.common.model.CubeError;
import cube.service.conference.model.Conference;

/**
 * author: kun .
 * date:   On 2018/9/10
 */
public interface CreateCallback {
    void onFinish(Conference conference);
    void onJoined(Conference conference);
    void onCreate(Conference conference);
    void onError(Conference conference, CubeError error);
}
