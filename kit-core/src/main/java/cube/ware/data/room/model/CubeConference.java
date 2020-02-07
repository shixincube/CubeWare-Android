package cube.ware.data.room.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * author: kun .
 * date:   On 2018/9/3
 */
@Entity
public class CubeConference {
    @NonNull
    @PrimaryKey
    private String timestamp;
    private String cubeId;
    private String conferenceName;
    private String ConferenceTime;

    public CubeConference() {}

    @Ignore
    public CubeConference(String cubeId, String conferenceName, String ConferenceTime) {
        this.cubeId = cubeId;
        this.conferenceName = conferenceName;
        this.ConferenceTime = ConferenceTime;
    }

    @NonNull
    public String getCubeId() {
        return cubeId;
    }

    public void setCubeId(@NonNull String cubeId) {
        this.cubeId = cubeId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    public String getConferenceTime() {
        return ConferenceTime;
    }

    public void setConferenceTime(String conferenceTime) {
        ConferenceTime = conferenceTime;
    }

    @Override
    public String toString() {
        return "CubeConference{" + "timestamp='" + timestamp + '\'' + ", cubeId='" + cubeId + '\'' + ", conferenceName='" + conferenceName + '\'' + ", ConferenceTime='" + ConferenceTime + '\'' + '}';
    }
}


