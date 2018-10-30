package cube.ware.ui.conference.eventbus;

import java.util.List;

import cube.service.conference.model.Conference;

/**
 * author: kun .
 * date:   On 2018/9/13
 */
public class UpdateTipViewEvent {
    public List<String> mGroupIds;

    public UpdateTipViewEvent(List<String> groupids) {
        mGroupIds = groupids;
    }
}
