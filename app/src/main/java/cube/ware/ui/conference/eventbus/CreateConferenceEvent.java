package cube.ware.ui.conference.eventbus;

import java.util.List;

/**
 * author: kun .
 * date:   On 2018/9/13
 */
public class CreateConferenceEvent {
    public List<String> mList;
    public List<String> invites;
    public String title;

    public CreateConferenceEvent(List<String> list, List<String> invites, String title) {
        mList = list;
        this.invites = invites;
        this.title = title;
    }
}
