package cube.ware.data.room.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

/**
 * 用户信息表
 *
 * @author LiuFeng
 * @data 2020/2/12 20:32
 */
@Entity
public class CubeUser {

    @PrimaryKey
    @NonNull
    private String cubeId;           //用户ID
    private String displayName;      //用户显示昵称
    @SerializedName("avator")
    private String avatar;           //用户头像

    public CubeUser() {}

    @Ignore
    public CubeUser(String cubeId, String displayName, String avatar) {
        this.cubeId = cubeId;
        this.displayName = displayName;
        this.avatar = avatar;
    }

    public String getCubeId() {
        return cubeId;
    }

    public void setCubeId(String cubeId) {
        this.cubeId = cubeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "CubeUser{" + "cubeId='" + cubeId + '\'' + ", displayName='" + displayName + '\'' + ", avatar='" + avatar + '\'' + '}';
    }
}
