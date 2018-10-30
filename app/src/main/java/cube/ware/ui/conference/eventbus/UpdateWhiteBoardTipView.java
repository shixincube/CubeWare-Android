package cube.ware.ui.conference.eventbus;

import java.util.List;

/**
 * Created by q on 2018/10/29.
 */

public class UpdateWhiteBoardTipView {
    public List<String> mGroupIds;

    public UpdateWhiteBoardTipView(List<String> groupids) {
        mGroupIds = groupids;
    }
}
