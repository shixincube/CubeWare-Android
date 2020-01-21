package cube.ware.service.conference.create;

import java.util.List;

import cube.service.conference.model.Conference;

/**
 * author: kun .
 * date:   On 2018/9/13
 */
public class CreateData {
    Conference mConference;
    List<String> mList;
    List<String> mInvites;
    String mTitle ;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Conference getConference() {
        return mConference;
    }

    public void setConference(Conference conference) {
        mConference = conference;
    }

    public List<String> getList() {
        return mList;
    }

    public void setList(List<String> list) {
        mList = list;
    }

    public List<String> getInvites() {
        return mInvites;
    }

    public void setInvites(List<String> invites) {
        mInvites = invites;
    }

    @Override
    public String toString() {
        return "CreateData{" +
                "mConference=" + mConference +
                ", mList=" + mList +
                ", mInvites=" + mInvites +
                ", mTitle='" + mTitle + '\'' +
                '}';
    }
}
