package cube.ware.ui.conference.eventbus;

import cube.service.conference.model.Conference;

/**
 * author: kun .
 * date:   On 2018/9/13
 */
public class InviteConferenceEvent {
    public Conference mConference;

    public InviteConferenceEvent(Conference conference) {
        mConference = conference;
    }
}
